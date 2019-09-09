package com.jbielak.githapi.repository.controller;

import com.jbielak.githapi.exception.NotFoundException;
import com.jbielak.githapi.exception.ServerException;
import com.jbielak.githapi.repository.client.RepositoryApiClient;
import com.jbielak.githapi.repository.model.Repository;
import javax.validation.constraints.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/repositories")
public class RepositoryController {

    private final RepositoryApiClient repositoryApiClient;

    public RepositoryController(RepositoryApiClient repositoryApiClient) {
        this.repositoryApiClient = repositoryApiClient;
    }

    @GetMapping("/{owner}/{fullName}")
    public ResponseEntity<Repository> getRepository(@PathVariable String owner,
                                                   @PathVariable String fullName,
                                                   @RequestHeader("Authorization") @NotNull String auth) {

        Repository repository;
        try {
            repository = repositoryApiClient.get(fullName, owner, auth);
        } catch (NotFoundException ex) {
            return ResponseEntity.notFound().build();
        } catch (ServerException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok(repository);
    }
}
