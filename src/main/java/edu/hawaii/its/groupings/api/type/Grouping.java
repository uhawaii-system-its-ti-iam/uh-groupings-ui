package edu.hawaii.its.groupings.api.type;

public class Grouping {
    private Group basis;
    private Group exclude;
    private Group include;
    private Group basisPlusIncludeMinusExclude;
    private Group owners;
    private String name;
    private String path;
    private boolean listservOn, optInOn, optOutOn = false;

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
        setBasisPlusIncludeMinusExclude(new EmptyGroup());
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
        this.name = path.split(":")[path.split(":").length - 1];
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

    public Group getBasisPlusIncludeMinusExclude() {
        return basisPlusIncludeMinusExclude;
    }

    public void setBasisPlusIncludeMinusExclude(Group basisPlusIncludeMinusExclude) {
        this.basisPlusIncludeMinusExclude = basisPlusIncludeMinusExclude != null ? basisPlusIncludeMinusExclude : new EmptyGroup();
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
