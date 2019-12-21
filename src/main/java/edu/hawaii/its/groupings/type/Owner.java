package edu.hawaii.its.groupings.type;

public class Owner {

    private String privilegeName;
    private String name;
    private String uid;
    private String uhUuid;

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

    public String getUhUuid() {
        return uhUuid;
    }

    public void setUhUuid(String uhUuid) {
        this.uhUuid = uhUuid;
    }

    @Override
    public String toString() {
        return "Owner ["
                + "privilegeName=" + privilegeName
                + ", name=" + name
                + ", uid=" + uid
                + ", uhUuid=" + uhUuid
                + "]";
    }
}
