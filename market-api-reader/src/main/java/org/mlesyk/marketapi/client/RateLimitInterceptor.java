package org.mlesyk.marketapi.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

@Slf4j
public class RateLimitInterceptor implements ClientHttpRequestInterceptor {

    public static final int MAX_RETRIES = 5;
    public static final long DEFAULT_BACKOFF_SECONDS = 60;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        ClientHttpResponse response = execution.execute(request, body);
        int attempts = 0;
        while (response.getStatusCode().equals(HttpStatus.TOO_MANY_REQUESTS) && attempts < MAX_RETRIES) {
            long sleepSeconds = parseRetryAfter(response.getHeaders());
            log.warn("ESI rate limit hit for {}, sleeping {}s before retry (attempt {}/{})",
                    request.getURI(), sleepSeconds, attempts + 1, MAX_RETRIES);
            response.close();
            try {
                Thread.sleep(sleepSeconds * 1000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("Interrupted while waiting for ESI rate limit reset", e);
            }
            response = execution.execute(request, body);
            attempts++;
        }
        return response;
    }

    private long parseRetryAfter(HttpHeaders headers) {
        String value = headers.getFirst(HttpHeaders.RETRY_AFTER);
        if (value == null) {
            return DEFAULT_BACKOFF_SECONDS;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            return DEFAULT_BACKOFF_SECONDS;
        }
    }
}
