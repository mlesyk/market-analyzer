package org.mlesyk.orders.profitable.model.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.mlesyk.orders.profitable.repository.dto.RegionOrderCalculatedValueDTO;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
@ToString
public class ProfitableOrdersDTO {
    private Integer marketTypeId;
    private RegionOrderCalculatedValueDTO buyOrder;
    private RegionOrderCalculatedValueDTO sellOrder;

    public ProfitableOrdersDTO(Integer marketTypeId, RegionOrderCalculatedValueDTO buyOrder, RegionOrderCalculatedValueDTO sellOrder) {
        this.marketTypeId = marketTypeId;
        this.buyOrder = buyOrder;
        this.sellOrder = sellOrder;
    }
}
