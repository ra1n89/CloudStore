package ru.CloudStorage.exception;

public class MinioFileDownloadException extends Throwable {
    public MinioFileDownloadException() {
        super();
    }

    public MinioFileDownloadException(String message) {
        super(message);
    }

    public MinioFileDownloadException(String message, Throwable cause) {
        super(message, cause);
    }

    public MinioFileDownloadException(Throwable cause) {
        super(cause);
    }

}
