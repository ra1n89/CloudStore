package ru.CloudStorage.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.CloudStorage.exception.CustomIoException;
import ru.CloudStorage.exception.MinioFileUploadException;
import ru.CloudStorage.models.CustomUser;
import ru.CloudStorage.service.MinioService;

@RestController
public class UploadController {

    @Autowired
    private MinioService minioService;

    @PostMapping("/upload")
    public ResponseEntity uploadFile(@RequestParam("file") MultipartFile file) throws MinioFileUploadException, CustomIoException {

        CustomUser customerUser = (CustomUser) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        Long userId = customerUser.getId();
        System.out.println("userId: " + userId);

        minioService.uploadFile(file, userId);
        return ResponseEntity.ok("{\"message\": \"File uploaded successfully!\"}");
    }
}

