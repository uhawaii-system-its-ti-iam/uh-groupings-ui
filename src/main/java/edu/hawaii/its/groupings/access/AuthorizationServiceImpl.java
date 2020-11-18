package edu.hawaii.its.groupings.access;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.cas.client.authentication.SimplePrincipal;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import edu.hawaii.its.api.controller.GroupingsRestController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class AuthorizationServiceImpl implements AuthorizationService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    GroupingsRestController groupingsRestController;

    private static final Log logger = LogFactory.getLog(AuthorizationServiceImpl.class);

    /**
     * Assign roles to user
     */
    @Override
    public RoleHolder fetchRoles(String uhUuid, String username) {
        RoleHolder roleHolder = new RoleHolder();
        Principal principal = new SimplePrincipal(username);
        roleHolder.add(Role.ANONYMOUS);
        roleHolder.add(Role.UH);

        //Determine if user is an owner.
        if (checkResultCodeJsonObject(groupingsRestController.isOwner(principal)))
            roleHolder.add(Role.OWNER);

        //Determine if a user is an admin.
        if (checkResultCodeJsonObject(groupingsRestController.isAdmin(principal)))
            roleHolder.add(Role.ADMIN);

        System.out.println("------------------------------------------------------");
        System.out.println(roleHolder.getAuthorities());
        System.out.println("------------------------------------------------------");
        return roleHolder;
    }

    /**
     * Return a boolean if the result code of a response is a Success, otherwise return false.
     */

    public boolean checkResultCodeJsonObject(ResponseEntity response) {
        String groupingAssignmentJson = (String) response.getBody();

        if (null == groupingAssignmentJson)
            return false;
        try {
            JSONObject jsonObject = new JSONObject(groupingAssignmentJson);
            System.out.println(jsonObject);
            JSONArray data = jsonObject.getJSONArray("data");
            JSONObject result = data.getJSONObject(0);
            logger.info(result);
            if ("SUCCESS".equals(result.get("resultCode")))
                return data.getBoolean(1);
        } catch (NullPointerException | JSONException e) {
            logger.info("Error in getting admin info. Error message: " + e.getMessage());
        }
        return false;
    }
}
