package com.king.backend.core.models;

import java.io.Serializable;

public class UserScore implements Serializable, Comparable<UserScore> {

    private int userId;
    private int score;
    public UserScore(int userId, int score) {
        this.userId = userId;
        this.score = score;
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public int getScore() {
        return score;
    }
    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserScore userScore = (UserScore) o;
        if (userId != userScore.userId)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        return userId;
    }

    @Override
    public int compareTo(UserScore o) {
        int compare = Integer.compare(o.score, this.score);
        //for the HighScore Set, if the both scores are equals, the oldest is the greatest
        if (compare == 0 && Integer.compare(o.userId, this.userId) != 0)
            compare = 1;
        return compare;
    }

    @Override
    public String toString() {
        return userId + "=" + score;
    }
}
