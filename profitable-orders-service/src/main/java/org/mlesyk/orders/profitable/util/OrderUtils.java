package org.mlesyk.orders.profitable.util;

import lombok.extern.slf4j.Slf4j;
import org.mlesyk.orders.profitable.client.MarketApiReaderClient;
import org.mlesyk.orders.profitable.client.StaticDataServiceRestClient;
import org.mlesyk.orders.profitable.model.ProfitableOrders;
import org.mlesyk.orders.profitable.model.rest.MarketTypeNameToIdDTO;
import org.mlesyk.orders.profitable.model.rest.MarketTypeRestDTO;
import org.mlesyk.orders.profitable.model.rest.ProfitableOrdersViewDTO;
import org.mlesyk.orders.profitable.model.rest.UniverseSystemRestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

@Component
@Slf4j
public class OrderUtils {

    private final StaticDataServiceRestClient staticDataClient;
    private final MarketApiReaderClient marketApiReaderClient;
    private final TaxCalculator taxCalculator;

    @Autowired
    public OrderUtils(StaticDataServiceRestClient staticDataClient, MarketApiReaderClient marketApiReaderClient, TaxCalculator taxCalculator) {
        this.staticDataClient = staticDataClient;
        this.marketApiReaderClient = marketApiReaderClient;
        this.taxCalculator = taxCalculator;
    }

    ThreadPoolExecutor executor = new ThreadPoolExecutor(6, 6,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>());

    public List<ProfitableOrdersViewDTO> fillProfitableOrdersViewDTO(List<ProfitableOrders> profitableOrders, List<Integer> avoidRouteRegionIds) {
        List<Callable<ProfitableOrdersViewDTO>> profitableOrdersWorkers = Collections.synchronizedList(new ArrayList<Callable<ProfitableOrdersViewDTO>>());
        List<ProfitableOrdersViewDTO> profitableOrdersPairs = new ArrayList<>();

        for (ProfitableOrders profitableOrder : profitableOrders) {
            profitableOrdersWorkers.add(new Callable<ProfitableOrdersViewDTO>() {
                @Override
                public ProfitableOrdersViewDTO call() throws Exception {
                    return assembleProfitableOrdersView(profitableOrder, avoidRouteRegionIds);
                }
            });
        }
        try {
            List<Future<ProfitableOrdersViewDTO>> answers = executor.invokeAll(profitableOrdersWorkers);
            for (Future<ProfitableOrdersViewDTO> orderPair : answers) {
                ProfitableOrdersViewDTO profitableOrder = orderPair.get();
                if(profitableOrder != null) {
                    profitableOrdersPairs.add(orderPair.get());
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error(e.getMessage());
        } catch (ExecutionException e) {
            log.error(e.getMessage());
        }
        return profitableOrdersPairs;
    }

    public ProfitableOrdersViewDTO assembleProfitableOrdersView(ProfitableOrders profitableOrder, List<Integer> avoidRouteRegionIds) throws IOException {
        Integer marketTypeId = profitableOrder.getMarketItemId();
        MarketTypeRestDTO marketType = null;
        try {
            marketType = staticDataClient.getItemById(marketTypeId);
        } catch (HttpClientErrorException e) {
            log.info("marketTypeId is not found in staticData DB: {}", marketTypeId);
            return null;
        }
        Integer marketItemID = profitableOrder.getMarketItemId();
        Integer marketGroupID = marketType.getGroupId();
        String marketItemName = marketType.getName().getEn();
        Long sellOrderId = profitableOrder.getSellOrderIdPK();
        Long buyOrderId = profitableOrder.getBuyOrderIdPK();

        Integer sellOrderVolumeRemain = profitableOrder.getSellOrderVolumeRemain();
        Integer buyOrderVolumeRemain = profitableOrder.getBuyOrderVolumeRemain();
        Double itemVolume = marketType.getVolume();

        Double buyOrderPrice = profitableOrder.getBuyOrderPrice();
        Double sellOrderPrice = profitableOrder.getSellOrderPrice();

        Long totalNetProfit = Math.round(calculateBuySellOrderPairProfit(profitableOrder.getSellOrderPrice(), profitableOrder.getSellOrderVolumeRemain(), profitableOrder.getBuyOrderPrice(), profitableOrder.getBuyOrderVolumeRemain()));

        Integer sellOrderSystemId = profitableOrder.getSellOrderSystemId();
        Integer buyOrderSystemId = profitableOrder.getBuyOrderSystemId();

        UniverseSystemRestDTO sellOrderSystem = staticDataClient.getUniverseSystemById(sellOrderSystemId);
        UniverseSystemRestDTO buyOrderSystem = staticDataClient.getUniverseSystemById(buyOrderSystemId);

        Integer sellOrderRegionId = profitableOrder.getSellOrderRegionId();
        Integer buyOrderRegionId = profitableOrder.getBuyOrderRegionId();

        List<Integer> jumpsIdsSecure = null;
        List<Integer> jumpsIdsShortest = null;
        try {
            jumpsIdsSecure = marketApiReaderClient.getSecureRoute(sellOrderSystemId, buyOrderSystemId, avoidRouteRegionIds);
            jumpsIdsShortest = marketApiReaderClient.getShortestRoute(sellOrderSystemId, buyOrderSystemId, avoidRouteRegionIds);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("Error reading route for {} and {}", sellOrderSystemId, buyOrderSystemId);
            jumpsIdsSecure = new ArrayList<>();
            jumpsIdsShortest = new ArrayList<>();
        }

        List<UniverseSystemRestDTO> jumpsSecure = staticDataClient.getUniverseSystemsById(jumpsIdsSecure);
        Integer totalJumpsSecure = jumpsIdsSecure.size();
        Integer totalJumpsShortest = jumpsIdsShortest.size();
        Integer lowSecJumps = Math.toIntExact(jumpsSecure.stream().filter(e -> e.getSecurity() > 0.0 && e.getSecurity() < 0.45).count());
        Integer nullSecJumps = Math.toIntExact(jumpsSecure.stream().filter(e -> e.getSecurity() < 0.0).count());
        Integer[] route = jumpsIdsSecure.toArray(new Integer[0]);

        return new ProfitableOrdersViewDTO.Builder()
                .setMarketItemID(marketItemID)
                .setMarketGroupID(marketGroupID)
                .setMarketItemName(marketItemName)
                .setSellOrderId(sellOrderId)
                .setBuyOrderId(buyOrderId)
                .setSellOrderVolumeRemain(sellOrderVolumeRemain)
                .setBuyOrderVolumeRemain(buyOrderVolumeRemain)
                .setItemVolume(itemVolume)
                .setSellOrderPrice(sellOrderPrice)
                .setBuyOrderPrice(buyOrderPrice)
                .setTotalNetProfit(totalNetProfit)
                .setSellOrderSystem(sellOrderSystem.getSystemName())
                .setBuyOrderSystem(buyOrderSystem.getSystemName())
                .setBuyOrderRegionId(buyOrderRegionId)
                .setSellOrderRegionId(sellOrderRegionId)
                .setTotalJumps(totalJumpsSecure)
                .setTotalJumpsShortest(totalJumpsShortest)
                .setLowSecJumps(lowSecJumps)
                .setNullSecJumps(nullSecJumps)
                .setRoute(route)
                .build();
    }

    public Double calculateBuySellOrderPairProfit(Double sellOrderPrice, Integer sellOrderVolume, Double buyOrderPrice, Integer buyOrderVolume) {
        int transactionVolume = Math.min(sellOrderVolume, buyOrderVolume);
        return (buyOrderPrice * (1.0 - taxCalculator.getSaleTax(0)) - sellOrderPrice) * transactionVolume;
    }

    public List<MarketTypeNameToIdDTO> getMarketTypeNameToIdMapping() {
        return staticDataClient.getMarketTypeNameToIdList();
    }
}
