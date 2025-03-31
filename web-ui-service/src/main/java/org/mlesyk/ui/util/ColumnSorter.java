package org.mlesyk.ui.util;

import org.mlesyk.ui.model.ProfitableOrdersTypeIdGroupView;
import org.mlesyk.ui.model.ProfitableOrdersViewDTO;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ColumnSorter {

    public Map<ProfitableOrdersTypeIdGroupView, List<ProfitableOrdersViewDTO>> sortTypeIdGroupView(String sortField, String sortDirection, Map<ProfitableOrdersTypeIdGroupView, List<ProfitableOrdersViewDTO>> profitableOrdersGroupedByTypeId) {
        if(sortDirection.equals("asc")) {
            switch (sortField) {
                case "name":
                    return profitableOrdersGroupedByTypeId.entrySet()
                            .stream()
                            .sorted(Comparator.comparing(e -> e.getKey().getMarketItemName()))
                            .collect(Collectors.toMap(Map.Entry::getKey,
                                    Map.Entry::getValue,
                                    (oldValue, newValue) -> oldValue,
                                    LinkedHashMap::new));
                case "sellOrdersCount":
                    return profitableOrdersGroupedByTypeId.entrySet()
                            .stream()
                            .sorted(Comparator.comparing(e -> e.getKey().getSellOrdersCount()))
                            .collect(Collectors.toMap(Map.Entry::getKey,
                                    Map.Entry::getValue,
                                    (oldValue, newValue) -> oldValue,
                                    LinkedHashMap::new));
                case "buyOrdersCount":
                    return profitableOrdersGroupedByTypeId.entrySet()
                            .stream()
                            .sorted(Comparator.comparing(e -> e.getKey().getBuyOrdersCount()))
                            .collect(Collectors.toMap(Map.Entry::getKey,
                                    Map.Entry::getValue,
                                    (oldValue, newValue) -> oldValue,
                                    LinkedHashMap::new));
                case "maxProfit":
                    return profitableOrdersGroupedByTypeId.entrySet()
                            .stream()
                            .sorted(Comparator.comparing(e -> e.getKey().getMaxProfit()))
                            .collect(Collectors.toMap(Map.Entry::getKey,
                                    Map.Entry::getValue,
                                    (oldValue, newValue) -> oldValue,
                                    LinkedHashMap::new));
                case "maxProfitPerCubicMeter":
                    return profitableOrdersGroupedByTypeId.entrySet()
                            .stream()
                            .sorted(Comparator.comparing(e -> e.getKey().getMaxProfitPerCubicMeter()))
                            .collect(Collectors.toMap(Map.Entry::getKey,
                                    Map.Entry::getValue,
                                    (oldValue, newValue) -> oldValue,
                                    LinkedHashMap::new));
                case "maxProfitPerJumpSafe":
                    return profitableOrdersGroupedByTypeId.entrySet()
                            .stream()
                            .sorted(Comparator.comparing(e -> e.getKey().getMaxProfitPerJumpSafe()))
                            .collect(Collectors.toMap(Map.Entry::getKey,
                                    Map.Entry::getValue,
                                    (oldValue, newValue) -> oldValue,
                                    LinkedHashMap::new));
                case "maxProfitPerJumpShort":
                    return profitableOrdersGroupedByTypeId.entrySet()
                            .stream()
                            .sorted(Comparator.comparing(e -> e.getKey().getMaxProfitPerJumpShort()))
                            .collect(Collectors.toMap(Map.Entry::getKey,
                                    Map.Entry::getValue,
                                    (oldValue, newValue) -> oldValue,
                                    LinkedHashMap::new));
            }
        } else {
            switch (sortField) {
                case "name":
                    return profitableOrdersGroupedByTypeId.entrySet()
                            .stream()
                            .sorted(Comparator.comparing(e -> e.getKey().getMarketItemName(), Comparator.reverseOrder()))
                            .collect(Collectors.toMap(Map.Entry::getKey,
                                    Map.Entry::getValue,
                                    (oldValue, newValue) -> oldValue,
                                    LinkedHashMap::new));
                case "sellOrdersCount":
                    return profitableOrdersGroupedByTypeId.entrySet()
                            .stream()
                            .sorted(Comparator.comparing(e -> e.getKey().getSellOrdersCount(), Comparator.reverseOrder()))
                            .collect(Collectors.toMap(Map.Entry::getKey,
                                    Map.Entry::getValue,
                                    (oldValue, newValue) -> oldValue,
                                    LinkedHashMap::new));
                case "buyOrdersCount":
                    return profitableOrdersGroupedByTypeId.entrySet()
                            .stream()
                            .sorted(Comparator.comparing(e -> e.getKey().getBuyOrdersCount(), Comparator.reverseOrder()))
                            .collect(Collectors.toMap(Map.Entry::getKey,
                                    Map.Entry::getValue,
                                    (oldValue, newValue) -> oldValue,
                                    LinkedHashMap::new));
                case "maxProfit":
                    return profitableOrdersGroupedByTypeId.entrySet()
                            .stream()
                            .sorted(Comparator.comparing(e -> e.getKey().getMaxProfit(), Comparator.reverseOrder()))
                            .collect(Collectors.toMap(Map.Entry::getKey,
                                    Map.Entry::getValue,
                                    (oldValue, newValue) -> oldValue,
                                    LinkedHashMap::new));
                case "maxProfitPerCubicMeter":
                    return profitableOrdersGroupedByTypeId.entrySet()
                            .stream()
                            .sorted(Comparator.comparing(e -> e.getKey().getMaxProfitPerCubicMeter(), Comparator.reverseOrder()))
                            .collect(Collectors.toMap(Map.Entry::getKey,
                                    Map.Entry::getValue,
                                    (oldValue, newValue) -> oldValue,
                                    LinkedHashMap::new));
                case "maxProfitPerJumpSafe":
                    return profitableOrdersGroupedByTypeId.entrySet()
                            .stream()
                            .sorted(Comparator.comparing(e -> e.getKey().getMaxProfitPerJumpSafe(), Comparator.reverseOrder()))
                            .collect(Collectors.toMap(Map.Entry::getKey,
                                    Map.Entry::getValue,
                                    (oldValue, newValue) -> oldValue,
                                    LinkedHashMap::new));
                case "maxProfitPerJumpShort":
                    return profitableOrdersGroupedByTypeId.entrySet()
                            .stream()
                            .sorted(Comparator.comparing(e -> e.getKey().getMaxProfitPerJumpShort(), Comparator.reverseOrder()))
                            .collect(Collectors.toMap(Map.Entry::getKey,
                                    Map.Entry::getValue,
                                    (oldValue, newValue) -> oldValue,
                                    LinkedHashMap::new));
            }
        }
        return profitableOrdersGroupedByTypeId.entrySet()
                .stream()
                .sorted(Comparator.comparing(e -> e.getKey().getMarketItemName()))
                .collect(Collectors.toMap(Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new));
    }
}