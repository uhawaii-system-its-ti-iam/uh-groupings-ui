package edu.hawaii.its.holiday.controller;

import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import edu.hawaii.its.holiday.access.User;

public class WithMockUserSecurityContextFactory
        implements WithSecurityContextFactory<WithMockUhUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockUhUser uhUser) {

        Set<GrantedAuthority> authorities = new LinkedHashSet<GrantedAuthority>();
        for (String role : uhUser.roles()) {
            authorities.add(new SimpleGrantedAuthority(role));
        }

        User user = new User(uhUser.username(), authorities);
        user.setUhuuid(uhUser.uhuuid());

        final Authentication auth =
                new UsernamePasswordAuthenticationToken(user, "pw", user.getAuthorities());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);

        return context;
    }

}