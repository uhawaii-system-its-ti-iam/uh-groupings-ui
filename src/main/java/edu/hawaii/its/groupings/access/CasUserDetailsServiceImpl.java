package edu.hawaii.its.groupings.access;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.cas.client.validation.Assertion;
import org.springframework.security.cas.userdetails.AbstractCasAssertionUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CasUserDetailsServiceImpl extends AbstractCasAssertionUserDetailsService {

    private static final Log logger = LogFactory.getLog(CasUserDetailsServiceImpl.class);

    private UserBuilder userBuilder;

    public CasUserDetailsServiceImpl(UserBuilder userBuilder) {
        super();
        this.userBuilder = userBuilder;
    }

    @Override
    protected UserDetails loadUserDetails(Assertion assertion) {
        if (assertion.getPrincipal() == null) {
            // Not sure this is possible.
            throw new UsernameNotFoundException("principal is null");
        }

        Map<String, Object> map = assertion.getPrincipal().getAttributes();
        logger.info("map: " + map);

        return userBuilder.make(new UhAttributes(map));
    }

}
