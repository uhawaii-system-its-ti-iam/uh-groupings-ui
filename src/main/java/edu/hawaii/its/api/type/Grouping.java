package edu.hawaii.its.api.type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "grouping")
public class Grouping {

    @Id
    @Column(name = "path")
    private String path;

    @Column
    private String name;

    @OneToOne
    private Group basis;

    @OneToOne
    private Group exclude;

    @OneToOne
    private Group include;

    @OneToOne
    private Group composite;

    @OneToOne
    private Group owners;

    @Column
    private boolean listservOn = false;

    @Column
    private boolean optInOn = false;

    @Column
    private boolean optOutOn = false;

    // Constructor.
    public Grouping() {
        this("");
    }

    // Constructor.
    public Grouping(String path) {
        setPath(path);

        setBasis(new EmptyGroup());
        setExclude(new EmptyGroup());
        setInclude(new EmptyGroup());
        setComposite(new EmptyGroup());
        setOwners(new EmptyGroup());
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path != null ? path : "";
        this.name = this.path;
        int index = this.name.lastIndexOf(':');
        if (index != -1) {
            this.name = this.name.substring(index + 1);
        }
    }

    public Group getBasis() {
        return basis;
    }

    public void setBasis(Group basis) {
        this.basis = basis != null ? basis : new EmptyGroup();
    }

    public Group getExclude() {
        return exclude;
    }

    public void setExclude(Group exclude) {
        this.exclude = exclude != null ? exclude : new EmptyGroup();
    }

    public Group getInclude() {
        return include;
    }

    public void setInclude(Group include) {
        this.include = include != null ? include : new EmptyGroup();
    }

    public Group getComposite() {
        return composite;
    }

    public void setComposite(Group composite) {
        this.composite = composite != null ? composite : new EmptyGroup();
    }

    public Group getOwners() {
        return owners;
    }

    public void setOwners(Group owners) {
        this.owners = owners != null ? owners : new EmptyGroup();
    }

    public boolean isListservOn() {
        return listservOn;
    }

    public void setListservOn(boolean listservOn) {
        this.listservOn = listservOn;
    }

    public boolean isOptInOn() {
        return optInOn;
    }

    public void setOptInOn(boolean optInOn) {
        this.optInOn = optInOn;
    }

    public boolean isOptOutOn() {
        return optOutOn;
    }

    public void setOptOutOn(boolean optOutOn) {
        this.optOutOn = optOutOn;
    }

    @Override
    public String toString() {
        return "Grouping [name=" + name
                + ", path=" + path
                + ", ListservOn=" + isListservOn()
                + ", OptInOn=" + isOptInOn()
                + ", OptOutOn=" + isOptOutOn()
                + ", basis=" + basis
                + ", owners=" + owners
                + "]";
    }
}
