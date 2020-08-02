package com.example.users.user.exceptions;

public class AlreadyExistsException extends Throwable {
    public static final String message = "Already exists!";

    public AlreadyExistsException() {
        super();
    }

    public AlreadyExistsException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return message;
    }

}
