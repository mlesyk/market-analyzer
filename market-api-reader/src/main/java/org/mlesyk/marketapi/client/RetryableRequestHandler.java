package org.mlesyk.marketapi.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RetryableRequestHandler {

    public static final String ERROR_LIMIT_REMAIN_HEADER = "X-Esi-Error-Limit-Remain";
    public static final String ERROR_LIMIT_RESET_HEADER = "X-Esi-Error-Limit-Reset";

    private static volatile int errorsRemain = 100;
    private static volatile int errorResetSeconds = 60;

    private final RestTemplate restTemplate;

    public RetryableRequestHandler(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public <T> T getWithRetry(String url, Class<T> responseType, Map<String, ?> uriVariables) {
        final int retries = 10;
        final long retryDelayMs = 5000L;

        for (int attempt = 0; attempt < retries; attempt++) {
            try {
                if (errorsRemain < 10) {
                    checkForErrorLimit(url, uriVariables);
                }
                ResponseEntity<T> response = restTemplate.getForEntity(url, responseType, uriVariables);
                return response.getBody();
            } catch (HttpStatusCodeException e) {
                if (e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                    log.debug("404 Not Found for ESI client request, {}", uriVariables);
                    throw e;
                }
                log.warn("HTTP error during request: {}, {}", e.getMessage(), uriVariables);
            } catch (Exception e) {
                log.warn("Error during request: {}, {}", e.getMessage(), uriVariables);
            }
            sleepBeforeRetry(retryDelayMs);
        }
        return null;
    }

    private void sleepBeforeRetry(long delayMs) {
        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted", e);
        }
    }

    private synchronized void checkForErrorLimit(String url, Map<String, ?> uriVariables) {
        log.debug("Checking error limit. errorsRemain = {}", errorsRemain);
        if (errorsRemain > 10) return;

        log.warn("Error limit met. Waiting {} seconds", errorResetSeconds);
        try {
            TimeUnit.SECONDS.sleep(errorResetSeconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted while waiting", e);
        }

        HttpHeaders headers = restTemplate.headForHeaders(url, uriVariables);
        errorsRemain = Integer.parseInt(Objects.requireNonNull(headers.getFirst(ERROR_LIMIT_REMAIN_HEADER)));
        errorResetSeconds = Integer.parseInt(Objects.requireNonNull(headers.getFirst(ERROR_LIMIT_RESET_HEADER)));

        checkForErrorLimit(url, uriVariables);
    }

    @Component
    public static class RestTemplateResponseErrorHandler implements ResponseErrorHandler {

        @Override
        public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
            HttpStatusCode statusCode = httpResponse.getStatusCode();
            if (statusCode.is4xxClientError() || statusCode.is5xxServerError()) {
                HttpHeaders httpHeaders = httpResponse.getHeaders();
                errorsRemain = Integer.parseInt(Objects.requireNonNull(httpHeaders.getFirst(ERROR_LIMIT_REMAIN_HEADER)));
                errorResetSeconds = Integer.parseInt(Objects.requireNonNull(httpHeaders.getFirst(ERROR_LIMIT_RESET_HEADER)));
                log.debug("Received error, remaining count until reset: {}", errorsRemain);
                return true;
            }
            return false;
        }

        @Override
        public void handleError(URI url, HttpMethod method, ClientHttpResponse httpResponse) throws IOException {
            if (httpResponse.getStatusCode().is5xxServerError()) {
                throw new HttpServerErrorException(httpResponse.getStatusCode(),
                        httpResponse.getStatusText(),
                        httpResponse.getHeaders(),
                        httpResponse.getBody().readAllBytes(),
                        null);
            } else if (httpResponse.getStatusCode().is4xxClientError()) {
                throw new HttpClientErrorException(httpResponse.getStatusCode(),
                        httpResponse.getStatusText(),
                        httpResponse.getHeaders(),
                        httpResponse.getBody().readAllBytes(),
                        null);
            }
        }
    }
}
