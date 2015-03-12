package com.integritygiving.activities;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.integrity.giving.R;
import com.integritygiving.configuration.Configuration;
import com.integritygiving.constants.Constants;
import com.integritygiving.logger.IGLogger;
import com.integritygiving.manager.NetworkManager;
import com.integritygiving.model.Address;
import com.integritygiving.model.ContactDetail;
import com.integritygiving.model.Location;
import com.integritygiving.model.Login;
import com.integritygiving.model.Offer;
import com.integritygiving.model.OfferDetail;
import com.integritygiving.model.OfferDisplayDetail;
import com.integritygiving.model.OfferResponse;
import com.integritygiving.model.Venue;
import com.integritygiving.utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.tz.sdk.base.TZUtils;
import com.tz.sdk.core.TZAcceptOffer;
import com.tz.sdk.core.TZGetOfferFavStatus;
import com.tz.sdk.core.TZOfferStatus;
import com.tz.sdk.core.TZRedeemedOffer;
import com.tz.sdk.core.TZResetOffer;

public class RedeemActivity extends BaseActivity implements OnClickListener {

	private LinearLayout layoutRedeemNow;
	private LinearLayout layoutAddFavorites;
	private TextView textVenueName;
	private TextView textOfferName;
	private TextView textAddress;
	private TextView textOfferValid;
	private TextView textPhoneNo;
	private OfferDisplayDetail displayDetail;
	private TextView textViewAddFav;
	private ImageView imageOffer;
	private ImageLoader imageLoader;
	private boolean userStatus;
	private LinearLayout layoutDonate;
	private TextView textViewDescription;
	private long offerRedeemTime;
	
	 public static final String MyPREFERENCES = "MyPrefs" ;
	 public static final String Password = "Password"; 
     public static final String UserName= "UserName"; 
     private PreferenceManager preferenceManager;
	public static SharedPreferences prefs;
	SharedPreferences sharedpreferences;
	String  Uname,password;
	OfferResponse offerResponse;
	String extra;
	private ArrayList<OfferDisplayDetail> arrayListOfferDisplayDetail;
	
	//OfferDisplayDetail displayDetailNoti;
	private GoogleMap map;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater.from(this).inflate(R.layout.activity_redeem,
				getRootLayout(), true);
		textViewDescription = (TextView) findViewById(R.id.text_description);
		imageOffer = (ImageView) findViewById(R.id.image_offer);
		textViewAddFav = (TextView) findViewById(R.id.text_add_fav);
		textVenueName = (TextView) findViewById(R.id.text_venue_name);
		textOfferName = (TextView) findViewById(R.id.text_offer_name);
		textAddress = (TextView) findViewById(R.id.text_address);
		textPhoneNo = (TextView) findViewById(R.id.text_phone_no);
		textOfferValid = (TextView) findViewById(R.id.text_offer_valid);
		layoutDonate = (LinearLayout) findViewById(R.id.layout_donate);
		layoutDonate.setOnClickListener(this);
		imageMap.setVisibility(View.VISIBLE);
		imageMap.setOnClickListener(this);
		layoutRedeemNow = (LinearLayout) findViewById(R.id.layout_redeem_now);
		layoutRedeemNow.setOnClickListener(this);
		layoutAddFavorites = (LinearLayout) findViewById(R.id.layout_add_favorites);
		layoutAddFavorites.setOnClickListener(this);
		displayDetail = (OfferDisplayDetail) getIntent().getSerializableExtra(
				"offer");
		ImageLoaderConfiguration config = ImageLoaderConfiguration
				.createDefault(RedeemActivity.this);
		
		
		 prefs = PreferenceManager.getDefaultSharedPreferences(this); 
		
		 sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
		
		 Uname=sharedpreferences.getString(UserName, "");
		 password=sharedpreferences.getString(Password, "");	
		 
		 Intent intent = getIntent();        
		 extra = intent.getStringExtra("notification");
		 String notificationResponce=intent.getStringExtra("notificationResponce");
		 
		 if (extra != null
					&& extra.equals("true")
					&& (intent.getFlags() & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) == 0) {
			
				 ShowNotificationOffer(notificationResponce);
				 getOffersNotification(offerResponse);     
			}	
		 	
				
		imageLoader = ImageLoader.getInstance();            
		imageLoader.init(config);
		setOfferData(displayDetail);
		
		userStatus = Utils.getInstance().getUSerState();   
		if (userStatus) {
			if (new NetworkManager(getApplicationContext())
					.isInternetConnected()) {
				Login login = Utils.getInstance().getLogin();
				
				System.out.println("Token" + Utils.getInstance().getToken());
				System.out.println("ID " + displayDetail.id);
				//System.out.println("UserName " + login.getUserName());
				//System.out.println("Password:- " + login.getPassword());
				
				new OfferstatusAsyncTast().execute(Utils.getInstance()
						.getToken(), displayDetail.id, Uname,
						password);
			} else {
				alertManager.showNetworkErrorToast(RedeemActivity.this);
			}
		} else {
			layoutDonate.setVisibility(View.VISIBLE);
			layoutRedeemNow.setVisibility(View.GONE);
			if (Uname == null) {
				layoutAddFavorites.setVisibility(View.GONE);
			} else {
				if (new NetworkManager(getApplicationContext())
						.isInternetConnected()) {
					Login login = Utils.getInstance().getLogin();
					new OfferFavoriteCheckAsyncTask().execute(Utils
							.getInstance().getToken(), displayDetail.id, Uname,password);
				} else {
					alertManager.showNetworkErrorToast(RedeemActivity.this);
				}
			}

		}

	}
	
	private void ShowNotificationOffer(String notificationResponce) {
		// TODO Auto-generated method stub
		
		
		if (notificationResponce != null) {
			try {
				offerResponse = new OfferResponse();
				Gson gson = new Gson();
				offerResponse = gson.fromJson(notificationResponce,
						offerResponse.getClass());   
				if (offerResponse.token != null) {
					Utils.getInstance().setToken(offerResponse.token);
				}
				/*Intent intent = new Intent(OfferListActivityNotification.this,
						OfferListActivityNotification.class);
				intent.putExtra(Constants.EXTRA_SHOW_CATEGORY_SORT_OPTION,
						showCategorySortOption);
				intent.putExtra(Constants.EXTRA_OFFER_SCREEN_MODE,
						offerScreenMode);
				intent.putExtra(Constants.EXTRA_MAP_ZOOM_LOCATION,
						mapZoomLocation);
				intent.putExtra("offers", offerResponse);
				startActivity(intent);*/
			} catch (JsonSyntaxException e) {

			}
		} else {
			//alertManager.showToast(OfferListActivity.this,"Offers are not available.");
		}
		
	}
	private void getOffersNotification(OfferResponse offerResponse) {
		IGLogger.d(this, "get offer other response");
		IGLogger.d(this, "" + offerResponse.availableOffers.size());
		arrayListOfferDisplayDetail = new ArrayList<OfferDisplayDetail>();
		// for (OfferDetail offerDetail : offerResponse.availableOffers) {
		for (int j = 0; j < offerResponse.availableOffers.size(); j++) {
			OfferDetail offerDetail = offerResponse.availableOffers.get(j);
			Venue venue = offerDetail.venue;
			// addMarker(venue, j);
			if (offerDetail.offers.size() > 0) {
				// IGLogger.d(this,
				// "offer size = " + offerDetail.offers.get(0).name);
				for (int i = 0; i < offerDetail.offers.size(); i++) {
					Offer offer = offerDetail.offers.get(i);
					displayDetail = new OfferDisplayDetail();
					displayDetail.id = offer.id;
					displayDetail.offersCount = i + 1;
					displayDetail.name = offer.name;
					displayDetail.description = offer.description;
					displayDetail.venueName = venue.name;
					displayDetail.validPeriod = offer.validPeriod;
					displayDetail.offerImageUri = offer.offerImageUri;
					Address address = venue.address;
					displayDetail.address = address.address1 + " "
							+ address.address2 + " " + address.city + " "
							+ address.stateProv + " " + address.postcode;
					displayDetail.staticId = venue.staticId;
					displayDetail.sysId = venue.sysId;
					displayDetail.geolocation = venue.geolocation;
					//IGLogger.d(this, venue.content.staticId + " : "+ venue.content.sysId);
					//String value = venue.distance.value;
					displayDetail.distance ="";
					displayDetail.metric = "";
					ArrayList<ContactDetail> listContactDetails = venue.contactDetails;
					IGLogger.d(this, "size = " + listContactDetails.size());
					// for (int k = 0; k < listContactDetails.size(); k++) {
					// ContactDetail contact = listContactDetails.get(k);
					// IGLogger.d(this, contact.type + " : values "
					// + contact.value);
					// if (contact.type.equalsIgnoreCase("CONTACT")) {
					// displayDetail.phone = contact.value;
					// IGLogger.d(this, "PHONE " + contact.value + ""
					// + offer.name);
					// break;
					// }
					// }
					displayDetail.phone ="";
					// ArrayList<String> attributes = venue.content.attributes;
					// if (attributes.size() > 0) {
					// String attribute = attributes.get(0);
					// if (attribute == null) {
					// displayDetail.category = "";
					// } else {
					// displayDetail.category = attributes.get(0);
					// }
					// }
					/*ArrayList<AttributeObjs> attributesObj = venue.content.attributeObjs;
					if (attributesObj.size() > 0) {
						displayDetail.category = attributesObj.get(0).displayName;
					}*/
					arrayListOfferDisplayDetail.add(displayDetail);
					//addMarker(venue, arrayListOfferDisplayDetail.size() - 1);
				}

			}
		}

	}

	private void addMarker(Venue venue, int index) {
		// if (map == null) {
		// return;
		// }
		IGLogger.d(this, "index = " + index);
		MarkerOptions markerOption = new MarkerOptions();
		Location location = null;
		location = venue.geolocation;
		if (location == null) {
			if (venue.content != null) {
				location = venue.content.geolocation;
			} else {
				android.location.Address address = Utils.getInstance()
						.getLocationFromAddress(
								getApplicationContext(),
								venue.address.city + " "
										+ venue.address.postcode + " "
										+ venue.address.stateProv);
				Location temp = new Location();
				temp.latitude = address.getLatitude();
				temp.longitude = address.getLongitude();
				location = temp;
			}
		}
		markerOption
				.position(new LatLng(location.latitude, location.longitude));
		IGLogger.d(this, "index = " + index);
		markerOption.title("" + index);
		BitmapDescriptor icon = BitmapDescriptorFactory
				.fromResource(R.drawable.map_pin_shadow);
		markerOption.icon(icon);
		map.addMarker(markerOption);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.image_map:
			// startActivity(new Intent(this, OfferMapActivity.class));
			Location geoLocation = displayDetail.geolocation;
			if (geoLocation == null) {
				alertManager.showToast(RedeemActivity.this,
						"Location is not available for this venue.");
				return;
			}
			Uri uri = Uri.parse("geo:0,0?q=" + geoLocation.latitude + ","
					+ geoLocation.longitude + "(" + displayDetail.venueName
					+ ")");
			Intent intentToMap = new Intent(android.content.Intent.ACTION_VIEW,
					uri);
			IGLogger.d(this, "uri = " + uri.toString());
			startActivity(intentToMap);
			break;
		case R.id.layout_redeem_now:
			if (Utils.getInstance().isProfileImageAvailable()) {
				showRedeemConfirmationDialog();
			} else {
				showUploadPictureDialog();
			}
			// Intent intent = new Intent(RedeemActivity.this,
			// UserProfileActivity.class);
			// intent.putExtra("offer", displayDetail);
			// startActivity(intent);
			break;
		case R.id.layout_add_favorites:
			String text = textViewAddFav.getText().toString();
			if (text.equalsIgnoreCase(getString(R.string.text_add_favorites))) {
				if (new NetworkManager(getApplicationContext())
						.isInternetConnected()) {
					Login login = Utils.getInstance().getLogin();
				/*	String token=Utils.getInstance().getToken();
					if(token.equals("")){
						new AddToFavAsyncTast().execute(Configuration.Token, displayDetail.id,
								displayDetail.venueName, displayDetail.staticId,Uname,password
								);
					}else{ */
					new AddToFavAsyncTast().execute(Configuration.Token, displayDetail.id,
							displayDetail.venueName, displayDetail.staticId,Uname,password
							);
					//}
				} else {
					alertManager.showNetworkErrorToast(RedeemActivity.this);
				}
			} else if (text
					.equalsIgnoreCase(getString(R.string.text_remove_from_favorites))) {
				if (new NetworkManager(getApplicationContext())
						.isInternetConnected()) {
					Login login = Utils.getInstance().getLogin();
					
				/*	String token=Utils.getInstance().getToken();
					if(token.equals("")){
						new ResetFavAsyncTast().execute(Configuration.Token, displayDetail.id,
								displayDetail.staticId, displayDetail.venueName,   
								Uname,password);

					}else{ */
					new ResetFavAsyncTast().execute(Configuration.Token, displayDetail.id,
							displayDetail.staticId, displayDetail.venueName,   
							Uname,password);
					//}
				} else {
					alertManager.showNetworkErrorToast(RedeemActivity.this);
				}
			}
			break;
		case R.id.layout_donate:
			String url = Configuration.IG_DONATE_CREATE_ACC_URL;
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(url));
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	private void showRedeemConfirmationDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		View view = LayoutInflater.from(this).inflate(
				R.layout.dialog_redeem_confirmation, null);
		alertDialogBuilder.setView(view);
		final AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.setCancelable(false);
		alertDialog.show();
		view.findViewById(R.id.text_yes_proceed).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						alertDialog.cancel();
						offerRedeemTime = System.currentTimeMillis();
						Login login = Utils.getInstance().getLogin();
						new RedeemOfferAsyncTask().execute(Utils.getInstance()
								.getToken(), displayDetail.id,
								displayDetail.staticId,
								displayDetail.venueName, Uname,
								password);
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
	
	/*@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();   
		   
		 Intent intent=null;  
		 if (extra != null  && extra.equals("true")) {  
			 intent=new Intent(this,FindOfferChangedActivity.class);  
			 startActivity(intent);
		 }else{
		   intent=new Intent(this,OfferListActivity.class);  
		   //intent.putExtra("offer_from_map","");
		   intent.putIntegerArrayListExtra("offer_from_map",null);
		   startActivity(intent);  
		 }  
	   Login login=new Login();
	   login.setUserName(Uname);
	   login.setPassword(password);	
	}
	*/

	private void setOfferData(OfferDisplayDetail displayDetail) {
		textViewDescription.setText(displayDetail.description);
		textViewDescription.setVisibility(View.VISIBLE);
		textVenueName.setText(displayDetail.venueName);
		if(!(displayDetail.address.contains("null"))){
		  textAddress.setText(displayDetail.address);
		}
		textPhoneNo.setText(displayDetail.phone);
		textOfferName.setText(displayDetail.name);
		ArrayList<String> validPeriod = displayDetail.validPeriod;
		String dateStart = validPeriod.get(0);
		String startValue = dateStart.substring(0, dateStart.indexOf(" "));
		String dateEnd = validPeriod.get(1);
		String endValue = dateEnd.substring(0, dateEnd.indexOf(" "));
		textOfferValid.setText(startValue + " - " + endValue);
		if (displayDetail.offerImageUri != null
				&& displayDetail.offerImageUri.length() > 0) {
			String url = new TZUtils().affeServerUrl
					+ displayDetail.offerImageUri + "/";
			imageLoader.displayImage(url, imageOffer,
					new ImageLoadingListener() {

						@Override
						public void onLoadingStarted(String arg0, View arg1) {
						}

						@Override
						public void onLoadingFailed(String arg0, View arg1,
								FailReason arg2) {
						}

						@Override
						public void onLoadingComplete(String arg0, View arg1,
								Bitmap arg2) {
						}

						@Override
						public void onLoadingCancelled(String arg0, View arg1) {
						}
					});
		}
	}

	private class OfferstatusAsyncTast extends TZOfferStatus {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			alertManager.showLoadingProgress(RedeemActivity.this);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			IGLogger.d(this, "response = " + result);
			alertManager.hideLoadingProgress();
			if (result != null) {
				JSONObject response;
				try {
					response = new JSONObject(result);
					boolean status = response.getBoolean("status");
					try {
						String token = response.getString("token");
						if (token != null && token.length() > 0) {
							Utils.getInstance().setToken(token);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					if (status) {
						layoutRedeemNow.setEnabled(true);
					} else {
						String message = response.getString("statusMessage");
						alertManager.showToast(RedeemActivity.this, message);
						layoutRedeemNow.setEnabled(false);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				alertManager.showToast(RedeemActivity.this, "Server error.");
			}
			if (new NetworkManager(getApplicationContext())
					.isInternetConnected()) {
				Login login = Utils.getInstance().getLogin();
				new OfferFavoriteCheckAsyncTask().execute(Utils.getInstance()
						.getToken(), displayDetail.id,Uname,password);
			} else {
				alertManager.showNetworkErrorToast(RedeemActivity.this);
			}
		}

	}

	private class AddToFavAsyncTast extends TZAcceptOffer {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			alertManager.showLoadingProgress(RedeemActivity.this);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			IGLogger.d(this, "response = " + result);  
			alertManager.hideLoadingProgress();
			if (result != null) {

				try {
					JSONObject jsonResponse = new JSONObject(result);
					boolean status = jsonResponse.getBoolean("success");
					try {
						String token = jsonResponse.getString("token");
						if (token != null && token.length() > 0) {
							Utils.getInstance().setToken(token);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					if (status) {
						alertManager.showToast(RedeemActivity.this,
								"Offer is added to favorites.");
						textViewAddFav
								.setText(getString(R.string.text_remove_from_favorites));
					} else {
						alertManager.showToast(RedeemActivity.this,
								"Offer is not added to favorites.");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			} else {
				alertManager.showToast(RedeemActivity.this,
						"Server response error.");
			}
		}
	}

	private class ResetFavAsyncTast extends TZResetOffer {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			alertManager.showLoadingProgress(RedeemActivity.this);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			IGLogger.d(this, "response = " + result);
			alertManager.hideLoadingProgress();
			if (result != null) {

				try {
					JSONObject jsonResponse = new JSONObject(result);
					boolean status = jsonResponse.getBoolean("success");
					try {
						String token = jsonResponse.getString("token");
						if (token != null && token.length() > 0) {
							Utils.getInstance().setToken(token);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					if (status) {
						alertManager.showToast(RedeemActivity.this,
								"Offer is removed from favorites.");
						textViewAddFav
								.setText(getString(R.string.text_add_favorites));
					} else {
						alertManager.showToast(RedeemActivity.this,
								"Offer is not removed from favorites.");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			} else {
				alertManager.showToast(RedeemActivity.this,
						"Server response error.");
			}
		}
	}

	private class RedeemOfferAsyncTask extends TZRedeemedOffer {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			alertManager.showLoadingProgress(RedeemActivity.this);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			IGLogger.d(this, "response = " + result);
			alertManager.hideLoadingProgress();
			if (result != null) {
				try {
					JSONObject response = new JSONObject(result);
					boolean success = response.getBoolean("success");
					try {
						String token = response.getString("token");
						if (token != null && token.length() > 0) {
							Utils.getInstance().setToken(token);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					if (success) {
						alertManager.showToast(RedeemActivity.this,
								"Offer is redeemed successfully.");
						// layoutRedeemNow.setEnabled(false);
						Intent intent = new Intent(RedeemActivity.this,
								UserProfileActivity.class);
						intent.putExtra(Constants.EXTRA_OFFER_REDEEM_TIME,
								offerRedeemTime);
						intent.putExtra("offer", displayDetail);
						startActivity(intent);
					} else {
						alertManager.showToast(RedeemActivity.this,
								"Offer either redeemed or declined.");
					}
				} catch (JSONException e) {
					alertManager.showToast(RedeemActivity.this,
							"Offer is not redeemed.");

				}
			} else {
				alertManager.showToast(RedeemActivity.this, "Server error.");
			}
		}
	}

	private class OfferFavoriteCheckAsyncTask extends TZGetOfferFavStatus {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			alertManager.showLoadingProgress(RedeemActivity.this);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			IGLogger.d(this, "response = " + result);
			alertManager.hideLoadingProgress();
			if (result != null) {
				try {
					JSONObject response = new JSONObject(result);
					boolean isOfferFavorite = response
							.getBoolean("isOfferFavorite");
					try {
						String token = response.getString("token");   
						if (token != null && token.length() > 0) {   
							Utils.getInstance().setToken(token);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					IGLogger.d(this, "isOfferFavorite = " + isOfferFavorite);
					if (isOfferFavorite) {
						textViewAddFav
								.setText(getString(R.string.text_remove_from_favorites));
					}
				} catch (JSONException e) {
					alertManager.showToast(RedeemActivity.this,
							"Offer is not redeemed.");

				}
			} else {
				alertManager.showToast(RedeemActivity.this, "Server error.");
			}
		}
	}

	private void showUploadPictureDialog() {
		final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("Profile Picture");
		alertDialog
				.setMessage("Please go to settings and add your profile picture to redeem an offer.");
		alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,
				getString(R.string.text_view_settings),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						alertDialog.dismiss();
						startActivity(new Intent(RedeemActivity.this,
								SettingsActivity.class));
					}
				});
		alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE,
				getString(R.string.text_close),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						alertDialog.dismiss();

					}
				});
		alertDialog.show();
	}
}
