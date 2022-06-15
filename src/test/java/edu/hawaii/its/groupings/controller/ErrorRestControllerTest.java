package edu.hawaii.its.groupings.controller;

import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;
import edu.hawaii.its.groupings.type.Feedback;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpSession;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SpringBootWebApplication.class })
public class ErrorRestControllerTest {

    @Autowired
    private ErrorRestController restController;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
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

        assertNotNull(session.getAttribute("feedback"));
        Feedback feedback = (Feedback) session.getAttribute("feedback");
        assertThat(feedback.getExceptionMessage(), equalTo("exception"));
    }

}
