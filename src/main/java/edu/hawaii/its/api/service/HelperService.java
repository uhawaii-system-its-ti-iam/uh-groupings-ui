package edu.hawaii.its.api.service;

import edu.hawaii.its.api.type.Grouping;
import edu.hawaii.its.api.type.GroupingsServiceResult;

import edu.internet2.middleware.grouperClient.ws.beans.ResultMetadataHolder;
import edu.internet2.middleware.grouperClient.ws.beans.WsGetMembershipsResults;

import java.util.List;

public interface HelperService {

    public String extractFirstMembershipID(WsGetMembershipsResults wsGetMembershipsResults);

    public WsGetMembershipsResults membershipsResults(String username, String group);

    public List<String> extractGroupings(List<String> groupPaths);

    public GroupingsServiceResult makeGroupingsServiceResult(ResultMetadataHolder resultMetadataHolder, String action);

    public GroupingsServiceResult makeGroupingsServiceResult(String resultCode, String action);

    public List<Grouping> makeGroupings(List<String> groupingPaths);

    public String parentGroupingPath(String group);
}
