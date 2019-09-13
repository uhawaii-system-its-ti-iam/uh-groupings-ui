package edu.hawaii.its.groupings.access;

public interface AuthorizationService {
    RoleHolder fetchRoles(String uhuuid, String username);
}