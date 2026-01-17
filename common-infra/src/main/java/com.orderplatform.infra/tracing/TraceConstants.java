package com.orderplatform.infra.tracing;

public final class TraceConstants {

    private TraceConstants() {}

    public static final String TRACE_ID_MDC_KEY = "traceId";
    public static final String TRACE_ID_HEADER = "X-Trace-Id";
}
