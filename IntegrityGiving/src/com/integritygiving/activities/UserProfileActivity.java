package com.integritygiving.activities;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.integrity.giving.R;
import com.integritygiving.constants.Constants;
import com.integritygiving.core.IGUserDetail;
import com.integritygiving.logger.IGLogger;
import com.integritygiving.manager.NetworkManager;
import com.integritygiving.model.Login;
import com.integritygiving.model.OfferDisplayDetail;
import com.integritygiving.model.UserDetailModel;
import com.integritygiving.utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.tz.sdk.base.TZUtils;

public class UserProfileActivity extends BaseActivity implements
		OnClickListener {

	private LinearLayout layoutCasierinfo;
	private TextView textVenueName;
	private TextView textOfferName;
	private TextView textAddress;
	private TextView textOfferValid;
	private TextView textPhoneNo;
	private long duration = 24 * 60 * 60;  
	private TextView textHours;
	private TextView textMinutes;
	private TextView textSeconds;
	private OfferDisplayDetail displayDetail;
	private ImageView imageUser;
	private ImageLoader imageLoader;
	private TextView textUserName;
	private TextView textViewDescription;
	private long offerRedeemTime;
	private TextView textViewRedeemTime;
	private TextView textViewCurrentTime;
	
	SharedPreferences sharedpreferences;
	String  Uname,password;
	 public static final String MyPREFERENCES = "MyPrefs" ;
	 public static final String Password = "Password"; 
     public static final String UserName= "UserName"; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater.from(this).inflate(R.layout.activity_user_profile,
				getRootLayout(), true);
		textViewRedeemTime = (TextView) findViewById(R.id.text_redeem_time);
		textViewCurrentTime = (TextView) findViewById(R.id.text_current_time);
		textViewDescription = (TextView) findViewById(R.id.text_description);
		textUserName = (TextView) findViewById(R.id.text_name);
		imageUser = (ImageView) findViewById(R.id.image_user);
		// textHours = (TextView) findViewById(R.id.text_hrs);
		// textMinutes = (TextView) findViewById(R.id.text_mins);
		// textSeconds = (TextView) findViewById(R.id.text_sec);
		textVenueName = (TextView) findViewById(R.id.text_venue_name);
		textOfferName = (TextView) findViewById(R.id.text_offer_name);
		textAddress = (TextView) findViewById(R.id.text_address);
		textPhoneNo = (TextView) findViewById(R.id.text_phone_no);
		textOfferValid = (TextView) findViewById(R.id.text_offer_valid);
		layoutCasierinfo = (LinearLayout) findViewById(R.id.layout_casier_info);
		layoutCasierinfo.setOnClickListener(this);
		
		sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
		
		 Uname=sharedpreferences.getString(UserName, "");
		 password=sharedpreferences.getString(Password, "");	
		
		
		displayDetail = (OfferDisplayDetail) getIntent().getSerializableExtra(
				"offer");
		offerRedeemTime = getIntent().getLongExtra(
				Constants.EXTRA_OFFER_REDEEM_TIME, 0);
		Timer timer = new Timer(Calendar.getInstance().getTimeInMillis(), 1000);
		timer.start();
		setOfferData(displayDetail);
		ImageLoaderConfiguration config = ImageLoaderConfiguration
				.createDefault(UserProfileActivity.this);
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(config);
		if (new NetworkManager(getApplicationContext()).isInternetConnected()) {
			Login login = Utils.getInstance().getLogin();
			new UserDetailAsyncTast().execute(Uname);
		} else {
			alertManager.showNetworkErrorToast(UserProfileActivity.this);
		}
		textViewRedeemTime.setText(Utils.getInstance().getCasierFormattedDate(
				offerRedeemTime));
		textViewCurrentTime.setText(Utils.getInstance().getCasierFormattedDate(
				0));
		
		
		// if (new
		// NetworkManager(getApplicationContext()).isInternetConnected()) {
		// new GetUserImageAsyncTask().execute(Utils.getInstance().getToken());
		// } else {
		// alertManager.showNetworkErrorToast(UserProfileActivity.this);
		// }
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.layout_casier_info:
			Intent intent = new Intent(UserProfileActivity.this,
					DonationActivity.class);
			intent.putExtra("offer", displayDetail);
			startActivity(intent);
			finish();
			break;

		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
	}

	private class Timer extends CountDownTimer {

		public Timer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			textViewCurrentTime.setText(Utils.getInstance()
					.getCasierFormattedDate(0));
			// duration = duration - 1;
			// final long hours = duration / (60 * 60);
			// final long min = (duration - (hours * 60 * 60)) / (60);
			// final long sec = (duration - ((hours * 60 * 60) + (min * 60)));
			// handler.post(new Runnable() {
			// @Override
			// public void run() {
			// textHours.setText(String.valueOf(hours));
			// textMinutes.setText(String.valueOf(min));
			// textSeconds.setText(String.valueOf(sec));
			// }
			// });
		}

		@Override
		public void onFinish() {

		}

	}

	private void setOfferData(OfferDisplayDetail displayDetail) {
		textViewDescription.setText(displayDetail.description);
		textVenueName.setText(displayDetail.venueName);
		textAddress.setText(displayDetail.address);
		textPhoneNo.setText(displayDetail.phone);
		textOfferName.setText(displayDetail.name);
		ArrayList<String> validPeriod = displayDetail.validPeriod;
		String dateStart = validPeriod.get(0);
		String startValue = dateStart.substring(0, dateStart.indexOf(" "));
		String dateEnd = validPeriod.get(1);
		String endValue = dateEnd.substring(0, dateEnd.indexOf(" "));
		textOfferValid.setText(startValue + " - " + endValue);

	}

	// private class GetUserImageAsyncTask extends TZGetUserImage {
	//
	// @Override
	// protected void onPreExecute() {
	// super.onPreExecute();
	// alertManager.showLoadingProgress(UserProfileActivity.this);
	// }
	//
	// @Override
	// protected void onPostExecute(String result) {
	// super.onPostExecute(result);
	// alertManager.hideLoadingProgress();
	//
	// // imageLoader.displayImage(result, imageUserProfile, options);
	// imageLoader.displayImage(result, imageUser,
	// new ImageLoadingListener() {
	//
	// @Override
	// public void onLoadingStarted(String arg0, View arg1) {
	// alertManager
	// .showLoadingProgress(UserProfileActivity.this);
	// }
	//
	// @Override
	// public void onLoadingFailed(String arg0, View arg1,
	// FailReason arg2) {
	// alertManager.hideLoadingProgress();
	// }
	//
	// @Override
	// public void onLoadingComplete(String arg0, View arg1,
	// Bitmap arg2) {
	// alertManager.hideLoadingProgress();
	// }
	//
	// @Override
	// public void onLoadingCancelled(String arg0, View arg1) {
	// alertManager.hideLoadingProgress();
	// }
	// });
	// }
	// }

	private class UserDetailAsyncTast extends IGUserDetail {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			alertManager.showLoadingProgress(UserProfileActivity.this);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			alertManager.hideLoadingProgress();
			IGLogger.d(this, result);
			result = result.substring(result.indexOf("{"), result.length());
			if (result != null) {
				Gson gson = new GsonBuilder().create();
				UserDetailModel userDetailModel = new UserDetailModel();
				userDetailModel = gson.fromJson(result,
						userDetailModel.getClass());
				// setData(userDetailModel);
				if (userDetailModel.user.firstName != null) {
					textUserName.setText(userDetailModel.user.firstName + " "
							+ userDetailModel.user.lastName);
				}
				// new GetUserImageAsyncTask().execute(Utils.getInstance()
				// .getToken());
				loadUserImage();
			} else {
				alertManager.showToast(UserProfileActivity.this,
						"Server error.");
			}
		}
	}

	private void loadUserImage() {
		TZUtils tzUtils = new TZUtils();
		String imageUrl = tzUtils.FEServerurl + tzUtils.UserImage + "?ticket="
				+ Utils.getInstance().getToken();
		IGLogger.d(this, "imageUrl = " + imageUrl);
		imageLoader.displayImage(imageUrl, imageUser,
				new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String arg0, View arg1) {
						alertManager
								.showLoadingProgress(UserProfileActivity.this);
					}

					@Override
					public void onLoadingFailed(String arg0, View arg1,
							FailReason arg2) {
						alertManager.hideLoadingProgress();
					}

					@Override
					public void onLoadingComplete(String arg0, View arg1,
							Bitmap arg2) {
						alertManager.hideLoadingProgress();
					}

					@Override
					public void onLoadingCancelled(String arg0, View arg1) {
						alertManager.hideLoadingProgress();
					}
				});

	}

}
