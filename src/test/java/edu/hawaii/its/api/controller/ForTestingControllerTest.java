package edu.hawaii.its.api.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import edu.hawaii.its.api.service.HttpRequestService;
import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;
import edu.hawaii.its.groupings.controller.WithMockUhUser;

@ActiveProfiles("localTest")
@SpringBootTest(classes = { SpringBootWebApplication.class })
public class ForTestingControllerTest {

    private static final String USERNAME = "user";

    private static final String REST_CONTROLLER_BASE = "/api/groupings/testing/";

    @Autowired
    GroupingsRestController groupingsRestController;

    @MockBean
    private HttpRequestService httpRequestService;

    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        when(httpRequestService.makeApiRequest(anyString(), anyString(), any(HttpMethod.class)))
                .thenReturn(new ResponseEntity(HttpStatus.BAD_REQUEST));
    }

    @Test
    @WithMockUhUser
    public void throwExceptionTest() throws Exception {
        String uri = REST_CONTROLLER_BASE + "exception";
        given(httpRequestService.makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.GET)))
                .willReturn(new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR));

        assertNotNull(mockMvc.perform(get(uri))
                .andExpect(status().isInternalServerError())
                .andReturn());

        verify(httpRequestService, times(1))
                .makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.GET));
    }
}
