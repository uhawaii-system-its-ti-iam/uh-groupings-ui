package edu.hawaii.its.groupings.configuration;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;

import edu.hawaii.its.groupings.access.UhAttributes;
import edu.hawaii.its.groupings.access.User;

public class OotbCustomUserDetails extends User {

    public OotbCustomUserDetails(String username, String password, Collection<GrantedAuthority> authorities,
            String givenName, String cn, String email) {
        super(username, password, authorities);
        Map<String, Object> defaultAttributes = new HashMap<>();
        defaultAttributes.put("cn", Arrays.asList(cn));
        defaultAttributes.put("mail", Arrays.asList(email));
        defaultAttributes.put("givenName", Arrays.asList(givenName));
        this.setAttributes(new UhAttributes(defaultAttributes));
    }

}
