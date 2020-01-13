package com.king.backend.httpserver;

import com.king.backend.core.exceptions.AuthorizationException;
import com.king.backend.core.exceptions.BackEndException;
import com.king.backend.core.exceptions.NotValidHttpException;
import com.king.backend.core.managers.GameManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Map;

public class BackEndHttpHandler implements HttpHandler {

    public static final String PARAMETER_ATTRIBUTE = "parameters";
    public static final String REQUEST_PARAMETER = "request";
    public static final String LEVEL_ID_PARAMETER = "levelid";
    public static final String SESSION_KEY_PARAMETER = "sessionkey";
    public static final String SCORE_PARAMETER = "score";
    public static final String USER_ID_PARAMETER = "userid";
    public static final String LOGIN_REQUEST = "login";
    public static final String SCORE_REQUEST = "score";
    public static final String HIGH_SCORE_LIST_REQUEST = "highscorelist";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_TEXT = "text/plain";

    private final GameManager gameManager;

    public BackEndHttpHandler(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String httpBody = "";
        int httpCode = HttpURLConnection.HTTP_OK;
        Map<String, String> parameters = (Map<String, String>) httpExchange.getAttribute(PARAMETER_ATTRIBUTE);
        String request = parameters.get(REQUEST_PARAMETER);
        try {
            switch (request) {
                case LOGIN_REQUEST:
                    final int userId = Integer.parseInt(parameters.get(USER_ID_PARAMETER));
                    httpBody = gameManager.login(userId);
                    break;
                case SCORE_REQUEST:
                    final String sessionKey = parameters.get(SESSION_KEY_PARAMETER);
                    final int score = Integer.parseInt(parameters.get(SCORE_PARAMETER));
                    final int levelId = Integer.parseInt(parameters.get(LEVEL_ID_PARAMETER));
                    gameManager.score(sessionKey, levelId, score);
                    break;
                case HIGH_SCORE_LIST_REQUEST:
                    final int levelId1 = Integer.parseInt(parameters.get(LEVEL_ID_PARAMETER));
                    httpBody = gameManager.highScoreList(levelId1);
                    break;
                default:
                    throw new NotValidHttpException("Invalid Request.");
            }
        } catch (NumberFormatException ex) {
            httpBody = "Invalid number format.";
            httpCode = HttpURLConnection.HTTP_BAD_REQUEST;
        } catch (NotValidHttpException ex) {
            httpBody = ex.getMessage();
            httpCode = HttpURLConnection.HTTP_NOT_FOUND;
        } catch (AuthorizationException ex) {
            httpBody = ex.getMessage();
            httpCode = HttpURLConnection.HTTP_UNAUTHORIZED;
        } catch (Exception ex) {
            httpBody = BackEndException.GENERIC_ERROR_MESSAGE;
            httpCode = HttpURLConnection.HTTP_INTERNAL_ERROR;
        }
        httpExchange.getResponseHeaders().add(CONTENT_TYPE, CONTENT_TEXT);
        httpExchange.sendResponseHeaders(httpCode, httpBody.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(httpBody.getBytes());
        os.close();
    }
}
