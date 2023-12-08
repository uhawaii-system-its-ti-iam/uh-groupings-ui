package edu.hawaii.its.groupings.access;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import org.apereo.cas.client.authentication.AttributePrincipal;
import org.apereo.cas.client.authentication.AttributePrincipalImpl;
import org.apereo.cas.client.validation.Assertion;

public class AssertionDummy implements Assertion {

    private static final long serialVersionUID = 1L;

    private String username;

    // Constructor.
    public AssertionDummy() {
        // Empty.
    }

    // Constructor.
    public AssertionDummy(String username) {
        this.username = username;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Date getAuthenticationDate() {
        return null;
    }

    @Override
    public Map<String, Serializable> getContext() {
        return null;
    }

    @Override
    public AttributePrincipal getPrincipal() {
        if (username != null) {
            return new AttributePrincipalImpl(username);
        }
        return null;
    }

    @Override
    public Date getValidFromDate() {
        return null;
    }

    @Override
    public Date getValidUntilDate() {
        return null;
    }

    @Override
    public boolean isValid() {
        return false;
    }

}