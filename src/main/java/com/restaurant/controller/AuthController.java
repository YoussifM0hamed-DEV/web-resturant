package com.restaurant.controller;

import com.restaurant.model.User;
import com.restaurant.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService; 

     // Show login page
    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";  // points to templates/auth/login.html
    }
    
    @GetMapping({"/", "/register"})
    public String registerPage(Model model) {
        model.addAttribute("user" , new User());
        return "auth/register";
    }
     
    
    @PostMapping("/register")
    public String register(@ModelAttribute User user, RedirectAttributes ra) {
        try {
            userService.register(user);
            ra.addFlashAttribute("success", "Registration successful! Please log in.");
            return "redirect:/login";
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }

}