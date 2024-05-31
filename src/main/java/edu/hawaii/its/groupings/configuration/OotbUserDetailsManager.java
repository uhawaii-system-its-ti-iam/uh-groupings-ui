package edu.hawaii.its.groupings.configuration;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import edu.hawaii.its.groupings.access.User;

public class OotbUserDetailsManager implements UserDetailsService {
    private final Map<String, User> users = new HashMap<>();

    public OotbUserDetailsManager() {
        initUsers();
    }

    private void initUsers() {
        users.put("USER", new User("user0123", "11111111",
                AuthorityUtils.createAuthorityList("ROLE_UH"),
                "DefaultUser", "user", "user@hawaii.edu"));
        users.put("OWNER", new User("owner0123", "22222222",
                AuthorityUtils.createAuthorityList("ROLE_UH", "ROLE_OWNER"),
                "OwnerUser", "owner", "owner@hawaii.edu"));
        users.put("ADMIN", new User("admin0123", "33333333",
                AuthorityUtils.createAuthorityList("ROLE_ADMIN", "ROLE_UH", "ROLE_OWNER"),
                "AdminUser", "admin", "admin@hawaii.edu"));
    }

    @Override
    public UserDetails loadUserByUsername(String userProfile) throws UsernameNotFoundException {
        if (!users.containsKey(userProfile)) {
            throw new UsernameNotFoundException("Should be one of USER, OWNER, or ADMIN");
        }
        return users.get(userProfile);
    }

    public Map<String, User> getUsers() {
        return users;
    }
}
