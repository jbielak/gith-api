package com.jbielak.githapi.repository.controller;

import com.jbielak.githapi.TestUtils;
import com.jbielak.githapi.exception.NotFoundException;
import com.jbielak.githapi.exception.ServerException;
import com.jbielak.githapi.repository.client.RepositoryApiClient;
import com.jbielak.githapi.repository.model.Repository;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RepositoryController.class)
@ExtendWith(SpringExtension.class)
public class RepositoryControllerTest {

    private static final String URI = "/repositories/owner/repo";
    private static final String REPOSITORY_NAME = "repo";
    private static final String OWNER_NAME = "owner";
    private static final String AUTH_HEADER_VALUE = "auth";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RepositoryApiClient repositoryApiClient;

    private Repository repository1;

    @BeforeEach
    void setup() {
        repository1 = new Repository();
        repository1.setFullName("repo");
        repository1.setDescription("desc");
        repository1.setCloneUrl("http://test.com");
    }


    @Test
    void givenGetRepositoryRequest_whenRequestIsCorrect_shouldReturnRepository()
            throws Exception {
        when(repositoryApiClient.get(REPOSITORY_NAME, OWNER_NAME, AUTH_HEADER_VALUE))
                .thenReturn(repository1);

        MvcResult result = mockMvc.perform(get(URI)
                .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER_VALUE)
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        int status = result.getResponse().getStatus();
        Assertions.assertEquals(HttpStatus.OK.value(), status,
                "Incorrect Response Status");

        verify(repositoryApiClient).get(REPOSITORY_NAME, OWNER_NAME, AUTH_HEADER_VALUE);

        @SuppressWarnings("unchecked")
        Repository repositoryResult = TestUtils
                .jsonToObject(result.getResponse().getContentAsString(), Repository.class);

        Assertions.assertNotNull(repositoryResult, "Repository not found");
        Assertions.assertEquals(REPOSITORY_NAME, repositoryResult.getFullName());
    }

    @Test
    void givenGetRepositoryRequest_whenAuthHeaderIsNotProvided_shouldReturnBadRequest()
            throws Exception {
        when(repositoryApiClient.get(REPOSITORY_NAME, OWNER_NAME, AUTH_HEADER_VALUE))
                .thenReturn(repository1);

        MvcResult result = mockMvc.perform(get(URI)
                .contentType(ContentType.APPLICATION_JSON.getMimeType()))
                .andReturn();
        int status = result.getResponse().getStatus();
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), status,
                "Incorrect Response Status");

        verifyZeroInteractions(repositoryApiClient);
    }

    @Test
    void givenGetRepositoryRequest_whenExternalApiReturnsNotFound_shouldReturnNotFound()
            throws Exception {
        when(repositoryApiClient.get(REPOSITORY_NAME, OWNER_NAME, AUTH_HEADER_VALUE))
                .thenThrow(NotFoundException.class);

       mockMvc.perform(get(URI).contentType(ContentType.APPLICATION_JSON.getMimeType())
               .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER_VALUE))
               .andExpect(status().isNotFound());

        verify(repositoryApiClient).get(REPOSITORY_NAME, OWNER_NAME, AUTH_HEADER_VALUE);
    }

    @Test
    void givenGetRepositoryRequest_whenExternalApiReturnError_shouldReturnInternalServerError()
            throws Exception {
        when(repositoryApiClient.get(REPOSITORY_NAME, OWNER_NAME, AUTH_HEADER_VALUE))
                .thenThrow(ServerException.class);

        mockMvc.perform(get(URI).contentType(ContentType.APPLICATION_JSON.getMimeType())
                .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER_VALUE))
                .andExpect(status().isInternalServerError());

        verify(repositoryApiClient).get(REPOSITORY_NAME, OWNER_NAME, AUTH_HEADER_VALUE);
    }

}
