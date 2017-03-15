package edu.hawaii.its.holiday.api;

import edu.internet2.middleware.grouperClient.ws.beans.WsSubject;

/**
 * Created by zknoebel on 3/15/2017.
 */
public class Grouping {
    WsSubject[] basis;
    WsSubject[] exclude;
    WsSubject[] include;

    public Grouping(WsSubject[] basis, WsSubject[] exclude, WsSubject[] include){
        this.basis = basis;
        this.exclude = exclude;
        this.include = include;
    }

    public Grouping(){

    }

    public WsSubject[] getBasis() {
        return basis;
    }

    public void setBasis(WsSubject[] basis) {
        this.basis = basis;
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
}
