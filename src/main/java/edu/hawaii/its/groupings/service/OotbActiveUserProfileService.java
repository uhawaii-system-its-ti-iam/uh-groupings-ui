package edu.hawaii.its.groupings.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import edu.hawaii.its.groupings.access.Role;
import edu.hawaii.its.groupings.access.User;
import edu.hawaii.its.groupings.type.OotbActiveProfile;
import edu.hawaii.its.groupings.util.JsonUtil;

@Service
public class OotbActiveUserProfileService implements UserDetailsService {
    private static final Log logger = LogFactory.getLog(OotbActiveUserProfileService.class);
    private final Map<String, User> users = new LinkedHashMap<>();

    // Default JSON file for active profiles
    private String profilesFileName = "ootb.active.user.profiles.json";

    public OotbActiveUserProfileService() {
        initUsers();
    }

    public OotbActiveUserProfileService(String profilesFileName) {
        this.profilesFileName = profilesFileName;
        initUsers();
    }

    private void initUsers() {
        List<OotbActiveProfile> ootbActiveProfiles =
                JsonUtil.asList(JsonUtil.readJsonFileToString(profilesFileName), OotbActiveProfile.class);
        ootbActiveProfiles.forEach(profile -> {
            users.put(profile.getAttributes().get("givenName"), createUserFromProfile(profile));
        });
    }

    private User createUserFromProfile(OotbActiveProfile profile) {
        User.Builder builder = new User.Builder(profile.getUid())
                .uhUuid(profile.getUhUuid())
                .addAuthorities(profile.getAuthorities());

        profile.getAttributes().forEach(builder::addAttribute);

        return builder.build();
    }

    public String findGivenNameForAdminRole() {
        Optional<String> givenName = users.values().stream()
                .filter(user -> user.hasRole(Role.ADMIN))
                .map(User::getGivenName)
                .findFirst();

        return givenName.orElse(null);
    }

    @Override
    public UserDetails loadUserByUsername(String userProfile) throws UsernameNotFoundException {
        if (!users.containsKey(userProfile)) {
            throw new UsernameNotFoundException("Should be one of MEMBER, OWNER, or ADMIN");
        }
        return users.get(userProfile);
    }

    public List<String> getAvailableProfiles() {
        return new ArrayList<>(getUsers().keySet());
    }

    public Map<String, User> getUsers() {
        return users;
    }
}
