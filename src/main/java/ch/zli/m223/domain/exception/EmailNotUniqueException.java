package ch.zli.m223.domain.exception;

public class EmailNotUniqueException extends Exception {
    public EmailNotUniqueException() {
        super();
    }

    public EmailNotUniqueException(String msg) {
        super(msg);
    }

    public EmailNotUniqueException(String msg, Exception e) {
        super(msg, e);
    }
}