package ru.CloudStorage.exception;

import io.minio.errors.MinioException;

public class MinioFileUploadException extends Throwable {
    public MinioFileUploadException() {
        super();
    }

    public MinioFileUploadException(String message) {
        super(message);
    }

    public MinioFileUploadException(String message, Throwable cause) {
        super(message, cause);
    }

    public MinioFileUploadException(Throwable cause) {
        super(cause);
    }
}
