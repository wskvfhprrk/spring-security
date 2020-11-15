package com.hejz.springbootstartersecurity.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    private String hello(){
        return "<h1>欢迎</h1>";
    }
}
