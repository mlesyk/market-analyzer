package org.mlesyk.ui.util;

import org.mlesyk.ui.model.ProfitableOrdersTypeIdGroupView;
import org.mlesyk.ui.model.ProfitableOrdersViewDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class OrderUtil {

    public Map<ProfitableOrdersTypeIdGroupView, List<ProfitableOrdersViewDTO>> getProfitableOrdersGroupedByTypeId(List<ProfitableOrdersViewDTO> profitableOrders) {
        Map<ProfitableOrdersTypeIdGroupView, List<ProfitableOrdersViewDTO>> orderByTypeIdMap = profitableOrders.stream()
                .collect(Collectors.groupingBy(ProfitableOrdersViewDTO::getMarketItemID,Collectors.toList()))
                .entrySet().stream().collect(Collectors.toMap(k -> new ProfitableOrdersTypeIdGroupView(k.getKey()), Map.Entry::getValue));
        for(Map.Entry<ProfitableOrdersTypeIdGroupView, List<ProfitableOrdersViewDTO>> entry: orderByTypeIdMap.entrySet()) {
            ProfitableOrdersTypeIdGroupView key = entry.getKey();
            List<ProfitableOrdersViewDTO> value = entry.getValue();
            key.setBuyOrdersCount(value.stream().map(ProfitableOrdersViewDTO::getBuyOrderId).distinct().count());
            key.setSellOrdersCount(value.stream().map(ProfitableOrdersViewDTO::getSellOrderId).distinct().count());
            key.setMaxProfit(value.stream().mapToLong(ProfitableOrdersViewDTO::getTotalNetProfit).max().orElse(0L));
            key.setMarketItemName(value.stream().map(ProfitableOrdersViewDTO::getMarketItemName).findFirst().orElse("NAN"));
            key.setMaxProfitPerCubicMeter(value.stream().mapToLong(ProfitableOrdersViewDTO::getOrderProfitPerCubicMeter).max().orElse(0L));
            key.setMaxProfitPerJumpSafe(value.stream().mapToLong(ProfitableOrdersViewDTO::getProfitPerJumpSafe).max().orElse(0L));
            key.setMaxProfitPerJumpShort(value.stream().mapToLong(ProfitableOrdersViewDTO::getProfitPerJumpShort).max().orElse(0L));
        }
        return orderByTypeIdMap;
    }
}
