package com.integritygiving.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.integrity.giving.R;
import com.integritygiving.model.OfferDisplayDetail;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tz.sdk.base.TZUtils;

public class FavoritesListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<OfferDisplayDetail> arrayListOffer;
	private ClickListener clickListener;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;

	public FavoritesListAdapter(Context context,
			ArrayList<OfferDisplayDetail> arrayListOffer) {
		this.context = context;
		this.arrayListOffer = arrayListOffer;
		imageLoader = ImageLoader.getInstance();
		ImageLoaderConfiguration config = ImageLoaderConfiguration
				.createDefault(context);
		imageLoader.init(config);
		options = getImageDisplayOptions(context);
		clickListener = new ClickListener();
	}

	@Override
	public int getCount() {
		return arrayListOffer.size();
	}

	@Override
	public Object getItem(int position) {
		return arrayListOffer.get(position);
	}

	@Override
	public long getItemId(int postition) {
		return postition;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.cell_favorites, null);
			viewHolder = new ViewHolder();
			viewHolder.textOfferName = (TextView) convertView
					.findViewById(R.id.text_offer_name);
			viewHolder.textAddress = (TextView) convertView
					.findViewById(R.id.text_address);
			// viewHolder.textOfferPrice = (TextView) convertView
			// .findViewById(R.id.text_offer_price);
			viewHolder.textCategory = (TextView) convertView
					.findViewById(R.id.text_category);
			viewHolder.textDays = (TextView) convertView
					.findViewById(R.id.text_days_left);
			viewHolder.textOfferexpire = (TextView) convertView
					.findViewById(R.id.text_offer_expire);
			// viewHolder.layoutViewOffer = (RelativeLayout) convertView
			// .findViewById(R.id.layout_view_offer);
			viewHolder.textVenue = (TextView) convertView
					.findViewById(R.id.text_venue_name);
			viewHolder.layoutParent = (RelativeLayout) convertView
					.findViewById(R.id.layout_parent);
			viewHolder.imageOffer = (ImageView) convertView
					.findViewById(R.id.image_offer);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		OfferDisplayDetail offer = arrayListOffer.get(position);
		viewHolder.textOfferName.setText(offer.name);
		

		if(!(offer.address.contains("null"))){
			viewHolder.textAddress.setText(offer.address);  
		}
		
		
	//	viewHolder.textAddress.setText(offer.address);
		viewHolder.textCategory.setText(offer.category);
		viewHolder.textVenue.setText(offer.venueName);
		viewHolder.textDays.setText(String.valueOf(offer.daysLeft) + "\n"
				+ context.getString(R.string.text_days_left));
		if (offer.valid) {
			viewHolder.layoutParent.setBackgroundColor(context.getResources()
					.getColor(R.color.color_landing_bg));
			// viewHolder.layoutViewOffer.setVisibility(View.VISIBLE);
			viewHolder.textOfferexpire.setVisibility(View.GONE);

		} else {
			viewHolder.textOfferexpire.setVisibility(View.VISIBLE);
			// viewHolder.layoutViewOffer.setVisibility(View.GONE);
			viewHolder.layoutParent.setBackgroundColor(context.getResources()
					.getColor(R.color.color_title_bg));
		}
		if (offer.offerImageUri != null && offer.offerImageUri.length() > 0) {
			// viewHolder.imageOffer.setVisibility(View.VISIBLE);
			String url = new TZUtils().affeServerUrl + offer.offerImageUri
					+ "/";
			imageLoader.displayImage(url, viewHolder.imageOffer, options);
		} else {
			viewHolder.imageOffer.setImageResource(R.drawable.no_image);
		}
		return convertView;
	}

	private class ViewHolder {
		private ImageView imageOffer;
		private TextView textVenue;
		private TextView textOfferName;
		private TextView textAddress;
		// private TextView textOfferPrice;
		private TextView textCategory;
		private TextView textDays;
		private TextView textOfferexpire;
		// private RelativeLayout layoutViewOffer;
		private RelativeLayout layoutParent;

	}

	private class ClickListener implements OnClickListener {

		@Override
		public void onClick(View view) {
			int position = (Integer) view.getTag();
			// Offer offer = arrayListOffer.get(position);
			// context.startActivity(new Intent(context,
			// OfferMapActivity.class));
		}

	}

	private DisplayImageOptions getImageDisplayOptions(Context context) {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.no_image)
				.showImageForEmptyUri(R.drawable.no_image)
				.showImageOnFail(R.drawable.no_image).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true).build();
		// DisplayImageOptions options = new DisplayImageOptions.Builder()
		// .cacheInMemory(true).bitmapConfig(Bitmap.Config.RGB_565)
		// .showStubImage(0).build();
		return options;
	}

}
