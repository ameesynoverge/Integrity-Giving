package com.integritygiving.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.integrity.giving.R;
import com.integritygiving.model.Attribute;

public class AttributesAdapter extends BaseAdapter {

	private List<Attribute> arrayListAttributes;
	private Context context;
	private int checkedPosition;
	private boolean hideLastPositionCheck;

	// private AttributesList attributesList;

	public AttributesAdapter(Context context,
			List<Attribute> arrayListAttributes, int checkedPosition,
			boolean hideLastPositionCheck) {
		this.context = context;
		this.arrayListAttributes = arrayListAttributes;
		this.checkedPosition = checkedPosition;
		this.hideLastPositionCheck = hideLastPositionCheck;
	}

	@Override
	public int getCount() {
		return arrayListAttributes.size();
	}

	@Override
	public Object getItem(int position) {
		return arrayListAttributes.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.cell_attributes, null);
			viewHolder.textAttribute = (TextView) convertView
					.findViewById(R.id.text_attribute);
			viewHolder.checkAttribute = (CheckBox) convertView
					.findViewById(R.id.check_attribute);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		Attribute attribute = arrayListAttributes.get(position);
		viewHolder.textAttribute.setText(attribute.displayName);
		if (position == checkedPosition) {
			viewHolder.checkAttribute.setVisibility(View.VISIBLE);
			viewHolder.checkAttribute.setChecked(true);

		} else {
			if (position == arrayListAttributes.size() - 1) {
				if (hideLastPositionCheck) {
					viewHolder.checkAttribute.setVisibility(View.GONE);
				} else {
					viewHolder.checkAttribute.setVisibility(View.VISIBLE);
					viewHolder.checkAttribute.setChecked(false);
				}
			} else {
				viewHolder.checkAttribute.setVisibility(View.VISIBLE);
				viewHolder.checkAttribute.setChecked(false);
			}
		}
		return convertView;
	}

	public void setCheckedPosition(int checkedPosition) {
		this.checkedPosition = checkedPosition;
	}

	public void increaseCheckedPos() {
		if (checkedPosition < arrayListAttributes.size() - 1) {
			checkedPosition++;
		}
	}

	public void decreaseCheckedPos() {
		if (checkedPosition > 0) {
			checkedPosition--;
		}
	}

	public int getCheckedPosition() {
		return checkedPosition;
	}

	private class ViewHolder {
		private TextView textAttribute;
		private CheckBox checkAttribute;

	}
}
