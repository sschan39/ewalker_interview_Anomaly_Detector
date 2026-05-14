package com.ewalker.anomaly;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class AnomalyScenarioReader {
    private static final Pattern OBJECT_PATTERN = Pattern.compile("\\{([^}]*)\\}");
    private static final Pattern INPUT_PATTERN = Pattern.compile("\"input\"\\s*:\\s*\"((?:\\\\.|[^\"])*)\"");
    private static final Pattern ANOMALY_PATTERN = Pattern.compile("\"anomaly\"\\s*:\\s*(true|false)");

    public List<AnomalyScenario> read(Path path) throws IOException {
        String content = Files.readString(path);
        List<AnomalyScenario> scenarios = new ArrayList<>();

        Matcher objectMatcher = OBJECT_PATTERN.matcher(content);
        while (objectMatcher.find()) {
            String objectText = objectMatcher.group(1);
            String input = extractInput(objectText);
            boolean expectedAnomaly = extractAnomaly(objectText);
            scenarios.add(new AnomalyScenario(input, expectedAnomaly));
        }

        return scenarios;
    }

    private String extractInput(String objectText) {
        Matcher matcher = INPUT_PATTERN.matcher(objectText);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Missing input field in JSON scenario");
        }

        return unescape(matcher.group(1));
    }

    private boolean extractAnomaly(String objectText) {
        Matcher matcher = ANOMALY_PATTERN.matcher(objectText);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Missing anomaly field in JSON scenario");
        }

        return Boolean.parseBoolean(matcher.group(1));
    }

    private String unescape(String value) {
        return value.replace("\\\\", "\\").replace("\\\"", "\"");
    }
}