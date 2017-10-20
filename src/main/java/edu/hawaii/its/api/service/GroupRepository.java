package edu.hawaii.its.api.service;

import edu.hawaii.its.api.type.Group;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GroupRepository extends CrudRepository<Group, String> {
    Group findByPath(String path);
    List<Group> findByMembersUsername(String username);
}
