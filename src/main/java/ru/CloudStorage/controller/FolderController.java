package ru.CloudStorage.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public String createFolder(@RequestParam("currentPath") String currentPath, @RequestParam("folderName") String folderName) {

        CustomUser customerUser = (CustomUser) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        Long userId = customerUser.getId(); // Получаем ID пользователя
        System.out.println("userId: " + userId);

        try {


            // Вызываем метод создания папки
            minioService.createFolder(userId, folderName, currentPath);

            // Перенаправляем пользователя на страницу с сообщением об успехе
            return "redirect:/success?message=Folder created successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            // Перенаправляем пользователя на страницу с сообщением об ошибке
            return "redirect:/error?message=Error creating folder: " + e.getMessage();
        }
    }
}

