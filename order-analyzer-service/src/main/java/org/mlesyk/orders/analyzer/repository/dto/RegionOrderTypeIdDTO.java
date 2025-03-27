package org.mlesyk.orders.analyzer.repository.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@AllArgsConstructor
@ToString
public class RegionOrderTypeIdDTO {
    private final Integer typeId;
}
