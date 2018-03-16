package edu.hawaii.its.api.type;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
public class Membership implements Comparable<Membership> {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column
    private String id;

    @ManyToOne
    private Person person;

    @ManyToOne
    private Group group;

    @Column
    private boolean selfOpted = false;

    @Column
    private boolean optInEnabled = false;

    @Column
    private boolean optOutEnabled = false;

    public Membership() {

    }

    public Membership(Person person, Group group) {
        this.person = person;
        this.group = group;
    }

    public String getId() {
        return id;
    }

    public Person getPerson() {
        return person;
    }

    public Group getGroup() {
        return group;
    }

    public boolean isSelfOpted() {
        return selfOpted;
    }

    public boolean isOptInEnabled() {
        return optInEnabled;
    }

    public boolean isOptOutEnabled() {
        return optOutEnabled;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public void setSelfOpted(boolean selfOpted) {
        this.selfOpted = selfOpted;
    }

    public void setOptInEnabled(boolean optInEnabled) {
        this.optInEnabled = optInEnabled;
    }

    public void setOptOutEnabled(boolean optOutEnabled) {
        this.optOutEnabled = optOutEnabled;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Membership) && (compareTo((Membership) o) == 0);
    }

    @Override
    public int compareTo(Membership membership) {
        if(membership != null) {
            int idComp = membership.getId() != null ? getId().compareTo(membership.getId()) : -1;
            int personComp = membership.getPerson() != null ? getPerson().compareTo(membership.getPerson()) : -1;
            int groupComp = membership.getGroup() != null ? getGroup().compareTo(membership.getGroup()) : -1;

            if (idComp != 0) {
                return idComp;
            }
            if (personComp != 0) {
                return personComp;
            }
            if (groupComp != 0) {
                return groupComp;
            }
            return 0;
        }
        return -1;
    }
}
