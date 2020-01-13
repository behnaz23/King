package com.king.backend.core.managers;

import com.king.backend.core.models.Session;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SessionManager {
    public static final int TIME_TO_LIVE = 600000;
    private final ConcurrentMap<String, Session> sessionActives;
    public SessionManager() {
        sessionActives = new ConcurrentHashMap<>();
    }
    public synchronized Session createNewSession(final Integer userId) {
        final long now = System.currentTimeMillis();
        final String newSessionKey = UUID.randomUUID().toString().replace("-", "");
        final Session session = new Session(userId, newSessionKey, now);
        sessionActives.put(newSessionKey, session);
        return session;
    }
    public synchronized boolean isSessionValid(final String sessionKey) {
        Session sessionActive = sessionActives.get(sessionKey);
        if (sessionActive != null) {
            if ((System.currentTimeMillis() - sessionActive.getCreatedTime()) > TIME_TO_LIVE) {
                sessionActives.remove(sessionKey);
                return false;
            } else {
                return true;
            }
        }
        return false;
    }
    public Session getSessionActive(final String sessionKey) {
        return sessionActives.get(sessionKey);
    }

    public synchronized void removeInvalidSessions() {
        long now = System.currentTimeMillis();
        for (Session session : new ArrayList<>(sessionActives.values())) {
            if ((now - session.getCreatedTime()) > TIME_TO_LIVE) {
                sessionActives.remove(session.getSessionKey());
            }
        }
    }
}
