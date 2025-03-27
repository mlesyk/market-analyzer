package org.mlesyk.orders.profitable.service;

import lombok.extern.slf4j.Slf4j;
import org.mlesyk.orders.profitable.client.MarketApiReaderClient;
import org.mlesyk.orders.profitable.model.ProfitableOrders;
import org.mlesyk.orders.profitable.model.RegionOrderStatistics;
import org.mlesyk.orders.profitable.model.rest.RegionOrderStatisticsRestDTO;
import org.mlesyk.orders.profitable.repository.ProfitableOrdersRepository;
import org.mlesyk.orders.profitable.repository.RegionOrderStatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.groupingBy;

@Service
@Slf4j
public class RegionOrderStatisticsConsumerService {

    @Value("${app.orders.statistics_store_days_limit}")
    Integer storeDaysLimit;

    private final RegionOrderStatisticsRepository regionOrderStatisticsRepository;
    private final ProfitableOrdersRepository profitableOrdersRepository;
    private final MarketApiReaderClient marketApiReaderClient;
    private final TaskExecutor taskExecutor;

    @Autowired
    public RegionOrderStatisticsConsumerService(
            RegionOrderStatisticsRepository regionOrderStatisticsRepository,
            ProfitableOrdersRepository profitableOrdersRepository,
            MarketApiReaderClient marketApiReaderClient,
            @Qualifier("statisticsReaderTaskExecutor") TaskExecutor taskExecutor) {
        this.regionOrderStatisticsRepository = regionOrderStatisticsRepository;
        this.profitableOrdersRepository = profitableOrdersRepository;
        this.marketApiReaderClient = marketApiReaderClient;
        this.taskExecutor = taskExecutor;
    }

    @Value("${app.services.statistics_enabled}")
    boolean statisticsEnabled;

    @Scheduled(cron = "*/15 * * * * *")
    public void consumerScheduler() {
        if (statisticsEnabled) {
            Date dateLimit = java.sql.Timestamp.valueOf(LocalDateTime.now().minusDays(storeDaysLimit));
            Iterable<ProfitableOrders> profitableOrders = profitableOrdersRepository.findAll();

            Map<Integer, List<ProfitableOrders>> ordersGroupedByTypeId = StreamSupport.stream(profitableOrders.spliterator(), false).collect(groupingBy(ProfitableOrders::getMarketItemId));

            for (Map.Entry<Integer, List<ProfitableOrders>> entry : ordersGroupedByTypeId.entrySet()) {
                Integer typeId = entry.getKey();
                Set<Integer> sellOrderRegionIds = entry.getValue().stream().map(ProfitableOrders::getSellOrderRegionId).collect(Collectors.toSet());
                Set<Integer> buyOrderRegionIds = entry.getValue().stream().map(ProfitableOrders::getBuyOrderRegionId).collect(Collectors.toSet());
                sellOrderRegionIds.addAll(buyOrderRegionIds);
                for (Integer regionId : sellOrderRegionIds) {
                    taskExecutor.execute(() -> {
                        try {
                            List<RegionOrderStatisticsRestDTO> orderStatistics = marketApiReaderClient.getOrderStatistics(regionId, typeId);
                            orderStatistics = orderStatistics.stream().filter(e -> e.getDate().before(dateLimit)).toList();
                            List<RegionOrderStatistics> orderStatisticsToSave = orderStatistics.stream().map(e -> new RegionOrderStatistics(e, typeId, regionId)).toList();
                            regionOrderStatisticsRepository.saveAll(orderStatisticsToSave);
                        } catch (Exception e) {
                            log.error("Error happened during read orders statistics for typeId {} in region {}", typeId, regionId, e);
                        }
                    });
                }
            }
        }
    }
}
