package edu.hawaii.its.groupings.access;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import edu.hawaii.its.groupings.util.Strings;

@Service
public final class UserBuilder {

    private static final Log logger = LogFactory.getLog(UserBuilder.class);

    private final AuthorizationService authorizationService;

    public UserBuilder(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    public User make(Map<String, ?> map) {
        return make(new UhAttributes(map));
    }

    public User make(UhAttributes attributes) {

        String uid = attributes.getUid();
        if (Strings.isEmpty(uid)) {
            // Should not happen, but just in case.
            throw new UsernameNotFoundException("uid is empty");
        }

        String uhUuid = attributes.getUhUuid();
        if (Strings.isEmpty(uhUuid)) {
            // When logging into a department account without an uhUuid.
            throw new UsernameNotFoundException(uid);
        }

        logger.debug("Adding roles start.");
        RoleHolder roleHolder = authorizationService.fetchRoles(uhUuid, uid);

        logger.info("Adding roles. uid: " + uid + "; roles: " + roleHolder.getAuthorities());
        User user = new User(uid, roleHolder.getAuthorities());
        logger.debug("Done adding roles; uid: " + uid);

        // Convert the uhUuid to a Long and record it.
        // Don't move this statement above the exists call
        // above because exists implicitly checks that the
        // Long data type conversion will work okay.
        user.setUhUuid(uhUuid);

        // Put all the attributes into the user
        // object just for the demonstration.
        // Above is what might commonly occur.
        user.setAttributes(attributes);

        if (!roleHolder.contains(Role.UH)) {
            throw new UsernameNotFoundException(uid);
        }

        return user;
    }

}
