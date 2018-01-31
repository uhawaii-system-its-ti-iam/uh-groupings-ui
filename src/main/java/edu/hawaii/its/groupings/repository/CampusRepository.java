package edu.hawaii.its.groupings.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import edu.hawaii.its.api.type.Campus;

public interface CampusRepository extends JpaRepository<Campus, Integer> {

    Campus findById(Integer id);

    Campus findTopByOrderByIdDesc();

    List<Campus> findAllByActual(String actual, Sort sort);

}
