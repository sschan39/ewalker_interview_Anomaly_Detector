package com.ewalker.anomaly;

import com.ewalker.anomaly.detector.AnomalyDetector;
import com.ewalker.anomaly.server.SimpleWebSocketServer;

import java.nio.file.Path;
import java.util.List;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length > 0 && "server".equals(args[0])) {
            startWebSocketServer();
        } else {
            runDetectorDemo(args.length > 0 ? args[0] : "test-data/anomaly-cases.json");
        }
    }

    private static void startWebSocketServer() throws Exception {
        SimpleWebSocketServer server = new SimpleWebSocketServer(8080);
        server.start();
        System.out.println("WebSocket server started. Open static/index.html in your browser.");
        System.out.println("Press Ctrl+C to stop.");
        Thread.currentThread().join();
    }

    private static void runDetectorDemo(String scenarioPath) throws Exception {
        Path scenarioFile = Path.of(scenarioPath);
        AnomalyScenarioReader reader = new AnomalyScenarioReader();
        AnomalyDetector detector = new AnomalyDetector();

        List<AnomalyScenario> scenarios = reader.read(scenarioFile);
        for (AnomalyScenario scenario : scenarios) {
            var result = detector.analyze(scenario.input());
            System.out.println(scenario.input() + " -> " + result.anomaly() + " (expected " + scenario.expectedAnomaly() + ")");
        }
    }
}