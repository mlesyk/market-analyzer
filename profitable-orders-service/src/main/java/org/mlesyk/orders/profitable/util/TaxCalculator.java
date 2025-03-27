package org.mlesyk.orders.profitable.util;

import org.springframework.stereotype.Component;

@Component
public class TaxCalculator {

  // Broker's fee %=5%−0.3%×Broker relations level−0.03%×Faction standing−0.02%×Corporation standing
  public double getBrokerFee(int brokerRelationsLevel, double factionStanding, double corpStanding) {
    return (0.05 - 0.003 * brokerRelationsLevel - 0.0003 * factionStanding - 0.0002 * corpStanding);
  }

  //Sales tax %=5%−0.55%×Accounting level
  public double getSaleTax(int accountingLevel) {
    return (0.05 - 0.0055 * accountingLevel);
  }
}
