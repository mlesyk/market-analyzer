package org.mlesyk.staticdata.controller;

import org.junit.jupiter.api.Test;
import org.mlesyk.staticdata.util.Healthcheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HealthcheckController.class)
class HealthcheckControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    Healthcheck healthcheck;

    @Test
    void whenServiceIsHealthy_thenReturnsHealthy() throws Exception {
        when(healthcheck.isServiceHealthy()).thenReturn(true);

        mockMvc.perform(get("/healthcheck"))
                .andExpect(status().isOk())
                .andExpect(content().string("Healthy"));
    }

    @Test
    void whenServiceIsNotHealthy_thenReturnsNotReady() throws Exception {
        when(healthcheck.isServiceHealthy()).thenReturn(false);

        mockMvc.perform(get("/healthcheck"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().string("Not ready"));
    }
}