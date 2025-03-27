package org.mlesyk.orders.analyzer.repository.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RegionOrderAvgPriceCalculatedDTO {
    private Integer typeId;
    private Double avgPrice;
}
