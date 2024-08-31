package edu.hawaii.its.groupings.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import edu.hawaii.its.groupings.access.Role;
import edu.hawaii.its.groupings.access.User;
import edu.hawaii.its.groupings.type.OotbActiveProfile;
import edu.hawaii.its.groupings.util.JsonUtil;

@Service
@Profile("ootb")
public class OotbActiveUserProfileService implements UserDetailsService {

    @Value("${ootb.profiles.filename}")
    private String profilesFileName;

    // User is for SecurityContextHolder in AuthenticationFilter in UI project
    private final Map<String, User> users = new LinkedHashMap<>();

    // OotbActiveProfile is for request body to make api request
    private final Map<String, OotbActiveProfile> activeProfiles = new LinkedHashMap<>();

    public OotbActiveUserProfileService() {}

    public OotbActiveUserProfileService(String profilesFileName) {
        this.profilesFileName = profilesFileName;
    }
    @PostConstruct
    private void initUsers() {
        List<OotbActiveProfile> ootbActiveProfiles =
                JsonUtil.asList(JsonUtil.readJsonFileToString(profilesFileName), OotbActiveProfile.class);
        ootbActiveProfiles.forEach(profile -> {
            String key = profile.getAttributes().get("givenName");
            users.put(key, createUserFromProfile(profile));
            activeProfiles.put(key, profile);
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

    public Map<String, OotbActiveProfile> getActiveProfiles() {
        return activeProfiles;
    }
}
