package ru.CloudStorage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.CloudStorage.exception.CustomIoException;
import ru.CloudStorage.exception.MinioFileDownloadException;
import ru.CloudStorage.models.CustomUser;
import ru.CloudStorage.service.MinioService;

import java.io.InputStream;

@Controller
public class DownloadController {

    @Autowired
    private MinioService minioService;

    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadFile(
            @RequestParam String path) throws Exception, MinioFileDownloadException, CustomIoException {

        CustomUser customerUser = (CustomUser) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        Long userId = customerUser.getId();

        InputStream inputStream = minioService.downloadFile(userId, path);

        String fileName = path.substring(path.lastIndexOf("/") + 1);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .headers(headers)
                .body(new InputStreamResource(inputStream));
    }
}
