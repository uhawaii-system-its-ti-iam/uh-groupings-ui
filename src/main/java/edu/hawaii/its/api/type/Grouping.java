package edu.hawaii.its.api.type;

public class Grouping {

    private String path;

    private String name;

    private Group basis;

    private Group exclude;

    private Group include;

    private Group composite;

    private Group owners;

    private boolean isListservOn = false;

    private boolean isReleasedGroupingOn = false;

    private boolean isOptInOn = false;

    private boolean isOptOutOn = false;

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
        return isListservOn;
    }

    public boolean isReleasedGroupingOn(){
        return isReleasedGroupingOn;
    }

    public void setListservOn(boolean listservOn) {
        this.isListservOn = listservOn;
    }

    public void setReleasedGroupingOn(boolean releasedGroupingOn) {
        this.isReleasedGroupingOn = releasedGroupingOn;
    }

    public boolean isOptInOn() {
        return isOptInOn;
    }

    public void setOptInOn(boolean optInOn) {
        this.isOptInOn = optInOn;
    }

    public boolean isOptOutOn() {
        return isOptOutOn;
    }

    public void setOptOutOn(boolean optOutOn) {
        this.isOptOutOn = optOutOn;
    }

    @Override
    public String toString() {
        return "Grouping [name=" + name
                + ", path=" + path
                + ", ListservOn=" + isListservOn()
                + ", ReleasedGroupingOn=" + isReleasedGroupingOn()
                + ", OptInOn=" + isOptInOn()
                + ", OptOutOn=" + isOptOutOn()
                + ", basis=" + basis
                + ", owners=" + owners
                + "]";
    }
}
