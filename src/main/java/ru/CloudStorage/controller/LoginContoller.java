package ru.CloudStorage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginContoller {


    @GetMapping ("/login")
    public String loginPage() {

        return "login";
    }
}
