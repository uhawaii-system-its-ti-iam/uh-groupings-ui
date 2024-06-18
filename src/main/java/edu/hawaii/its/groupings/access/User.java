package edu.hawaii.its.groupings.access;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class User extends org.springframework.security.core.userdetails.User {
    public static final long serialVersionUID = 2L;
    private String uhUuid;
    private UhAttributes attributes;

    public User(String uid, String uhUuid, Collection<GrantedAuthority> authorities) {
        super(uid, "", authorities);
        setUhUuid(uhUuid);
    }

    public User(String uid, Collection<GrantedAuthority> authorities) {
        super(uid, "", authorities);
    }

    private User(Builder builder) {
        super(builder.uid, "", builder.authorities);
        this.uhUuid = builder.uhUuid;
        this.attributes = new UhAttributes(builder.attributes);
    }

    public String getUid() {
        return getUsername();
    }

    public String getUhUuid() {
        return uhUuid;
    }

    public void setUhUuid(String uhUuid) {
        this.uhUuid = uhUuid;
    }

    public String getAttribute(String name) {
        return attributes.getValue(name);
    }

    public UhAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(UhAttributes attributes) {
        this.attributes = attributes;
    }

    public String getName() {
        return attributes.getValue("cn");
    }

    public String getGivenName() {
        return attributes.getValue("givenName");
    }

    public boolean hasRole(Role role) {
        return getAuthorities().contains(new SimpleGrantedAuthority(role.longName()));
    }

    @Override
    public String toString() {
        return "User [uid=" + getUid()
                + ", uhUuid=" + getUhUuid()
                + ", super-class: " + super.toString() + "]";
    }

    public static class Builder {
        private final String uid;
        private String uhUuid;
        private Collection<GrantedAuthority> authorities;
        private final Map<String, Object> attributes = new HashMap<>();

        public Builder(String uid) {
            this.uid = uid;
        }

        public Builder uhUuid(String uhUuid) {
            this.uhUuid = uhUuid;
            return this;
        }

        public Builder authorities(Collection<GrantedAuthority> authorities) {
            this.authorities = authorities;
            return this;
        }

        public Builder addAttribute(String key, String value) {
            this.attributes.put(key, Collections.singletonList(value));
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}
