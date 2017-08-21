package edu.hawaii.its.api.type;

import java.util.HashMap;
import java.util.Map;

public class MockGrouperDatabase {

    private Map groupings = new HashMap<String, Grouping>();


    public MockGrouperDatabase() {
        //empty
    }

    public Grouping getGrouping(String groupingPath) {
        return (Grouping)groupings.get(groupingPath);
    }

    public void addGrouping(String groupingPath) {
        groupings.put(groupingPath, new Grouping(groupingPath));
    }

    public void addGrouping(Grouping grouping) {
        groupings.put(grouping.getPath(), grouping);
    }
}
