package com.treco.dex.api.infrastructure.telemetry;

public class CorrelationIdContext {
    private static final ThreadLocal<String> correlationId = new ThreadLocal<>();

    public static void setCorrelationId(String id) {
        correlationId.set(id);
    }

    public static String getCorrelationId() {
        return correlationId.get();
    }

    public static void clear() {
        correlationId.remove();
    }
}
