package org.mlesyk.orders.profitable.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
public class RegionOrderAvgPriceCompositeKey implements Serializable {

    private Integer typeId;
    private Boolean isBuyOrder;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegionOrderAvgPriceCompositeKey that = (RegionOrderAvgPriceCompositeKey) o;
        return Objects.equals(typeId, that.typeId) && Objects.equals(isBuyOrder, that.isBuyOrder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeId, isBuyOrder);
    }
}
