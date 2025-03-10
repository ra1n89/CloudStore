package ru.CloudStorage.exception;

public class MinioFolderCreateException extends Throwable {
    public MinioFolderCreateException() {
        super();
    }

    public MinioFolderCreateException(String message) {
        super(message);
    }

    public MinioFolderCreateException(String message, Throwable cause) {
        super(message, cause);
    }

    public MinioFolderCreateException(Throwable cause) {
        super(cause);
    }
}
