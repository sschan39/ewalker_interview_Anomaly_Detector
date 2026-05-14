# ewalker_interview_Anomaly_Detector

Pure Java anomaly detector starter with JSON-driven backend tests and a WebSocket front end.

## What is included

- **Extensible rule-based detector** with pluggable detection rules:
  - SQL Injection detection (`1=1`, `OR 1=1`, `DROP`, etc.)
  - Admin keyword detection (`admin`, `root`, `password`, etc.)
  - Repeated pattern detection
  - Shannon entropy-based detection
- A JSON scenario file for backend testing at `test-data/anomaly-cases.json`.
- JUnit tests without Maven.
- A simple WebSocket server that feeds detector results to a web browser.
- A plain HTML page at `static/index.html` to display results.

## Architecture

The detector uses a **composite rule pattern** where detection logic is pluggable:

```java
AnomalyDetector detector = new AnomalyDetector();
detector.addRule(new CustomRule()); // Add custom rule
```

### Creating a custom detection rule

Implement the `DetectionRule` interface:

```java
public class MyCustomRule implements DetectionRule {
    @Override
    public RuleResult evaluate(String input) {
        if (input.contains("suspicious")) {
            return new RuleResult(true, "Suspicious input detected", 0.9);
        }
        return new RuleResult(false, "", 0.0);
    }
}
```

Then add it to the detector:

```java
detector.addRule(new MyCustomRule());
```

## Run tests

```bash
chmod +x scripts/test.sh
./scripts/test.sh
```

## Run the detector demo from JSON

```bash
javac -d .build/classes $(find src/main/java -name '*.java')
java -cp .build/classes com.ewalker.anomaly.Main test-data/anomaly-cases.json
```

## Run the WebSocket server

```bash
javac -d .build/classes $(find src/main/java -name '*.java')
java -cp .build/classes com.ewalker.anomaly.Main server
```

Then open `static/index.html` in your browser and send text to analyze.

**Debug logs** are written to `logs/anomaly-detector.log`.
