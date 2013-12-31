package com.insideaem.recorder.api;

import org.apache.sling.commons.json.JSONObject;

public class RecorderInfo {

	private final boolean enabled;
	private final String currentSessionName;
	private final JSONObject recordedChanges;

	public RecorderInfo(boolean enabled, String currentSessionName,
			JSONObject recordedChanges) {
		this.enabled = enabled;
		this.currentSessionName = currentSessionName;
		this.recordedChanges = recordedChanges;
	}

	public JSONObject getRecordedChanges() {
		return recordedChanges;
	}

	public String getCurrentSessionName() {
		return currentSessionName;
	}

	public boolean isEnabled() {
		return enabled;
	}

}
