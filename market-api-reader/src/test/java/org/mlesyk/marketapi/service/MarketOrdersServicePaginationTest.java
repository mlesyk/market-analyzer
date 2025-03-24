package org.mlesyk.marketapi.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mlesyk.marketapi.client.MarketRestClient;
import org.mlesyk.marketapi.model.MarketOrder;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@ExtendWith(SpringExtension.class)
class MarketOrdersServicePaginationTest {

    @Mock
    MarketRestClient marketRestClient;

    @InjectMocks
    private MarketOrdersService marketOrdersService;

    private static Stream<Arguments> pageDataProvider() {
        return Stream.of(
                Arguments.of(5, 10),
                Arguments.of(11, 20),
                Arguments.of(22, 30),
                Arguments.of(33, 40),
                Arguments.of(44, 50),
                Arguments.of(55, 60),
                Arguments.of(66, 70),
                Arguments.of(77, 80),
                Arguments.of(88, 90),
                Arguments.of(99, 100),
                Arguments.of(101, 110),
                Arguments.of(112, 120),
                Arguments.of(122, 130),
                Arguments.of(133, 140)
        );
    }

    @ParameterizedTest
    @MethodSource("pageDataProvider")
    void testPageExpected(Integer pageResponseOk, Integer expectedResponse) {
        assertThat(mockPageRequest(pageResponseOk)).isEqualTo(expectedResponse);
    }

    private Integer mockPageRequest(Integer pageExpected) {
        Mockito.when(marketRestClient.getRegionOrderInfoList(ArgumentMatchers.anyMap())).thenAnswer(new Answer<List<MarketOrder>>() {
            @Override
            public List<MarketOrder> answer(InvocationOnMock invocation) {
                Object[] objects = invocation.getArguments();
                System.out.println(objects[0]);
                int page = (Integer) ((Map<String, Object>) objects[0]).get("page");
                System.out.println("Page property = " + page);
                System.out.println("Page expected = " + pageExpected);
                if (page > pageExpected) {
                    throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
                } else {
                    return new ArrayList<MarketOrder>();
                }
            }
        });
        return marketOrdersService.findRegionOrderPagesAmount(10, 10, 11111);
    }

}
