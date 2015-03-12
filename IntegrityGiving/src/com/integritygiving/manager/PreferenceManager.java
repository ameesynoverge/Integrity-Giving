package com.integritygiving.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.integritygiving.model.Login;

public class PreferenceManager {

	// private PreferenceManager _instance;
	private final String KEY_USER_NAME = "KEY_USER_NAME";
	private final String KEY_PASSWORD = "KEY_PASSWORD";
	private final String IG_PREFERENCES = "IG_PREFERENCES";
	private SharedPreferences sharedPreference;

	public PreferenceManager(Context context) {
		sharedPreference = context.getSharedPreferences(IG_PREFERENCES,
				Context.MODE_PRIVATE);
	}

	// public PreferenceManager getInstance() {
	// if (_instance == null) {
	// _instance = new PreferenceManager();
	// }
	// return _instance;
	// }

	public void saveLogin(Login login) {
		Editor editor = sharedPreference.edit();
		editor.putString(KEY_USER_NAME, login.getUserName());
		editor.putString(KEY_PASSWORD, login.getPassword());
		editor.commit();
	}

	public Login getLogin() {
		String userName = sharedPreference.getString(KEY_USER_NAME, null);
		String password = sharedPreference.getString(KEY_PASSWORD, null);
		if (userName == null || password == null) {
			return null;
		}
		Login login = new Login();
		login.setUserName(userName);
		login.setPassword(password);
		return login;
		// return null;
	}

	public void clearLogin() {
		Editor editor = sharedPreference.edit();
		editor.remove(KEY_USER_NAME);
		editor.remove(KEY_PASSWORD);
		editor.commit();
	}

}
