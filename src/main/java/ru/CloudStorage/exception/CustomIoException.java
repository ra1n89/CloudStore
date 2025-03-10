package ru.CloudStorage.exception;

public class CustomIoException extends Throwable {
    public CustomIoException() {
        super();
    }

    public CustomIoException(String message) {
        super(message);
    }

    public CustomIoException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomIoException(Throwable cause) {
        super(cause);
    }

}
