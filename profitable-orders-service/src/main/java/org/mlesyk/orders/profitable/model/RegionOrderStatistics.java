package org.mlesyk.orders.profitable.model;

import jakarta.persistence.*;
import lombok.*;
import org.mlesyk.orders.profitable.model.rest.RegionOrderStatisticsRestDTO;

import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "region_order_statistics", indexes = {
        @Index(name = "typeIdIndex", columnList = "typeId")})
public class RegionOrderStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Integer typeId;
    private Integer regionId;
    private Double average;
    private Date date;
    private Double highest;
    private Double lowest;
    private Integer orderCount;
    private Long volume;

    public RegionOrderStatistics(RegionOrderStatisticsRestDTO regionOrderStatisticsRestDTO, Integer typeId, Integer regionId) {
        this.typeId = typeId;
        this.regionId = regionId;
        this.average = regionOrderStatisticsRestDTO.getAverage();
        this.date = regionOrderStatisticsRestDTO.getDate();
        this.highest = regionOrderStatisticsRestDTO.getHighest();
        this.lowest = regionOrderStatisticsRestDTO.getLowest();
        this.orderCount = regionOrderStatisticsRestDTO.getOrderCount();
        this.volume = regionOrderStatisticsRestDTO.getVolume();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegionOrderStatistics order = (RegionOrderStatistics) o;
        return Objects.equals(typeId, order.typeId) && Objects.equals(regionId, order.regionId) && Objects.equals(date, order.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeId, regionId, date);
    }
}
