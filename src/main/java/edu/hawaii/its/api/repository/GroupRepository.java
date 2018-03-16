package edu.hawaii.its.api.repository;

import edu.hawaii.its.api.type.Group;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GroupRepository extends CrudRepository<Group, String> {
    Group findByPath(String path);
    List<Group> findByMembersUsername(String username);
}
