package edu.hawaii.its.groupings.configuration;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

public class OotbStaticUserAuthenticationFilter extends GenericFilterBean {
    private final String serverType;
    private final String name;
    private final String username;
    private final String cn;
    private final String email;
    private final List<String> authorities;

    public OotbStaticUserAuthenticationFilter(String serverType, String name, String username, String cn, String email,
            List<String> authorities) {
        this.serverType = serverType;
        this.name = name;
        this.username = username;
        this.authorities = authorities;
        this.cn = cn;
        this.email = email;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        if (serverType.equals("OOTB") && SecurityContextHolder.getContext().getAuthentication() == null) {
            OotbCustomUserDetails ootbCustomUserDetails = new OotbCustomUserDetails(
                    username, "password",
                    AuthorityUtils.createAuthorityList(authorities),
                    name, cn, email);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    ootbCustomUserDetails, null, ootbCustomUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }
}
