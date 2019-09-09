package com.jbielak.githapi.repository.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class Repository implements Serializable {

    private String fullName;

    private String description;

    private String cloneUrl;

    private int stars;

    private ZonedDateTime createdAt;

    @JsonProperty("fullName")
    public String getFullName() {
        return fullName;
    }

    @JsonProperty("full_name")
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("cloneUrl")
    public String getCloneUrl() {
        return cloneUrl;
    }

    @JsonProperty("clone_url")
    public void setCloneUrl(String cloneUrl) {
        this.cloneUrl = cloneUrl;
    }

    @JsonProperty("stars")
    public int getStars() {
        return stars;
    }

    @JsonProperty("stargazers_count")
    public void setStars(int stars) {
        this.stars = stars;
    }

    @JsonProperty("createdAt")
    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    @JsonProperty("created_at")
    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Repository{"
                + "fullName='" + fullName + '\''
                + ", description='" + description + '\''
                + ", cloneUrl='" + cloneUrl + '\''
                + ", stars=" + stars
                + ", createdAt='" + createdAt + '\''
                + '}';
    }
}
