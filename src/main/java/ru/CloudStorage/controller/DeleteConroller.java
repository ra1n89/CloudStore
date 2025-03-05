package ru.CloudStorage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.CloudStorage.models.CustomUser;
import ru.CloudStorage.service.MinioService;

@Controller
public class DeleteConroller {

    @Autowired
    private MinioService minioService;
    @GetMapping("/delete")
    public String deleteFileOrFolder(
            @RequestParam String path) throws Exception {
        // Получаем текущего пользователя
        CustomUser customerUser = (CustomUser) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        Long userId = customerUser.getId();
        String userPrefix = "user-" + userId + "-files/";

        // Формируем полный путь к объекту в MinIO
        String fullPath = path;

        // Удаляем объект (файл или папку)
       
        minioService.deleteFile(userId, fullPath);


        // Перенаправляем пользователя на текущую страницу
        return "redirect:/?path=" + userPrefix ;
    }
}
