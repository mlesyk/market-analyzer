package org.mlesyk.ui.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProfitableOrdersViewDTO {

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

    private Integer sellOrderRegionId;
    private Integer buyOrderRegionId;

    private Integer totalJumps;
    private Integer totalJumpsShortest;
    private Integer lowSecJumps;
    private Integer nullSecJumps;
    private Integer[] route;

    public Long getOrderVolume() {
        return Math.round(Math.min(sellOrderVolumeRemain, buyOrderVolumeRemain) * itemVolume);
    }

    public String getOrderVolumeFormatted() {
        return String.format("%.2f", (Math.min(sellOrderVolumeRemain, buyOrderVolumeRemain) * itemVolume));
    }

    public String getBuyOrderPriceFormatted() {
        return String.format("%.2f", buyOrderPrice);
    }

    public String getSellOrderPriceFormatted() {
        return String.format("%.2f", sellOrderPrice);
    }

    public Long getOrderProfitPerCubicMeter() {
        return Math.round(totalNetProfit / (Math.min(sellOrderVolumeRemain, buyOrderVolumeRemain) * itemVolume));
    }

    public Long getProfitPerJumpSafe() {
        return Math.round(totalNetProfit.doubleValue() / totalJumps);
    }

    public Long getProfitPerJumpShort() {
        return Math.round(totalNetProfit.doubleValue() / totalJumpsShortest);
    }

    public Integer getItemsCount() {
        return Math.min(sellOrderVolumeRemain, buyOrderVolumeRemain);
    }
}