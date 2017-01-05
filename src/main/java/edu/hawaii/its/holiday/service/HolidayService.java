package edu.hawaii.its.holiday.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import edu.hawaii.its.holiday.type.Holiday;
import edu.hawaii.its.holiday.type.Type;
import edu.hawaii.its.holiday.type.UserRole;
import edu.hawaii.its.holiday.util.Dates;

@Repository("holidayService")
public class HolidayService {

    @PersistenceContext
    private EntityManager em;

    @Transactional(readOnly = true)
    @Cacheable(value = "holidaysById", key = "#id")
    public Holiday findHoliday(Integer id) {
        return em.find(Holiday.class, id);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "holidayTypesById", key = "#id")
    public Type findType(Integer id) {
        return em.find(Type.class, id);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "holidays")
    public List<Holiday> findHolidays() {
        String qlString = "select a from Holiday a "
                + "order by a.observedDate desc";
        return em.createQuery(qlString, Holiday.class).getResultList();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "holidaysByYear", key = "#year")
    public List<Holiday> findHolidays(Integer year) {
        String qlString = "select a from Holiday a "
                + "where (a.observedDate between :start and :end) "
                + "or (a.officialDate between :start and :end) "
                + "order by a.observedDate desc";

        return em.createQuery(qlString, Holiday.class)
                .setParameter("start", Dates.toDate(Dates.firstDateOfYear(year)))
                .setParameter("end", Dates.toDate(Dates.lastDateOfYear(year)))
                .getResultList();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "holidayTypes")
    public List<Type> findTypes() {
        String qlString = "select a from Type a "
                + "order by a.id, a.version";
        return em.createQuery(qlString, Type.class).getResultList();
    }

    @Transactional(readOnly = true)
    public List<UserRole> findUserRoles() {
        String qlString = "select a from UserRole a "
                + "order by a.id, a.version";
        return em.createQuery(qlString, UserRole.class).getResultList();
    }

}
