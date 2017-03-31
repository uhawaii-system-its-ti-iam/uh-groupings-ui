package edu.hawaii.its.holiday.api.type;

/**
 * Created by zknoebel on 3/15/2017.
 */
public class Grouping {
    Group basis;
    Group basisPlusInclude;
    Group exclude;
    Group include;
    Group basisPlusIncludeMinusExclude;
    String name;
    String path;

    public Grouping(Group basis, Group basisPlusInclude, Group exclude, Group include, Group basisPlusIncludeMinusExclude){
        this.basis = basis;
        this.basisPlusInclude = basisPlusInclude;
        this.exclude = exclude;
        this.include = include;
        this.basisPlusIncludeMinusExclude = basisPlusIncludeMinusExclude;
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

    public Group getBasisPlusInclude() {
        return basisPlusInclude;
    }

    public void setBasisPlusInclude(Group basisPlusInclude) {
        this.basisPlusInclude = basisPlusInclude;
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
}
