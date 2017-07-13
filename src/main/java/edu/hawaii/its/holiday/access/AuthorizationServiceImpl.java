package edu.hawaii.its.holiday.access;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import edu.hawaii.its.api.controller.*;
import edu.hawaii.its.api.service.*;
import edu.hawaii.its.api.type.*;

@Service
public class AuthorizationServiceImpl implements AuthorizationService {

    //
    // TODO: This needs to be implemented with real lookups.
    //

    @Value("#{'${app.user.roles}'.split(',')}")
    private List<String> users;

    private Map<String, List<Role>> userMap = new HashMap<>();

    @Autowired
    private GroupingsService gs;

    @Autowired
    private GroupingsRestController gc;

    private static final Log logger = LogFactory.getLog(UserBuilder.class);

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
     * Assigns roles to user
     *
     * @param uhuuid    : The UH uuid of the user.
     * @param username  : The username of the person to find the user.
     * @return          : Returns an array list of roles assigned to the user.
     */
    public RoleHolder fetchRoles(String uhuuid, String username) {
        RoleHolder roleHolder = new RoleHolder();
        roleHolder.add(Role.ANONYMOUS);
        roleHolder.add(Role.UH);
        if(fetchOwner(username)) {
            roleHolder.add(Role.OWNER);
        }

        List<Role> roles = userMap.get(uhuuid);
        if (roles != null) {
            for (Role role : roles) {
                roleHolder.add(role);
            }
        }
        return roleHolder;
    }

    /**
     * Determines if a user is a owner of any grouping.
     *
     * @param username the username of the user.
     * @return true if the person has groupings that they own, otherwise false.
     */
    public boolean fetchOwner(String username) {
        try {
            MyGroupings result = gc.myGroupings(username).getBody();
            System.out.println("//////////////////////////////");
            if (!result.getGroupingsOwned().isEmpty()) {
                System.out.println("This person is an owner");
                return true;
            } else {
                System.out.println("This person is not owner");
            }
            System.out.println("//////////////////////////////");
        } catch (Exception e) {
            logger.info("The grouping for this person is " + e.getMessage());
        }
        return false;
    }
}