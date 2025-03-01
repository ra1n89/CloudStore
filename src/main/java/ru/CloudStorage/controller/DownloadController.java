package ru.CloudStorage.controller;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.CloudStorage.models.CustomUser;
import ru.CloudStorage.service.MinioService;

import java.io.InputStream;

@Controller
public class DownloadController {

    @Autowired
    private MinioService minioService;

    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadFile(
            @RequestParam String path) throws Exception {
        // Получаем текущего пользователя
        CustomUser customerUser = (CustomUser) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        Long userId = customerUser.getId();

        // Формируем полный путь к файлу в MinIO
        //String fullPath = "user-" + userId + "-files/" + path;

        // Получаем объект из MinIO
        InputStream inputStream = minioService.downloadFile(userId, path);

        // Получаем имя файла из пути
        String fileName = path.substring(path.lastIndexOf("/") + 1);

        // Настраиваем заголовки для скачивания
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        // Возвращаем файл клиенту
        return ResponseEntity.ok()
                .headers(headers)
                .body(new InputStreamResource(inputStream));
    }
}
