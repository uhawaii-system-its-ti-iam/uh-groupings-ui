package edu.hawaii.its.holiday.api;

import edu.internet2.middleware.grouperClient.ws.beans.WsSubject;

/**
 * Created by zknoebel on 3/15/2017.
 */
public class Grouping {
    WsSubject[] basis;
    WsSubject[] basisPlusInclude;
    WsSubject[] exclude;
    WsSubject[] include;
    WsSubject[] basisPlusIncludeMinusExclude;

    public Grouping(WsSubject[] basis, WsSubject[] basisPlusInclude, WsSubject[] exclude, WsSubject[] include, WsSubject[] basisPlusIncludeMinusExclude){
        this.basis = basis;
        this.basisPlusInclude = basisPlusInclude;
        this.exclude = exclude;
        this.include = include;
        this.basisPlusIncludeMinusExclude = basisPlusIncludeMinusExclude;
    }

    public Grouping(){

    }

    public WsSubject[] getBasis() {
        return basis;
    }

    public void setBasis(WsSubject[] basis) {
        this.basis = basis;
    }

    public WsSubject[] getBasisPlusInclude() {
        return basisPlusInclude;
    }

    public void setBasisPlusInclude(WsSubject[] basisPlusInclude) {
        this.basisPlusInclude = basisPlusInclude;
    }

    public WsSubject[] getExclude() {
        return exclude;
    }

    public void setExclude(WsSubject[] exclude) {
        this.exclude = exclude;
    }

    public WsSubject[] getInclude() {
        return include;
    }

    public void setInclude(WsSubject[] include) {
        this.include = include;
    }

    public WsSubject[] getBasisPlusIncludeMinusExclude() {
        return basisPlusIncludeMinusExclude;
    }

    public void setBasisPlusIncludeMinusExclude(WsSubject[] basisPlusIncludeMinusExclude) {
        this.basisPlusIncludeMinusExclude = basisPlusIncludeMinusExclude;
    }
}
