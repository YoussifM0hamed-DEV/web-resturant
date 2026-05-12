package com.restaurant.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // Added this import
import org.springframework.web.bind.annotation.GetMapping;

import com.restaurant.service.MenuService;

@Controller
public class MenuController {
    
    private final MenuService menuService;

    // Added Constructor for Dependency Injection
    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping("/menu")
    public String menu(Model model) {
        model.addAttribute(
            "menuByCategory",
            menuService.getMenuGroupedByCategory()
        );
        return "menu/menu";
    }
}
