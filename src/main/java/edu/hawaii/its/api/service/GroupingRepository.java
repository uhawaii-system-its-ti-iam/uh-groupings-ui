package edu.hawaii.its.api.service;

import edu.hawaii.its.api.type.Grouping;
import edu.hawaii.its.api.type.Person;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GroupingRepository extends CrudRepository<Grouping, String> {
    Grouping findByPath(String path);
    Grouping findByIncludePathOrExcludePathOrCompositePathOrOwnersPath(String path0, String path1, String path2, String path3);
    List<Grouping> findByOwnersMembersUsername(String username);
}
