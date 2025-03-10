package ru.CloudStorage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.CloudStorage.exception.MinioRenameException;
import ru.CloudStorage.models.CustomUser;
import ru.CloudStorage.service.MinioService;

@Controller
public class MoveOrRenameController {

    @Autowired
    private MinioService minioService;


    @PostMapping("/rename")
    public String renameFileOrFolder(@RequestParam String path, @RequestParam String newPath, Model model) throws MinioRenameException, Exception {
        CustomUser customerUser = (CustomUser) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        Long userId = customerUser.getId();

        minioService.renameFileOrFolder(userId, path, newPath);
        return "redirect:/";
    }

    @PostMapping("/move")
    public String moveFileOrFolder(@RequestParam String path, @RequestParam String newPath, Model model) throws MinioRenameException, Exception {
        CustomUser customerUser = (CustomUser) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        Long userId = customerUser.getId();

        minioService.moveFileOrFolder(userId, path, newPath);
        return "redirect:/";
    }
}
