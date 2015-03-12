package com.integritygiving.activities;


import notification.note_ig;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.integrity.giving.R;
import com.integritygiving.activities.FavoritesActivity;
import com.integritygiving.activities.FindOfferChangedActivity;
import com.integritygiving.activities.SettingsActivity;
import com.integritygiving.configuration.Configuration;
import com.integritygiving.core.IGUserStatus;
import com.integritygiving.logger.IGLogger;
import com.integritygiving.manager.AlertManager;
import com.integritygiving.manager.PreferenceManager;
import com.integritygiving.model.Login;
import com.integritygiving.utils.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.tz.sdk.base.TZUtils;

public class LandingActivity extends Activity implements OnClickListener {

	private LinearLayout layoutOffers;
	private LinearLayout layoutFavorites;
	private LinearLayout layoutSettings;
	private AlertManager alertManager;
	// private String email;
	// private String password;
	private ImageLoader imageLoader;
	private LinearLayout layoutDonate;
	private DisplayImageOptions options;
	private ImageView imageUser;
	SharedPreferences sharedpreferences;

	private PreferenceManager preferenceManager;
	
	public static final String MyPREFERENCES = "MyPrefs" ;
	
	public static final String ProductPruchase="Productpruchase"; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		alertManager = new AlertManager();
		setContentView(R.layout.activity_landing);
	
		Intent serviceIntent=new Intent(LandingActivity.this, note_ig.class);
		LandingActivity.this.startService(serviceIntent);
		
		layoutOffers = (LinearLayout) findViewById(R.id.layout_offers);
		layoutFavorites = (LinearLayout) findViewById(R.id.layout_favorites);
		layoutSettings = (LinearLayout) findViewById(R.id.layout_settings);
		layoutDonate = (LinearLayout) findViewById(R.id.layout_donate);
		imageUser = (ImageView) findViewById(R.id.image_user);
		layoutDonate.setOnClickListener(this);
		layoutOffers.setOnClickListener(this);
		layoutFavorites.setOnClickListener(this);
		layoutSettings.setOnClickListener(this);
		ImageLoaderConfiguration config = ImageLoaderConfiguration
				.createDefault(LandingActivity.this);
		imageLoader = ImageLoader.getInstance();   
		imageLoader.init(config);
		options = new DisplayImageOptions.Builder().build();
		preferenceManager = new PreferenceManager(getApplicationContext());
		sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

	
		Configuration.CallFirst=false;
		Login login = preferenceManager.getLogin();
		String anme=login.getUserName();
		System.out.println("name is :- " + anme);   
		if (login != null) {   
			new UserStatusAsyncTask().execute(login.getUserName());
		}   
		Intent intent = getIntent();   
		String extra = intent.getStringExtra("notification");
		String notificationResponce=intent.getStringExtra("notificationResponce");

		
		
	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {
		case R.id.layout_offers:
			startActivity(new Intent(this, FindOfferChangedActivity.class));
			break;
		case R.id.layout_favorites:
			startActivity(new Intent(this, FavoritesActivity.class));
			break;
		case R.id.layout_settings:
			Intent intentToSettings = new Intent(LandingActivity.this,
					SettingsActivity.class);
			// intentToSettings.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			startActivity(intentToSettings);
			break;
		case R.id.layout_donate:
			String url = Configuration.IG_DONATE_CREATE_ACC_URL;
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(url));  
			startActivity(intent);
			break;

		}
	}

	private class UserStatusAsyncTask extends IGUserStatus {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			alertManager.showLoadingProgress(LandingActivity.this);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			alertManager.hideLoadingProgress();  
			IGLogger.d(this, "response = " + result);
			if (result != null && result.length() > 0) {   
				try {
					result = result.substring(result.indexOf("{"),
							result.length());
					try {
						JSONObject jsonObject = new JSONObject(result);   
						boolean status = jsonObject.getBoolean("status");
						
						Login login = preferenceManager.getLogin();
						login.setProductPurchase(status);
						StoreLoginStatus(status);
						IGLogger.d(this, "user status = " + status);
						Utils.getInstance().setUSerStatus(status);
						if (!status) {
							layoutDonate.setVisibility(View.VISIBLE);  
						}
					} catch (JSONException e) {
						e.printStackTrace();
					} catch (ArrayIndexOutOfBoundsException e) {
						alertManager.showToast(LandingActivity.this,
								"Server error.");
					}
					checkForUSerImage();
				} catch (Exception e) {
					e.printStackTrace();
					layoutDonate.setVisibility(View.VISIBLE);
				}
			} else {
				layoutDonate.setVisibility(View.VISIBLE);
			}
		}
	}
	
	public void StoreLoginStatus(Boolean Product){
		
		  Login login = preferenceManager.getLogin();
		  if (login != null) {
			  Editor editor = sharedpreferences.edit();
		      editor.putString(ProductPruchase, String.valueOf(Product));
		      editor.commit(); 
		  } 
		
	}
	

	private void checkForUSerImage() {
		TZUtils tzUtils = new TZUtils();
		String imageUrl = tzUtils.FEServerurl + tzUtils.UserImage + "?ticket="
				+ Utils.getInstance().getToken();
		IGLogger.d(this, "imageUrl = " + imageUrl);
		imageLoader.displayImage(imageUrl, imageUser,// new
														// ImageView(LandingActivity.this),
				new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String arg0, View arg1) {
						IGLogger.d(this, "onLoadingStarted");
						alertManager.showLoadingProgress(LandingActivity.this);
					}

					@Override
					public void onLoadingFailed(String arg0, View arg1,
							FailReason arg2) {
						IGLogger.d(this, "onLoadingFailed");
						Utils.getInstance().setProfileImageAvailable(false);
						alertManager.hideLoadingProgress();
					}

					@Override
					public void onLoadingComplete(String arg0, View arg1,
							Bitmap arg2) {
						IGLogger.d(this, "onLoadingComplete");
						Utils.getInstance().setProfileImageAvailable(true);
						alertManager.hideLoadingProgress();
					}

					@Override
					public void onLoadingCancelled(String arg0, View arg1) {
						IGLogger.d(this, "onLoadingCancelled");
						alertManager.hideLoadingProgress();
					}
				});

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		IGLogger.d(this, "onSaveInstanceState");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Utils.getInstance().setToken(null);
		Utils.getInstance().setLogin(null);
	}
}
