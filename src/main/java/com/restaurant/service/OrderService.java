package com.restaurant.service;

import com.restaurant.model.*;
import com.restaurant.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final MenuItemRepository menuItemRepository;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository, MenuItemRepository menuItemRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.menuItemRepository = menuItemRepository;
    }

    @Transactional
    public Order placeOrder(String userEmail, Map<Long, Integer> itemQuantities, String notes) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = new Order();
        order.setUser(user);
        order.setNotes(notes);

        for (Map.Entry<Long, Integer> entry : itemQuantities.entrySet()) {
            if (entry.getValue() <= 0) continue;
            MenuItem menuItem = menuItemRepository.findById(entry.getKey())
                .orElseThrow(() -> new RuntimeException("Item not found"));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setMenuItem(menuItem);
            orderItem.setQuantity(entry.getValue());
            orderItem.setUnitPrice(BigDecimal.valueOf(menuItem.getPrice()));
            order.getItems().add(orderItem);
        }

        if (order.getItems().isEmpty()) {
            throw new RuntimeException("Cannot place an empty order");
        }

        order.calculateTotal();
        return orderRepository.save(order);
    }

    public List<Order> getOrdersByUser(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return orderRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc();
    }

    public Order getById(Long id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Transactional
    public Order updateStatus(Long orderId, Order.Status status) {
        Order order = getById(orderId);
        order.setStatus(status);
        return orderRepository.save(order);
    }

    @Transactional
    public void cancelOrder(Long orderId, String userEmail) {
        Order order = getById(orderId);
        if (!order.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("Not authorized");
        }
        if (order.getStatus() != Order.Status.PENDING) {
            throw new RuntimeException("Only pending orders can be cancelled");
        }
        order.setStatus(Order.Status.CANCELLED);
        orderRepository.save(order);
    }
}