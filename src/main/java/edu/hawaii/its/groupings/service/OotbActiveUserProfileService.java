package edu.hawaii.its.groupings.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import edu.hawaii.its.groupings.access.User;

@Service
public class OotbActiveUserProfileService implements UserDetailsService {
    private final Map<String, User> users = new HashMap<>();

    public OotbActiveUserProfileService() {
        initUsers();
    }

    private void initUsers() {
        // Initialize member with uid "member0123"
        users.put("MEMBER", new User.Builder("member0123")  // UID is clearly the first parameter in the constructor
                .uhUuid("11111111")
                .authorities(AuthorityUtils.createAuthorityList("ROLE_UH", "ROLE_OOTB"))
                .addAttribute("cn", "MEMBER")
                .addAttribute("mail", "member@hawaii.edu")
                .addAttribute("givenName", "DefaultMember")
                .build());

        // Initialize owner with uid "owner0123"
        users.put("OWNER", new User.Builder("owner0123")
                .uhUuid("22222222")
                .authorities(AuthorityUtils.createAuthorityList("ROLE_UH", "ROLE_OWNER", "ROLE_OOTB"))
                .addAttribute("cn", "OWNER")
                .addAttribute("mail", "owner@hawaii.edu")
                .addAttribute("givenName", "OwnerUser")
                .build());

        // Initialize admin with uid "admin0123"
        users.put("ADMIN", new User.Builder("admin0123")
                .uhUuid("33333333")
                .authorities(AuthorityUtils.createAuthorityList("ROLE_ADMIN", "ROLE_UH", "ROLE_OWNER", "ROLE_OOTB"))
                .addAttribute("cn", "ADMIN")
                .addAttribute("mail", "admin@hawaii.edu")
                .addAttribute("givenName", "AdminUser")
                .build());
    }

    @Override
    public UserDetails loadUserByUsername(String userProfile) throws UsernameNotFoundException {
        if (!users.containsKey(userProfile)) {
            throw new UsernameNotFoundException("Should be one of MEMBER, OWNER, or ADMIN");
        }
        return users.get(userProfile);
    }

    public Map<String, User> getUsers() {
        return users;
    }
}
