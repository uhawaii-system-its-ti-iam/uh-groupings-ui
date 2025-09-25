package edu.hawaii.its.groupings.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import edu.hawaii.its.groupings.access.UserContextService;
import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;
import edu.hawaii.its.groupings.service.JwtService;

@ActiveProfiles("localTest")
@SpringBootTest(classes = { SpringBootWebApplication.class })
public class DebugControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ApplicationContext applicationContext;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserContextService userContextService;

    @Value("${cas.login.url}")
    private String casLoginUrl;

    private static final String TEST_UID = "testUser";
    private static final String TEST_JWT = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0In0.test";

    @BeforeEach
    public void setUp() {
        mockMvc = webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        given(userContextService.getCurrentUid()).willReturn(TEST_UID);
        given(jwtService.generateToken()).willReturn(TEST_JWT);
    }

    @Test
    public void testGetJWTWithNoAuth() throws Exception {
        mockMvc.perform(get("/development/jwt"))
                .andExpect(status().is3xxRedirection())
                .andExpect(status().is(302))
                .andExpect(redirectedUrlPattern(casLoginUrl + "**"))
                .andExpect(content().string(equalTo("")))
                .andReturn();

        verify(userContextService, times(0)).getCurrentUser();
        verify(jwtService, times(0)).generateToken();
    }

    @Test
    @WithMockUhAdmin
    public void testGetJWTWithAuth() throws Exception {
        mockMvc.perform(get("/development/jwt"))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo(TEST_JWT)));

        verify(userContextService, times(1)).getCurrentUid();
        verify(jwtService, times(1)).generateToken();
    }

    @Test
    public void testDebugControllerIsLoadedInLocalhostProfile() {
        assertTrue(applicationContext.containsBean("debugController"),
                "DebugController should be loaded in localhost profile");
    }
}