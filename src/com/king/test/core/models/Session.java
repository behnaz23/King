package com.king.backend.core.models;

import java.io.Serializable;
public class Session implements Serializable {

    private Integer userId;
    private String sessionKey;
    private long createdTime;
    public Session(Integer userId, String sessionKey, long createdTime) {
        this.userId = userId;
        this.sessionKey = sessionKey;
        this.createdTime = createdTime;
    }
    public Integer getUserId() {
        return userId;
    }
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Session session = (Session) o;
        if (createdTime != session.createdTime)
            return false;
        if (sessionKey != null ? !sessionKey.equals(session.sessionKey) : session.sessionKey != null)
            return false;
        if (!userId.equals(session.userId))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = userId.hashCode();
        result = 31 * result + (sessionKey != null ? sessionKey.hashCode() : 0);
        result = 31 * result + (int) (createdTime ^ (createdTime >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return sessionKey;
    }
}
