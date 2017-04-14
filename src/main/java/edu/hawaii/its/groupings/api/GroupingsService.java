package edu.hawaii.its.groupings.api;

import edu.hawaii.its.groupings.api.type.Grouping;

import java.util.List;

/**
 * Created by zknoebel on 4/13/2017.
 */
public interface GroupingsService {
    public boolean hasListServe(String grouping);
    public List<Grouping> groupingsIn(String username);
}
