package ru.CloudStorage.utils;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import lombok.experimental.UtilityClass;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@UtilityClass
public class MinioUtils {



    public static void isRootFolderExists(MinioClient minioClient) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        boolean found =
                minioClient.bucketExists(BucketExistsArgs.builder().bucket("user-files").build());
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket("user-files").build());
        }
    }

}
