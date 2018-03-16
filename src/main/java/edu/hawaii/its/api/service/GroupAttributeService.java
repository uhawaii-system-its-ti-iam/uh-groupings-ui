package edu.hawaii.its.api.service;

import edu.hawaii.its.api.type.GroupingsServiceResult;

import java.util.List;

public interface GroupAttributeService {

    public GroupingsServiceResult changeListservStatus(String groupingPath, String ownerUsername, boolean listservOn);

    public List<GroupingsServiceResult> changeOptInStatus(String groupingPath, String ownerUsername, boolean optInOn);

    public List<GroupingsServiceResult> changeOptOutStatus(String groupingPath, String ownerUsername, boolean optOutOn);

    public boolean hasListserv(String groupingPath);

    public boolean optOutPermission(String groupingPath);

    public boolean optInPermission(String groupingPath);
}
