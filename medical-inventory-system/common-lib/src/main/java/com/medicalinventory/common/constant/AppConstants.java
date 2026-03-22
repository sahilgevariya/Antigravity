package com.medicalinventory.common.constant;

/**
 * Application-wide constants shared across all microservices.
 */
public final class AppConstants {

    private AppConstants() {
        // Prevent instantiation
    }

    // ── API Versioning ──────────────────────────────────────────────
    public static final String API_V1 = "/api/v1";

    // ── Pagination Defaults ─────────────────────────────────────────
    public static final String DEFAULT_PAGE = "0";
    public static final String DEFAULT_PAGE_SIZE = "20";
    public static final int MAX_PAGE_SIZE = 100;

    // ── Kafka Topics ────────────────────────────────────────────────
    public static final String TOPIC_STOCK_ALERT = "stock.alert";
    public static final String TOPIC_STOCK_MOVEMENT = "stock.movement";
    public static final String TOPIC_ORDER_CREATED = "order.created";
    public static final String TOPIC_ORDER_APPROVED = "order.approved";
    public static final String TOPIC_PAYMENT_COMPLETED = "payment.completed";
    public static final String TOPIC_PAYMENT_FAILED = "payment.failed";
    public static final String TOPIC_AUDIT_EVENT = "audit.event";

    // ── Cache Keys Prefixes ─────────────────────────────────────────
    public static final String CACHE_PRODUCT_PREFIX = "product:";
    public static final String CACHE_CATEGORY_PREFIX = "category:";
    public static final String CACHE_STOCK_PREFIX = "stock:";
    public static final String CACHE_LOW_STOCK_KEY = "low-stock-alerts";

    // ── Cache TTL (seconds) ─────────────────────────────────────────
    public static final int CACHE_TTL_SHORT = 300;       // 5 minutes
    public static final int CACHE_TTL_MEDIUM = 600;      // 10 minutes
    public static final int CACHE_TTL_LONG = 3600;       // 1 hour

    // ── Security / JWT ──────────────────────────────────────────────
    public static final String AUTH_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_PHARMACIST = "PHARMACIST";
    public static final String ROLE_DOCTOR = "DOCTOR";
    public static final String ROLE_SUPPLIER = "SUPPLIER";

    // ── Trace / MDC ─────────────────────────────────────────────────
    public static final String TRACE_ID = "traceId";
    public static final String SPAN_ID = "spanId";
    public static final String USER_ID = "userId";
    public static final String REQUEST_ID = "requestId";
}
