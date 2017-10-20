package edu.hawaii.its.holiday.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import edu.hawaii.its.holiday.configuration.SpringBootWebApplication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SpringBootWebApplication.class})
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
    public void requestInfo() throws Exception {
        mockMvc.perform(get("/info"))
                .andExpect(status().isOk())
                .andExpect(view().name("info"));
    }

    @Test
    @WithMockUhUser(username = "uh")
    public void requestCampus() throws Exception {
        mockMvc.perform(get("/campus"))
                .andExpect(status().isOk())
                .andExpect(view().name("campus"));

        mockMvc.perform(get("/campuses"))
                .andExpect(status().isOk())
                .andExpect(view().name("campus"));
    }

    @Test
    @WithMockUhUser(username = "uh")
    public void memberships() throws Exception {
        mockMvc.perform(get("/memberships"))
                .andExpect(status().isOk())
                .andExpect(view().name("memberships"));
    }

    @Test
    @WithMockUhUser(username = "admin", roles = {"ROLE_UH", "ROLE_ADMIN"})
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
    @WithMockUhUser(username = "admin", roles = {"ROLE_UH", "ROLE_ADMIN"})
    public void groupings() throws Exception {
        mockMvc.perform(get("/groupings"))
                .andExpect(status().isOk())
                .andExpect(view().name("groupings"));
    }

    @Test
    @WithMockUhUser(username = "owner", roles = {"ROLE_UH", "ROLE_OWNER"})
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
}
