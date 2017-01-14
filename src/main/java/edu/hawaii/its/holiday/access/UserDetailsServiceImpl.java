package edu.hawaii.its.holiday.access;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.cas.client.validation.Assertion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.cas.userdetails.AbstractCasAssertionUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("myiamUserDetailsService")
public class UserDetailsServiceImpl extends AbstractCasAssertionUserDetailsService {

    private static final Log logger = LogFactory.getLog(UserDetailsServiceImpl.class);

    @Autowired
    private UserBuilder userBuilder;

    @Override
    protected UserDetails loadUserDetails(Assertion assertion) {
        if (assertion.getPrincipal() == null) {
            // Not sure this is possible.
            throw new UsernameNotFoundException("principal is null");
        }

        String username = assertion.getPrincipal().getName();
        if (username == null || username.trim().length() == 0) {
            // Not sure this possible, either.
            throw new UsernameNotFoundException("username is null or empty");
        }

        Map<String, Object> map = assertion.getPrincipal().getAttributes();
        if (logger.isDebugEnabled()) {
            logger.debug("map: " + map);
        }

        return userBuilder.make(new UhCasAttributes(map));
    }

}
