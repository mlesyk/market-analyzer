package org.mlesyk.marketapi.util.parameters;

public enum  RouteType {
    SHORTEST("shortest"),
    SECURE("secure"),
    INSECURE("insecure");

    private String name;

    RouteType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static RouteType fromString(String name) {
        for (RouteType type : RouteType.values()) {
            if (type.name.equalsIgnoreCase(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No constant with name " + name + " found");
    }
}