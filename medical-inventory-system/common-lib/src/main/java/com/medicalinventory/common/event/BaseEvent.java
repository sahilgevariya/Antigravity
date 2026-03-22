package com.medicalinventory.common.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * Base class for all Kafka domain events.
 * Every event published to Kafka should extend this class.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEvent {

    private String eventId;
    private String eventType;
    private LocalDateTime occurredAt;
    private String performedBy;
    private String traceId;

    /**
     * The source service that produced this event.
     * e.g., "inventory-service", "order-service"
     */
    private String sourceService;
}
