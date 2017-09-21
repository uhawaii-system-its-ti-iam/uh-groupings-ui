package edu.hawaii.its.holiday.service;

import edu.hawaii.its.holiday.type.Message;

import javax.persistence.EntityManager;

public interface MessageService {
    public EntityManager getEntityManager();

    public void setEntityManager(EntityManager em);

    public void evictCache();

    public Message findMessage(int id);

    public Message add(Message message);

    public Message update(Message message);

}
