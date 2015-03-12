package com.integritygiving.activities;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.integrity.giving.R;
import com.integritygiving.core.IGDonate;
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

public class DonationActivity extends BaseActivity implements OnClickListener {

	private TextView textOfferValidTill;
	private TextView textNoThnakYou;
	private String amount = "0.0";
	private OfferDisplayDetail displayDetail;
	private TextView textOfferName;
	private TextView textVenueName;
	private TextView textAddress;
	private ImageLoader imageLoader;
	private ImageView imageOffer;
	private TextView textOrgName;
	private TextView textDate;
	private String orgName;
	private TextView textPhoneNo;
	private TextView textViewDescription;

	
	SharedPreferences sharedpreferences;
	String  Uname,password;
	 public static final String MyPREFERENCES = "MyPrefs" ;
	 public static final String Password = "Password"; 
     public static final String UserName= "UserName"; 

     
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater.from(this).inflate(R.layout.activity_donation,
				getRootLayout(), true);
		textViewDescription = (TextView) findViewById(R.id.text_description);
		textDate = (TextView) findViewById(R.id.text_date);
		textOrgName = (TextView) findViewById(R.id.text_org_name);
		imageOffer = (ImageView) findViewById(R.id.image_offer);
		textVenueName = (TextView) findViewById(R.id.text_venue_name);
		textOfferName = (TextView) findViewById(R.id.text_offer_name);
		textAddress = (TextView) findViewById(R.id.text_address);
		textNoThnakYou = (TextView) findViewById(R.id.text_no_thank_you);
		textPhoneNo = (TextView) findViewById(R.id.text_phone_no);
		textNoThnakYou.setOnClickListener(this);
		findViewById(R.id.text_donate_one_dollor).setOnClickListener(this);
		findViewById(R.id.text_donate_two_dollor).setOnClickListener(this);
		findViewById(R.id.text_donate_three_dollor).setOnClickListener(this);
		imageMap.setVisibility(View.VISIBLE);
		imageMap.setOnClickListener(this);
		textOfferValidTill = (TextView) findViewById(R.id.text_offer_valid_till);
		textOfferValidTill.setPaintFlags(textOfferValidTill.getPaintFlags()
				| Paint.STRIKE_THRU_TEXT_FLAG);
		displayDetail = (OfferDisplayDetail) getIntent().getSerializableExtra(
				"offer");
		
		sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
		
		 Uname=sharedpreferences.getString(UserName, "");
		 password=sharedpreferences.getString(Password, "");	
		 
		ImageLoaderConfiguration config = ImageLoaderConfiguration
				.createDefault(DonationActivity.this);
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(config);
		setOfferData();
		if (new NetworkManager(getApplicationContext()).isInternetConnected()) {
			Login login = Utils.getInstance().getLogin();
			new UserDetailAsyncTast().execute(Uname);
		} else {
			alertManager.showNetworkErrorToast(DonationActivity.this);
		}
	}

	@Override
	public void onBackPressed() {
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.text_no_thank_you:
			finish();
			break;
		case R.id.image_map:
			Uri uri = Uri.parse("geo:0,0?q="
					+ displayDetail.geolocation.latitude + ","
					+ displayDetail.geolocation.longitude + "("
					+ displayDetail.venueName + ")");
			Intent intentToMap = new Intent(android.content.Intent.ACTION_VIEW,
					uri);
			IGLogger.d(this, "uri = " + uri.toString());
			startActivity(intentToMap);
			break;
		case R.id.text_donate_one_dollor:
			showConfirmationDialog(getString(R.string.text_donate_one_dollor_msg));
			amount = "1";
			break;
		case R.id.text_donate_two_dollor:
			showConfirmationDialog(getString(R.string.text_donate_two_dollor_msg));
			amount = "2";
			break;
		case R.id.text_donate_three_dollor:
			showConfirmationDialog(getString(R.string.text_donate_three_dollor_msg));
			amount = "3";
			break;
		default:
			break;
		}
	}

	private void showConfirmationDialog(String message) {
		if (orgName != null) {
			message = message + " " + orgName + "?";
		}
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		View view = LayoutInflater.from(this).inflate(
				R.layout.dialog_donation_confirmation, null);
		alertDialogBuilder.setView(view);
		final AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.setCancelable(false);
		alertDialog.show();
		TextView textMsg = (TextView) view.findViewById(R.id.text_msg);
		textMsg.setText(message);
		view.findViewById(R.id.text_yes_proceed).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						alertDialog.cancel();
						// finish();
						if (new NetworkManager(getApplicationContext())
								.isInternetConnected()) {
							Login login = Utils.getInstance().getLogin();
							new DonateAsyncTask().execute(Uname,
									amount, password,
									"RhUEgcXJXTwdq9VjbKX8SsRt");
						} else {
							alertManager
									.showNetworkErrorToast(DonationActivity.this);
						}
					}
				});
		view.findViewById(R.id.text_cancel).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						alertDialog.cancel();
					}
				});

	}

	private class DonateAsyncTask extends IGDonate {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			alertManager.showLoadingProgress(DonationActivity.this);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			alertManager.hideLoadingProgress();
			if (result != null && result.length() > 0) {
				JSONObject response;
				result = result.substring(result.indexOf("{"), result.length());
				try {
					IGLogger.d(this, "response = " + result);
					response = new JSONObject(result);
					boolean status = response.getBoolean("status");
					String statusMessage = null;
					if (status) {
						statusMessage = response.getString("statusMessage");
					} else {
						statusMessage = "There was an issue processing your donation. Please try again the next time you redeem an offer.";
					}
					alertManager
							.showToast(DonationActivity.this, statusMessage);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			finish();
		}
	}

	private void setOfferData() {
		if (displayDetail != null) {
			textVenueName.setText(displayDetail.venueName);
			textViewDescription.setText(displayDetail.description);
			textAddress.setText(displayDetail.address);
			textOfferName.setText(displayDetail.name);
			textPhoneNo.setText(displayDetail.phone);
			ArrayList<String> validPeriod = displayDetail.validPeriod;
			String dateStart = validPeriod.get(0);
			String startValue = dateStart.substring(0, dateStart.indexOf(" "));
			String dateEnd = validPeriod.get(1);
			String endValue = dateEnd.substring(0, dateEnd.indexOf(" "));
			textOfferValidTill.setText(startValue + " - " + endValue);
			textDate.setText(Utils.getInstance().getFormattedDate());
			if (displayDetail.offerImageUri != null
					&& displayDetail.offerImageUri.length() > 0) {
				imageLoader.displayImage(new TZUtils().affeServerUrl
						+ displayDetail.offerImageUri + "/", imageOffer,
						new ImageLoadingListener() {

							@Override
							public void onLoadingStarted(String arg0, View arg1) {
							}

							@Override
							public void onLoadingFailed(String arg0, View arg1,
									FailReason arg2) {
							}

							@Override
							public void onLoadingComplete(String arg0,
									View arg1, Bitmap arg2) {
							}

							@Override
							public void onLoadingCancelled(String arg0,
									View arg1) {
							}
						});
			}
		}
	}

	private class UserDetailAsyncTast extends IGUserDetail {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			alertManager.showLoadingProgress(DonationActivity.this);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			IGLogger.d(this, result);
			alertManager.hideLoadingProgress();
			result = result.substring(result.indexOf("{"), result.length());
			if (result != null) {
				Gson gson = new GsonBuilder().create();
				UserDetailModel userDetailModel = new UserDetailModel();
				userDetailModel = gson.fromJson(result,
						userDetailModel.getClass());
				orgName = userDetailModel.user.fundraiser;
				if (orgName != null) {
					textOrgName.setText(orgName);
				}

			} else {
				alertManager.showToast(DonationActivity.this, "Server error.");
			}
		}
	}
}
