package edu.hawaii.its.holiday.api.type;

/**
 * Created by zknoebel on 3/15/2017.
 */
public class Grouping {
    private Group basis;
    private Group exclude;
    private Group include;
    private Group basisPlusIncludeMinusExclude;
    private Group owners;
    private String name;
    private String path;
    private boolean hasListServe = false;

    public Grouping(Group basis, Group exclude, Group include, Group basisPlusIncludeMinusExclude, Group owners){
        this.basis = basis;
        this.exclude = exclude;
        this.include = include;
        this.basisPlusIncludeMinusExclude = basisPlusIncludeMinusExclude;
        this.owners = owners;
    }

    public Grouping(String path){
        this.path = path;
        name = path.split(":")[path.split(":").length -1];
    }

    public Grouping(){

    }


    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
        name = path.split(":")[path.split(":").length -1];
    }

    public Group getBasis() {
        return basis;
    }

    public void setBasis(Group basis) {
        this.basis = basis;
    }

    public Group getExclude() {
        return exclude;
    }

    public void setExclude(Group exclude) {
        this.exclude = exclude;
    }

    public Group getInclude() {
        return include;
    }

    public void setInclude(Group include) {
        this.include = include;
    }

    public Group getBasisPlusIncludeMinusExclude() {
        return basisPlusIncludeMinusExclude;
    }

    public void setBasisPlusIncludeMinusExclude(Group basisPlusIncludeMinusExclude) {
        this.basisPlusIncludeMinusExclude = basisPlusIncludeMinusExclude;
    }

    public Group getOwners() {
        return owners;
    }

    public void setOwners(Group owners) {
        this.owners = owners;
    }

    public boolean getHasListServe() {
        return hasListServe;
    }

    public void setListServe(boolean hasListServe){
            this.hasListServe = hasListServe;
    }

}
