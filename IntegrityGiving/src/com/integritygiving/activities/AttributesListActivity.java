package com.integritygiving.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.integrity.giving.R;
import com.integritygiving.adapter.AttributesAdapter;
import com.integritygiving.constants.Constants;
import com.integritygiving.model.Attribute;
import com.integritygiving.model.AttributesList;

public class AttributesListActivity extends BaseActivity {
	private ListView listAttributes;
	private AttributesAdapter adapter;
	private LinearLayout layoutDone;
	private Attribute attribute;
	private int position;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater.from(this).inflate(R.layout.activity_attributes_list,
				getRootLayout(), true);
		listAttributes = (ListView) findViewById(R.id.list_attributes);
		layoutDone = (LinearLayout) findViewById(R.id.layout_done);
		AttributesList attributesList = (AttributesList) getIntent()
				.getSerializableExtra(Constants.EXTRA_ATTRIBUTES);
		adapter = new AttributesAdapter(getApplicationContext(),
				attributesList.attributeList, -1, false);
		listAttributes.setAdapter(adapter);
		listAttributes.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				adapter.setCheckedPosition(position);
				adapter.notifyDataSetChanged();
				attribute = (Attribute) parent.getItemAtPosition(position);
				AttributesListActivity.this.position = position;
			}
		});
		layoutDone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (attribute != null) {
					Intent data = new Intent();
					data.putExtra(Constants.EXTRA_SELECTED_ATTRIBUTE, attribute);
					data.putExtra("pos", position);
					setResult(RESULT_OK, data);
				} else {
					setResult(RESULT_CANCELED);
				}
				finish();
			}
		});
	}
}
