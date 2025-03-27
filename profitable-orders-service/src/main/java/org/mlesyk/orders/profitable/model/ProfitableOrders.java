package org.mlesyk.orders.profitable.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.mlesyk.orders.profitable.model.rest.ProfitableOrdersDTO;
import org.mlesyk.orders.profitable.repository.dto.RegionOrderCalculatedValueDTO;

import java.util.Objects;

@Getter
@Setter
@ToString
@Entity
@IdClass(ProfitableOrdersCompositeKey.class)
public class ProfitableOrders {

    @Id
    private Long sellOrderIdPK;

    @Id
    private Long buyOrderIdPK;

    private Integer marketItemId;
    private Double buyOrderPrice;
    private Double sellOrderPrice;
    private Integer buyOrderSystemId;
    private Integer sellOrderSystemId;
    private Integer buyOrderRegionId;
    private Integer sellOrderRegionId;
    private Integer buyOrderVolumeRemain;
    private Integer sellOrderVolumeRemain;
    private Double orderValue;

    public ProfitableOrders() {

    }

    public ProfitableOrders(Integer marketTypeId, RegionOrderCalculatedValueDTO sellOrder, RegionOrderCalculatedValueDTO buyOrder) {
        this.sellOrderIdPK = sellOrder.getOrderId();
        this.buyOrderIdPK = buyOrder.getOrderId();
        this.marketItemId = marketTypeId;
        this.buyOrderPrice = buyOrder.getPrice();
        this.sellOrderPrice = sellOrder.getPrice();
        this.buyOrderSystemId = buyOrder.getSystemId();
        this.sellOrderSystemId = sellOrder.getSystemId();
        this.buyOrderRegionId = buyOrder.getRegionId();
        this.sellOrderRegionId = sellOrder.getRegionId();
        this.buyOrderVolumeRemain = buyOrder.getVolumeRemain();
        this.sellOrderVolumeRemain = sellOrder.getVolumeRemain();
        this.orderValue = Math.min(sellOrder.getVolumeRemain(), buyOrder.getVolumeRemain()) * buyOrder.getPrice();
    }

    public ProfitableOrders(ProfitableOrdersDTO profitableOrdersDTO) {
        this(profitableOrdersDTO.getMarketTypeId(), profitableOrdersDTO.getSellOrder(), profitableOrdersDTO.getBuyOrder());
    }

    public ProfitableOrders(Long sellOrderIdPK, Long buyOrderIdPK, Integer marketItemId, Double buyOrderPrice, Double sellOrderPrice,
                            Integer buyOrderSystemId, Integer sellOrderSystemId, Integer buyOrderRegionId, Integer sellOrderRegionId,
                            Integer buyOrderVolumeRemain, Integer sellOrderVolumeRemain, Double orderValue) {
        this.sellOrderIdPK = sellOrderIdPK;
        this.buyOrderIdPK = buyOrderIdPK;
        this.marketItemId = marketItemId;
        this.buyOrderPrice = buyOrderPrice;
        this.sellOrderPrice = sellOrderPrice;
        this.buyOrderSystemId = buyOrderSystemId;
        this.sellOrderSystemId = sellOrderSystemId;
        this.buyOrderRegionId = buyOrderRegionId;
        this.sellOrderRegionId = sellOrderRegionId;
        this.buyOrderVolumeRemain = buyOrderVolumeRemain;
        this.sellOrderVolumeRemain = sellOrderVolumeRemain;
        this.orderValue = orderValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProfitableOrders that = (ProfitableOrders) o;
        return Objects.equals(sellOrderIdPK, that.sellOrderIdPK) &&
                Objects.equals(buyOrderIdPK, that.buyOrderIdPK);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sellOrderIdPK, buyOrderIdPK);
    }

}