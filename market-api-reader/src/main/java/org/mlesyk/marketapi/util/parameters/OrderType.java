package org.mlesyk.marketapi.util.parameters;

public enum OrderType {

    BUY("buy"),
    SELL("sell"),
    ALL("all");

    private String name;

    OrderType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}