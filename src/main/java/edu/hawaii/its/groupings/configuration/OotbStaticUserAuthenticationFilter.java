package edu.hawaii.its.groupings.configuration;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.GenericFilterBean;

import edu.hawaii.its.groupings.service.OotbActiveUserProfileService;

@Service
@Profile("ootb")
public class OotbStaticUserAuthenticationFilter extends GenericFilterBean {

    private final OotbActiveUserProfileService ootbActiveUserProfileService;

    private String currentUserProfile;

    public OotbStaticUserAuthenticationFilter(
            OotbActiveUserProfileService ootbActiveUserProfileService) {
        this.ootbActiveUserProfileService = ootbActiveUserProfileService;
    }

    public void setUserProfile(String userProfile) {
        this.currentUserProfile = userProfile;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = ootbActiveUserProfileService.loadUserByUsername(
                    currentUserProfile);
            if(userDetails != null) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }
}

