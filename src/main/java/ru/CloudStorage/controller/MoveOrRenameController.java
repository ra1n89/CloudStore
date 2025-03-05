package ru.CloudStorage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.CloudStorage.models.CustomUser;
import ru.CloudStorage.service.MinioService;

@Controller
public class MoveOrRenameController {

    @Autowired
    private MinioService minioService;


    @PostMapping("/rename")
    public String renameFileOrFolder(@RequestParam String path, @RequestParam String newPath, Model model) {
        // Получаем текущего пользователя
        CustomUser customerUser = (CustomUser) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        Long userId = customerUser.getId();

        try {
            minioService.renameFileOrFolder(userId, path, newPath);
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("error", "Error renaming file or folder: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/move")
    public String moveFileOrFolder(@RequestParam String path, @RequestParam String newPath, Model model) {
        // Получаем текущего пользователя
        CustomUser customerUser = (CustomUser) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        Long userId = customerUser.getId();

        try {
            minioService.moveFileOrFolder(userId, path, newPath);
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("error", "Error moving file or folder: " + e.getMessage());
            return "error";
        }
    }
}
