package edu.hawaii.its.groupings.type;

import java.io.Serializable;

public class Employee implements Serializable {

    public static final long serialVersionUID = 2L;
    private Long uhUuid ;

    public Employee() {
        // Empty.
    }

    public Employee(Long uhUuid ) {
        this.uhUuid  = uhUuid ;
    }

    public Long getUhUuid() {
        return uhUuid ;
    }

    public void setUhUuid(Long uhUuid ) {
        this.uhUuid  = uhUuid ;
    }

    @Override
    public String toString() {
        return "Employee [uhUuid=" + uhUuid  + "]";
    }

}
