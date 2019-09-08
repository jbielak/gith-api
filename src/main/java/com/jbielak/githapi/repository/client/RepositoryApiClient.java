package com.jbielak.githapi.repository.client;

import com.jbielak.githapi.repository.model.Repository;

public interface RepositoryApiClient {

    Repository get(String name, String username, String auth);
}
