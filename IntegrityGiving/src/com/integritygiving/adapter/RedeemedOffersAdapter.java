package com.integritygiving.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.integrity.giving.R;
import com.integritygiving.model.OfferDisplayDetail;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tz.sdk.base.TZUtils;

public class RedeemedOffersAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<OfferDisplayDetail> arrayListOffer;
	private ClickListener clickListener;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;

	public RedeemedOffersAdapter(Context context,
			ArrayList<OfferDisplayDetail> arrayListOffer) {
		this.context = context;
		this.arrayListOffer = arrayListOffer;
		clickListener = new ClickListener();
		imageLoader = ImageLoader.getInstance();
		ImageLoaderConfiguration config = ImageLoaderConfiguration
				.createDefault(context);
		imageLoader.init(config);
		options = getImageDisplayOptions(context);
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
					R.layout.cell_redeemed_offers, null);
			viewHolder = new ViewHolder();
			viewHolder.textOfferName = (TextView) convertView
					.findViewById(R.id.text_offer_name);
			viewHolder.textVenueName = (TextView) convertView
					.findViewById(R.id.text_venue_name);
			viewHolder.textAddress = (TextView) convertView
					.findViewById(R.id.text_address);
			viewHolder.textCategory = (TextView) convertView
					.findViewById(R.id.text_category);
			viewHolder.redeemedOn = (TextView) convertView
					.findViewById(R.id.text_redeemed_on);
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
		
		viewHolder.textCategory.setText(offer.category);
		viewHolder.textVenueName.setText(offer.venueName);
		viewHolder.redeemedOn.setText(context.getString(R.string.text_redeemed)
				+ " " + offer.lastRedemption);
		if (offer.offerImageUri != null && offer.offerImageUri.length() > 0) {
			String url = new TZUtils().affeServerUrl + offer.offerImageUri + "/";
			imageLoader.displayImage(url, viewHolder.imageOffer, options);
		} else {
			viewHolder.imageOffer.setImageResource(R.drawable.no_image);
		}
		return convertView;
	}

	private class ViewHolder {
		private ImageView imageOffer;
		private TextView textVenueName;
		private TextView textAddress;
		private TextView textOfferName;
		private TextView textCategory;
		private TextView redeemedOn;
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
