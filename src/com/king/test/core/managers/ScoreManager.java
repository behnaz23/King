package com.king.backend.core.managers;

import com.king.backend.core.models.HighScore;
import com.king.backend.core.models.UserScore;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ScoreManager {

    private ConcurrentMap<Integer, HighScore> highScoresMap;
    public ScoreManager() {
        highScoresMap = new ConcurrentHashMap<>();
    }
    public synchronized void saveScore(final UserScore userScore, final Integer levelId) {
        HighScore highScore = highScoresMap.get(levelId);
        if (highScore == null) {
            highScore = new HighScore();
            highScoresMap.putIfAbsent(levelId, highScore);
        }
        highScore.add(userScore);
    }

    public HighScore getHighScore(final int levelId) {
        if (!highScoresMap.containsKey(levelId)) {
            return new HighScore();
        }
        return highScoresMap.get(levelId);
    }


}
