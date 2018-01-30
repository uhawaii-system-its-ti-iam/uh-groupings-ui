package edu.hawaii.its.groupings.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.hawaii.its.groupings.type.Message;

@Service("messageService")
public class MessageServiceImpl implements MessageService {

    private static final Log logger = LogFactory.getLog(MessageServiceImpl.class);
    private EntityManager em;

    @Override
    @PersistenceContext
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
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