package com.ewalker.anomaly;

import com.ewalker.anomaly.detector.AnomalyDetector;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AnomalyScenarioReaderTest {
    @Test
    void readsScenariosFromJsonFile() throws Exception {
        List<AnomalyScenario> scenarios = new AnomalyScenarioReader().read(Path.of("test-data/anomaly-cases.json"));
        AnomalyDetector detector = new AnomalyDetector();

        assertEquals(3, scenarios.size());
        for (AnomalyScenario scenario : scenarios) {
            assertEquals(scenario.expectedAnomaly(), detector.analyze(scenario.input()).anomaly(), scenario.input());
        }
    }
}