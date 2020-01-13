package com.king.backend.core.managers;

import com.king.backend.core.exceptions.AuthorizationException;
import com.king.backend.core.models.HighScore;
import com.king.backend.core.models.Session;
import com.king.backend.core.models.UserScore;
import com.king.backend.core.schedulers.SessionCleanerScheduler;

public class GameManager {

    public volatile static GameManager instance;
    private final ScoreManager scoreManager;
    private final SessionManager sessionManager;
    private final SessionCleanerScheduler sessionCleaner;
    public GameManager() {
        scoreManager = new ScoreManager();
        sessionManager = new SessionManager();
        sessionCleaner = new SessionCleanerScheduler(sessionManager);
        sessionCleaner.startService();
    }

    public static GameManager getInstance() {
        if (instance == null) {
            synchronized (GameManager.class) {
                if (instance == null) {
                    instance = new GameManager();
                }
            }
        }
        return instance;
    }

    public String login(int userId) {
        Session session = sessionManager.createNewSession(userId);
        return session.getSessionKey();
    }

    public void score(String sessionKey, int levelId, int score) throws AuthorizationException {
        if (!sessionManager.isSessionValid(sessionKey)) {
            throw new AuthorizationException();
        }
        Session session = sessionManager.getSessionActive(sessionKey);
        if (session == null) {
            throw new AuthorizationException();
        }
        UserScore userScore = new UserScore(session.getUserId(), score);
        scoreManager.saveScore(userScore, levelId);
    }
    public String highScoreList(int levelId) {
        HighScore highScore = scoreManager.getHighScore(levelId);
        return highScore.toString();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
}
