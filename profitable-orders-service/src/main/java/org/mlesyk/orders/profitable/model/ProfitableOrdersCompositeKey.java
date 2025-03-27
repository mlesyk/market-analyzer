package org.mlesyk.orders.profitable.model;

import java.io.Serializable;

public class ProfitableOrdersCompositeKey implements Serializable {
    private Long sellOrderIdPK;
    private Long buyOrderIdPK;
}
