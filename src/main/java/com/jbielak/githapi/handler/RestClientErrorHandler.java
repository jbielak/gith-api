package com.jbielak.githapi.handler;

import com.jbielak.githapi.exception.NotFoundException;
import com.jbielak.githapi.exception.ServerException;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

public class RestClientErrorHandler implements ResponseErrorHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RestClientErrorHandler.class);

    @Override
    public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
        return (clientHttpResponse.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR
                || clientHttpResponse.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR);
    }

    @Override
    public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
        LOG.error("RestClientErrorHandler | HTTP Status Code: {}", clientHttpResponse.getStatusCode().value());

        if (clientHttpResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
            throw new NotFoundException("404 - Not Found.");
        }

        throw new ServerException("An error has occurred. Please try again later.");
    }
}