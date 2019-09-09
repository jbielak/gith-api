package com.jbielak.githapi.handler;

import com.jbielak.githapi.exception.NotFoundException;
import com.jbielak.githapi.exception.ServerException;
import com.jbielak.githapi.repository.model.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@ExtendWith(SpringExtension.class)
@RestClientTest
public class RestClientErrorHandlerTest {

    @Autowired
    private RestTemplateBuilder builder;

    private MockRestServiceServer server;

    private RestTemplate restTemplate;

    private final String URI = "/repositories/owner/repo";

    @BeforeEach
    public void beforeEach() {
        restTemplate = this.builder
                .errorHandler(new RestClientErrorHandler())
                .build();

        server = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void givenRemoteApiCall_when404Error_thenThrowNotFound() {

        this.server
                .expect(ExpectedCount.once(), requestTo(URI))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThrows(NotFoundException.class, () ->
                restTemplate.getForObject(URI, Repository.class));

    }

    @Test
    public void givenRemoteApiCall_whenErrorDifferentThan404_thenThrowServerException() {
        this.server
                .expect(ExpectedCount.once(), requestTo(URI))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThrows(ServerException.class, () ->
                restTemplate.getForObject(URI, Repository.class));

    }
}
