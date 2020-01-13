package com.king.backend.httpserver;

import com.king.backend.core.exceptions.BackEndException;
import com.king.backend.core.exceptions.NotValidHttpException;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
public class BackEndHttpFilter extends Filter {

    private static final String LOGIN_PATTERN = "/(\\d*)/login";
    private static final String SCORE_PATTERN = "/(\\d*)/score\\?sessionkey=(.*)";
    private static final String HIGH_SCORE_LIST_PATTERN = "/(\\d*)/highscorelist";

    @Override
    public void doFilter(HttpExchange httpExchange, Chain chain) throws IOException {
        try {
            Map<String, String> parameters;
            String uri = httpExchange.getRequestURI().toString();
            if (uri.matches(LOGIN_PATTERN)) {
                parameters = parseLoginParameters(httpExchange);
            } else if (uri.matches(SCORE_PATTERN)) {
                parameters = parseScoreParameters(httpExchange);
            } else if (uri.matches(HIGH_SCORE_LIST_PATTERN)) {
                parameters = parseHighScoreListParameters(httpExchange);
            } else {
                throw new NotValidHttpException("Invalid URL.");
            }
            //Pass the parameter to the BackEndHttpHandler
            httpExchange.setAttribute(BackEndHttpHandler.PARAMETER_ATTRIBUTE, parameters);
            chain.doFilter(httpExchange);
        } catch (NotValidHttpException ex) {
            exceptionHandledResponse(ex.getMessage(), httpExchange, HttpURLConnection.HTTP_NOT_FOUND);
        } catch (Exception ex) {
            exceptionHandledResponse(ex.getMessage(), httpExchange, HttpURLConnection.HTTP_BAD_REQUEST);
        }
    }

    private static Map<String, String> parseLoginParameters(HttpExchange httpExchange)
            throws NotValidHttpException {
        validHttpMethod(httpExchange, "GET");
        Map<String, String> parameters = new HashMap<>();
        String uri = httpExchange.getRequestURI().toString();
        String userId = uri.split("/")[1];
        parameters.put(BackEndHttpHandler.REQUEST_PARAMETER, BackEndHttpHandler.LOGIN_REQUEST);
        parameters.put(BackEndHttpHandler.USER_ID_PARAMETER, userId);
        return parameters;
    }

    private static Map<String, String> parseScoreParameters(HttpExchange httpExchange)
            throws NotValidHttpException, BackEndException {
        validHttpMethod(httpExchange, "POST");
        Map<String, String> parameters = new HashMap<>();
        String uri = httpExchange.getRequestURI().toString();
        String[] uriStrings = uri.split("/");
        String levelId = uriStrings[1];
        String[] paramsStrings = uriStrings[2].split(BackEndHttpHandler.SESSION_KEY_PARAMETER + "=");
        String sessionKey = paramsStrings[1];
        String score;
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(httpExchange.getRequestBody(), "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            try {
                score = bufferedReader.readLine();
            } finally {
                bufferedReader.close();
                inputStreamReader.close();
            }
        } catch (Exception ex) {
            throw new BackEndException(ex.getMessage());
        }
        parameters.put(BackEndHttpHandler.REQUEST_PARAMETER, BackEndHttpHandler.SCORE_REQUEST);
        parameters.put(BackEndHttpHandler.LEVEL_ID_PARAMETER, levelId);
        parameters.put(BackEndHttpHandler.SESSION_KEY_PARAMETER, sessionKey);
        parameters.put(BackEndHttpHandler.SCORE_PARAMETER, score);
        return parameters;
    }

    private static Map<String, String> parseHighScoreListParameters(HttpExchange httpExchange)
            throws NotValidHttpException {
        validHttpMethod(httpExchange, "GET");
        Map<String, String> parameters = new HashMap<>();
        String uri = httpExchange.getRequestURI().toString();
        String levelId = uri.split("/")[1];
        parameters.put(BackEndHttpHandler.REQUEST_PARAMETER, BackEndHttpHandler.HIGH_SCORE_LIST_REQUEST);
        parameters.put(BackEndHttpHandler.LEVEL_ID_PARAMETER, levelId);
        return parameters;
    }

    private static void validHttpMethod(HttpExchange httpExchange, String method)
            throws NotValidHttpException {
        if (!method.equalsIgnoreCase(httpExchange.getRequestMethod())) {
            throw new NotValidHttpException("Method '" + httpExchange.getRequestMethod() + "' is not supported.");
        }
    }

    private void exceptionHandledResponse(String message, HttpExchange httpExchange, int statusCode)
            throws IOException {
        if (message == null || message.isEmpty())
            message = BackEndException.GENERIC_ERROR_MESSAGE;
        httpExchange.sendResponseHeaders(statusCode, message.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(message.getBytes());
        os.close();
    }

    @Override
    public String description() {
        return "Manage the Http Requests.";
    }
}
