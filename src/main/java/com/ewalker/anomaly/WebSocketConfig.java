package com.ewalker.anomaly;

import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;

public final class WebSocketConfig {
    private final URI endpoint;
    private final Duration connectTimeout;

    public WebSocketConfig(URI endpoint, Duration connectTimeout) {
        this.endpoint = endpoint;
        this.connectTimeout = connectTimeout;
    }

    public static WebSocketConfig defaultConfig() {
        return new WebSocketConfig(URI.create("ws://localhost:8080/ws/anomaly"), Duration.ofSeconds(10));
    }

    public URI endpoint() {
        return endpoint;
    }

    public Duration connectTimeout() {
        return connectTimeout;
    }

    public HttpClient createHttpClient() {
        return HttpClient.newBuilder()
                .connectTimeout(connectTimeout)
                .build();
    }
}