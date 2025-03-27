package org.mlesyk.orders.profitable.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.*;
import org.mlesyk.orders.profitable.model.rest.RegionOrderAvgPriceCalculatedDTO;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@IdClass(RegionOrderAvgPriceCompositeKey.class)
@Table(name = "region_order_avg_prices")
public class RegionOrderAvgPriceCalculated {

    @Id
    private Integer typeId;
    private Double avgPrice;
    @Id
    private Boolean isBuyOrder;

    public RegionOrderAvgPriceCalculated(RegionOrderAvgPriceCalculatedDTO dto, Boolean isBuyOrder) {
        this.typeId = dto.getTypeId();
        this.avgPrice = dto.getAvgPrice();
        this.isBuyOrder = isBuyOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegionOrderAvgPriceCalculated regionOrderAvgPriceCalculated = (RegionOrderAvgPriceCalculated) o;
        return Objects.equals(typeId, regionOrderAvgPriceCalculated.typeId) && Objects.equals(isBuyOrder, regionOrderAvgPriceCalculated.isBuyOrder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeId, isBuyOrder);
    }
}
