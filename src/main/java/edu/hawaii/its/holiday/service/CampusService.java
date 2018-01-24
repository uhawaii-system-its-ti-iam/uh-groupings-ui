package edu.hawaii.its.holiday.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.hawaii.its.holiday.repository.CampusRepository;
import edu.hawaii.its.holiday.type.Campus;

@Service
public class CampusService {

    @Autowired
    private CampusRepository campusRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = "campusesAll")
    public List<Campus> findAll() {
        return campusRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "campusesById", key = "#id")
    public Campus find(Integer id) {
        return campusRepository.findById(id);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "campusesActualAll")
    public List<Campus> findActualAll() {
        return campusRepository.findAllByActual("Y", new Sort("id"));
    }

    @Transactional(readOnly = true)
    public Campus findFirst() {
        return campusRepository.findTopByOrderByIdDesc();
    }

    public CampusRepository getCampusRepository() {
        return campusRepository;
    }

    public void setCampusRepository(CampusRepository campusRepository) {
        this.campusRepository = campusRepository;
    }
}
