package org.mlesyk.orders.profitable.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withPrecision;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TaxCalculator.class})
class TaxCalculatorTest {

    @Autowired
    TaxCalculator taxCalculator;

    @Test
    void testGetBrokerFeeWithMaxSkills() {
        int brokerRelationsLevelMaxLevel = 5;
        double factionStandingMax = 10;
        double corpStanding = 10;
        double expected = 0.03;
        double precision = 0.000001;
        assertThat(taxCalculator.getBrokerFee(brokerRelationsLevelMaxLevel, factionStandingMax, corpStanding)).isEqualTo(expected, withPrecision(precision));
    }

    @Test
    void testGetSaleTaxWithMaxSkills() {
        int accountingLevel = 5;
        double expected = 0.0225;
        double precision = 0.000001;
        assertThat(taxCalculator.getSaleTax(accountingLevel)).isEqualTo(expected, withPrecision(precision));
    }
}
