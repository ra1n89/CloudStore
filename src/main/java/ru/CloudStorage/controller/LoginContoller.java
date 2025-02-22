package ru.CloudStorage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class First {

    @ResponseBody
    @GetMapping("/first")
    public String hello() {
        return "Hello, from FirstController!";
    }

}
