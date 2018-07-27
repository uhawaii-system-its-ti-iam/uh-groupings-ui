package edu.hawaii.its.groupings.controller;

import org.junit.Before;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import org.junit.runner.RunWith;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.request.WebRequest;

import edu.hawaii.its.api.type.GroupingsHTTPException;
import edu.hawaii.its.api.type.GroupingsServiceResult;
import edu.hawaii.its.api.type.GroupingsServiceResultException;
import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;

@ActiveProfiles("localTest")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SpringBootWebApplication.class })
public class ErrorControllerAdviceTest {

    @Autowired
    private ErrorControllerAdvice errorControllerAdvice;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = webAppContextSetup(context).build();
    }
    @Test
    public void nullTest(){
        assertNotNull(errorControllerAdvice);
    }

    @Test
    public void RuntimeTest() {
        RuntimeException re = new RuntimeException();
        errorControllerAdvice.handleRuntimeException(re);

        String runtime = "<500,edu.hawaii.its.api.type.GroupingsHTTPException:"
                + " runtime exception,{}>";
        assertThat(errorControllerAdvice.handleRuntimeException(re).toString(), equalTo(runtime));
    }
    @Test
    public void IllegalArgumentTest() {
        IllegalArgumentException IAE = new IllegalArgumentException();
      //  WebRequest req = new WebRequest();

    }
    @Test
    public void UnsupportedOpTest() {
        UnsupportedOperationException UOE = new UnsupportedOperationException();
        errorControllerAdvice.handleUnsupportedOperationException(UOE);
        String UnOpE = "<501,edu.hawaii.its.api.type.GroupingsHTTPException: "
                + "Method not implemented,{}>";
        assertThat(errorControllerAdvice.handleUnsupportedOperationException(UOE).toString(), equalTo(UnOpE));

    }
    @Test
    public void ExceptionTest() {
        Exception E = new Exception();
        errorControllerAdvice.handleException(E);
        String exception = "<500,edu.hawaii.its.api.type.GroupingsHTTPException: "
                + "Exception,{}>";
        assertThat(errorControllerAdvice.handleException(E).toString(), equalTo(exception));
    }
 /*   @Test
    public void ExceptionHandleTest() throws GroupingsServiceResultException {
        GroupingsServiceResultException GSRE = new GroupingServiceResultException();
        errorControllerAdvice.handleGroupingsServiceResultException(GSRE);
        String SRE = "<400,edu.hawaii.its.api.type.GroupingsHTTPException: "
                + "Groupings Service resulted in FAILURE,{}>";
        assertThat(errorControllerAdvice.handleGroupingsServiceResultException(GSRE).toString(), equalTo(SRE));
    }*/
    @Test
    public void typeMismatchTest() {
        Exception E1 = new Exception();
        errorControllerAdvice.handleTypeMismatchException(E1);
        assertThat(errorControllerAdvice.handleTypeMismatchException(E1), equalTo("redirect:/error"));
    }
}