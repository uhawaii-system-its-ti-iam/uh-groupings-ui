package edu.hawaii.its.groupings.access;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class UserContextService {

    public User getCurrentUser() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                return (User) authentication.getPrincipal();
            }
        }
        return new AnonymousUser();
    }

    public String getCurrentUid() {
        User user = getCurrentUser();
        return user != null ? user.getUid() : "";
    }

    public String getCurrentUhUuid() {
        User user = getCurrentUser();
        return user != null ? user.getUhUuid() : "";
    }

    public void setCurrentUhUuid(String uhUuid) {
        User user = getCurrentUser();
        if (null != user && user.hasRole(Role.ADMIN)) {
            user.setUhUuid(uhUuid);
        }
    }

    public String toString() {
        return "UserContextServiceImpl [context=" + SecurityContextHolder.getContext() + "]";
    }
}
