package notification;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;



/******************************************************************************* 
 * ___________________________________________________________________________ 
 * 
 * OOHExchange.com CONFIDENTIAL 
 * ____________________________ 
 * 
 * Copyright 2014 OOHExchange.com   All Rights Reserved. 
 * 
 * NOTICE:  All information contained herein is, and remains 
 * the property of OOHExchange.com The intellectual 
 * and technical concepts contained herein are proprietary to OOHExchange.com 
 * and may be covered by U.S. and Foreign Patents, 
 * patents in process, and are protected by trade secret or copyright law. 
 * Dissemination of this information or reproduction of this material 
 * is strictly forbidden unless prior written permission is obtained 
 * from OOHExchange.com 
 * ___________________________________________________________________________ 
 * 
 *******************************************************************************/



public class ServiceHandler {

	static HttpResponse response = null;
	public final static int GET = 1;
	public final static int POST = 2;
	static String Response = null;

	public ServiceHandler() {

	}

	/*
	 * Making service call
	 * @url - url to make request
	 * @method - http request method
	 * */
	public String makeServiceCall(String url, int method) {
		return this.makeServiceCallOriginal(url, method, null);
	}
	
	public String makeServiceCallForAllData(String url, int method) {
		return this.makeServiceCallOriginalData(url, method, null);
	}
	
	public String makeServiceCallForSubmit(String url, int method,JSONObject object) {
		return this.makeServiceCallSubmit(url, method, object);
	}
	
	
	public String makeServiceCallOriginalData(String url, int method,
			List<NameValuePair> params) {
		try {
			// http client
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpEntity httpEntity = null;
			HttpResponse httpResponse = null;
			
			// Checking http request method type
			 if (method == GET) {
				// appending params to url  
				if (params != null) {
					String paramString = URLEncodedUtils
							.format(params, "utf-8");        
					url += "?" + paramString;
				}
				HttpGet httpGet = new HttpGet(url);      
				//httpGet.setHeader("Authorization", "bearer c6f4ae5e-adb1-41b9-9e89-6cd8fdab7407");
				//httpGet.setHeader("Authorization", "bearer "+variable.Berar_Value);

				httpResponse = httpClient.execute(httpGet);

			}
			httpEntity = httpResponse.getEntity();
			Response = EntityUtils.toString(httpEntity);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return Response;

	}
	
	public String makeServiceCallOriginal(String url, int method,
			List<NameValuePair> params) {
		try {
			// http client
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpEntity httpEntity = null;
			HttpResponse httpResponse = null;
			
			// Checking http request method type
			 if (method == GET) {
				// appending params to url  
				if (params != null) {
					String paramString = URLEncodedUtils
							.format(params, "utf-8");        
					url += "?" + paramString;
				}
				HttpGet httpGet = new HttpGet(url);   
				//httpGet.setHeader("username", "oohexadmin@gmail.com");
				//httpGet.setHeader("password", "Abcd@1234");
					

				httpResponse = httpClient.execute(httpGet);

			}
			httpEntity = httpResponse.getEntity();
			Response = EntityUtils.toString(httpEntity);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return Response;

	}
	
	
	public String makeServiceCallSubmit(String url, int method,
			JSONObject object) {
				
		try {
			// http client
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpEntity httpEntity = null;
			HttpResponse httpResponse = null;
			
			// Checking http request method type
			if (method == POST) {
				HttpPost httpPost = new HttpPost(url);   
				httpPost.setHeader("Content-type", "application/json");
		        StringEntity se = new StringEntity(object.toString()); 
		        se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
		        httpPost.setEntity(se); 
				httpResponse = httpClient.execute(httpPost);

			} 
			httpEntity = httpResponse.getEntity(); 
			Response = EntityUtils.toString(httpEntity);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return Response;

	
   
	}
	
	public String makeServiceCallSubmitImages(String url, int method,
			JSONObject object) {
				
		try {
			// http client
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpEntity httpEntity = null;
			HttpResponse httpResponse = null;
			
			// Checking http request method type
			if (method == POST) {
				HttpPost httpPost = new HttpPost(url);
				httpPost.setHeader("Content-type", "application/json");
				//httpPost.setHeader("Authorization", "bearer "+variable.Berar_Value);
				//String bear="bearer "+variable.Berar_Value;
				
				//System.out.println("Value from bear "+bear);
				
		        StringEntity se = new StringEntity(object.toString()); 
		        se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "image/jpg"));
		        
		        
		        File file = new File("C:\\Users\\Kamal\\Desktop\\PDFServlet1.pdf");

		        MultipartEntity mpEntity = new MultipartEntity();
		        ContentBody cbFile = new FileBody(file, "multipart/form-data");
		        mpEntity.addPart("file", cbFile);
		        httpPost.setEntity(mpEntity); 
		        
		      //  httpPost.setEntity(se); 
				httpResponse = httpClient.execute(httpPost);

			} 
			httpEntity = httpResponse.getEntity();
			Response = EntityUtils.toString(httpEntity);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return Response;

	    
	}


	/*
	 * Making service call
	 * @url - url to make request
	 * @method - http request method
	 * @params - http request params
	 * */
	public String makeServiceCall(String url, int method,
			List<NameValuePair> params) {
				
		
		  //  String url = s;
		    StringBuilder body = new StringBuilder();
		    HttpClient  httpclient = new DefaultHttpClient(); // create new httpClient
		    HttpGet httpGet = new HttpGet(url); // create new httpGet object
		    httpGet.setHeader("username", "oohexadmin@gmail.com");
			httpGet.setHeader("password", "Abcd@1234");
			

		    try {
		        response = httpclient.execute(httpGet); // execute httpGet
		        StatusLine statusLine = response.getStatusLine();
		        int statusCode = statusLine.getStatusCode();
		        if (statusCode == HttpStatus.SC_OK) {
		            // System.out.println(statusLine);
		            body.append(statusLine + "\n");
		            HttpEntity e = response.getEntity();
		            String entity = EntityUtils.toString(e);
		            body.append(entity);
		        } else {
		            body.append(statusLine + "\n");
		            // System.out.println(statusLine);
		        }
		    } catch (ClientProtocolException e) {
		        e.printStackTrace();
		    } catch (IOException e) {
		        e.printStackTrace();
		    } finally {
		        //((Object) httpGet).releaseConnection(); // stop connection
		    }
		    return body.toString(); // return the String
		
		//return response; 

	} 
}
