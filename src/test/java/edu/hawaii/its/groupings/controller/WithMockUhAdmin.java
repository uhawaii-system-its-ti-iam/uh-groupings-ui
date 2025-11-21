package edu.hawaii.its.groupings.controller;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockAdminSecurityContextFactory.class)
public @interface WithMockUhAdmin {
    String uid() default "admin";

    String[] roles() default {"ROLE_UH", "ROLE_ADMIN"};

    String uhuuid() default "12345679";

    String name() default "Admin";
}
