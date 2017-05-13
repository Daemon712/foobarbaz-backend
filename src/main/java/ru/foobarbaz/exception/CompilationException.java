package ru.foobarbaz.exception;

public class CompilationException extends RuntimeException {
    private String className;

    public CompilationException(String className, String message) {
        super(message);
        this.className = className;
    }

    public String getClassName() {
        return className;
    }
}
