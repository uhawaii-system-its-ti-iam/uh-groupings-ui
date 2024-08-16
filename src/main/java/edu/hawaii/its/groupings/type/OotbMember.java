package edu.hawaii.its.groupings.type;

public class OotbMember {

    private String name;
    private String uhUuid;
    private String uid;

    public OotbMember(String name, String uhUuid, String uid) {
        this.name = name;
        this.uhUuid = uhUuid;
        this.uid = uid;
    }

    public OotbMember() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUhUuid() {
        return uhUuid;
    }

    public void setUhUuid(String uhUuid) {
        this.uhUuid = uhUuid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
