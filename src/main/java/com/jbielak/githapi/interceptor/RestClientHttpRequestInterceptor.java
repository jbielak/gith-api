package com.jbielak.githapi.interceptor;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class RestClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(RestClientHttpRequestInterceptor.class);

    private static final String HEADER_VALUE_MASK = "****";

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] bytes,
                                        ClientHttpRequestExecution execution) throws IOException {
        LOG.info("URI: {}", request.getURI());
        LOG.info("HTTP Method: {}", request.getMethodValue());
        maskAuthorizationHeaderValue(request.getHeaders());
        LOG.info("HTTP Headers: {}", request.getHeaders());

        return execution.execute(request, bytes);
    }

    private void maskAuthorizationHeaderValue(HttpHeaders httpHeaders) {
        if (httpHeaders.containsKey(HttpHeaders.AUTHORIZATION)) {
            httpHeaders.set(HttpHeaders.AUTHORIZATION, HEADER_VALUE_MASK);
        }
    }
}
