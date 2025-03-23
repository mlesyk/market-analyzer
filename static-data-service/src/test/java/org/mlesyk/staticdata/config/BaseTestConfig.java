package org.mlesyk.staticdata.config;

import org.mlesyk.staticdata.AppCommandLineRunner;
import org.mlesyk.staticdata.repository.ItemGroupRepository;
import org.mlesyk.staticdata.repository.ItemRepository;
import org.mlesyk.staticdata.repository.SystemRepository;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
// import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;

// TODO: change after issue is resolved
// https://github.com/spring-projects/spring-framework/issues/33934
@TestConfiguration
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
public class BaseTestConfig {
    // @MockBean
    // private SystemRepository systemRepository;

    // @MockBean
    // private ItemGroupRepository itemGroupRepository;

    // @MockBean
    // private ItemRepository itemRepository;

    // @MockBean
    // private AppCommandLineRunner appCommandLineRunner;

    @Bean
    SystemRepository systemRepository() {
        return Mockito.mock(SystemRepository.class);
    }

    @Bean
    ItemGroupRepository itemGroupRepository() {
        return Mockito.mock(ItemGroupRepository.class);
    }

    @Bean
    ItemRepository itemRepository() {
        return Mockito.mock(ItemRepository.class);
    }

    @Bean
    AppCommandLineRunner appCommandLineRunner() {
        return Mockito.mock(AppCommandLineRunner.class);
    }

}
