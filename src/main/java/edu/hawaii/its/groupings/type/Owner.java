package edu.hawaii.its.groupings.type;

public class Owner {

    private String privilegeName;
    private String name;
    private String uid;
    private String uhuuid;

    // Constructor.
    public Owner() {
        // Empty.
    }

    // Constructor.
    public Owner(String privilegeName) {
        this.privilegeName = privilegeName;
    }

    public String getPrivilegeName() {
        return privilegeName;
    }

    public void setPrivilegeName(String privilegeName) {
        this.privilegeName = privilegeName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUhuuid() {
        return uhuuid;
    }

    public void setUhuuid(String uhuuid) {
        this.uhuuid = uhuuid;
    }

    @Override
    public String toString() {
        return "Owner ["
                + "privilegeName=" + privilegeName
                + ", name=" + name
                + ", uid=" + uid
                + ", uhuuid=" + uhuuid
                + "]";
    }
}
