package edu.hawaii.its.api.service;

import edu.hawaii.its.api.type.Grouping;
import edu.hawaii.its.api.type.GroupingsServiceResult;
import edu.hawaii.its.api.type.Person;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Service("groupingFactoryService")
@Profile(value = { "default", "dev", "localTest" })
public class GroupingFactoryServiceImplLocal implements GroupingFactoryService {
    @Autowired
    private GrouperFactoryService gfs;

    @Override
    public List<GroupingsServiceResult> makeGrouping(String username, String groupingPath, List<String> basis,
            List<String> include,
            List<String> exclude, List<String> owners) {
        return null;
    }

    //set of elements in list0 or list1
    private List<String> union(List<String> list0, List<String> list1) {
        if (list0 != null) {

            //remove duplicates
            Set<String> treeSet = new TreeSet<>(list0);
            treeSet.addAll(list1);

            return new ArrayList<>(treeSet);
        } else {

            return list1 != null ? list1 : new ArrayList<>();
        }
    }

    //set of elements in list0, but not in list1
    private List<String> complement(List<String> list0, List<String> list1) {
        if (list0 != null) {
            if (list1 == null) {
                return list0;
            }
            list0.removeAll(list1);
            return list0;
        }
        return new ArrayList<>();
    }

    //set of elements in both list0 and list1
    private List<String> intersection(List<String> list0, List<String> list1) {
        if (list0 == null || list1 == null) {
            return new ArrayList<>();
        }
        //todo there wont be duplicates
        Set<String> treeSet = new TreeSet<>(list0);
        treeSet.retainAll(list1);

        return new ArrayList<>(treeSet);

    }

}
