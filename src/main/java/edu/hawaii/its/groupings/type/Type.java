package edu.hawaii.its.groupings.type;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Type implements Serializable {

    public static final long serialVersionUID = 43L;

    @JsonIgnore
    private Integer id;

    @JsonIgnore
    private Integer version;

    private String description;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Type ["
                + "id=" + id
                + ", description=" + description
                + "]";
    }

}
