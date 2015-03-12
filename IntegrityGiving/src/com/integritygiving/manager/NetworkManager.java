package com.integritygiving.manager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * The Class NetworkManager.
 */
public class NetworkManager {

	/** The context. */
	private Context context;

	/**
	 * Instantiates a new network manager.
	 * 
	 * @param context
	 *            the context
	 */
	public NetworkManager(Context context) {
		this.context = context;
	}

	/**
	 * Checks if is internet connected.
	 * 
	 * @return true, if is internet connected
	 */
	public boolean isInternetConnected() {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;

	}
}
