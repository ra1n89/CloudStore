package ru.CloudStorage.exception;

public class MinioRenameException extends Throwable {
    public MinioRenameException() {
        super();
    }

    public MinioRenameException(String message) {
        super(message);
    }

    public MinioRenameException(String message, Throwable cause) {
        super(message, cause);
    }

    public MinioRenameException(Throwable cause) {
        super(cause);
    }
}
