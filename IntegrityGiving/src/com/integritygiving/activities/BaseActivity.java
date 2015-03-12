package com.integritygiving.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.integrity.giving.R;
import com.integritygiving.manager.AlertManager;

public class BaseActivity extends FragmentActivity {

	protected ImageView imageMap;
	protected AlertManager alertManager;
	protected Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);   
		setContentView(R.layout.activity_base);
		alertManager = new AlertManager();  
		handler = new Handler();  
		imageMap = (ImageView) findViewById(R.id.image_map);  
	}

	protected RelativeLayout getRootLayout() {
		RelativeLayout rootLayoutChild = (RelativeLayout) findViewById(R.id.layout_child);
		return rootLayoutChild;
	}
}
