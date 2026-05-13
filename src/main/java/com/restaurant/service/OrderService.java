package com.restaurant.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurant.model.MenuItem;
import com.restaurant.model.Order;
import com.restaurant.model.OrderItem;
import com.restaurant.model.User;
import com.restaurant.repository.MenuItemRepository;
import com.restaurant.repository.OrderRepository;
import com.restaurant.repository.UserRepository;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final MenuItemRepository menuItemRepository;
    private final N8nWebhookService n8nWebhookService;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository, MenuItemRepository menuItemRepository, N8nWebhookService n8nWebhookService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.menuItemRepository = menuItemRepository;
        this.n8nWebhookService = n8nWebhookService;
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
        Order savedOrder = orderRepository.save(order);

        // Send order notification to n8n webhook without interrupting the normal order flow.
        notifyN8nWebhook(savedOrder);
        return savedOrder;
    }

    private void notifyN8nWebhook(Order order) {
        try {
            n8nWebhookService.sendOrderNotification(order);
        } catch (Exception e) {
            logger.error("Unable to send n8n order notification for order {}", order.getId(), e);
        }
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