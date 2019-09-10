package edu.hawaii.its.groupings.access;

import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class RoleHolder {

    private Set<GrantedAuthority> authorities = new LinkedHashSet<>();

    public void add(Role role) {
        authorities.add(new SimpleGrantedAuthority(role.longName()));
    }

    public Set<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public int size() {
        return authorities.size();
    }

    public boolean contains(Role role) {
        return authorities.contains(new SimpleGrantedAuthority(role.longName()));
    }

    @Override
    public String toString() {
        return "RoleHolder [authorities=" + authorities + "]";
    }
}
