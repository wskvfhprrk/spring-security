package com.hejz.springsecurityjdbc.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    /**
     * 只有user和admim角色可以访问到
     * @return
     */
    @RequestMapping("/user")
    public String user(){
        return "<h1>你好，user</h1>";
    }

    /**
     * 只有admin可以访问到
     * @return
     */
    @RequestMapping("/admin")
    public String admin(){
        return "<h1>你好，admin</h1>";
    }

    /**
     * 任何人都可以访问
     * @return
     */
    @RequestMapping("/")
    public String hello(){
        return "<h1>你好</h1>";
    }
}
