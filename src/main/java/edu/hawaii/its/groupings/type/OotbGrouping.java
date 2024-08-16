package edu.hawaii.its.groupings.type;

import java.util.List;

public class OotbGrouping {
    private String name;
    private String displayName;
    private String extension;
    private String displayExtension;
    private String description;
    private List<OotbMember> members;

    public OotbGrouping(String name, String displayName, String extension, String displayExtension, String description,
            List<OotbMember> members) {
        this.name = name;
        this.displayName = displayName;
        this.extension = extension;
        this.displayExtension = displayExtension;
        this.description = description;
        this.members = members;
    }

    public OotbGrouping() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getDisplayExtension() {
        return displayExtension;
    }

    public void setDisplayExtension(String displayExtension) {
        this.displayExtension = displayExtension;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<OotbMember> getMembers() {
        return members;
    }

    public void setMembers(List<OotbMember> members) {
        this.members = members;
    }
}
