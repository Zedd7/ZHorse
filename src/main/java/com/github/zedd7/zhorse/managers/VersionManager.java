package com.github.zedd7.zhorse.managers;

import com.github.zedd7.zhorse.ZHorse;
import com.google.gson.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class VersionManager {

	private static final String API_URL = "https://api.spiget.org/v2/resources/3384/versions";

	ZHorse zh;

	public VersionManager(ZHorse zh) {
		this.zh = zh;
	}

	public void checkForUpdates() {
		new BukkitRunnable() {

			@Override
			public void run() {
				String currentVersionDescription = zh.getDescription().getVersion();
				String[] currentVersionParameters = currentVersionDescription.split(" ");
				String currentVersionName = currentVersionParameters[0];
				boolean isCurrentVersionSnapshot = currentVersionParameters.length > 1;

				try {
					LinkedHashMap<String, Date> versionDates = getVersionDates();
					String latestVersionName = versionDates.keySet().iterator().next(); // First key
					Date latestVersionDate = versionDates.get(latestVersionName);
					Date currentVersionDate = versionDates.get(currentVersionName);
					if (latestVersionDate.after(currentVersionDate) || (latestVersionDate.equals(currentVersionDate) && isCurrentVersionSnapshot)) {
						String updateAvailableMessage = String.format(
								"This server is still running ZHorse %s. Please update to ZHorse %s (released on %s).",
								currentVersionDescription, latestVersionName, new SimpleDateFormat("MMM'' dd, yyyy").format(latestVersionDate)
						);
						zh.getLogger().warning(updateAvailableMessage);
					}
				} catch (Exception e) {}
			}

		}.runTaskLaterAsynchronously(zh, 10 * 20); // 10 seconds
	}

	private LinkedHashMap<String, Date> getVersionDates() {
		LinkedHashMap<String, Date> versionDates = new LinkedHashMap<>(); // Keep versions in insertion order
		JsonArray resourceVersions = new JsonArray();
		int pageNumber = 1;
		do try {
			resourceVersions = getResourceVersions(pageNumber);
			for (JsonElement resourceVersion : resourceVersions) {
				if (!resourceVersion.isJsonObject()) {
					String errorMessage = "Resource version is not a json object";
					throw new JsonSyntaxException(new Exception(errorMessage));
				}
				JsonObject version = resourceVersion.getAsJsonObject();
				String versionName = version.get("name").getAsString();
				long releaseTimestamp = version.get("releaseDate").getAsLong() * 1000; // To milliseconds
				Date releaseDate = new Date(releaseTimestamp);
				versionDates.put(versionName, releaseDate);
			}
			pageNumber++;
		} catch (Exception e) {
			zh.getLogger().warning("Encountered an issue when attempting to check for a new version.");
			zh.getLogger().warning(e.getMessage());
		} while (resourceVersions.size() > 0);
		return versionDates;
	}

	private JsonArray getResourceVersions(int pageNumber) throws Exception {
		Map<String, String> payloadParameters = new HashMap<String, String>() {{
			put("fields", buildFieldsString(Arrays.asList("name", "releaseDate")));
			put("sort", "-name");
			put("page", String.valueOf(pageNumber));
		}};
		URL requestURL = buildRequestURL(API_URL, payloadParameters);
		HttpURLConnection connection = (HttpURLConnection) requestURL.openConnection();
		if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
			String errorMessage = "Response code : " + connection.getResponseCode();
			throw new JsonSyntaxException(new Exception(errorMessage));
		}
		InputStream inputStream = connection.getInputStream();
		InputStreamReader reader = new InputStreamReader(inputStream);
		JsonElement element = new JsonParser().parse(reader);
		if (!element.isJsonArray()) {
			String errorMessage = "Response is not a json array";
			throw new JsonSyntaxException(new Exception(errorMessage));
		}
		return element.getAsJsonArray();
	}

	private String buildFieldsString(List<String> fieldList) {
		String fieldsString = "";
		for (String field : fieldList) {
			if (!fieldsString.isEmpty()) {
				fieldsString += "%2C";
			}
			fieldsString += field;
		}
		return fieldsString;
	}

	private URL buildRequestURL(String apiURL, Map<String, String> payloadParameters) throws MalformedURLException {
		String payload = "";
		for (Map.Entry<String, String> payloadParameter : payloadParameters.entrySet()) {
			if (!payload.isEmpty()) {
				payload += "&";
			}
			String parameter = payloadParameter.getKey();
			String value = payloadParameter.getValue();
			payload += parameter + "=" + value;
		}
		String requestURL = apiURL + "?" + payload;
		return new URL(requestURL);
	}
}
