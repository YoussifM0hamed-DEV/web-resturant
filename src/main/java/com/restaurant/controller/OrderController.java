package com.restaurant.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.restaurant.service.MenuService;
import com.restaurant.service.OrderService;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final MenuService menuService;

    public OrderController(MenuService menuService, OrderService orderService) {
        this.menuService = menuService;
        this.orderService = orderService;
    }

    @GetMapping("/new")
    public String newOrder(Model model) {
        model.addAttribute("menuByCategory", menuService.getMenuGroupedByCategory());
        return "order/new-order";
    }

    @PostMapping("/place")
    public String placeOrder(@RequestParam Map<String, String> params,
                            @RequestParam(value = "notes", required = false) String notes,
                            Authentication auth, RedirectAttributes ra) {
        try {
            Map<Long, Integer> quantities = new HashMap<>();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (entry.getKey().startsWith("item_")) {
                    Long itemId = Long.parseLong(entry.getKey().substring(5));
                    int qty = Integer.parseInt(entry.getValue());
                    if (qty > 0) quantities.put(itemId, qty);
                }
            }
            var order = orderService.placeOrder(auth.getName(), quantities, notes);
            ra.addFlashAttribute("success", "Order #" + order.getId() + " placed successfully!");
            return "redirect:/orders/my";
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/orders/new";
        }
    }

    @GetMapping("/my")
    public String myOrders(Model model, Authentication auth) {
        model.addAttribute("orders", orderService.getOrdersByUser(auth.getName()));
        return "order/my-orders";
    }

    @GetMapping("/{id}")
    public String orderDetail(@PathVariable Long id, Model model, Authentication auth) {
        var order = orderService.getById(id);
        boolean isAdmin = auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !order.getUser().getEmail().equals(auth.getName())) {
            return "redirect:/orders/my";
        }
        model.addAttribute("order", order);
        return "order/order-detail";
    }

    @PostMapping("/{id}/cancel")
    public String cancelOrder(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        try {
            orderService.cancelOrder(id, auth.getName());
            ra.addFlashAttribute("success", "Order cancelled.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/orders/my";
    }
}