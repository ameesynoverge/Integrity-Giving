package com.integritygiving.server.communication;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class ServerCommunicationManager {

	private final int METHOD_GET = 1;
	private final int METHOD_POST = 2;

	public void sendRequest(String url, int methodType) {
		// Create client and set our specific user-agent string
		HttpClient client = new DefaultHttpClient();
		HttpUriRequest request = getUriRequest(url, methodType);

		// new HttpGet("http://stackoverflow.com/opensearch.xml");
		request.setHeader("User-Agent", "set your desired User-Agent");

		try {
			HttpResponse response = client.execute(request);

			// Check if server response is valid
			StatusLine status = response.getStatusLine();
			if (status.getStatusCode() != 200) {
				throw new IOException("Invalid response from server: "
						+ status.toString());
			}

			// Pull content stream from response
			HttpEntity entity = response.getEntity();
			InputStream inputStream = entity.getContent();

			ByteArrayOutputStream content = new ByteArrayOutputStream();

			// Read response into a buffered stream
			int readBytes = 0;
			byte[] sBuffer = new byte[512];
			while ((readBytes = inputStream.read(sBuffer)) != -1) {
				content.write(sBuffer, 0, readBytes);
			}
			String data = content.toString();
			Log.d("tag", "response = " + response);
			// Return result from buffered stream

		} catch (IOException e) {
			Log.d("error", e.getLocalizedMessage());
		}
	}

	private HttpUriRequest getUriRequest(String url, int methodType) {
		HttpUriRequest httpUriRequest = null;
		switch (methodType) {
		case METHOD_GET:
			httpUriRequest = new HttpGet(url);
			break;
		case METHOD_POST:
			httpUriRequest = new HttpPost(url);
			break;

		}
		return httpUriRequest;
	}
}
