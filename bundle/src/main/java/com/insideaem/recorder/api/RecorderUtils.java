package com.insideaem.recorder.api;

import org.apache.jackrabbit.api.security.user.User;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecorderUtils {
	public static final String PN_RECORDER_ENABLED = "recorderEnabled";
	public static final String PN_RECORDED_CHANGES = "recordedChanges";
	public static final String PN_RECORDER_SESSION_NAME = "recorderSessionName";

	private static Logger logger = LoggerFactory.getLogger(RecorderUtils.class);

	public static RecorderInfo getRecorderInfo(User currentUser) {
		RecorderInfo result = null;
		boolean enabled = false;
		String currentSessionName = null;
		JSONObject recordedChanges = new JSONObject();

		try {
			enabled = currentUser.getProperty(RecorderUtils.PN_RECORDER_ENABLED)[0]
					.getBoolean();
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
		}

		try {
			currentSessionName = currentUser
					.getProperty(RecorderUtils.PN_RECORDER_SESSION_NAME)[0]
					.getString();
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
		}

		try {
			recordedChanges = new JSONObject(
					currentUser.getProperty(RecorderUtils.PN_RECORDED_CHANGES)[0]
							.getString());
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
		}

		result = new RecorderInfo(enabled, currentSessionName, recordedChanges);

		return result;
	}

}
