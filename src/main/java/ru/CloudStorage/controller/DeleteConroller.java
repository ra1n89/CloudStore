package ru.CloudStorage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.CloudStorage.exception.MinioFileDeleteException;
import ru.CloudStorage.models.CustomUser;
import ru.CloudStorage.service.MinioService;

@Controller
public class DeleteConroller {

    @Autowired
    private MinioService minioService;

    @GetMapping("/delete")
    public String deleteFileOrFolder(
            @RequestParam String path) throws Exception, MinioFileDeleteException {
        CustomUser customerUser = (CustomUser) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        Long userId = customerUser.getId();
        String userPrefix = "user-" + userId + "-files/";

        String fullPath = path;
        minioService.deleteFile(userId, fullPath);

        return "redirect:/?path=" + userPrefix;
    }
}
