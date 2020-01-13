package com.king.backend.core.exceptions;
public class AuthorizationException extends Exception {
    private static final long serialVersionUID = 5163433612347123492L;
    public static final String AUTHORIZATION_ERROR = "Authorization Error";
    public AuthorizationException() {
        super(AUTHORIZATION_ERROR);
    }
    public AuthorizationException(String msg) {
        super(msg);
    }
}
