package ru.CloudStorage.service;

import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
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

    public void uploadFile(MultipartFile file, Long userId) throws Exception {
        try (InputStream inputStream = file.getInputStream()) {

            boolean found =
                    minioClient.bucketExists(BucketExistsArgs.builder().bucket("user-files").build());
            if (!found) {

                minioClient.makeBucket(MakeBucketArgs.builder().bucket("user-files").build());
            } else {
                System.out.println("Bucket 'user-files' already exists.");
            }

            String objectPath = "user-" + userId + "-files"  + "/" + file.getOriginalFilename();

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectPath)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());
        } catch (MinioException e) {
            throw new Exception("Error uploading file to MinIO", e);
        }
    }

    public InputStream downloadFile(Long userId, String fullPath) throws Exception {
        InputStream inputStream;
        try  {
            boolean found =
                    minioClient.bucketExists(BucketExistsArgs.builder().bucket("user-files").build());
            if (!found) {

                minioClient.makeBucket(MakeBucketArgs.builder().bucket("user-files").build());
            } else {
                System.out.println("Bucket 'user-files' already exists.");
            }

            //String fullPath = "user-" + userId + "-files"  + "/" + file.getOriginalFilename();

             inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fullPath)
                            .build());

        } catch (MinioException e) {
            throw new Exception("Error uploading file to MinIO", e);
        }
        return inputStream;
    }

    public void deleteFile(Long userId, String fullPath) throws Exception {

        try  {
            boolean found =
                    minioClient.bucketExists(BucketExistsArgs.builder().bucket("user-files").build());
            if (!found) {

                minioClient.makeBucket(MakeBucketArgs.builder().bucket("user-files").build());
            } else {
                System.out.println("Bucket 'user-files' already exists.");
            }

            //String fullPath = "user-" + userId + "-files"  + "/" + file.getOriginalFilename();

            if (fullPath.endsWith("/")) {
                // Удаляем все объекты с этим префиксом
                deleteFolderContents("user-files", fullPath);
            } else {
                // Удаляем конкретный файл
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket("user-files")
                                .object(fullPath)
                                .build());
                System.out.println("File deleted: " + fullPath);
            }

        } catch (MinioException e) {
            throw new Exception("Error uploading file to MinIO", e);
        }

    }

    public void createFolder(Long userId, String folderName, String currentPath) throws Exception {
        try {
            // Формируем путь к папке
            String folderPath = currentPath + "/" + folderName + "/";

            // Создаем пустой объект с именем, заканчивающимся на "/"
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(folderPath)
                            .stream(new ByteArrayInputStream(new byte[0]), 0, -1)
                            .contentType("application/octet-stream")
                            .build());

            System.out.println("Folder created: " + folderPath);
        } catch (MinioException e) {
            throw new Exception("Error creating folder in MinIO", e);
        }
    }

    public void renameFileOrFolder(Long userId, String oldPath, String newPath) throws Exception {
        try {
            // Проверяем, существует ли старый объект
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(oldPath)
                            .build());

            // Копируем объект с новым именем
            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .bucket(bucketName)
                            .object(newPath)
                            .source(CopySource.builder().bucket(bucketName).object(oldPath).build())
                            .build());

            // Удаляем старый объект
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(oldPath)
                            .build());

            System.out.println("Renamed from " + oldPath + " to " + newPath);
        } catch (MinioException e) {
            throw new Exception("Error renaming file or folder in MinIO", e);
        }
    }

    public void moveFileOrFolder(Long userId, String oldPath, String newPath) throws Exception {
        renameFileOrFolder(userId, oldPath, newPath);
    }

    public List<String> listUserFiles(Long userId, String path) throws Exception {



        List<String> filesAndFolders = new ArrayList<>();
        try {
            // Формируем префикс для поиска объектов пользователя
            //String prefix = "user-" + userId + "-files"  + "/" + path;
            String fullPath;
            String userPrefix = "user-" + userId + "-files/";
            if (Objects.equals(path, "")){
                fullPath = userPrefix;
            } else {
                fullPath =  path;
            }


            // Проверяем, что путь начинается с префикса пользователя
            if (!fullPath.startsWith(userPrefix)) {
                throw new SecurityException("Access denied: You can only access your own files.");
            }

            // Получаем список объектов с префиксом
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .prefix(fullPath)
                            .recursive(false) // Не рекурсивно, чтобы видеть папки
                            .build());

            // Собираем список файлов и папок
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
            // Получаем список всех объектов с префиксом
            Iterable<Result<Item>> objects = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .prefix(folderPath)
                            .recursive(true)
                            .build());

            // Удаляем каждый объект
            for (Result<Item> result : objects) {
                Item item = result.get();
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(bucketName)
                                .object(item.objectName())
                                .build());
                System.out.println("Deleted: " + item.objectName());
            }

            System.out.println("Folder deleted: " + folderPath);
        } catch (MinioException e) {
            throw new Exception("Error deleting folder contents from MinIO", e);
        }
    }
}
