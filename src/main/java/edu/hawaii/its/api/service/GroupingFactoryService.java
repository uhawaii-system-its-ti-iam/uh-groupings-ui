package edu.hawaii.its.api.service;

import edu.hawaii.its.api.type.GroupingsServiceResult;

import java.util.List;

public interface GroupingFactoryService {

    public List<GroupingsServiceResult> makeGrouping(
            String username,
            String groupingPath,
            List<String> basis,
            List<String> include,
            List<String> exclude,
            List<String> owners);
}
