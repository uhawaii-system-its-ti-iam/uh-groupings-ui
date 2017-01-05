package edu.hawaii.its.holiday.type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import edu.hawaii.its.holiday.util.Dates;

@Entity
@Table(name = "holiday")
@JsonRootName(value = "data")
public class Holiday implements Serializable {

    public static final long serialVersionUID = 53L;

    @Id
    @Column(name = "id")
    @JsonIgnore
    private Integer id;

    @Column(name = "version")
    @JsonIgnore
    private Integer version;

    @Column(name = "description")
    private String description;

    @Column(name = "observed_date")
    @Temporal(TemporalType.DATE)
    @JsonSerialize(using = HolidayDateSerializer.class)
    private Date observedDate;

    @Column(name = "official_date")
    @Temporal(TemporalType.DATE)
    @JsonSerialize(using = HolidayDateSerializer.class)
    private Date officialDate;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "holiday_type",
               joinColumns = @JoinColumn(name = "holiday_id", unique = false),
               inverseJoinColumns = @JoinColumn(name = "type_id", unique = false))
    @OrderBy(value = "id")
    private List<Type> holidayTypes = new ArrayList<Type>(0);

    // Constructor.
    public Holiday() {
        // Empty.
    }

    // Constructor.
    public Holiday(Date officialDate, Date observedDate) {
        this();
        this.officialDate = officialDate;
        this.observedDate = observedDate;
    }

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

    public Date getObservedDate() {
        return observedDate;
    }

    public void setObservedDate(Date observedDate) {
        this.observedDate = observedDate;
    }

    public Date getOfficialDate() {
        return officialDate;
    }

    public void setOfficialDate(Date officialDate) {
        this.officialDate = officialDate;
    }

    public List<Type> getHolidayTypes() {
        return holidayTypes;
    }

    public void setHolidayTypes(List<Type> holidayTypes) {
        this.holidayTypes = holidayTypes;
    }

    @Transient
    public Integer getYear() {
        if (observedDate != null) {
            return Dates.yearOfDate(observedDate);
        }
        return null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((observedDate == null) ? 0 : observedDate.hashCode());
        result = prime * result + ((officialDate == null) ? 0 : officialDate.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Holiday other = (Holiday) obj;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (observedDate == null) {
            if (other.observedDate != null)
                return false;
        } else if (!observedDate.equals(other.observedDate))
            return false;
        if (officialDate == null) {
            if (other.officialDate != null)
                return false;
        } else if (!officialDate.equals(other.officialDate))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Holiday ["
                + "id=" + id
                + ", description=" + description
                + ", observedDate=" + observedDate
                + ", officialDate=" + officialDate
                + ", holidayTypes=" + holidayTypes
                + "]";
    }

}
