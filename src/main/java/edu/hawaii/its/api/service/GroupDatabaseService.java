package edu.hawaii.its.api.service;

import edu.hawaii.its.groupings.type.Campus;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

//@Repository
public class GroupDatabaseService {

//    @PersistenceContext
//    private EntityManager em;
//
//    @Transactional(readOnly = true)
//    @Cacheable(value = "campuses")
//    public List<Campus> findAll() {
//        String qlString = "select s from Campus s "
//                + "where s.actual = 'N' "
//                + "order by s.id";
//        return em.createQuery(qlString, Campus.class).getResultList();
//    }
//
//    @Transactional(readOnly = true)
//    @Cacheable(value = "campusesById", key = "#id")
//    public Campus find(Integer id) {
//        return em.find(Campus.class, id);
//    }
//
//    @Transactional(readOnly = true)
//    public List<Campus> findActualAll() {
//        String qlString = "select s from Campus s "
//                + "where s.actual = 'Y' "
//                + "order by s.id";
//        return em.createQuery(qlString, Campus.class).getResultList();
//    }

}
