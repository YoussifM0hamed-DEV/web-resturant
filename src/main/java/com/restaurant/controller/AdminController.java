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

    @GetMapping("/menu")
    public String manageMenu(Model model) {
        model.addAttribute("items", menuService.getAll());
        return "admin/manage-menu";
    }

    @GetMapping("/menu/new")
    public String newItemForm(Model model) {
        model.addAttribute("item", new MenuItem());
        return "admin/menu-form";
    }

    @GetMapping("/menu/edit/{id}")
    public String editItemForm(@PathVariable Long id, Model model) {
        model.addAttribute("item", menuService.getById(id));
        return "admin/menu-form";
    }

    @PostMapping("/menu/save")
    public String saveItem(@ModelAttribute MenuItem item, RedirectAttributes ra) {
        menuService.save(item);
        ra.addFlashAttribute("success", "Menu item saved!");
        return "redirect:/admin/menu";
    }

    @PostMapping("/menu/delete/{id}")
    public String deleteItem(@PathVariable Long id, RedirectAttributes ra) {
        menuService.delete(id);
        ra.addFlashAttribute("success", "Item deleted.");
        return "redirect:/admin/menu";
    }

    @PostMapping("/menu/toggle/{id}")
    public String toggleItem(@PathVariable Long id, RedirectAttributes ra) {
        var item = menuService.toggleAvailability(id);
        ra.addFlashAttribute("success", item.getName() + " is now " + (item.isAvailable() ? "available" : "unavailable"));
        return "redirect:/admin/menu";
    }

    @GetMapping("/orders")
    public String manageOrders(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        model.addAttribute("statuses", Order.Status.values());
        return "admin/manage-orders";
    }

    @PostMapping("/orders/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam Order.Status status, RedirectAttributes ra) {
        orderService.updateStatus(id, status);
        ra.addFlashAttribute("success", "Order #" + id + " updated to " + status);
        return "redirect:/admin/orders";
    }
}