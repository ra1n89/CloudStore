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

    public String deleteFile(Long userId, String fullPath) throws Exception {

        try  {
            boolean found =
                    minioClient.bucketExists(BucketExistsArgs.builder().bucket("user-files").build());
            if (!found) {

                minioClient.makeBucket(MakeBucketArgs.builder().bucket("user-files").build());
            } else {
                System.out.println("Bucket 'user-files' already exists.");
            }

            //String fullPath = "user-" + userId + "-files"  + "/" + file.getOriginalFilename();


                    minioClient.removeObject(
                            RemoveObjectArgs.builder()
                                    .bucket(bucketName)
                                    .object(fullPath)
                                    .build());

        } catch (MinioException e) {
            throw new Exception("Error uploading file to MinIO", e);
        }
          return "redirect:/?path=" + currentPath;;
    }

    public void createFolder(Long userId, String folderName) throws Exception {
        try {
            // Формируем путь к папке
            String folderPath = "user-" + userId + "-files"  + "/" + folderName + "/";

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
    public List<String> listUserFiles(Long userId, String path) throws Exception {
        List<String> filesAndFolders = new ArrayList<>();
        try {
            // Формируем префикс для поиска объектов пользователя
            String prefix = "user-" + userId + "-files"  + "/" + path;

            // Получаем список объектов с префиксом
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .prefix(path)
                            .recursive(false) // Не рекурсивно, чтобы видеть папки
                            .build());

            // Собираем список файлов и папок
            for (Result<Item> result : results) {
                Item item = result.get();
                filesAndFolders.add(item.objectName());
            }
        } catch (MinioException e) {
            throw new Exception("Error listing files in MinIO", e);
        }
        return filesAndFolders;
    }


}
