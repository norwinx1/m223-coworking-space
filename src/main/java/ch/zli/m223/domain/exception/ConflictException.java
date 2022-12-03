package ch.zli.m223.domain.exception;

public class ConflictException extends Exception {
    public ConflictException() {
        super();
    }

    public ConflictException(String msg) {
        super(msg);
    }

    public ConflictException(String msg, Exception e) {
        super(msg, e);
    }
}