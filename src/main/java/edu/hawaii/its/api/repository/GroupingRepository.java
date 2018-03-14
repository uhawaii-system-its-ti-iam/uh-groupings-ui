package edu.hawaii.its.api.repository;

import edu.hawaii.its.api.type.Grouping;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GroupingRepository extends CrudRepository<Grouping, String> {
    Grouping findByPath(String path);

    Grouping findByIncludePathOrExcludePathOrCompositePathOrOwnersPath(String path0, String path1, String path2, String path3);

    Grouping findByOwnersPath(String path);

    List<Grouping> findByOwnersMembersUsername(String username);
}
