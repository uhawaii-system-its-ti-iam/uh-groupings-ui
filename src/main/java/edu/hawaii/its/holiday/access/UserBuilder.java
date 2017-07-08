package edu.hawaii.its.holiday.access;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import edu.hawaii.its.holiday.util.Strings;

@Service
public final class UserBuilder {

    private static final Log logger = LogFactory.getLog(UserBuilder.class);

    @Autowired
    private AuthorizationService authorizationService;    

    public final User make(Map<String, ?> map) {
        return make(new UhCasAttributes(map));
    }

    public final User make(UhAttributes attributes) {

        String uid = attributes.getUid();
        if (Strings.isEmpty(uid)) {
            // Should not happen, but just in case.
            throw new UsernameNotFoundException("uid is empty");
        }

        logger.debug("Adding roles start.");
        String uhuuid = attributes.getUhUuid();
        String username = attributes.getUid();
        RoleHolder roleHolder = authorizationService.fetchRoles(uhuuid,username);

        logger.info("Adding roles. uid: " + uid + "; roles: " + roleHolder.getAuthorites());
        User user = new User(uid, roleHolder.getAuthorites());
        logger.debug("Done adding roles; uid: " + uid);

        // Convert the uhuuid to a Long and record it.
        // Don't move this statement above the exists call
        // above because exists implicitly checks that the
        // Long data type conversion will work okay.
        user.setUhuuid(Long.valueOf(uhuuid));

        // Put all the attributes into the user
        // object just for the demonstration.
        // Above is what might commonly occur.
        user.setAttributes(attributes);

        return user;
    }

}
