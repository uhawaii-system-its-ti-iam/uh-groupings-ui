package edu.hawaii.its.holiday.type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "unique_uh_number_v")
public class Employee implements Serializable {

    public static final long serialVersionUID = 2L;
    private Long uhNumber;

    public Employee() {
        // Empty.
    }

    public Employee(Long uhNumber) {
        this.uhNumber = uhNumber;
    }

    @Id
    @Column(name = "UH_NUMBER")
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
