package edu.hawaii.its.groupings.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import edu.hawaii.its.groupings.type.Campus;

@Repository
public class CampusService {

    @PersistenceContext
    private EntityManager em;

    @Transactional(readOnly = true)
    @Cacheable(value = "campuses")
    public List<Campus> findAll() {
        String qlString = "select s from Campus s "
                + "where s.actual = 'N' "
                + "order by s.id";
        return em.createQuery(qlString, Campus.class).getResultList();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "campusesById", key = "#id")
    public Campus find(Integer id) {
        return em.find(Campus.class, id);
    }

    @Transactional(readOnly = true)
    public List<Campus> findActualAll() {
        String qlString = "select s from Campus s "
                + "where s.actual = 'Y' "
                + "order by s.id";
        return em.createQuery(qlString, Campus.class).getResultList();
    }

}
