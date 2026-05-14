package com.ewalker.anomaly;

import com.ewalker.anomaly.detector.AnomalyDetector;
import com.ewalker.anomaly.detector.DetectionResult;

import java.net.http.WebSocket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public final class AnomalyWebSocketClient implements WebSocket.Listener {
    private final AnomalyDetector detector;

    public AnomalyWebSocketClient(AnomalyDetector detector) {
        this.detector = detector;
    }

    @Override
    public void onOpen(WebSocket webSocket) {
        webSocket.request(1);
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        DetectionResult result = detector.analyze(data.toString());
        System.out.println(toJson(result));
        webSocket.request(1);
        return CompletableFuture.completedFuture(null);
    }

    private String toJson(DetectionResult result) {
        return "{" +
                "\"anomaly\":" + result.anomaly() + ',' +
                "\"reason\":\"" + escape(result.reason()) + "\"," +
                "\"entropy\":" + result.entropy() +
                '}';
    }

    private String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}