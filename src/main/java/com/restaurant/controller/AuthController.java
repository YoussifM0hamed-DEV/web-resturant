package com.restaurant.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";  // points to templates/auth/login.html
    }

    @GetMapping({"/", "/register"})
    public String registerPage() {
        return "auth/register";
    }

    
}