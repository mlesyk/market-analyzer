package org.mlesyk.orders.profitable.model.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RegionOrderStatisticsRestDTO {

    private Double average;
    private Date date;
    private Double highest;
    private Double lowest;
    private Integer orderCount;
    private Long volume;
}
