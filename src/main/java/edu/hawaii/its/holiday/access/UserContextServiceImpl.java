package edu.hawaii.its.holiday.access;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class UserContextServiceImpl implements UserContextService {

    @Override
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

    @Override
    public String getCurrentUsername() {
        User user = getCurrentUser();
        return user != null ? user.getUsername() : "";
    }

    @Override
    public Long getCurrentUhuuid() {
        User user = getCurrentUser();
        return user != null ? user.getUhuuid() : Long.valueOf(0);
    }

    public void setCurrentUhuuid(Long uhuuid) {
        User user = getCurrentUser();
        if (user != null) {
            if (user.hasRole(Role.ADMIN)) {
                user.setUhuuid(uhuuid);
            }
        }
    }

    public String toString() {
        return "UserContextServiceImpl [context=" + SecurityContextHolder.getContext() + "]";
    }
}
