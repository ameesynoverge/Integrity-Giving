package com.integritygiving.activities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.integrity.giving.R;
import com.integritygiving.configuration.Configuration;
import com.integritygiving.core.IGCreateAccount;
import com.integritygiving.logger.IGLogger;
import com.integritygiving.manager.AlertManager;
import com.integritygiving.manager.NetworkManager;
import com.integritygiving.manager.PreferenceManager;
import com.integritygiving.model.Login;
import com.integritygiving.utils.Utils;
import com.tz.sdk.base.service.RoadsFECasAsync;

public class PitchActivity extends Activity implements OnClickListener {

	private AlertManager alertManager;
	private EditText editUserName;
	private EditText editPassword;
	private String _valueToken = null;
	private String email;
	private String password;
	private PreferenceManager preferenceManager;
	
	public static final String MyPREFERENCES = "MyPrefs" ;
	SharedPreferences sharedpreferences;
	public static final String Password = "Password"; 
	public static final String UserName= "UserName"; 
	public static final String Token= "Token"; 


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pitch);
		editUserName = (EditText) findViewById(R.id.edit_user_name);
		editPassword = (EditText) findViewById(R.id.edit_password);
		editUserName.requestFocus();
		alertManager = new AlertManager();
		preferenceManager = new PreferenceManager(getApplicationContext());
		findViewById(R.id.text_continue).setOnClickListener(this);
		findViewById(R.id.layout_donate).setOnClickListener(this);
		findViewById(R.id.layout_login).setOnClickListener(this);
		// Uri uri = Uri.parse("geo:0,0?q=12.9667,77.5667");
		// Intent intentToMap = new Intent(android.content.Intent.ACTION_VIEW,
		// uri);
		// IGLogger.d(this, "uri = " + uri.toString());
		// startActivity(intentToMap);
		
		
		sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

		Login login = preferenceManager.getLogin();
		if (login != null) {
			// email = login.getUserName();
			// password = login.getPassword();
			editUserName.setText(login.getUserName());
			editPassword.setText(login.getPassword());
			Configuration.Username=login.getUserName();
			StoreLogin();
			findViewById(R.id.layout_login).performClick();
		}
	}
	
	public void StoreLogin(){
		
		  Login login = preferenceManager.getLogin();
		  if (login != null) {
			  Editor editor = sharedpreferences.edit();
		      editor.putString(UserName,login.getUserName());
		      editor.putString(Password, login.getPassword());
		      editor.putString(Token, login.getToken());
		      editor.commit(); 
		  } 
		
	}
	   
	
	public void StoreLoginToken(String token){
		
		  Login login = preferenceManager.getLogin();
		  if (login != null) {
			  Editor editor = sharedpreferences.edit();
		      editor.putString(Token,token);
		      editor.commit(); 
		  } 
		
	}

	private void showRedeemConfirmationDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		View view = LayoutInflater.from(this).inflate(
				R.layout.dialog_continue_with_email, null);
		alertDialogBuilder.setView(view);
		final AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
		final EditText editEmail = (EditText) view
				.findViewById(R.id.edit_email);
		view.findViewById(R.id.layout_continue).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						email = editEmail.getText().toString();
						if (!emailPatterMatching(email)) {
							alertManager.showToast(PitchActivity.this,
									"Please enter a valid Email Address.");
							return;
						}
						if (emailValidation(email)) {
							alertDialog.cancel();
							// startActivity(new Intent(PitchActivity.this,
							// LandingActivity.class));
							// finish();
							if (new NetworkManager(PitchActivity.this)
									.isInternetConnected()) {
								new CreateAccount(email).execute(email,
										"RhUEgcXJXTwdq9VjbKX8SsRt");
							} else {
								alertManager
										.showNetworkErrorToast(PitchActivity.this);
							}
						} else {
							alertManager.showToast(getApplicationContext(),
									getString(R.string.text_please_enter));
						}
					}
				});

	}

	private boolean emailValidation(String email) {
		if (email != null && email.trim().length() > 0) {
			return true;
		} else {
			return false;
		}

	}

	private boolean passwordValidation(String password) {
		if (password != null && password.trim().length() > 0) {
			return true;
		} else {
			return false;
		}

	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {
		case R.id.text_continue:
			// showRedeemConfirmationDialog();
			// startActivity(new Intent(PitchActivity.this,
			// FindOfferActivity.class));
			startActivity(new Intent(PitchActivity.this,
					FindOfferChangedActivity.class));
			break;
		case R.id.layout_donate:
			String url = Configuration.IG_DONATE_CREATE_ACC_URL;   
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(url));  
			startActivity(intent);
			break;
		case R.id.layout_login:
			 LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
			 boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			 if(statusOfGPS){
			
			email = editUserName.getText().toString();
			password = editPassword.getText().toString();
			if (!emailPatterMatching(email)) {
				alertManager.showToast(PitchActivity.this,
						"Please enter a valid Email Id.");
				return;
			}
			if (emailValidation(email) && passwordValidation(password)) {
				if (new NetworkManager(getApplicationContext())

				.isInternetConnected()) {
					// new LoginAsyncTask().execute(
					// "demo02@demo.com", "demo02").toString();
					new LoginAsyncTask().execute(email, password).toString();

				} else {
					alertManager.showNetworkErrorToast(PitchActivity.this);
				}

			} else {
				alertManager.showToast(getApplicationContext(),
						getString(R.string.text_error_email_password));
			}
		}else{
			 Toast.makeText(PitchActivity.this, "Please turn on GPS for fetching location.", Toast.LENGTH_LONG).show();
		}
			break;
		default:
			break;
		}

	}

	public class LoginAsyncTask extends RoadsFECasAsync {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			IGLogger.d(this, "login request send");
			alertManager.showLoadingProgress(PitchActivity.this);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			IGLogger.d(this, "login response = " + result);
			// alertManager.hideLoadingProgress();
			alertManager.hideLoadingProgress();
			if (result != null) {   
				boolean status = false;
				try {
					JSONObject jsonObject = new JSONObject(result);
					status = jsonObject.getBoolean("status");
					IGLogger.d(this, "status " + status);
					if (status) {
						Login login = new Login();
						login.setUserName(email);
						login.setPassword(password);
						String token = jsonObject.getString("token");
						Configuration.Token=token;
						login.setToken(token);
						StoreLoginToken(token);
						Utils.getInstance().setToken(token);
						Utils.getInstance().setLogin(login);
						startActivity(new Intent(PitchActivity.this,
								LandingActivity.class));
						preferenceManager.saveLogin(login);
						finish();
					} else {
						alertManager.showToast(PitchActivity.this,
								"Please check your credentials");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				IGLogger.d(this, "token = " + Utils.getInstance().getToken());
			}

			else {
				alertManager.showToast(PitchActivity.this,
						"Please check your credentials.");
			}
		}
	}

	private class CreateAccount extends IGCreateAccount {
		String email;

		public CreateAccount(String email) {
			this.email = email;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			alertManager.showLoadingProgress(PitchActivity.this);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			alertManager.hideLoadingProgress();
			if (result != null) {
				result = result.substring(result.indexOf("{"), result.length());
				try {
					JSONObject response = new JSONObject(result);
					boolean status = response.getBoolean("status");
					if (status) {
						password = response.getString("password");
						new LoginAsyncTask().execute(email, password);

					} else {
						String statusMessage = response
								.getString("statusMessage");
						alertManager
								.showToast(PitchActivity.this,
										"An account with this email address already exists. Please log in.");
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private boolean emailPatterMatching(String email) {
		boolean status = false;
		// Pattern emailPattern = Pattern
		// .compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@"
		// + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\."
		// + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");

		String pattern = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";
		Pattern emailPattern = Pattern.compile(pattern,
				Pattern.CASE_INSENSITIVE);
		Matcher matcher = emailPattern.matcher(email);
		if (matcher.matches()) {
			status = true;
		} else {
			status = false;
		}
		return status;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// Utils.getInstance().setToken(null);
		// Utils.getInstance().setLogin(null);
	}
}
