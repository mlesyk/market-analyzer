package org.mlesyk.marketapi.util;

import org.mlesyk.marketapi.util.parameters.OrderType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class OrdersRestQueryBuilder {

    // ----------------QueryParams for RegionOrderQueryBuilder----------------
    private static String orderType = "order_type";
    private static String page = "page";
    private static String regionId = "region_id_path_param";
    private static String typeId = "type_id";

    private Map<String, Object> webParams;

    private OrderType orderTypeValue;
    private Integer pageValue;
    private Integer regionIdValue;
    private Integer typeIdValue;

    private OrdersRestQueryBuilder() {
        this.webParams = new HashMap<>();
    }

    public static OrdersRestQueryBuilder getInstance() {
        return new OrdersRestQueryBuilder();
    }

    public Map<String, Object> build() {
        Optional.ofNullable(orderTypeValue).ifPresent(value -> webParams.put(orderType, value));
        Optional.ofNullable(pageValue).ifPresent(value -> webParams.put(page, value));
        Optional.ofNullable(regionIdValue).ifPresent(value -> webParams.put(regionId, value));
        Optional.ofNullable(typeIdValue).ifPresent(value -> webParams.put(typeId, value));
        return webParams;
    }

    public OrdersRestQueryBuilder setOrderType(OrderType orderTypeValue) {
        this.orderTypeValue = orderTypeValue;
        return this;
    }

    public OrdersRestQueryBuilder setPage(Integer pageValue) {
        this.pageValue = pageValue;
        return this;
    }

    public OrdersRestQueryBuilder setRegionId(Integer regionIdValue) {
        this.regionIdValue = regionIdValue;
        return this;
    }

    public OrdersRestQueryBuilder setTypeId(Integer typeIdValue) {
        this.typeIdValue = typeIdValue;
        return this;
    }
}