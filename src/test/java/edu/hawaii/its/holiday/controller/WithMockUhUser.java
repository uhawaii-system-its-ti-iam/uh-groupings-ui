package edu.hawaii.its.holiday.controller;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockUserSecurityContextFactory.class)
public @interface WithMockUhUser {
    String username() default "user";

    String[] roles() default {"ROLE_UH"};

    long uhuuid() default 12345678L;

    String name() default "User";
}
