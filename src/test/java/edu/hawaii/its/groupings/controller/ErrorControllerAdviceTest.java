package edu.hawaii.its.groupings.controller;

import org.junit.runner.RunWith;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.request.WebRequest;
import org.springframework.beans.factory.annotation.Autowired;

import edu.hawaii.its.api.type.GroupingsServiceResultException;
import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;
import edu.internet2.middleware.grouperClient.ws.GcWebServiceError;

import java.security.Principal;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;


@ActiveProfiles("localTest")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SpringBootWebApplication.class })
public class ErrorControllerAdviceTest {

    @Autowired
    private ErrorControllerAdvice errorControllerAdvice;

    @Test
    public void nullTest(){
        assertNotNull(errorControllerAdvice);
    }

    @Test
    public void gcWebServiceTest()throws Exception {
        Object Ro = new Object();
        GcWebServiceError Gc = new GcWebServiceError(Ro);
        errorControllerAdvice.handleGcWebServiceError(Gc);
           String gce = "<404,edu.hawaii.its.api.type.GroupingsHTTPException,[]>";
        assertThat(errorControllerAdvice.handleGcWebServiceError(Gc).toString(),equalTo(gce));
    }

    @Test
    public void runtimeTest() {
        RuntimeException re = new RuntimeException();
        errorControllerAdvice.handleRuntimeException(re);
        String runtime = "<500,edu.hawaii.its.api.type.GroupingsHTTPException:"
                + " runtime exception,[]>";
        assertThat(errorControllerAdvice.handleRuntimeException(re).toString(), equalTo(runtime));
    }
    @Test
    public void illegalArgumentTest() {
        IllegalArgumentException IAE = new IllegalArgumentException();
        WebRequest Req = new WebRequest() {
            @Override public String getHeader(String s) {
                return null;
            }

            @Override public String[] getHeaderValues(String s) {
                return new String[0];
            }

            @Override public Iterator<String> getHeaderNames() {
                return null;
            }

            @Override public String getParameter(String s) {
                return null;
            }

            @Override public String[] getParameterValues(String s) {
                return new String[0];
            }

            @Override public Iterator<String> getParameterNames() {
                return null;
            }

            @Override public Map<String, String[]> getParameterMap() {
                return null;
            }

            @Override public Locale getLocale() {
                return null;
            }

            @Override public String getContextPath() {
                return null;
            }

            @Override public String getRemoteUser() {
                return null;
            }

            @Override public Principal getUserPrincipal() {
                return null;
            }

            @Override public boolean isUserInRole(String s) {
                return false;
            }

            @Override public boolean isSecure() {
                return false;
            }

            @Override public boolean checkNotModified(long l) {
                return false;
            }

            @Override public boolean checkNotModified(String s) {
                return false;
            }

            @Override public boolean checkNotModified(String s, long l) {
                return false;
            }

            @Override public String getDescription(boolean b) {
                return null;
            }

            @Override public Object getAttribute(String s, int i) {
                return null;
            }

            @Override public void setAttribute(String s, Object o, int i) {
            //intentionally empty
            }

            @Override public void removeAttribute(String s, int i) {
            //intentionally empty
            }

            @Override public String[] getAttributeNames(int i) {
                return new String[0];
            }

            @Override public void registerDestructionCallback(String s, Runnable runnable, int i) {
                //intentionally empty
            }

            @Override public Object resolveReference(String s) {
                return null;
            }

            @Override public String getSessionId() {
                return null;
            }

            @Override public Object getSessionMutex() {
                return null;
            }
        };
        errorControllerAdvice.handleIllegalArgumentException(IAE,Req);
        String IAException = "<404,edu.hawaii.its.api.type.GroupingsHTTPException: "
                + "Resource not available,[]>";
        assertThat(errorControllerAdvice.handleIllegalArgumentException(IAE,Req).toString(), equalTo(IAException));

    }
    @Test
    public void unsupportedOpTest() {
        UnsupportedOperationException UOE = new UnsupportedOperationException();
        errorControllerAdvice.handleUnsupportedOperationException(UOE);
        String UnOpE = "<501,edu.hawaii.its.api.type.GroupingsHTTPException: "
                + "Method not implemented,[]>";
        assertThat(errorControllerAdvice.handleUnsupportedOperationException(UOE).toString(), equalTo(UnOpE));

    }
    @Test
    public void exceptionTest() {
        Exception E = new Exception();
        errorControllerAdvice.handleException(E);
        String exception = "<500,edu.hawaii.its.api.type.GroupingsHTTPException: "
                + "Exception,[]>";
        assertThat(errorControllerAdvice.handleException(E).toString(), equalTo(exception));
    }
    @Test
    public void exceptionHandleTest() throws GroupingsServiceResultException {
        GroupingsServiceResultException GSRE = new GroupingsServiceResultException();
        errorControllerAdvice.handleGroupingsServiceResultException(GSRE);
        String SRE = "<400,edu.hawaii.its.api.type.GroupingsHTTPException: "
                + "Groupings Service resulted in FAILURE,[]>";
        assertThat(errorControllerAdvice.handleGroupingsServiceResultException(GSRE).toString(), equalTo(SRE));
    }
    @Test
    public void typeMismatchTest() {
        Exception E1 = new Exception();
        errorControllerAdvice.handleTypeMismatchException(E1);
        assertThat(errorControllerAdvice.handleTypeMismatchException(E1), equalTo("redirect:/error"));
    }
}
