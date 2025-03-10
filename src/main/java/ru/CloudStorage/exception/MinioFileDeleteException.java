package ru.CloudStorage.exception;

public class MinioFileDeleteException extends Throwable {
    public MinioFileDeleteException() {
        super();
    }

    public MinioFileDeleteException(String message) {
        super(message);
    }

    public MinioFileDeleteException(String message, Throwable cause) {
        super(message, cause);
    }

    public MinioFileDeleteException(Throwable cause) {
        super(cause);
    }

}
