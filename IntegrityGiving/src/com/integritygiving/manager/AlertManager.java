package com.integritygiving.manager;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.integrity.giving.R;

public class AlertManager {

	private ProgressDialog progressDialog;

	public void showToast(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}

	public void showLoadingProgress(Context context) {
		// if (progressDialog == null) {
		progressDialog = new ProgressDialog(context,
				ProgressDialog.THEME_HOLO_LIGHT);
		progressDialog.setCancelable(false);
		// }
		if (!progressDialog.isShowing()) {
			progressDialog.setMessage(context.getString(R.string.text_loading));
			progressDialog.show();
		}
	}

	public void hideLoadingProgress() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.hide();
		}
	}

	public void showNetworkErrorToast(Context context) {
		Toast.makeText(context, "Please check your internet connection.",
				Toast.LENGTH_LONG).show();
	}
}
