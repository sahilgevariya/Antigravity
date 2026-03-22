package com.medicalinventory.inventory.event;

import com.medicalinventory.common.constant.AppConstants;
import com.medicalinventory.common.event.StockAlertEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Publishes stock-related events to Kafka.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StockEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Publish a low-stock alert event when product drops below min level.
     */
    public void publishLowStockAlert(Long productId, String productName, String sku,
                                      int currentQuantity, int minStockLevel, String locationName) {
        StockAlertEvent event = StockAlertEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("STOCK_ALERT")
                .occurredAt(LocalDateTime.now())
                .sourceService("inventory-service")
                .productId(productId)
                .productName(productName)
                .sku(sku)
                .currentQuantity(currentQuantity)
                .minStockLevel(minStockLevel)
                .locationName(locationName)
                .build();

        kafkaTemplate.send(AppConstants.TOPIC_STOCK_ALERT, sku, event);
        log.warn("Low stock alert published: {} (qty: {}, min: {})", productName, currentQuantity, minStockLevel);
    }
}
