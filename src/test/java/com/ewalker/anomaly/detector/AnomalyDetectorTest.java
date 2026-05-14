package com.ewalker.anomaly.detector;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AnomalyDetectorTest {
    private final AnomalyDetector detector = new AnomalyDetector();

    @Test
    void flagsRepeatedPatternAsAnomaly() {
        DetectionResult result = detector.analyze("abcabcabcabc");

        assertTrue(result.anomaly());
        assertTrue(result.reason().contains("pattern"));
    }

    @Test
    void flagsLowEntropyAsAnomaly() {
        DetectionResult result = detector.analyze("aaaaabaaaaa");

        assertTrue(result.anomaly());
    }

    @Test
    void acceptsVariedTextAsNormal() {
        DetectionResult result = detector.analyze("server health check message 42");

        assertFalse(result.anomaly());
    }
}