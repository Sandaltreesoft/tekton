package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/telegram/messages")
public class MyCotroller {

    @GetMapping
    public String get() {

        return "Message from Sonar foso!";
    }
}
