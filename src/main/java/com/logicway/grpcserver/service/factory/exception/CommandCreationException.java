package com.logicway.grpcserver.service.factory.exception;

public class CommandCreationException extends Exception {

    public CommandCreationException() {
        super();
    }

    public CommandCreationException(String message) {
        super(message);
    }

    public CommandCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommandCreationException(Throwable cause) {
        super(cause);
    }
}
