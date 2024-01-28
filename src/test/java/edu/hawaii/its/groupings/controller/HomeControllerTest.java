package edu.hawaii.its.groupings.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import jakarta.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;
import edu.hawaii.its.groupings.type.Feedback;

@ActiveProfiles("localTest")
@SpringBootTest(classes = { SpringBootWebApplication.class })
public class HomeControllerTest {

    @Value("${app.url.home}")
    private String appUrlHome;

    @Value("${cas.login.url}")
    private String casLoginUrl;

    @Autowired
    private HomeController homeController;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
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
        MvcResult mvcResult = mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andReturn();
        assertNotNull(mvcResult);

        mvcResult = mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andReturn();
        assertNotNull(mvcResult);
    }

    @Test
    public void requestLogin() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/login"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern(casLoginUrl + "**"))
                .andReturn();
        assertNotNull(mvcResult);
    }

    @Test
    @WithMockUhUser
    public void requestLoginViaUh() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andReturn();
        assertNotNull(mvcResult);
    }

    @Test
    @WithAnonymousUser
    public void loginViaAnonymous() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/login"))
                .andExpect(status().is3xxRedirection())
                .andExpect(status().is(302))
                .andExpect(redirectedUrlPattern(casLoginUrl + "**"))
                .andReturn();
        assertNotNull(mvcResult);
    }

    @Test
    @WithMockUhUser
    public void loginViaUh() throws Exception {
        // Logged in already, URL is home page.
        MvcResult mvcResult = mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andReturn();
        assertNotNull(mvcResult);
    }

    @Test
    @WithMockUhUser
    public void logoutViaUh() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/logout").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(status().is(302))
                .andReturn();
        assertThat(mvcResult.getResponse().getRedirectedUrl(), is(appUrlHome));
    }

    @Test
    public void requestAbout() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/about"))
                .andExpect(status().isOk())
                .andExpect(view().name("about"))
                .andReturn();
        assertNotNull(mvcResult);
    }

    @Test
    @WithMockUhUser(uid = "uh")
    public void memberships() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/memberships"))
                .andExpect(status().isOk())
                .andExpect(view().name("memberships"))
                .andReturn();
        assertNotNull(mvcResult);
    }

    @Test
    @WithMockUhUser(uid = "admin", roles = { "ROLE_UH", "ROLE_ADMIN" })
    public void admin() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin"))
                .andReturn();
        assertNotNull(mvcResult);
    }

    @Test
    @WithMockUhUser(uid = "uh")
    public void adminViaUh() throws Exception {
        // Not high enough role for access.
        MvcResult mvcResult = mockMvc.perform(get("/admin"))
                .andExpect(status().is4xxClientError())
                .andReturn();
        assertNotNull(mvcResult);
    }

    @Test
    @WithAnonymousUser
    public void adminViaAnonymous() throws Exception {
        // Anonymous users not allowed into admin area.
        MvcResult mvcResult = mockMvc.perform(get("/admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern(casLoginUrl + "**"))
                .andReturn();
        assertNotNull(mvcResult);
    }

    @Test
    @WithMockUhUser(uid = "admin", roles = { "ROLE_UH", "ROLE_ADMIN" })
    public void groupings() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/groupings"))
                .andExpect(status().isOk())
                .andExpect(view().name("groupings"))
                .andReturn();
        assertNotNull(mvcResult);
    }

    @Test
    @WithMockUhUser(uid = "owner", roles = { "ROLE_UH", "ROLE_OWNER" })
    public void groupingsViaOwner() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/groupings"))
                .andExpect(status().isOk())
                .andExpect(view().name("groupings"))
                .andReturn();
        assertNotNull(mvcResult);
    }

    @Test
    @WithMockUhUser(uid = "uh")
    public void groupingsViaUH() throws Exception {
        // Not high enough role for access

        ResultActions result = mockMvc.perform(get("/groupings"))
                .andExpect(status().is4xxClientError());
        assertNotNull(result);
    }

    @Test
    @WithAnonymousUser
    public void groupingsViaAnonymous() throws Exception {
        // Anonymous users not allowed into admin area.
        ResultActions result = mockMvc.perform(get("/groupings"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern(casLoginUrl + "**"));
        assertNotNull(result);
    }

    @Test
    @WithMockUhUser
    public void requestFeedbackWithoutException() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/feedback"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("feedback", hasProperty("email", is("user@hawaii.edu"))))
                .andExpect(model().attribute("feedback", hasProperty("exceptionMessage", nullValue())))
                .andExpect(model().attributeExists("feedback"))
                .andReturn();
        assertNotNull(mvcResult);
    }

    @Test
    @WithMockUhUser
    public void requestFeedbackWithException() throws Exception {
        HttpSession session = mockMvc.perform(get("/feedback")
                .sessionAttr("feedback", new Feedback("exception")))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("feedback"))
                .andExpect(model().attribute("feedback", hasProperty("exceptionMessage", is("exception"))))
                .andExpect(model().attribute("feedback", hasProperty("email", is("user@hawaii.edu"))))
                .andReturn()
                .getRequest()
                .getSession();

        assert session != null;
        assertThat(session.getAttribute("feedback"), notNullValue());
    }

    @Test
    @WithMockUhUser
    public void feedbackSubmit() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/feedback")
                .with(csrf())
                .flashAttr("feedback", new Feedback()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/feedback"))
                .andExpect(flash().attribute("success", is(true)))
                .andReturn();
        assertNotNull(mvcResult);
    }

    @Test
    public void requestUrl404() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/404"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"))
                .andReturn();
        assertNotNull(mvcResult);
    }

    @Test
    public void requestNonExistentUrl() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/not-a-url"))
                .andExpect(status().is3xxRedirection())
                .andReturn();
        assertNotNull(mvcResult);
    }

    @ParameterizedTest
    @ValueSource(strings = {"apiError", "preferenceErrorModal", "addModal", "multiAddModal", "removeModal", "multiRemoveModal", "resetModal",
            "successfulGroupResetModal", "removeFromGroupsModal", "emptyGroupModal", "syncDestModal", "removeErrorModal", "timeoutModal",
            "roleErrorModal", "ownerErrorModal", "optErrorModal", "importModal", "importConfirmationModal", "importSuccessModal", "importErrorModal", "dynamicModal"})
    @WithMockUhUser(uid = "uh")
    public void requestModal(String modalName) throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/modal/" + modalName))
                .andExpect(status().isOk())
                .andExpect(view().name("modal/" + modalName))
                .andReturn();
        assertNotNull(mvcResult);
    }
}
