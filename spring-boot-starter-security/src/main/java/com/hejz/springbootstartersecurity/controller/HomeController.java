package com.hejz.springbootstartersecurity.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    private String hello(){
        return "<h1>欢迎</h1>";
    }
    @GetMapping("/admin")
    private String admin(){
        return "<h1>欢迎 管理员</h1>";
    }
    @GetMapping("/user")
    private String user(){
        return "<h1>欢迎 用户</h1>";
    }
}
