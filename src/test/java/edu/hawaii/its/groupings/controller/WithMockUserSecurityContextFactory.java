package edu.hawaii.its.groupings.controller;

import edu.hawaii.its.groupings.access.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.LinkedHashSet;
import java.util.Set;

public class WithMockUserSecurityContextFactory
        implements WithSecurityContextFactory<WithMockUhUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockUhUser uhUser) {

        Set<GrantedAuthority> authorities = new LinkedHashSet<>();
        for (String role : uhUser.roles()) {
            authorities.add(new SimpleGrantedAuthority(role));
        }

        User user = new User(uhUser.username(), authorities);
        user.setUhUuid(uhUser.uhUuid());

        final Authentication auth =
                new UsernamePasswordAuthenticationToken(user, "pw", user.getAuthorities());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);

        return context;
    }

}