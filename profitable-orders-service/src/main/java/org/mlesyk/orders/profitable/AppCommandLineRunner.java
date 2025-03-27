package org.mlesyk.orders.profitable;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AppCommandLineRunner {

    private final Environment env;

    @Autowired
    public AppCommandLineRunner(Environment env) {
        this.env = env;
    }

    @PostConstruct
    void init() {
        if(log.isDebugEnabled()) {
            log.debug(env.getProperty("app.orders.profit_amount_value"));
            log.debug(env.getProperty("app.orders.sell_order_prise_threshold"));
            log.debug(env.getProperty("app.orders.buy_order_prise_threshold"));
            log.debug(env.getProperty("app.client.static_data.url"));
            log.debug(env.getProperty("app.client.static_data.types_path"));
            log.debug(env.getProperty("app.client.static_data.systems_path"));
            log.debug(env.getProperty("app.client.market_api_reader.url"));
            log.debug(env.getProperty("app.client.market_api_reader.route_path"));
            log.debug(env.getProperty("app.client.market_api_reader.statistics_path"));
            log.debug(env.getProperty("app.kafka.topic.orders"));
            log.debug(env.getProperty("app.kafka.topic.orders_registry"));
        }
    }
}
