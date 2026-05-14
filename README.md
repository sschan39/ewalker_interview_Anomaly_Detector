# ewalker_interview_Anomaly_Detector

Pure Java anomaly detector starter with JSON-driven backend tests and a WebSocket front end.

## What is included

- A detector that uses repeated-pattern checks and Shannon entropy.
- A JSON scenario file for backend testing at `test-data/anomaly-cases.json`.
- JUnit tests without Maven.
- A simple WebSocket server that feeds detector results to a web browser.
- A plain HTML page at `static/index.html` to display results.

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
