package edu.hawaii.its.holiday.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("employeeService")
@Repository
public class EmployeeServiceImpl implements EmployeeService {

    private EntityManager em;

    @PersistenceContext
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean exists(String s) {
        if (!isValid(s)) {
            return false;
        }

        Long uhId = Long.valueOf(s);
        String qlString = "select e from Employee e "
                + "where e.uhNumber = :uhNumber";
        Query query = em.createQuery(qlString);
        query.setParameter("uhNumber", uhId);

        return query.getResultList().size() > 0;
    }

    private boolean isValid(String str) {
        return str != null && str.matches("\\d+");
    }
}