package edu.hawaii.its.groupings.type;

import java.io.Serializable;

public class UserRole implements Serializable {

    public static final long serialVersionUID = 33L;

    private Integer id;
    private Integer version;
    private String authority;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    @Override
    public String toString() {
        return "UserRole "
                + "[id=" + id
                + ", version=" + version
                + ", authority=" + authority + "]";
    }
}
