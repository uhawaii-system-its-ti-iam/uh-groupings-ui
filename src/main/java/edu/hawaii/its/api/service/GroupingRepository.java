package edu.hawaii.its.api.service;

import edu.hawaii.its.api.type.Grouping;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GroupingRepository extends CrudRepository<Grouping, String> {
    List<Grouping> findByPath(String path);
}
