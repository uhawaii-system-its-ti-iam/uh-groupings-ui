package edu.hawaii.its.api.service;

import edu.hawaii.its.api.type.Grouping;
import edu.hawaii.its.api.type.GroupingsServiceResult;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("groupingFactoryService")
@Profile(value = { "localhost", "test", "integrationTest", "qa", "prod" })
public class GroupingFactoryServiceImpl implements GroupingFactoryService{
    @Override
    public List<GroupingsServiceResult> addGrouping(String username, String groupingPath, List<String> basis,
            List<String> include,
            List<String> exclude, List<String> owners) {
        return null;
    }

    @Override public List<GroupingsServiceResult> deleteGrouping(String adminUsername, String groupingPath) {
        //todo
        return null;
    }

}
