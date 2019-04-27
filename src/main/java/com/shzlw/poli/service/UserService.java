package com.shzlw.poli.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.shzlw.poli.dao.UserDao;
import com.shzlw.poli.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    /**
     * Key: Session key
     * Value: User
     */
    private static Cache<String, User> SESSION_USER_CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();

    @Autowired
    UserDao userDao;

    public User getSessionCache(String sessionKey) {
        try {
            User user = SESSION_USER_CACHE.get(sessionKey, () -> userDao.findBySessionKey(sessionKey));
            return user;
        } catch (ExecutionException e) {
            return null;
        }
    }

    public void newOrUpdateSessionCache(User user, String oldSessionKey, String newSessionKey) {
        removeFromSessionCache(oldSessionKey);
        SESSION_USER_CACHE.put(newSessionKey, user);
    }

    public void removeFromSessionCache(String sessionKey) {
        if (sessionKey != null) {
            SESSION_USER_CACHE.invalidate(sessionKey);
        }
    }
}
