package edu.hawaii.its.groupings.controller;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockUserSecurityContextFactory.class)
public @interface WithMockUhUser {
    String username() default "user";

    String[] roles() default {"ROLE_UH"};

    String uhUuid() default "12345678";

    String name() default "User";
}
