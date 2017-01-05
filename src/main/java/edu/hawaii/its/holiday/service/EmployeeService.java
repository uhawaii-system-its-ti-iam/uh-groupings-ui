package edu.hawaii.its.holiday.service;

import javax.persistence.EntityManager;

public interface EmployeeService {
    public EntityManager getEntityManager();

    public boolean exists(String uhuuid);
}
