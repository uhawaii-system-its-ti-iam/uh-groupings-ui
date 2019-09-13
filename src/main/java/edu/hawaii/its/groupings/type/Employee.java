package edu.hawaii.its.groupings.type;

import java.io.Serializable;

public class Employee implements Serializable {

    public static final long serialVersionUID = 2L;
    private Long uhNumber;

    public Employee() {
        // Empty.
    }

    public Employee(Long uhNumber) {
        this.uhNumber = uhNumber;
    }

    public Long getUhNumber() {
        return uhNumber;
    }

    public void setUhNumber(Long uhNumber) {
        this.uhNumber = uhNumber;
    }

    @Override
    public String toString() {
        return "Employee [uhNumber=" + uhNumber + "]";
    }

}
