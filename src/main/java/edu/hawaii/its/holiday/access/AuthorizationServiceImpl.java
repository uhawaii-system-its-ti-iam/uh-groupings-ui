package edu.hawaii.its.holiday.access;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class AuthorizationServiceImpl implements AuthorizationService {

    //
    // TODO: This needs to be implemented with real lookups.
    //

    @Value("#{'${app.user.roles}'.split(',')}")
    private List<String> users;

    private Map<String, List<Role>> userMap = new HashMap<>();

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

    public RoleHolder fetchRoles(String uhuuid) {
        RoleHolder roleHolder = new RoleHolder();
        roleHolder.add(Role.ANONYMOUS);
        roleHolder.add(Role.UH);
        List<Role> roles = userMap.get(uhuuid);
        if (roles != null) {
            for (Role role : roles) {
                roleHolder.add(role);
            }
        }

        return roleHolder;
    }
}
