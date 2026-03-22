package com.medicalinventory.common.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Event published to the audit topic for audit trail tracking.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AuditEvent extends BaseEvent {

    private String entityType;
    private String entityId;
    private String action;      // CREATE, UPDATE, DELETE, READ
    private String oldValue;    // JSON string of previous state (nullable for CREATE)
    private String newValue;    // JSON string of new state (nullable for DELETE)
    private String ipAddress;
}
