package org.mlesyk.orders.analyzer.repository.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RegionOrderCalculatedValueDTO {
    private Long orderId;
    private Long locationId;
    private Boolean isBuyOrder;
    private Double price;
    private Integer systemId;
    private Integer typeId;
    private Integer volumeRemain;
    private Integer regionId;
    private Double orderValue;
}