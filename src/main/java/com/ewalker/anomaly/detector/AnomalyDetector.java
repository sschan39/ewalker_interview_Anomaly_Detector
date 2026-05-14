package com.ewalker.anomaly.detector;

public class AnomalyDetector {
    private final double entropyThreshold;

    public AnomalyDetector() {
        this(3.0);
    }

    public AnomalyDetector(double entropyThreshold) {
        this.entropyThreshold = entropyThreshold;
    }

    public DetectionResult analyze(String message) {
        String normalized = message == null ? "" : message.trim();
        if (normalized.isEmpty()) {
            return new DetectionResult(true, "empty message", 0.0);
        }

        double entropy = calculateEntropy(normalized);
        if (hasRepeatedPattern(normalized)) {
            return new DetectionResult(true, "repeated pattern", entropy);
        }

        if (entropy < entropyThreshold) {
            return new DetectionResult(true, "low entropy", entropy);
        }

        return new DetectionResult(false, "normal", entropy);
    }

    double calculateEntropy(String value) {
        int[] counts = new int[Character.MAX_VALUE + 1];
        int length = value.length();

        for (int index = 0; index < length; index++) {
            counts[value.charAt(index)]++;
        }

        double entropy = 0.0;
        for (int count : counts) {
            if (count == 0) {
                continue;
            }

            double probability = (double) count / length;
            entropy -= probability * (Math.log(probability) / Math.log(2));
        }

        return entropy;
    }

    boolean hasRepeatedPattern(String value) {
        String stripped = value.replaceAll("\\s+", "");
        int length = stripped.length();

        if (length < 4) {
            return false;
        }

        for (int patternLength = 1; patternLength <= length / 2; patternLength++) {
            if (length % patternLength != 0) {
                continue;
            }

            String pattern = stripped.substring(0, patternLength);
            if (pattern.repeat(length / patternLength).equals(stripped)) {
                return true;
            }
        }

        return false;
    }
}