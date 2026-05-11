package com.restaurant.controller;

import com.restaurant.model.MenuItem;
import com.restaurant.model.Order;
import com.restaurant.service.MenuService;
import com.restaurant.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final MenuService menuService;
    private final OrderService orderService;

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("totalOrders", orderService.getAllOrders().size());
        model.addAttribute("totalItems", menuService.getAll().size());
        model.addAttribute("recentOrders", orderService.getAllOrders().stream().limit(5).toList());
        return "admin/dashboard";
    }

    @PostMapping("/menu/save")
    public String saveItem(@ModelAttribute MenuItem item, RedirectAttributes ra) {
        menuService.save(item);
        ra.addFlashAttribute("success", "Menu item saved!");
        return "redirect:/admin/menu";
    }

    @PostMapping("/orders/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam Order.Status status, RedirectAttributes ra) {
        orderService.updateStatus(id, status);
        ra.addFlashAttribute("success", "Order #" + id + " updated to " + status);
        return "redirect:/admin/orders";
    }
}   