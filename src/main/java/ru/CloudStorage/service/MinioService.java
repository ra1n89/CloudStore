package ru.CloudStorage.service;

import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.CloudStorage.exception.*;
import ru.CloudStorage.utils.MinioUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class MinioService {


    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    public void uploadFile(MultipartFile file, Long userId) throws MinioFileUploadException, CustomIoException {
        try (InputStream inputStream = file.getInputStream()) {

            MinioUtils.isRootFolderExists(minioClient);

            String objectPath = "user-" + userId + "-files" + "/" + file.getOriginalFilename();

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectPath)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());
        } catch (MinioException e) {
            throw new MinioFileUploadException("Error uploading file to MinIO: MinioException", e);
        } catch (IOException e) {
            throw new CustomIoException("Error uploading file to MinIO: IOException", e);
        } catch (Exception e) {
            throw new GeneralException("Unknown Error", e);
        }
    }

    public InputStream downloadFile(Long userId, String fullPath) throws MinioFileDownloadException, CustomIoException {
        InputStream inputStream;
        try {
            MinioUtils.isRootFolderExists(minioClient);

            inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fullPath)
                            .build());

        } catch (MinioException e) {
            throw new MinioFileDownloadException("Error uploading file to MinIO: MinioException", e);
        } catch (IOException e) {
            throw new CustomIoException("Error uploading file to MinIO: IOException", e);
        } catch (Exception e) {
            throw new GeneralException("Unknown Error", e);
        }
        return inputStream;
    }

    public void deleteFile(Long userId, String fullPath) throws Exception, MinioFileDeleteException {

        try {
            MinioUtils.isRootFolderExists(minioClient);

            if (fullPath.endsWith("/")) {
                deleteFolderContents("user-files", fullPath);
            } else {
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket("user-files")
                                .object(fullPath)
                                .build());
                System.out.println("File deleted: " + fullPath);
            }

        } catch (MinioException e) {
            throw new MinioFileDeleteException("Error deleting file or folder from MinIO: MinioException", e);
        } catch (Exception e) {
            throw new GeneralException("Unknown Error", e);
        }
    }

    public void createFolder(Long userId, String folderName, String currentPath) throws Exception, MinioFolderCreateException {
        try {
            MinioUtils.isRootFolderExists(minioClient);

            String folderPath = currentPath + "/" + folderName + "/";

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(folderPath)
                            .stream(new ByteArrayInputStream(new byte[0]), 0, -1)
                            .contentType("application/octet-stream")
                            .build());

            System.out.println("Folder created: " + folderPath);
        } catch (MinioException e) {
            throw new MinioFolderCreateException("Error creating folder in MinIO: MinioException", e);
        } catch (Exception e) {
            throw new GeneralException("Unknown Error", e);
        }
    }

    public void renameFileOrFolder(Long userId, String oldPath, String newPath) throws Exception, MinioRenameException {
        try {
            MinioUtils.isRootFolderExists(minioClient);

            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(oldPath)
                            .build());

            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .bucket(bucketName)
                            .object(newPath)
                            .source(CopySource.builder().bucket(bucketName).object(oldPath).build())
                            .build());

            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(oldPath)
                            .build());

            System.out.println("Renamed from " + oldPath + " to " + newPath);
        } catch (MinioException e) {
            throw new MinioRenameException("Error renaming item in MinIO: MinioException", e);
        } catch (Exception e) {
            throw new GeneralException("Unknown Error", e);
        }
    }

    public void moveFileOrFolder(Long userId, String oldPath, String newPath) throws Exception, MinioRenameException {
        renameFileOrFolder(userId, oldPath, newPath);
    }

    public List<String> listUserFiles(Long userId, String path) throws Exception {

        List<String> filesAndFolders = new ArrayList<>();
        try {
            String fullPath;
            String userPrefix = "user-" + userId + "-files/";
            if (Objects.equals(path, "")) {
                fullPath = userPrefix;
            } else {
                fullPath = path;
            }

            if (!fullPath.startsWith(userPrefix)) {
                throw new SecurityException("Access denied: You can only access your own files.");
            }

            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .prefix(fullPath)
                            .recursive(false)
                            .build());

            for (Result<Item> result : results) {
                Item item = result.get();
                filesAndFolders.add(item.objectName().substring(userPrefix.length()));
            }
        } catch (MinioException e) {
            throw new Exception("Error listing files in MinIO", e);
        }
        return filesAndFolders;
    }

    private void deleteFolderContents(String bucketName, String folderPath) throws Exception {
        try {
            Iterable<Result<Item>> objects = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .prefix(folderPath)
                            .recursive(true)
                            .build());

            for (Result<Item> result : objects) {
                Item item = result.get();
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(bucketName)
                                .object(item.objectName())
                                .build());
            }

        } catch (MinioException e) {
            throw new Exception("Error deleting folder contents from MinIO", e);
        }
    }
}
