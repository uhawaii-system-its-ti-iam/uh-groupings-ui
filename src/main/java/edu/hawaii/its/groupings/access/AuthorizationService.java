package edu.hawaii.its.groupings.access;

public interface AuthorizationService {
    RoleHolder fetchRoles(String uhUuid, String username);
}