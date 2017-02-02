package edu.hawaii.its.holiday.controller;


import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import edu.hawaii.its.holiday.configuration.SpringBootWebApplication;
import edu.hawaii.its.holiday.controller.GroupingsController;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

/**
 * Created by zac on 1/31/17.
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SpringBootWebApplication.class})
public class GroupingsControllerTest {

    @Autowired
    private GroupingsController groupingsController;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = webAppContextSetup(context).build();
    }

    @Test
    public void testConstruction(){
        assertNotNull(groupingsController);
    }


}
