package com.jbielak.githapi.repository.client;

import com.jbielak.githapi.repository.model.Repository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RepositoryRestTemplateApiClient implements RepositoryApiClient {

    private final RestTemplate restTemplate;

    public RepositoryRestTemplateApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${api.github.repositories_uri}")
    private String repositoriesUri;

    @Override
    public Repository get(String name, String username, String auth) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, auth);
        HttpEntity entity = new HttpEntity(headers);

        ResponseEntity<Repository> responseEntity = restTemplate
                .exchange(repositoriesUri, HttpMethod.GET, entity, Repository.class, username, name);

        return responseEntity.getBody();
    }
}
