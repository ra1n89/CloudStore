package ru.CloudStorage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.CloudStorage.models.CustomUser;
import ru.CloudStorage.service.MinioService;

import java.util.List;

@Controller
public class HomeController {

    private final MinioService minioService;

    @Autowired
    public HomeController(MinioService minioService) {
        this.minioService = minioService;
    }

    @GetMapping("/")
    public String homePage(Model model, @RequestParam(required = false, defaultValue = "") String path) {
        System.out.println(path);
        CustomUser customerUser = (CustomUser) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        Long userId = customerUser.getId();
        System.out.println("userId: " + userId);

        try {
            List<String> filesAndFolders = minioService.listUserFiles(userId, path);
            model.addAttribute("filesAndFolders", filesAndFolders);
            model.addAttribute("currentPath", path);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error loading files: " + e.getMessage());

        }
        return "home";
    }
}
