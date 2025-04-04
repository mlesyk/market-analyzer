package org.mlesyk.orders.profitable.model.rest;

import java.util.Date;

public class ProfitableOrdersViewDTO {

    private final Integer marketItemID;
    private final Integer marketGroupID;
    private final String marketItemName;

    private final Long sellOrderId;
    private final Long buyOrderId;

    private final Integer sellOrderVolumeRemain;
    private final Integer buyOrderVolumeRemain;
    private final Double itemVolume;

    private final Double buyOrderPrice;
    private final Double sellOrderPrice;

    private final Long totalNetProfit;
    private final Date calculationTimestamp;

    private final String sellOrderSystem;
    private final String buyOrderSystem;

    private final Integer sellOrderRegionId;
    private final Integer buyOrderRegionId;

    private final String sellOrderRegionName;
    private final String buyOrderRegionName;

    private final Integer totalJumps;
    private final Integer totalJumpsShortest;
    private final Integer lowSecJumps;
    private final Integer nullSecJumps;
    private final Integer[] route;

    public ProfitableOrdersViewDTO(Builder b) {
        this.marketItemID = b.marketItemID;
        this.marketGroupID = b.marketGroupID;
        this.marketItemName = b.marketItemName;
        this.sellOrderId = b.sellOrderId;
        this.buyOrderId = b.buyOrderId;
        this.sellOrderVolumeRemain = b.sellOrderVolumeRemain;
        this.buyOrderVolumeRemain = b.buyOrderVolumeRemain;
        this.itemVolume = b.itemVolume;
        this.buyOrderPrice = b.buyOrderPrice;
        this.sellOrderPrice = b.sellOrderPrice;
        this.totalNetProfit = b.totalNetProfit;
        this.calculationTimestamp = b.calculationTimestamp;
        this.sellOrderSystem = b.sellOrderSystem;
        this.buyOrderSystem = b.buyOrderSystem;
        this.sellOrderRegionId = b.sellOrderRegionId;
        this.buyOrderRegionId = b.buyOrderRegionId;
        this.sellOrderRegionName = b.sellOrderRegionName;
        this.buyOrderRegionName = b.buyOrderRegionName;
        this.totalJumps = b.totalJumps;
        this.totalJumpsShortest = b.totalJumpsShortest;
        this.lowSecJumps = b.lowSecJumps;
        this.nullSecJumps = b.nullSecJumps;
        this.route = b.route;
    }

    public static class Builder {

        private Integer marketItemID;
        private Integer marketGroupID;
        private String marketItemName;

        private Long sellOrderId;
        private Long buyOrderId;

        private Integer sellOrderVolumeRemain;
        private Integer buyOrderVolumeRemain;
        private Double itemVolume;

        private Double buyOrderPrice;
        private Double sellOrderPrice;

        private Long totalNetProfit;
        private Date calculationTimestamp;

        private String sellOrderSystem;
        private String buyOrderSystem;

        private String sellOrderRegionName;
        private String buyOrderRegionName;

        private Integer sellOrderRegionId;
        private Integer buyOrderRegionId;

        private Integer totalJumps;
        private Integer totalJumpsShortest;
        private Integer lowSecJumps;
        private Integer nullSecJumps;
        private Integer[] route;

        public Builder() {
            calculationTimestamp = new Date();
        }

        public Builder setMarketItemID(Integer marketItemID) {
            this.marketItemID = marketItemID;
            return this;
        }

        public Builder setMarketGroupID(Integer marketGroupID) {
            this.marketGroupID = marketGroupID;
            return this;
        }

        public Builder setMarketItemName(String marketItemName) {
            this.marketItemName = marketItemName;
            return this;
        }

        public Builder setSellOrderId(Long sellOrderId) {
            this.sellOrderId = sellOrderId;
            return this;
        }

        public Builder setBuyOrderId(Long buyOrderId) {
            this.buyOrderId = buyOrderId;
            return this;
        }

        public Builder setSellOrderVolumeRemain(Integer sellOrderVolumeRemain) {
            this.sellOrderVolumeRemain = sellOrderVolumeRemain;
            return this;
        }

        public Builder setBuyOrderVolumeRemain(Integer buyOrderVolumeRemain) {
            this.buyOrderVolumeRemain = buyOrderVolumeRemain;
            return this;
        }

        public Builder setItemVolume(Double itemVolume) {
            this.itemVolume = itemVolume;
            return this;
        }

        public Builder setTotalNetProfit(Long totalNetProfit) {
            this.totalNetProfit = totalNetProfit;
            return this;
        }

        public Builder setSellOrderSystem(String sellOrderSystem) {
            this.sellOrderSystem = sellOrderSystem;
            return this;
        }

        public Builder setBuyOrderSystem(String buyOrderSystem) {
            this.buyOrderSystem = buyOrderSystem;
            return this;
        }

        public Builder setTotalJumps(Integer totalJumps) {
            this.totalJumps = totalJumps;
            return this;
        }

        public Builder setTotalJumpsShortest(Integer totalJumpsShortest) {
            this.totalJumpsShortest = totalJumpsShortest;
            return this;
        }

        public Builder setLowSecJumps(Integer lowSecJumps) {
            this.lowSecJumps = lowSecJumps;
            return this;
        }

        public Builder setNullSecJumps(Integer nullSecJumps) {
            this.nullSecJumps = nullSecJumps;
            return this;
        }

        public Builder setRoute(Integer[] route) {
            this.route = route;
            return this;
        }

        public Builder setBuyOrderRegionId(Integer buyOrderRegionId) {
            this.buyOrderRegionId = buyOrderRegionId;
            return this;
        }

        public Builder setSellOrderRegionId(Integer sellOrderRegionName) {
            this.sellOrderRegionId = sellOrderRegionId;
            return this;
        }

        public Builder setBuyOrderRegionName(String buyOrderRegionName) {
            this.buyOrderRegionName = buyOrderRegionName;
            return this;
        }

        public Builder setSellOrderRegionName(String sellOrderRegionName) {
            this.sellOrderRegionName = sellOrderRegionName;
            return this;
        }

        public Builder setBuyOrderPrice(Double buyOrderPrice) {
            this.buyOrderPrice = buyOrderPrice;
            return this;
        }

        public Builder setSellOrderPrice(Double sellOrderPrice) {
            this.sellOrderPrice = sellOrderPrice;
            return this;
        }

        public ProfitableOrdersViewDTO build() {
            return new ProfitableOrdersViewDTO(this);
        }
    }

    public Integer getMarketItemID() {
        return marketItemID;
    }

    public Integer getMarketGroupID() {
        return marketGroupID;
    }

    public String getMarketItemName() {
        return marketItemName;
    }

    public Long getSellOrderId() {
        return sellOrderId;
    }

    public Long getBuyOrderId() {
        return buyOrderId;
    }

    public Integer getSellOrderVolumeRemain() {
        return sellOrderVolumeRemain;
    }

    public Integer getBuyOrderVolumeRemain() {
        return buyOrderVolumeRemain;
    }

    public Double getItemVolume() {
        return itemVolume;
    }

    public Long getTotalNetProfit() {
        return totalNetProfit;
    }

    public Date getCalculationTimestamp() {
        return calculationTimestamp;
    }

    public String getSellOrderSystem() {
        return sellOrderSystem;
    }

    public String getBuyOrderSystem() {
        return buyOrderSystem;
    }

    public Integer getSellOrderRegionId() {
        return sellOrderRegionId;
    }

    public Integer getBuyOrderRegionId() {
        return buyOrderRegionId;
    }

    public String getSellOrderRegionName() {
        return sellOrderRegionName;
    }

    public String getBuyOrderRegionName() {
        return buyOrderRegionName;
    }

    public Integer getTotalJumps() {
        return totalJumps;
    }

    public Integer getTotalJumpsShortest() {
        return totalJumpsShortest;
    }

    public Integer getLowSecJumps() {
        return lowSecJumps;
    }

    public Integer getNullSecJumps() {
        return nullSecJumps;
    }

    public Double getBuyOrderPrice() {
        return buyOrderPrice;
    }

    public Double getSellOrderPrice() {
        return sellOrderPrice;
    }

    public Integer[] getRoute() {
        return route;
    }
}
