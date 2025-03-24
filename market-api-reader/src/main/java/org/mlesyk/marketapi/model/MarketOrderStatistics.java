package org.mlesyk.marketapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.util.Date;

/**
 * [
 *   {
 *     "average": 90760000,
 *     "date": "2020-12-18",
 *     "highest": 90760000,
 *     "lowest": 90760000,
 *     "order_count": 1,
 *     "volume": 1
 *   }
 * ]
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MarketOrderStatistics {

    private Double average;
    private Date date;
    private Double highest;
    private Double lowest;
    private Integer orderCount;
    private Long volume;

}
