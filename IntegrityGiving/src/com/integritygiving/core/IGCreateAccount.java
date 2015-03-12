package com.integritygiving.core;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.integritygiving.configuration.Configuration;
import com.integritygiving.logger.IGLogger;

public class IGCreateAccount extends AsyncTask<String, String, String> {
	String setValue = null;

	// Get request to get Offer list from server.
	@Override
	protected String doInBackground(String... params) {
		try {
			String url = Configuration.IG_BASE_URL
					+ Configuration.IG_CREATE_ACCOUNT;
			IGLogger.d(this, "url " + url);
			DefaultHttpClient client = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			JSONObject jsonObject = new JSONObject();
			// jsonObject.put("username", params[0]);
			jsonObject.put("username", params[0]);
			jsonObject.put("appkey", params[1]);
			StringEntity body = new StringEntity(jsonObject.toString());
			httpPost.setEntity(body);
			IGLogger.d(this, "" + jsonObject.toString());
			HttpResponse response = client.execute(httpPost);
			org.apache.http.HttpEntity entity = response.getEntity();
			setValue = getASCIIContentFromEntity(entity);
			System.out.println("setValue: " + setValue);
		} catch (Exception e) {
			e.printStackTrace();
			e.getMessage();
		}

		return setValue;
	}

	protected String getASCIIContentFromEntity(org.apache.http.HttpEntity entity)
			throws IllegalStateException, IOException {
		InputStream in = entity.getContent();
		StringBuffer out = new StringBuffer();
		int n = 1;
		while (n > 0) {
			byte[] b = new byte[4096];
			n = in.read(b);
			if (n > 0)
				out.append(new String(b, 0, n));
		}
		return out.toString();
	}
}
