package com.zxxk.exception;

/**
 * Created by shiti on 17-4-20.
 */
public class ClassificationException extends RuntimeException {

    public ClassificationException(String message) {
        super(message);
    }

    public ClassificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
