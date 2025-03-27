package org.mlesyk.orders.analyzer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.*;

import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Table(name = "region_order", indexes = {
        @Index(name = "orderValueIndex", columnList = "orderValue"),
        @Index(name = "isBuyOrderOrderValueIndex", columnList = "isBuyOrder, orderValue"),
        @Index(name = "orderValueIsBuyOrderIndex", columnList = "orderValue, isBuyOrder")
})
public class RegionOrder {

    private Integer duration;
    private Boolean isBuyOrder;
    private Date issued;
    private Long locationId;
    private Integer minVolume;
    @Id
    private Long orderId;
    private Double price;
    private String range;
    private Integer systemId;
    private Integer typeId;
    private Integer volumeRemain;
    private Integer volumeTotal;
    private Integer regionId;
    private Double orderValue;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegionOrder order = (RegionOrder) o;
        return Objects.equals(orderId, order.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }

}