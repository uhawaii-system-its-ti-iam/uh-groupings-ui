package edu.hawaii.its.groupings.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import edu.hawaii.its.groupings.api.GroupingsService;
import edu.hawaii.its.groupings.api.type.Group;
import edu.hawaii.its.groupings.api.type.Grouping;
import edu.hawaii.its.groupings.api.type.Person;
import edu.hawaii.its.holiday.configuration.SpringBootWebApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SpringBootWebApplication.class })
public class GroupingsControllerTest {

    @Value("${app.iam.request.form}")
    private String requestForm;

    @MockBean
    private GroupingsService groupingsService;

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
    @WithMockUhUser
    public void getGrouping() throws Exception {
        final String grouping = "grouping";
        final String username = "username";

        given(groupingsService.getGrouping(grouping, username))
                .willReturn(grouping());

        mockMvc.perform(get("/grouping")
                .param("grouping", grouping)
                .param("username", username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value("bob"))
                .andExpect(jsonPath("path").value("test:ing:me:bob"))
                .andExpect(jsonPath("hasListserv").value("true"))
                .andExpect(jsonPath("basis.members", hasSize(3)))
                .andExpect(jsonPath("basis.members[0].name").value("b0-name"))
                .andExpect(jsonPath("basis.members[0].uuid").value("b0-uuid"))
                .andExpect(jsonPath("basis.members[0].username").value("b0-username"))
                .andExpect(jsonPath("basis.members[1].name").value("b1-name"))
                .andExpect(jsonPath("basis.members[1].uuid").value("b1-uuid"))
                .andExpect(jsonPath("basis.members[1].username").value("b1-username"))
                .andExpect(jsonPath("basis.members[2].name").value("b2-name"))
                .andExpect(jsonPath("basis.members[2].uuid").value("b2-uuid"))
                .andExpect(jsonPath("basis.members[2].username").value("b2-username"))
                .andExpect(jsonPath("exclude.members", hasSize(1)))
                .andExpect(jsonPath("exclude.members[0].name").value("e0-name"))
                .andExpect(jsonPath("exclude.members[0].name").value("e0-name"))
                .andExpect(jsonPath("exclude.members[0].uuid").value("e0-uuid"))
                .andExpect(jsonPath("include.members", hasSize(2)))
                .andExpect(jsonPath("include.members[1].name").value("i1-name"))
                .andExpect(jsonPath("include.members[1].name").value("i1-name"))
                .andExpect(jsonPath("include.members[1].uuid").value("i1-uuid"))
                .andExpect(jsonPath("owners.members", hasSize(4)))
                .andExpect(jsonPath("owners.members[3].name").value("o3-name"))
                .andExpect(jsonPath("owners.members[3].uuid").value("o3-uuid"))
                .andExpect(jsonPath("owners.members[3].username").value("o3-username"))
                .andExpect(jsonPath("basisPlusIncludeMinusExclude.members", hasSize(0)));
    }

    // Test data.
    private Grouping grouping() {
        Grouping grouping = new Grouping("test:ing:me:bob");

        Group basisGroup = new Group();
        basisGroup.addMember(new Person("b0-name", "b0-uuid", "b0-username"));
        basisGroup.addMember(new Person("b1-name", "b1-uuid", "b1-username"));
        basisGroup.addMember(new Person("b2-name", "b2-uuid", "b2-username"));
        grouping.setBasis(basisGroup);

        Group exclude = new Group();
        exclude.addMember(new Person("e0-name", "e0-uuid", "e0-username"));
        grouping.setExclude(exclude);

        Group include = new Group();
        include.addMember(new Person("i0-name", "i0-uuid", "i0-username"));
        include.addMember(new Person("i1-name", "i1-uuid", "i1-username"));
        grouping.setInclude(include);

        Group owners = new Group();
        owners.addMember(new Person("o0-name", "o0-uuid", "o0-username"));
        owners.addMember(new Person("o1-name", "o1-uuid", "o1-username"));
        owners.addMember(new Person("o2-name", "o2-uuid", "o2-username"));
        owners.addMember(new Person("o3-name", "o3-uuid", "o3-username"));
        grouping.setOwners(owners);

        grouping.setHasListserv(true);

        return grouping;
    }

    @Test
    @WithMockUhUser
    public void getCancelOptOut() throws Exception {
        final String grouping = "grouping";
        final String username = "username";

        given(groupingsService.cancelOptOut(grouping, username))
                .willReturn(new Object[] { "A", "B", "C", grouping, username });

        mockMvc.perform(get("/cancelOptOut")
                .param("grouping", grouping)
                .param("username", username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[0]").value("A"))
                .andExpect(jsonPath("$[1]").value("B"))
                .andExpect(jsonPath("$[2]").value("C"))
                .andExpect(jsonPath("$[3]").value(grouping))
                .andExpect(jsonPath("$[4]").value(username));
    }

    @Test
    @WithMockUhUser
    public void getAddGrouping() throws Exception {
        mockMvc.perform(get("/addGrouping"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(URI.create(requestForm).toString()));
    }

    @Test
    @WithMockUhUser
    public void getDeleteGrouping() throws Exception {
        mockMvc.perform(get("/deleteGrouping"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(URI.create(requestForm).toString()));
    }
}
