package org.mlesyk.ui.model;

import lombok.*;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProfitableOrdersTypeIdGroupView {
    private Integer marketItemID;
    private String marketItemName;
    private Long sellOrdersCount;
    private Long buyOrdersCount;
    private Long maxProfit;
    private Long maxProfitPerCubicMeter;
    private Long maxProfitPerJumpSafe;
    private Long maxProfitPerJumpShort;

    public ProfitableOrdersTypeIdGroupView(Integer marketItemID) {
        this.marketItemID = marketItemID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProfitableOrdersTypeIdGroupView that = (ProfitableOrdersTypeIdGroupView) o;
        return Objects.equals(marketItemID, that.marketItemID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(marketItemID);
    }
}
