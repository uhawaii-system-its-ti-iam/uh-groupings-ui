package edu.hawaii.its.groupings.access;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.hawaii.its.api.controller.GroupingsRestController;
import edu.hawaii.its.api.type.AdminListsHolder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.cas.client.authentication.SimplePrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthorizationServiceImpl implements AuthorizationService {

    //
    // TODO: This needs to be implemented with real lookups.
    //

    @Value("#{'${app.user.roles}'.split(',')}")
    private List<String> users;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private Map<String, List<Role>> userMap = new HashMap<>();

    @Autowired
    GroupingsRestController groupingsRestController;

    private static final Log logger = LogFactory.getLog(AuthorizationServiceImpl.class);

    @PostConstruct
    public void init() {
        Assert.notNull(users, "property 'app.user.roles' is required.");

        for (String u : users) {
            String[] uhn = u.split("@");
            if (uhn.length > 0) {
                String id = uhn[0];
                List<Role> roles = new ArrayList<>();
                if (uhn.length > 1) {
                    for (String s : uhn[1].split("\\+")) {
                        roles.add(Role.valueOf(s));
                    }
                }
                userMap.put(id, roles);
            }
        }
    }

    /**
     * Assign roles to user
     */
    @Override
    public RoleHolder fetchRoles(String uhUuid, String username) {
        RoleHolder roleHolder = new RoleHolder();
        Principal principal = new SimplePrincipal(uhUuid);
        roleHolder.add(Role.ANONYMOUS);
        roleHolder.add(Role.UH);

        if (checkPriv(groupingsRestController.hasOwnerPrivs(principal))) {
            roleHolder.add(Role.OWNER);
        }
        if (checkPriv(groupingsRestController.hasAdminPrivs(principal))) {
            roleHolder.add(Role.ADMIN);
        }
        logger.info("fetchRoles: username: " + username + " " + roleHolder.getAuthorities() + ";");
        return roleHolder;
    }

    private boolean checkPriv(ResponseEntity response) {
        String groupingAssignmentJson = (String) response.getBody();

        System.out.println("-----------------------------------------------" + groupingAssignmentJson);

        if (null == groupingAssignmentJson) {
            return false;
        }
        return Boolean.parseBoolean(groupingAssignmentJson);
    }

}