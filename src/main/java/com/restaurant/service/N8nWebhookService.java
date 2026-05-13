package com.restaurant.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.restaurant.model.Order;
import com.restaurant.model.OrderItem;

@Service
public class N8nWebhookService {

    private static final Logger logger = LoggerFactory.getLogger(N8nWebhookService.class);
    private final RestClient restClient;
    private final String webhookUrl;

    public N8nWebhookService(@Value("${n8n.webhook.url}") String webhookUrl) {
        this.restClient = RestClient.create();
        this.webhookUrl = webhookUrl;
    }

    /**
     * Send a JSON payload to the configured n8n webhook after order placement.
     * Uses existing order and customer information.
     */
    public void sendOrderNotification(Order order) {
        if (webhookUrl == null || webhookUrl.isBlank()) {
            logger.warn("n8n webhook URL is not configured; skipping notification for order {}", order.getId());
            return;
        }

        OrderNotificationPayload payload = new OrderNotificationPayload(
            order.getUser().getUsername(),
            order.getUser().getEmail(),
            "",
            "",
            order.getItems().stream().map(this::serializeItem).toList(),
            order.getTotal() == null ? 0.0 : order.getTotal().doubleValue()
        );

        try {
            restClient.post()
                .uri(webhookUrl)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(payload)
                .retrieve()
                .body(String.class);
            logger.info("Sent n8n webhook notification for order {}", order.getId());
        } catch (RestClientException ex) {
            logger.error("Failed to send order notification to n8n webhook for order {}", order.getId(), ex);
            // Do not fail order placement if webhook delivery fails.
        }
    }

    private ItemPayload serializeItem(OrderItem item) {
        return new ItemPayload(
            item.getMenuItem().getName(),
            item.getQuantity(),
            item.getUnitPrice() == null ? 0.0 : item.getUnitPrice().doubleValue(),
            item.getSubtotal() == null ? 0.0 : item.getSubtotal().doubleValue()
        );
    }

    private static record OrderNotificationPayload(
        String customerName,
        String email,
        String phone,
        String address,
        List<ItemPayload> items,
        double totalPrice
    ) {
    }

    private static record ItemPayload(
        String name,
        int quantity,
        double unitPrice,
        double subtotal
    ) {
    }
}
