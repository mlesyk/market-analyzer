package org.mlesyk.marketapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.util.Date;
import java.util.Objects;


/**
 * https://esi.evetech.net/latest/markets/10000001/orders/?datasource=tranquility&order_type=buy&page=1
 * [
 *   {
 *     "duration": 90,
 *     "is_buy_order": true,
 *     "issued": "2021-01-19T01:51:08Z",
 *     "location_id": 60013024,
 *     "min_volume": 1,
 *     "order_id": 5902041090,
 *     "price": 27850,
 *     "range": "region",
 *     "system_id": 30000030,
 *     "type_id": 14202,
 *     "volume_remain": 20,
 *     "volume_total": 20
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
public class MarketOrder {

    private Integer duration;
    private Boolean isBuyOrder;
    private Date issued;
    private Long locationId;
    private Integer minVolume;
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
        MarketOrder order = (MarketOrder) o;
        return Objects.equals(orderId, order.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }

}
