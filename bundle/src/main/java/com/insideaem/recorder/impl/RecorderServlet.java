package com.insideaem.recorder.impl;

import java.io.IOException;
import java.util.Iterator;

import javax.jcr.Session;
import javax.jcr.ValueFactory;
import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.insideaem.recorder.api.RecorderInfo;
import com.insideaem.recorder.api.RecorderUtils;

@SlingServlet(extensions = { "json" }, selectors = { "store", "control" }, methods = {
		"GET", "POST" }, resourceTypes = { "insideaem/recorder" }, metatype = false)
public class RecorderServlet extends SlingAllMethodsServlet {

	Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws ServletException,
			IOException {
		String cmd = request.getParameter("cmd");

		try {
			User currentUser = request.getResourceResolver()
					.adaptTo(User.class);

			Session session = request.getResourceResolver().adaptTo(
					Session.class);
			ValueFactory valueFactory = session.getValueFactory();
			boolean enabled = "start".equals(cmd);
			if ("start".equals(cmd) || "stop".equals(cmd)) {
				currentUser.setProperty(RecorderUtils.PN_RECORDER_ENABLED,
						valueFactory.createValue(enabled));

				if (enabled) {
					// Set current recording session name
					String recordingSessionName = request
							.getParameter(RecorderUtils.PN_RECORDER_SESSION_NAME);
					currentUser.setProperty(
							RecorderUtils.PN_RECORDER_SESSION_NAME,
							valueFactory.createValue(recordingSessionName));
				} else {
					currentUser
							.removeProperty(RecorderUtils.PN_RECORDER_SESSION_NAME);
				}
			} else if ("delete".equals(cmd)) {
				JSONObject recordedChanges = RecorderUtils.getRecorderInfo(
						currentUser).getRecordedChanges();
				String recordingSessionName = request
						.getParameter(RecorderUtils.PN_RECORDER_SESSION_NAME);
				recordedChanges.remove(recordingSessionName);

				currentUser.setProperty(RecorderUtils.PN_RECORDED_CHANGES,
						valueFactory.createValue(recordedChanges.toString()));
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	protected void doGet(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws ServletException,
			IOException {

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		try {

			User currentUser = request.getResourceResolver()
					.adaptTo(User.class);
			RecorderInfo recorderInfo = RecorderUtils
					.getRecorderInfo(currentUser);
			JSONArray changes = new JSONArray();

			JSONObject recordedChanges = recorderInfo.getRecordedChanges();
			Iterator<String> keys = recordedChanges.keys();

			while (keys.hasNext()) {
				String sessionName = keys.next();
				JSONArray entries = recordedChanges.getJSONArray(sessionName);
				for (int i = 0; i < entries.length(); i++) {
					JSONObject entry = entries.getJSONObject(i);
					entry.put(RecorderUtils.PN_RECORDER_SESSION_NAME,
							sessionName);

					changes.put(entry);
				}

			}

			JSONObject result = new JSONObject();
			result.put("changes", changes);
			result.put("count", changes.length());
			result.put(RecorderUtils.PN_RECORDER_ENABLED,
					recorderInfo.isEnabled());
			result.put(RecorderUtils.PN_RECORDER_SESSION_NAME,
					recorderInfo.getCurrentSessionName());
			response.getWriter().println(result);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
}
