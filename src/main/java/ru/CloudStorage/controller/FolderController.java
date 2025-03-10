package ru.CloudStorage.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.CloudStorage.exception.MinioFolderCreateException;
import ru.CloudStorage.models.CustomUser;
import ru.CloudStorage.service.MinioService;

@Controller
public class FolderController {

    private final MinioService minioService;

    @Autowired
    public FolderController(MinioService minioService) {
        this.minioService = minioService;
    }

    @PostMapping("/create-folder")
    public String createFolder(@RequestParam("currentPath") String currentPath, @RequestParam("folderName") String folderName) throws MinioFolderCreateException, Exception {

        CustomUser customerUser = (CustomUser) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        Long userId = customerUser.getId();
        System.out.println("userId: " + userId);
        minioService.createFolder(userId, folderName, currentPath);

        return "redirect:/success?message=Folder created successfully!";

    }
}
