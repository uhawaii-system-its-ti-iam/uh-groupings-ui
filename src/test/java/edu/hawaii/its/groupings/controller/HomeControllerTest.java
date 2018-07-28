package edu.hawaii.its.groupings.controller;


import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.hamcrest.Matchers.hasProperty;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import edu.hawaii.its.groupings.type.Feedback;
import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;
import javax.servlet.http.HttpSession;

@ActiveProfiles("localTest")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SpringBootWebApplication.class })
public class HomeControllerTest {

    @Value("${cas.login.url}")
    private String casLoginUrl;

    @Autowired
    private HomeController homeController;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void testConstruction() {
        assertNotNull(homeController);
    }

    @Test
    public void requestHome() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));

        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));
    }

    @Test
    public void requestLogin() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern(casLoginUrl + "**"));
    }

    @Test
    @WithMockUhUser
    public void requestLoginViaUh() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:home"));
    }

    @Test
    public void requestInfo() throws Exception {
        mockMvc.perform(get("/info"))
                .andExpect(status().isOk())
                .andExpect(view().name("info"));
    }

    @Test
    @WithMockUhUser(username = "uh")
    public void memberships() throws Exception {
        mockMvc.perform(get("/memberships"))
                .andExpect(status().isOk())
                .andExpect(view().name("memberships"));
    }

    @Test
    @WithMockUhUser(username = "admin", roles = { "ROLE_UH", "ROLE_ADMIN" })
    public void admin() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin"));
    }

    @Test
    @WithMockUhUser(username = "uh")
    public void adminViaUh() throws Exception {
        // Not high enough role for access.
        mockMvc.perform(get("/admin"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithAnonymousUser
    public void adminViaAnonymous() throws Exception {
        // Anonymous users not allowed into admin area.
        mockMvc.perform(get("/admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern(casLoginUrl + "**"));
    }

    @Test
    @WithMockUhUser(username = "admin", roles = { "ROLE_UH", "ROLE_ADMIN" })
    public void groupings() throws Exception {
        mockMvc.perform(get("/groupings"))
                .andExpect(status().isOk())
                .andExpect(view().name("groupings"));
    }

    @Test
    @WithMockUhUser(username = "owner", roles = { "ROLE_UH", "ROLE_OWNER" })
    public void groupingsViaOwner() throws Exception {
        mockMvc.perform(get("/groupings"))
                .andExpect(status().isOk())
                .andExpect(view().name("groupings"));
    }

    @Test
    @WithMockUhUser(username = "uh")
    public void groupingsViaUH() throws Exception {
        // Not high enough role for access
        mockMvc.perform(get("/groupings"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithAnonymousUser
    public void groupingsViaAnonymous() throws Exception {
        // Anonymous users not allowed into admin area.
        mockMvc.perform(get("/groupings"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern(casLoginUrl + "**"));
    }

    @Test
    @WithMockUhUser
    public void requestFeedbackWithoutException() throws Exception {
        mockMvc.perform(get("/feedback"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("feedback", hasProperty("email", equalTo("user@hawaii.edu"))))
                .andExpect(model().attribute("feedback", hasProperty("exceptionMessage", nullValue())))
                .andExpect(model().attributeExists("feedback"));
    }

    @Test
    @WithMockUhUser
    public void requestFeedbackWithException() throws Exception {
        HttpSession session = mockMvc.perform(get("/feedback")
                .sessionAttr("feedback", new Feedback("exception")))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("feedback"))
                .andExpect(model().attribute("feedback", hasProperty("exceptionMessage", equalTo("exception"))))
                .andExpect(model().attribute("feedback", hasProperty("email", equalTo("user@hawaii.edu"))))
                .andReturn()
                .getRequest()
                .getSession();
        assertNull(session.getAttribute("feedback"));
    }

    @Test
    @WithMockUhUser
    public void feedbackSubmit() throws Exception {
        mockMvc.perform(post("/feedback")
                .with(csrf())
                .flashAttr("feedback", new Feedback()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/feedback"))
                .andExpect(flash().attribute("success", equalTo(true)));
    }

    @Test
    public void requestUrl404() throws Exception {
        mockMvc.perform(get("/404"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));
    }

    @Test
    public void requestNonExistentUrl() throws Exception {
        mockMvc.perform(get("/not-a-url"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUhUser(username = "uh")
    public void requestInfoModal() throws Exception {
        mockMvc.perform(get("/modal/infoModal"))
                .andExpect(status().isOk())
                .andExpect(view().name("modal/infoModal"));
    }

    @Test
    @WithMockUhUser(username = "uh")
    public void requestApiErrorModal() throws Exception {
        mockMvc.perform(get("/modal/apiError"))
                .andExpect(status().isOk())
                .andExpect(view().name("modal/apiError"));
    }

    @Test
    @WithMockUhUser(username = "uh")
    public void requestPreferenceErrorModal() throws Exception {
        mockMvc.perform(get("/modal/preferenceErrorModal"))
                .andExpect(status().isOk())
                .andExpect(view().name("modal/preferenceErrorModal"));
    }

    @Test
    @WithMockUhUser(username = "uh")
    public void requestAddModal() throws Exception {
        mockMvc.perform(get("/modal/addModal"))
                .andExpect(status().isOk())
                .andExpect(view().name("modal/addModal"));
    }

    @Test
    @WithMockUhUser(username = "uh")
    public void requestRemoveModal() throws Exception {
        mockMvc.perform(get("/modal/removeModal"))
                .andExpect(status().isOk())
                .andExpect(view().name("modal/removeModal"));
    }

    @Test
    @WithMockUhUser(username = "uh")
    public void requestOptModal() throws Exception {
        mockMvc.perform(get("/modal/optModal"))
                .andExpect(status().isOk())
                .andExpect(view().name("modal/optModal"));
    }

    @Test
    @WithMockUhUser(username = "uh")
    public void requestCheckModal() throws Exception {
        mockMvc.perform(get("/modal/checkModal"))
                .andExpect(status().isOk())
                .andExpect(view().name("modal/checkModal"));
    }

    @Test
    @WithMockUhUser(username = "uh")
    public void requestConfirmAddModal() throws Exception {
        mockMvc.perform(get("/modal/confirmAddModal"))
                .andExpect(status().isOk())
                .andExpect(view().name("modal/confirmAddModal"));
    }

}
