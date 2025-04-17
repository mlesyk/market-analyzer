package org.mlesyk.orders.profitable.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlesyk.orders.profitable.client.MarketApiReaderClient;
import org.mlesyk.orders.profitable.client.StaticDataServiceRestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {OrderUtils.class, TaxCalculator.class})
class OrderUtilsTest {

    @MockitoBean
    StaticDataServiceRestClient staticDataClient;

    @MockitoBean
    MarketApiReaderClient marketApiReaderClient;

    @Autowired
    OrderUtils orderUtils;

    @Test
    void testCalculateBuySellOrderPairProfit() {
        Double sellOrderPrice = 3000.0;
        Integer sellOrderVolume = 2532294;
        Double buyOrderPrice = 3363.0;
        Integer buyOrderVolume = 3961557;
        Double calculated = orderUtils.calculateBuySellOrderPairProfit(sellOrderPrice, sellOrderVolume, buyOrderPrice, buyOrderVolume);
        System.out.println("Value = " + calculated );

    }

}
