package ru.foobarbaz.exception;

public class DeniedOperationException extends RuntimeException {
    public DeniedOperationException() {
    }

    public DeniedOperationException(String message) {
        super(message);
    }
}
