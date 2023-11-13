package edu.hawaii.its.groupings.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;
import edu.hawaii.its.groupings.type.Feedback;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpSession;

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
    @WithMockUhUser(username = "uh")
    public void memberships() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/memberships"))
                .andExpect(status().isOk())
                .andExpect(view().name("memberships"))
                .andReturn();
        assertNotNull(mvcResult);
    }

    @Test
    @WithMockUhUser(username = "admin", roles = { "ROLE_UH", "ROLE_ADMIN" })
    public void admin() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin"))
                .andReturn();
        assertNotNull(mvcResult);
    }

    @Test
    @WithMockUhUser(username = "uh")
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
    @WithMockUhUser(username = "admin", roles = { "ROLE_UH", "ROLE_ADMIN" })
    public void groupings() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/groupings"))
                .andExpect(status().isOk())
                .andExpect(view().name("groupings"))
                .andReturn();
        assertNotNull(mvcResult);
    }

    @Test
    @WithMockUhUser(username = "owner", roles = { "ROLE_UH", "ROLE_OWNER" })
    public void groupingsViaOwner() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/groupings"))
                .andExpect(status().isOk())
                .andExpect(view().name("groupings"))
                .andReturn();
        assertNotNull(mvcResult);
    }

    @Test
    @WithMockUhUser(username = "uh")
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

    @Test
    @WithMockUhUser(username = "uh")
    public void requestApiErrorModal() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/modal/apiError"))
                .andExpect(status().isOk())
                .andExpect(view().name("modal/apiError"))
                .andReturn();
        assertNotNull(mvcResult);
    }

    @Test
    @WithMockUhUser(username = "uh")
    public void requestPreferenceErrorModal() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/modal/preferenceErrorModal"))
                .andExpect(status().isOk())
                .andExpect(view().name("modal/preferenceErrorModal"))
                .andReturn();
        assertNotNull(mvcResult);
    }

    @Test
    @WithMockUhUser(username = "uh")
    public void requestAddModal() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/modal/addModal"))
                .andExpect(status().isOk())
                .andExpect(view().name("modal/addModal"))
                .andReturn();
        assertNotNull(mvcResult);
    }

    @Test
    @WithMockUhUser(username = "uh")
    public void requestMultiAddModal() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/modal/multiAddModal"))
                .andExpect(status().isOk())
                .andExpect(view().name("modal/multiAddModal"))
                .andReturn();
        assertNotNull(mvcResult);
    }

    @Test
    @WithMockUhUser(username = "uh")
    public void requestRemoveModal() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/modal/removeModal"))
                .andExpect(status().isOk())
                .andExpect(view().name("modal/removeModal"))
                .andReturn();
        assertNotNull(mvcResult);
    }

    @Test
    @WithMockUhUser(username = "uh")
    public void requestMultiRemoveModal() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/modal/multiRemoveModal"))
                .andExpect(status().isOk())
                .andExpect(view().name("modal/multiRemoveModal"))
                .andReturn();
        assertNotNull(mvcResult);
    }

    @Test
    @WithMockUhUser(username = "uh")
    public void requestResetModal() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/modal/resetModal"))
                .andExpect(status().isOk())
                .andExpect(view().name("modal/resetModal"))
                .andReturn();
        assertNotNull(mvcResult);
    }

    @Test
    @WithMockUhUser(username = "uh")
    public void requestSuccessfulGroupResetModal() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/modal/successfulGroupResetModal"))
                .andExpect(status().isOk())
                .andExpect(view().name("modal/successfulGroupResetModal"))
                .andReturn();
        assertNotNull(mvcResult);
    }

    @Test
    @WithMockUhUser(username = "uh")
    public void requestRemoveFromGroupsModal() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/modal/removeFromGroupsModal"))
                .andExpect(status().isOk())
                .andExpect(view().name("modal/removeFromGroupsModal"))
                .andReturn();
        assertNotNull(mvcResult);
    }

    @Test
    @WithMockUhUser(username = "uh")
    public void requestEmptyGroupModal() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/modal/emptyGroupModal"))
                .andExpect(status().isOk())
                .andExpect(view().name("modal/emptyGroupModal"))
                .andReturn();
        assertNotNull(mvcResult);
    }

    @Test
    @WithMockUhUser(username = "uh")
    public void requestSyncDestModal() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/modal/syncDestModal"))
                .andExpect(status().isOk())
                .andExpect(view().name("modal/syncDestModal"))
                .andReturn();
        assertNotNull(mvcResult);
    }

    @Test
    @WithMockUhUser(username = "uh")
    public void requestRemoveErrorModal() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/modal/removeErrorModal"))
                .andExpect(status().isOk())
                .andExpect(view().name("modal/removeErrorModal"))
                .andReturn();
        assertNotNull(mvcResult);
    }

    @Test
    @WithMockUhUser(username = "uh")
    public void requestTimeoutModal() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/modal/timeoutModal"))
                .andExpect(status().isOk())
                .andExpect(view().name("modal/timeoutModal"))
                .andReturn();
        assertNotNull(mvcResult);
    }

    @Test
    @WithMockUhUser(username = "uh")
    public void requestRoleErrorModal() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/modal/roleErrorModal"))
                .andExpect(status().isOk())
                .andExpect(view().name("modal/roleErrorModal"))
                .andReturn();
        assertNotNull(mvcResult);
    }

    @Test
    @WithMockUhUser(username = "uh")
    public void requestOwnerErrorModal() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/modal/ownerErrorModal"))
                .andExpect(status().isOk())
                .andExpect(view().name("modal/ownerErrorModal"))
                .andReturn();
        assertNotNull(mvcResult);
    }

    @Test
    @WithMockUhUser(username = "uh")
    public void requestOptErrorModal() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/modal/optErrorModal"))
                .andExpect(status().isOk())
                .andExpect(view().name("modal/optErrorModal"))
                .andReturn();
        assertNotNull(mvcResult);
    }

    @Test
    @WithMockUhUser(username = "uh")
    public void requestImportModal() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/modal/importModal"))
                .andExpect(status().isOk())
                .andExpect(view().name("modal/importModal"))
                .andReturn();
        assertNotNull(mvcResult);
    }

    @Test
    @WithMockUhUser(username = "uh")
    public void requestImportConfirmationModal() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/modal/importConfirmationModal"))
                .andExpect(status().isOk())
                .andExpect(view().name("modal/importConfirmationModal"))
                .andReturn();
        assertNotNull(mvcResult);
    }

    @Test
    @WithMockUhUser(username = "uh")
    public void requestImportSuccessModal() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/modal/importSuccessModal"))
                .andExpect(status().isOk())
                .andExpect(view().name("modal/importSuccessModal"))
                .andReturn();
        assertNotNull(mvcResult);
    }

    @Test
    @WithMockUhUser(username = "uh")
    public void requestImportErrorModal() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/modal/importErrorModal"))
                .andExpect(status().isOk())
                .andExpect(view().name("modal/importErrorModal"))
                .andReturn();
        assertNotNull(mvcResult);
    }

    @Test
    @WithMockUhUser(username = "uh")
    public void requestDynamicModal() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/modal/dynamicModal"))
                .andExpect(status().isOk())
                .andExpect(view().name("modal/dynamicModal"))
                .andReturn();
        assertNotNull(mvcResult);
    }
}
