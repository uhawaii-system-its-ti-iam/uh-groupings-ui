package edu.hawaii.its.groupings.access;

import java.util.LinkedHashMap;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class DelegatingAuthenticationFailureHandler
        extends org.springframework.security.web.authentication.DelegatingAuthenticationFailureHandler {

    public DelegatingAuthenticationFailureHandler(String appUrlBase) {
        super(new LinkedHashMap<>() {{
                  put(UsernameNotFoundException.class, new AuthenticationFailureHandler(appUrlBase + "/uhuuid-error"));
              }},
                new AuthenticationFailureHandler(appUrlBase + "/error"));
    }
}
