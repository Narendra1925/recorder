package com.insideaem.recorder.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jcr.Session;
import javax.jcr.ValueFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.apache.sling.servlets.post.Modification;
import org.apache.sling.servlets.post.SlingPostProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.PageManager;
import com.insideaem.recorder.api.RecorderInfo;
import com.insideaem.recorder.api.RecorderUtils;

@Service
@Component(immediate = true)
public class ContentChangeRecorder implements SlingPostProcessor {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	public void process(SlingHttpServletRequest request,
			List<Modification> modifications) throws Exception {
		// Build a list of modified paths
		Set<String> modifiedPaths = new HashSet<String>();
		String resourcePath = request.getResource().getPath();

		if (isPage(resourcePath, request)) {
			modifiedPaths.add(getPagePath(resourcePath));
		}

		for (Modification modification : modifications) {
			String sourcePath = modification.getSource();
			if (sourcePath != null && isPage(sourcePath, request)) {
				modifiedPaths.add(getPagePath(sourcePath));
			}

			String destinationPath = modification.getDestination();
			if (destinationPath != null && isPage(destinationPath, request)) {
				modifiedPaths.add(getPagePath(destinationPath));
			}
		}

		User currentUser = request.getResourceResolver().adaptTo(User.class);
		RecorderInfo recorderInfo = RecorderUtils.getRecorderInfo(currentUser);

		if (recorderInfo.isEnabled()
				&& StringUtils.isNotBlank(recorderInfo.getCurrentSessionName())) {
			String currentSessionName = recorderInfo.getCurrentSessionName();
			JSONObject recordedChanges = recorderInfo.getRecordedChanges();

			if (!recordedChanges.has(currentSessionName)) {
				recordedChanges.put(currentSessionName, new JSONArray());
			}

			JSONArray changesArray = recordedChanges
					.getJSONArray(currentSessionName);
			for (String modifiedPath : modifiedPaths) {
				addChangeToGroup(changesArray, modifiedPath);
			}

			Session session = request.getResourceResolver().adaptTo(
					Session.class);
			ValueFactory valueFactory = session.getValueFactory();

			currentUser.setProperty(RecorderUtils.PN_RECORDED_CHANGES,
					valueFactory.createValue(recordedChanges.toString()));
		}

	}

	private void addChangeToGroup(JSONArray records, String modifiedPath)
			throws JSONException {
		boolean alreadyAdded = false;
		for (int i = 0; i < records.length(); i++) {
			JSONObject record = records.getJSONObject(i);
			if (StringUtils.equalsIgnoreCase(modifiedPath,
					record.getString("path"))) {
				alreadyAdded = true;
				// Update timestamp
				record.put("timestamp", System.currentTimeMillis());
				break;
			}
		}

		if (!alreadyAdded) {
			JSONObject newRecord = new JSONObject();
			newRecord.put("path", modifiedPath);
			newRecord.put("timestamp", System.currentTimeMillis());
			records.put(newRecord);
		}

	}

	private static String getPagePath(String path) {
		return StringUtils.isNotBlank(path) && path.contains("/jcr:content") ? StringUtils
				.substringBefore(path, "jcr:content") : path;
	}

	private static boolean isPage(String path, SlingHttpServletRequest request) {

		boolean result = false;
		if (StringUtils.isNotBlank(path)) {
			PageManager pageManager = request.getResourceResolver().adaptTo(
					PageManager.class);
			result = pageManager.getContainingPage(path) != null;
		}

		return result;
	}

}
