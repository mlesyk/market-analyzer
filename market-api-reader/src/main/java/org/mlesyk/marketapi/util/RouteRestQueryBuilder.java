package org.mlesyk.marketapi.util;

import org.mlesyk.marketapi.util.parameters.RouteType;

import java.util.*;
import java.util.stream.Collectors;

public class RouteRestQueryBuilder {

    // ----------------QueryParams for RouteCalculatorQueryBuilder----------------
    private static String origin = "origin_path_param";
    private static String destination = "destination_path_param";
    private static String routeType = "flag";
    private static String systemIdPair = "connections";   // &connections=30000032|30000035
    private static String avoidRegionIds = "avoid";

    private Map<String, Object> webParams;

    private Integer originValue;
    private Integer destinationValue;
    private RouteType routeTypeValue;
    private List<SystemIdPair> systemIdPairValue;
    private List<Integer> avoidRegionIdsValue;

    public RouteRestQueryBuilder() {
        this.webParams = new HashMap<>();
    }

    public static RouteRestQueryBuilder getInstance() {
        return new RouteRestQueryBuilder();
    }

    public Map<String, Object> build() {
        Optional.ofNullable(originValue).ifPresent(value -> webParams.put(origin, value));
        Optional.ofNullable(destinationValue).ifPresent(value -> webParams.put(destination, value));
        Optional.ofNullable(routeTypeValue).ifPresent(value -> webParams.put(routeType, value));
        Optional.ofNullable(systemIdPairValue).ifPresent(value -> webParams.put(systemIdPair, getSystemIdPairWebParameter(value)));
        Optional.ofNullable(avoidRegionIdsValue).ifPresent(value -> webParams.put(avoidRegionIds, value.toArray(new Integer[0])));
        return webParams;
    }

    public RouteRestQueryBuilder setOriginValue(Integer originValue) {
        this.originValue = originValue;
        return this;
    }

    public RouteRestQueryBuilder setDestinationValue(Integer destinationValue) {
        this.destinationValue = destinationValue;
        return this;
    }

    public RouteRestQueryBuilder setRouteType(RouteType routeTypeValue) {
        this.routeTypeValue = routeTypeValue;
        return this;
    }

    public RouteRestQueryBuilder addConnectedSystemIdPair(Integer systemIdX, Integer systemIdY) {
        if(systemIdPairValue == null) {
            systemIdPairValue = new ArrayList<>();
        }
        this.systemIdPairValue.add(new SystemIdPair(systemIdX, systemIdY));
        return this;
    }

    public RouteRestQueryBuilder addRegionIdToAvoid(Integer avoidRegionIdParam) {
        if(avoidRegionIdParam == null) {
            return this;
        }
        if(avoidRegionIdsValue == null) {
            avoidRegionIdsValue = new ArrayList<>();
        }
        this.avoidRegionIdsValue.add(avoidRegionIdParam);
        return this;
    }

    public RouteRestQueryBuilder addRegionIdsToAvoid(List<Integer> avoidRegionIdsParam) {
        if(avoidRegionIdsParam == null) {
            return this;
        }
        if(avoidRegionIdsValue == null) {
            avoidRegionIdsValue = new ArrayList<>();
        }
        this.avoidRegionIdsValue.addAll(avoidRegionIdsParam);
        return this;
    }

    private String getSystemIdPairWebParameter(List<SystemIdPair> systemIdPairList) {
        return systemIdPairList.stream().map(e -> e.getSystemIdX().toString() + "|" + e.getSystemIdY()).collect(Collectors.joining(","));
    }

    public static class SystemIdPair {
        public final Integer systemIdX;
        public final Integer systemIdY;
        public SystemIdPair(Integer systemIdX, Integer systemIdY) {
            this.systemIdX = systemIdX;
            this.systemIdY = systemIdY;
        }

        public Integer getSystemIdX() {
            return systemIdX;
        }

        public Integer getSystemIdY() {
            return systemIdY;
        }
    }
}