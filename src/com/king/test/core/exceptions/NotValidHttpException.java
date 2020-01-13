package com.king.backend.core.exceptions;

public class NotValidHttpException extends Exception {

    private static final long serialVersionUID = 4354356123447123492L;
    public NotValidHttpException() {
        super();
    }
    public NotValidHttpException(String msg) {
        super(msg);
    }
}
