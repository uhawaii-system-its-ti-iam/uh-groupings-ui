package edu.hawaii.its.holiday.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.hawaii.its.holiday.type.Message;

@Service("messageService")
public class MessageServiceImpl implements MessageService {

    private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);
    private EntityManager em;

    @PersistenceContext
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    public EntityManager getEntityManager() {
        return em;
    }

    @CacheEvict(value = "messages", allEntries = true)
    public void evictCache() {
        // Empty.
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "messages", key = "#id")
    public Message findMessage(int id) {
        Message message = null;
        try {
            message = em.find(Message.class, id);
        } catch (Exception e) {
            logger.error("Error:", e);
        }
        return message;
    }

    @Override
    @Transactional
    @CachePut(value = "messages", key = "#result.id")
    public Message update(Message message) {
        em.merge(message);
        return message;
    }

    @Override
    @Transactional
    @CachePut(value = "messages", key = "#result.id")
    public Message add(Message message) {
        em.persist(message);
        return message;
    }

}