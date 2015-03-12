package com.integritygiving.activities;

import java.io.ByteArrayOutputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.integrity.giving.R;
import com.integritygiving.core.IGUserDetail;
import com.integritygiving.logger.IGLogger;
import com.integritygiving.manager.NetworkManager;
import com.integritygiving.model.BillingAddress;
import com.integritygiving.model.Login;
import com.integritygiving.model.UserDetail;
import com.integritygiving.model.UserDetailModel;
import com.integritygiving.utils.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.tz.sdk.base.TZUtils;
import com.tz.sdk.core.TZUplaodUserImage;

public class SettingsActivity extends BaseActivity implements OnClickListener {

	private TextView textFirstName;
	private TextView textLastName;
	private TextView textEmail;
	private TextView textAddress;
	private TextView textCity;
	private TextView textState;
	private TextView textPhone;
	private TextView textPostal;
	private final int CAPTURE_IMAGE = 1001;
	private final int SELECT_IMAGE = 1002;
	private ImageView imageUserProfile;
	private Bitmap photo;
	private ImageLoader imageLoader;
	DisplayImageOptions options;
	// private Login login;
	private TextView textAccountStatus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater.from(this).inflate(R.layout.activity_settings,
				getRootLayout(), true);
		IGLogger.d(this, "onCreate");
		imageUserProfile = (ImageView) findViewById(R.id.image_user);
		textAccountStatus = (TextView) findViewById(R.id.text_account_status);
		textPhone = (TextView) findViewById(R.id.text_phone);
		textPostal = (TextView) findViewById(R.id.text_postal);
		textCity = (TextView) findViewById(R.id.text_city);
		textState = (TextView) findViewById(R.id.text_state);
		textAddress = (TextView) findViewById(R.id.text_address);
		textFirstName = (TextView) findViewById(R.id.text_first_name);
		textLastName = (TextView) findViewById(R.id.text_last_name);
		textEmail = (TextView) findViewById(R.id.text_email);
		findViewById(R.id.layout_choose_photo).setOnClickListener(this);
		findViewById(R.id.layout_take_photo).setOnClickListener(this);
		findViewById(R.id.layout_save).setOnClickListener(this);
		ImageLoaderConfiguration config = ImageLoaderConfiguration
				.createDefault(SettingsActivity.this);
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(config);
		options = new DisplayImageOptions.Builder().build();
		if (new NetworkManager(getApplicationContext()).isInternetConnected()) {
			Login login = Utils.getInstance().getLogin();
			if (login == null) {
				// login = new PreferenceManager(getApplicationContext())
				// .getLogin();
				// Utils.getInstance().setLogin(login);
			}
			IGLogger.d(this, "login" + login.getUserName());
			new UserDetailAsyncTast().execute(login.getUserName());
		} else {
			alertManager.showNetworkErrorToast(SettingsActivity.this);
		}
	}

	public class UserDetailAsyncTast extends IGUserDetail {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			alertManager.showLoadingProgress(SettingsActivity.this);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			IGLogger.d(this, "result = " + result);
			alertManager.hideLoadingProgress();
			if (result != null) {
				try {
					result = result.substring(result.indexOf("{"),
							result.length());
					Gson gson = new GsonBuilder().create();
					UserDetailModel userDetailModel = new UserDetailModel();
					userDetailModel = gson.fromJson(result,
							userDetailModel.getClass());
					setData(userDetailModel);
					// new GetUserImageAsyncTask().execute(Utils.getInstance()
					// .getToken());
					loadUserImage();
				} catch (Exception e) {

				}
			} else {
				alertManager.showToast(SettingsActivity.this, "Server error.");
			}
		}
	}

	private void setData(UserDetailModel userDetailModel) {
		UserDetail userDetail = userDetailModel.user;
		textEmail.setText(userDetail.email);
		if (userDetail.firstName != null) {
			textFirstName.setText(userDetail.firstName);
		}
		if (userDetail.lastName != null) {
			textLastName.setText(userDetail.lastName);
		}
		BillingAddress billingAddress = userDetail.billingAddress;
		if (billingAddress.address1 != null) {
			textAddress.setText(billingAddress.address1);
		}
		if (billingAddress.city != null) {
			textCity.setText(billingAddress.city);
		}
		if (billingAddress.state != null) {
			textState.setText(billingAddress.state);
		}
		if (billingAddress.postal != null) {
			textPostal.setText(billingAddress.postal);
		}
		if (billingAddress.phone != null) {
			textPhone.setText(billingAddress.phone);
		}
		String valid = userDetail.valid;
		if (valid != null && valid.length() > 0) {
			String text = "Valid through "
					+ valid.substring(0, valid.indexOf(" "));
			textAccountStatus.setText(text);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		IGLogger.d(this, "onActivityResult");
		if (resultCode == Activity.RESULT_OK && null != data) {
			if (requestCode == CAPTURE_IMAGE) {
				Bundle extras = data.getExtras();
				if (extras != null) {
					photo = extras.getParcelable("data");
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
					imageUserProfile.setImageBitmap(photo);
				}
			} else if (requestCode == SELECT_IMAGE) {
				Uri pickedImage = data.getData();
				String[] filePath = { MediaStore.Images.Media.DATA };
				Cursor cursor = getContentResolver().query(pickedImage,
						filePath, null, null, null);
				cursor.moveToFirst();
				String imagePath = cursor.getString(cursor
						.getColumnIndex(filePath[0]));
				photo = BitmapFactory.decodeFile(imagePath);
				imageUserProfile.setImageBitmap(photo);
				cursor.close();
			}
		}
		System.gc();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.layout_choose_photo:
			// Intent intentToGallery = new Intent(
			// Intent.ACTION_PICK,
			// android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			// intentToGallery.putExtra("crop", "true");
			// startActivityForResult(intentToGallery, SELECT_IMAGE);
			System.gc();
			Intent intentToGallery = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			// intentToGallery.putExtra("crop", "true");
			startActivityForResult(intentToGallery, SELECT_IMAGE);
			break;

		case R.id.layout_take_photo:
			Intent intentToCamera = new Intent(
					"android.media.action.IMAGE_CAPTURE");
			startActivityForResult(intentToCamera, CAPTURE_IMAGE);
			break;
		case R.id.layout_save:
			if (photo != null) {
				// Utils.getInstance().setUserImage(photo);
				new UploadUserImageAsyncTask().execute(Utils.getInstance()
						.getToken(), Utils.getInstance().bitMapToString(photo));
			} else {
				alertManager.showToast(SettingsActivity.this,
						"Image is already saved.");
			}
			break;
		default:
			break;
		}
	}

	private class UploadUserImageAsyncTask extends TZUplaodUserImage {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			alertManager.showLoadingProgress(SettingsActivity.this);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			alertManager.hideLoadingProgress();
			try {
				if (result != null) {
					JSONObject response = new JSONObject(result);
					boolean status = response.getBoolean("status");
					String statusMessage = response.getString("statusMessage");
					IGLogger.d(this, "image upload status = " + status
							+ " status message = " + statusMessage);
					if (status) {
						Utils.getInstance().setProfileImageAvailable(true);
						IGLogger.d(this, "status = " + status);
					}
					alertManager
							.showToast(SettingsActivity.this, statusMessage);
				} else {
					alertManager.showToast(SettingsActivity.this,
							"No response from server.");
				}

			} catch (JSONException e) {
				alertManager.showToast(SettingsActivity.this,
						"No response from server.");
				e.printStackTrace();
			}
		}
	}

	// private class GetUserImageAsyncTask extends TZGetUserImage {
	//
	// @Override
	// protected void onPreExecute() {
	// super.onPreExecute();
	// alertManager.showLoadingProgress(SettingsActivity.this);
	// }
	//
	// @Override
	// protected void onPostExecute(String result) {
	// super.onPostExecute(result);
	// alertManager.hideLoadingProgress();
	//
	// // imageLoader.displayImage(result, imageUserProfile, options);
	// IGLogger.d(this, "image url = " + imageUserProfile);
	// imageLoader.displayImage(result, imageUserProfile,
	// new ImageLoadingListener() {
	//
	// @Override
	// public void onLoadingStarted(String arg0, View arg1) {
	// alertManager
	// .showLoadingProgress(SettingsActivity.this);
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
	private void loadUserImage() {
		TZUtils tzUtils = new TZUtils();
		String imageUrl = tzUtils.FEServerurl + tzUtils.UserImage + "?ticket="
				+ Utils.getInstance().getToken();
		IGLogger.d(this, "imageUrl = " + imageUrl);
		imageLoader.displayImage(imageUrl, imageUserProfile,
				new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String arg0, View arg1) {
						alertManager.showLoadingProgress(SettingsActivity.this);
					}

					@Override
					public void onLoadingFailed(String arg0, View arg1,
							FailReason arg2) {
						Utils.getInstance().setProfileImageAvailable(false);
						alertManager.hideLoadingProgress();
					}

					@Override
					public void onLoadingComplete(String arg0, View arg1,
							Bitmap arg2) {
						Utils.getInstance().setProfileImageAvailable(true);
						alertManager.hideLoadingProgress();
					}

					@Override
					public void onLoadingCancelled(String arg0, View arg1) {
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
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		IGLogger.d(this, "onRestoreInstanceState");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.gc();
		IGLogger.d(this, "onDestroy");
	}

}