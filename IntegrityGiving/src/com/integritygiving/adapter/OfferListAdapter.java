package com.integritygiving.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.integrity.giving.R;
import com.integritygiving.model.OfferDisplayDetail;
import com.integritygiving.utils.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tz.sdk.base.TZUtils;

public class OfferListAdapter extends BaseAdapter implements Filterable {

	private Context context;
	private List<OfferDisplayDetail> arrayListofferResponce;
	private List<OfferDisplayDetail> arrayListoriginalValue;
	private ClickListener clickListener;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private String token = null;
	private boolean isMarketOfferShown;

	public OfferListAdapter(Context context,
			ArrayList<OfferDisplayDetail> arrayListofferResponce,
			boolean isMarketOfferShown) {
		this.context = context;
		this.arrayListofferResponce = arrayListofferResponce;
		clickListener = new ClickListener();
		imageLoader = ImageLoader.getInstance();
		ImageLoaderConfiguration config = ImageLoaderConfiguration
				.createDefault(context);
		imageLoader.init(config);
		// options = new DisplayImageOptions.Builder().build();
		options = getImageDisplayOptions(context);
		token = Utils.getInstance().getToken();
		this.isMarketOfferShown = isMarketOfferShown;
		// options.getImageOnFail(context.getResources().getDrawable(
		// R.drawable.ic_launcher));
	}

	@Override
	public int getCount() {
		return arrayListofferResponce.size();
	}

	@Override
	public Object getItem(int position) {
		return arrayListofferResponce.get(position);
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
					R.layout.cell_offer_list, null);
			viewHolder = new ViewHolder();
			viewHolder.textVenue = (TextView) convertView
					.findViewById(R.id.text_venue_name);
			viewHolder.textAddress = (TextView) convertView
					.findViewById(R.id.text_address);
			// viewHolder.textOfferPrice = (TextView) convertView
			// .findViewById(R.id.text_offer_price);
			viewHolder.textCategory = (TextView) convertView
					.findViewById(R.id.text_category);
			viewHolder.textDistance = (TextView) convertView
					.findViewById(R.id.text_distance);
			viewHolder.textOfferName = (TextView) convertView
					.findViewById(R.id.text_offer_name);
			viewHolder.textCategory = (TextView) convertView
					.findViewById(R.id.text_category);
			viewHolder.imageOffer = (ImageView) convertView
					.findViewById(R.id.image_offer);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		OfferDisplayDetail displayDetail = arrayListofferResponce.get(position);
		viewHolder.textVenue.setText(displayDetail.venueName);
		
		if(!(displayDetail.address.contains("null"))){
			viewHolder.textAddress.setText(displayDetail.address);  
		}
		
	
		viewHolder.textOfferName.setText(displayDetail.name);
		viewHolder.textCategory.setText(displayDetail.category);
		if (displayDetail.offerImageUri != null
				&& displayDetail.offerImageUri.length() > 0) {
			String url = new TZUtils().affeServerUrl
					+ displayDetail.offerImageUri + "/";
			imageLoader.displayImage(url, viewHolder.imageOffer, options);
			// viewHolder.imageOffer.setVisibility(View.VISIBLE);
		} else {
			// viewHolder.imageOffer.setVisibility(View.INVISIBLE);
			viewHolder.imageOffer.setImageResource(R.drawable.no_image);
		}
		// viewHolder.textOfferPrice.setText(offer.getOfferPrice());
		// viewHolder.textCategory.setText(offer.getCategory());
		if (isMarketOfferShown) {
			viewHolder.textDistance.setVisibility(View.INVISIBLE);
		} else {
			viewHolder.textDistance.setText(displayDetail.distance + " "
					+ displayDetail.metric);
		}
		// + " miles");
		return convertView;
	}

	private class ViewHolder {
		private TextView textVenue;
		private TextView textAddress;
		// private TextView textOfferPrice;
		private TextView textCategory;
		private TextView textDistance;
		private TextView textOfferName;
		private ImageView imageOffer;

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

	@Override
	public Filter getFilter() {
		Filter filter = new Filter() {

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {
				// has the filtered values
				arrayListofferResponce = (List<OfferDisplayDetail>) results.values;
				// notifies the data with new filtered values
				notifyDataSetChanged();
			}

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				// Holds the results of a filtering operation in values
				FilterResults results = new FilterResults();
				List<OfferDisplayDetail> FilteredArrList = new ArrayList<OfferDisplayDetail>();

				if (arrayListoriginalValue == null) {
					// saves the original data in mOriginalValues
					arrayListoriginalValue = new ArrayList<OfferDisplayDetail>(
							arrayListofferResponce);
				}

				/********
				 * 
				 * If constraint(CharSequence that is received) is null returns
				 * the mOriginalValues(Original) values else does the Filtering
				 * and returns FilteredArrList(Filtered)
				 * 
				 ********/
				if (constraint == null || constraint.length() == 0) {

					// set the Original result to return
					results.count = arrayListoriginalValue.size();
					results.values = arrayListoriginalValue;
				} else {
					constraint = constraint.toString().toLowerCase(Locale.US);
					for (int i = 0; i < arrayListoriginalValue.size(); i++) {
						String data = arrayListoriginalValue.get(i).venueName;
						if (data.toLowerCase(Locale.US).contains(
								constraint.toString())) {
							FilteredArrList.add(arrayListoriginalValue.get(i));
						}
					}
					// set the Filtered result to return
					results.count = FilteredArrList.size();
					results.values = FilteredArrList;
				}
				return results;
			}
		};
		return filter;
	}

	public void setData(ArrayList<OfferDisplayDetail> arrayListofferResponce) {
		this.arrayListofferResponce = arrayListofferResponce;
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
