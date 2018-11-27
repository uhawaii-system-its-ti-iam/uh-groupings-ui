package edu.hawaii.its.groupings.type;

import java.io.Serializable;

public class Message implements Serializable {

    public static final long serialVersionUID = 2L;
    public static final int GATE_MESSAGE = 1;
    public static final int ACCESS_DENIED_MESSAGE = 2;

    private Integer id;
    private Integer typeId;
    private String text;
    private String enabled;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    @Override
    public String toString() {
        return "Message [id=" + id
                + ", typeId=" + typeId
                + ", enabled=" + enabled
                + ", text=" + text
                + "]";
    }

}
