package ru.CloudStorage.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.CloudStorage.exception.DatabaseException;
import ru.CloudStorage.service.UserService;


@Controller
public class RegistrationController {
    private static final Logger log = LoggerFactory.getLogger(RegistrationController.class);

    @Autowired
    private UserService userService;


    @GetMapping("/register")
    public String showRegistrationForm() {
        log.warn("showRegistrationForm  started");
        return "register";
    }

    // Обработка данных регистрации
    @PostMapping("/register")
    public String registerUser(@RequestParam String username, @RequestParam String password) {
        log.warn("Controller registerUser started", username);
        try {
            userService.registerUser(username, password);
            return "redirect:/login";
        } catch (Exception e) {
            throw new DatabaseException("Error registering user");
        }

    }
}
