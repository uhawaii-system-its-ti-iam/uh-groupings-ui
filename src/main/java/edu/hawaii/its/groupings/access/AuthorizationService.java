package edu.hawaii.its.groupings.access;

public interface AuthorizationService {
    public RoleHolder fetchRoles(String uhuuid, String username);
}