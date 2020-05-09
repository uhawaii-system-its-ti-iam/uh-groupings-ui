package edu.hawaii.its.groupings.access;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.hawaii.its.api.controller.GroupingsRestController;
import edu.hawaii.its.api.type.AdminListsHolder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.cas.client.authentication.SimplePrincipal;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Null;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthorizationServiceImpl implements AuthorizationService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private Map<String, List<Role>> userMap = new HashMap<>();

    @Autowired
    GroupingsRestController groupingsRestController;

    private static final Log logger = LogFactory.getLog(AuthorizationServiceImpl.class);

    /**
     * Assign roles to user
     *
     * @param uhUuid   : The UH uuid of the user.
     * @param username : The username of the person to find the user.
     * @return : Returns an array list of roles assigned to the user.
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

        List<Role> roles = userMap.get(uhUuid);
        if (roles != null) {
            for (Role role : roles) {
                roleHolder.add(role);
            }
        }
        System.out.println("------------------------------------------------------");
        System.out.println(roleHolder.getAuthorities());
        System.out.println("------------------------------------------------------");
        return roleHolder;
    }

    /**
     * Return a boolean if the result code of a response is a Success, otherwise return false.
     *
     * @param response - in this case the response can be represented as follows.
     *                 {response {data [groupingsServiceResult: class, Boolean: object]}}
     * @return boolean
     */
    public boolean checkResultCodeJsonObject(ResponseEntity response) {
        String groupingAssignmentJson = (String) response.getBody();

        if (null == groupingAssignmentJson)
            return false;
        try {
            JSONObject jsonObject = new JSONObject(groupingAssignmentJson);
            System.out.println(jsonObject);
            JSONArray data = jsonObject.getJSONArray("data");
            JSONObject result = jsonObject.getJSONObject("groupingsServiceResult");
            logger.info("**************************************************");
            logger.info(result);
            logger.info("**************************************************");
            if ("SUCCESS".equals(result.get("resultCode")))
                return data.getBoolean(0);
        } catch (NullPointerException | JSONException e) {
            logger.info("Error in getting admin info. Error message: " + e.getMessage());
        }
        return false;
    }
}
