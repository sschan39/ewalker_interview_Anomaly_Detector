package com.ewalker.anomaly.detector;

public final class DetectionResult {
	private final boolean anomaly;
	private final String reason;
	private final double entropy;

	public DetectionResult(boolean anomaly, String reason, double entropy) {
		this.anomaly = anomaly;
		this.reason = reason;
		this.entropy = entropy;
	}

	public boolean anomaly() {
		return anomaly;
	}

	public String reason() {
		return reason;
	}

	public double entropy() {
		return entropy;
	}
}