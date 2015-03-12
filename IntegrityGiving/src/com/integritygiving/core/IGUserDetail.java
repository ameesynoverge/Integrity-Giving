package com.integritygiving.core;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;

import com.integritygiving.configuration.Configuration;
import com.integritygiving.logger.IGLogger;

public class IGUserDetail extends AsyncTask<String, String, String> {
	String setValue = null;

	// Get request to get Offer list from server.
	@Override
	protected String doInBackground(String... params) {
		try {
			String url = Configuration.IG_BASE_URL + Configuration.IG_USER
					+ params[0] + "/" + Configuration.IG_DETAILS;
			IGLogger.d(this, "url " + url);
			DefaultHttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(url);
			HttpResponse response = client.execute(httpGet);
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
