package com.restaurant.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.restaurant.controller.DTO.RegisterDTO;
import com.restaurant.model.User;
import com.restaurant.service.UserService;

import jakarta.validation.Valid;

@Controller
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    } 

     // Show login page
    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";  // points to templates/auth/login.html
    }
    
    @GetMapping({"/", "/register"})
    public String registerPage(Model model) {
        model.addAttribute("registerDTO", new RegisterDTO());
        return "auth/register";
    }
         
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerDTO") RegisterDTO registerDTO, 
                          BindingResult result, RedirectAttributes ra) {
        if (result.hasErrors()) {
            return "auth/register";
        }
        
        try {
            User user = new User();
            user.setUsername(registerDTO.getUsername());
            user.setPassword(registerDTO.getPassword());
            user.setEmail(registerDTO.getEmail());
            userService.register(user);
            ra.addFlashAttribute("success", "Registration successful! Please log in.");
            return "redirect:/login";
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }

}