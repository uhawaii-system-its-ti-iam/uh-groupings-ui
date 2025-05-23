package edu.hawaii.its.groupings.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import jakarta.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;
import edu.hawaii.its.groupings.type.Feedback;

@ActiveProfiles("localTest")
@SpringBootTest(classes = { SpringBootWebApplication.class })
public class ErrorRestControllerTest {

    @Autowired
    private ErrorRestController restController;
    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = webAppContextSetup(context).build();
    }
    @Test
    public void testConstruction() {
        assertNotNull(restController);
    }
    @Test
    public void httpPostFeedbackError() throws Exception {
        HttpSession session = mockMvc.perform(post("/feedback/error")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"exceptionMessage\":\"exception\"}"))
                .andExpect(status().isNoContent())
                .andReturn()
                .getRequest()
                .getSession();
        assert session != null;
        assertNotNull(session.getAttribute("feedback"));
        Feedback feedback = (Feedback) session.getAttribute("feedback");
        assertThat(feedback.getExceptionMessage(), is("exception"));
    }
}