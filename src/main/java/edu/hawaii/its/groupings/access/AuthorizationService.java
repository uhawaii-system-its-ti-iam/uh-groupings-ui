package edu.hawaii.its.groupings.access;

import org.springframework.http.ResponseEntity;

public interface AuthorizationService {
    RoleHolder fetchRoles(String uhUuid, String username);

    boolean checkResultCodeJsonObject(ResponseEntity response);
}