package com.alexarkhipov.vkrenamer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NeoVK {
	private static final Logger logger = LoggerFactory.getLogger(NeoVK.class);

	private static final String LOG_MESSAGE = "'{}' is set to '{}'";

	private final String url;

	private static final String ACCESS_TOKEN = "access_token";
	private static final String OWNER_ID = "owner_id";
	private static final String SEND_MESSAGE = "message";
	private static final String SERVICES = "services";

	private final boolean test;
	private final Map<String, String> data;

	public NeoVK(String url, String accesstoken, String ownerid) {
		this(url, accesstoken, ownerid, false);
		logger.debug("{} constructor 1 is called", this.getClass().getName());
	}

	public NeoVK(String url, String accesstoken, String ownerid, boolean test) {
		this(url, accesstoken, ownerid, "", test);
		logger.debug("{} constructor 2 is called", this.getClass().getName());
	}

	public NeoVK(String url, String accesstoken, String ownerid, String services, boolean test) {
		logger.debug("{} constructor 3 is called", this.getClass().getName());
		this.test = test;
		data = new HashMap<>();
		this.url = url;
		logger.debug(LOG_MESSAGE, "URL", url);
		setAssessToken(accesstoken);
		setOwnerId(ownerid);
		setServices(services);
	}

	public void setServices(String s) {
		logger.debug(LOG_MESSAGE, SERVICES, s);
		data.put(SERVICES, s);
	}

	public void setMessage(String m) {
		logger.debug(LOG_MESSAGE, SEND_MESSAGE, m);
		data.put(SEND_MESSAGE, m);
	}

	public void setAssessToken(String at) {
		logger.debug(LOG_MESSAGE, ACCESS_TOKEN, at);
		data.put(ACCESS_TOKEN, at);
	}

	public void setOwnerId(String oi) {
		logger.debug(LOG_MESSAGE, OWNER_ID, oi);
		data.put(OWNER_ID, oi);
	}

	public void publishPost(NeoManagePost m, NeoPost p) {
		String s = m.getPostContent(p);
		publishPost(s);
	}

	public void publishPost(String content) {
		if (test) {
			logger.info("Testing mode: skipping publishing...");
			return;
		}

		setMessage(content);

		String response = sendRequest();
		Integer postId;
		try {
			postId = getPostId(response);
			logger.info("Posted successfully. Post ID = {}", postId);
		} catch (JSONException e) {
			logger.error("Something wrong occured during posting. Cannot parse json object: {}.", response);
			logger.error(e.getMessage());
		}

	}

	private int getPostId(String json) throws JSONException {
		JSONObject obj = new JSONObject(json);
		return obj.getJSONObject("response").getInt("post_id");

	}

	private List<NameValuePair> makeParams() {
		// Request parameters and other properties.
		List<NameValuePair> params = new ArrayList<>(data.size());
		for (Map.Entry<String, String> entry : data.entrySet()) {
			if (!entry.getValue().isEmpty()) {
				logger.debug("Values added: {} : {}", entry.getKey(), entry.getValue());
				params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
		}
		return params;
	}

	private String sendRequest() {
		StringBuilder s = new StringBuilder();
		try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
			HttpPost httppost = new HttpPost(url);

			List<NameValuePair> params = makeParams();
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			// Execute and get the response.
			HttpResponse response = httpclient.execute(httppost);

			HttpEntity entity = response.getEntity();

			if (entity != null) {
				InputStreamReader in = new InputStreamReader(entity.getContent());
				BufferedReader br = new BufferedReader(in);
				String line;
				s = new StringBuilder();
				while ((line = br.readLine()) != null) {
					s.append(line);
				}
				br.close();
				logger.info("Response received - {} bytes: {}", s.length(), s);
			}
		} catch (IOException ex1) {
			logger.error("Error: {}", ex1.getMessage());
		}

		return s.toString();
	}

}
