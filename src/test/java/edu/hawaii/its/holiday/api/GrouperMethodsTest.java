package edu.hawaii.its.holiday.api;

import edu.hawaii.its.holiday.configuration.SpringBootWebApplication;
import edu.hawaii.its.holiday.controller.GroupingsController;
import edu.internet2.middleware.grouperClient.ws.beans.WsAddMemberResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsDeleteMemberResults;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by zac on 1/31/17.
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SpringBootWebApplication.class})
public class GrouperMethodsTest {


    @Test
    public void addSelfOptedTest(){
        assertTrue(true);
        //todo
    }

    @Test
    public void checkSelfOptedTest(){
        assertTrue(true);
        //todo
    }

    @Test
    public void removeSelfOptedTest(){
        assertTrue(true);
        //todo
    }

    @Test
    public void inGroupTest(){
        assertTrue(true);
        //todo
    }

    @Test
    public void groupOptInPermissionTest(){
        assertTrue(true);
        //todo
    }

    @Test
    public void groupOptOutPermissionTest(){
        assertTrue(true);
        //todo
    }

    @Test
    public void updateLastModifiedTest(){
        assertTrue(true);
        //todo
    }
}
