package com.integritygiving.activities;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.integrity.giving.R;
import com.integritygiving.constants.Constants;
import com.integritygiving.logger.IGLogger;
import com.integritygiving.model.Address;
import com.integritygiving.model.ContactDetail;
import com.integritygiving.model.Location;
import com.integritygiving.model.MarketOffersList;
import com.integritygiving.model.MarketOffersResponse;
import com.integritygiving.model.Offer;
import com.integritygiving.model.OfferDetail;
import com.integritygiving.model.OfferDisplayDetail;
import com.integritygiving.model.OfferResponse;
import com.integritygiving.model.Venue;
import com.integritygiving.utils.Utils;
import com.integritygiving.view.TouchableWrapper.UpdateMapAfterUserInterection;

public class OfferMapActivity extends BaseActivity implements
		UpdateMapAfterUserInterection {

	private GoogleMap map;
	private PopupWindow popupWindow;
	private ArrayList<OfferDisplayDetail> arrayListOfferDisplayDetail;
	private View parent;
	private Location mapZoomLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater.from(this).inflate(R.layout.activity_offer_map,
				getRootLayout(), true);
		map = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		// map.setOnMapClickListener(new OnMapClickListener() {
		//
		// @Override
		// public void onMapClick(LatLng arg0) {
		// IGLogger.d(this, "setOnMapClickListener");
		// if (popupWindow != null && popupWindow.isShowing()) {
		// popupWindow.dismiss();
		// popupWindow = null;
		// }
		// }
		// });
		map.setOnCameraChangeListener(new OnCameraChangeListener() {
			@Override
			public void onCameraChange(CameraPosition arg0) {
				IGLogger.d(this, "setOnCameraChangeListener");
				if (popupWindow != null && popupWindow.isShowing()) {
					popupWindow.dismiss();
					popupWindow = null;
				}
			}
		});

		OfferResponse offerResponse = (OfferResponse) getIntent()
				.getSerializableExtra("offers");
		mapZoomLocation = (Location) getIntent().getSerializableExtra(
				Constants.EXTRA_MAP_ZOOM_LOCATION);
		if (offerResponse == null) {
			MarketOffersResponse marketOfferResponse = (MarketOffersResponse) getIntent()
					.getSerializableExtra("market_offers");
			getOffers(marketOfferResponse);
		} else {
			getOffers(offerResponse);
		}
		map.setOnMarkerClickListener(markerClickListener);
		setMapZoomLocation();
	}

	private void setMapZoomLocation() {
		CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(
				mapZoomLocation.latitude, mapZoomLocation.longitude));
		CameraUpdate zoom = CameraUpdateFactory.zoomTo(5);
		map.moveCamera(center);
		map.animateCamera(zoom);
	}

	private void addMarker(Venue venue, int index) {
		IGLogger.d(this, "index = " + index);
		MarkerOptions markerOption = new MarkerOptions();
		Location location = null;
		location = venue.geolocation;
		if (location == null) {
			if (venue.content != null) {
				location = venue.content.geolocation;
			} else {
				android.location.Address address = Utils.getInstance()
						.getLocationFromAddress(
								getApplicationContext(),
								venue.address.city + " "
										+ venue.address.postcode + " "
										+ venue.address.stateProv);
				Location temp = new Location();
				temp.latitude = address.getLatitude();
				temp.longitude = address.getLongitude();
				location = temp;
			}
		}
		markerOption
				.position(new LatLng(location.latitude, location.longitude));
		IGLogger.d(this, "index = " + index);
		markerOption.title("" + index);
		BitmapDescriptor icon = BitmapDescriptorFactory
				.fromResource(R.drawable.map_pin_shadow);
		markerOption.icon(icon);
		map.addMarker(markerOption);
	}

	private OnMarkerClickListener markerClickListener = new OnMarkerClickListener() {

		@Override
		public boolean onMarkerClick(Marker marker) {
			final int index = Integer.parseInt(marker.getTitle());
			Log.d("TAG", "MarkerClickListener " + index);
			final OfferDisplayDetail displayDetail = arrayListOfferDisplayDetail
					.get(index);
			if (popupWindow == null) {
				View popUpView = LayoutInflater.from(getApplicationContext())
						.inflate(R.layout.view_map_marker, null);
				parent = popUpView.findViewById(R.id.layout_parent);

				popupWindow = new PopupWindow(OfferMapActivity.this);
				popupWindow.setContentView(popUpView);
				popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
				popupWindow.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
				popupWindow.showAtLocation(findViewById(R.id.layout_parent),
						Gravity.CENTER, 0, 0);
			}
			parent.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					popupWindow.dismiss();
					popupWindow = null;
					parent = null;
					Intent intent = new Intent(OfferMapActivity.this,
							OfferListActivity.class);
					// intent.putParcelableArrayListExtra("offer_from_map",
					// getSelecteVenueOffers(index));
					intent.putExtra("offer_from_map",
							getSelecteVenueOffers(index));
					// intent.putExtra("offer", displayDetail);
					startActivity(intent);
				}
			});
			TextView textViewVenueName = (TextView) parent
					.findViewById(R.id.text_venue_name);
			textViewVenueName.setText(displayDetail.venueName);
			TextView textViewAddress = (TextView) parent
					.findViewById(R.id.text_address);
			textViewAddress.setText(displayDetail.address);
			TextView textViewCurrentOffers = (TextView) parent
					.findViewById(R.id.text_current_offers);
			textViewCurrentOffers.setText(String.valueOf(getSelecteVenueOffers(
					index).size()));
			return true;
		}
	};

	private void getOffers(OfferResponse offerResponse) {
		IGLogger.d(this, "" + offerResponse.availableOffers.size());
		arrayListOfferDisplayDetail = new ArrayList<OfferDisplayDetail>();
		// for (OfferDetail offerDetail : offerResponse.availableOffers) {
		for (int j = 0; j < offerResponse.availableOffers.size(); j++) {
			OfferDetail offerDetail = offerResponse.availableOffers.get(j);
			Venue venue = offerDetail.venue;
			// addMarker(venue, j);
			if (offerDetail.offers.size() > 0) {
				// IGLogger.d(this,
				// "offer size = " + offerDetail.offers.get(0).name);
				for (int i = 0; i < offerDetail.offers.size(); i++) {
					Offer offer = offerDetail.offers.get(i);
					OfferDisplayDetail displayDetail = new OfferDisplayDetail();
					displayDetail.id = offer.id;
					displayDetail.offersCount = i + 1;
					displayDetail.name = offer.name;
					displayDetail.description = offer.description;
					displayDetail.venueName = venue.content.name;
					displayDetail.validPeriod = offer.validPeriod;
					displayDetail.offerImageUri = offer.offerImageUri;
					Address address = venue.content.address;
					displayDetail.address = address.address1 + " "
							+ address.address2 + " " + address.city + " "
							+ address.stateProv + " " + address.postcode;
					displayDetail.staticId = venue.content.staticId;
					displayDetail.sysId = venue.content.sysId;
					displayDetail.geolocation = venue.content.geolocation;
					IGLogger.d(this, venue.content.staticId + " : "
							+ venue.content.sysId);
					String value = venue.distance.value;
					displayDetail.distance = value.substring(0,
							value.indexOf(".") + 2);
					displayDetail.metric = venue.distance.metric;
					ArrayList<ContactDetail> listContactDetails = venue.content.contactDetails;
					IGLogger.d(this, "size = " + listContactDetails.size());
					// for (int k = 0; k < listContactDetails.size(); k++) {
					// ContactDetail contact = listContactDetails.get(k);
					// IGLogger.d(this, contact.type + " : values "
					// + contact.value);
					// if (contact.type.equalsIgnoreCase("CONTACT")) {
					// displayDetail.phone = contact.value;
					// IGLogger.d(this, "PHONE " + contact.value + ""
					// + offer.name);
					// break;
					// }
					// }
					displayDetail.phone = venue.content.address.mainPhone;
					ArrayList<String> attributes = venue.content.attributes;
					if (attributes.size() > 0) {
						String attribute = attributes.get(0);
						if (attribute == null) {
							displayDetail.category = "";
						} else {
							displayDetail.category = attributes.get(0);
						}
					}
					arrayListOfferDisplayDetail.add(displayDetail);
					addMarker(venue, arrayListOfferDisplayDetail.size() - 1);
				}

			}
		}
	}

	private void getOffers(MarketOffersResponse marketOffersResponse) {
		IGLogger.d(this, "no of market offers"
				+ marketOffersResponse.availableOffers.size());
		// ArrayList<OfferDisplayDetail> arrayListOfferDisplayDetail = new
		// ArrayList<OfferDisplayDetail>();
		if (marketOffersResponse.availableOffers.size() > 0) {
			arrayListOfferDisplayDetail = new ArrayList<OfferDisplayDetail>();
			for (MarketOffersList marketOffer : marketOffersResponse.availableOffers) {
				for (int i = 0; i < marketOffer.offers.size(); i++) {
					Offer offer = marketOffer.offers.get(i);
					Venue venue = marketOffer.venue;
					// addMarker(venue, i);
					OfferDisplayDetail displayDetail = new OfferDisplayDetail();
					displayDetail.id = offer.id;
					displayDetail.name = offer.name;
					displayDetail.offersCount = i + 1;
					displayDetail.description = offer.description;
					displayDetail.venueName = venue.name;
					IGLogger.d(this, "venue name = " + displayDetail.venueName);
					displayDetail.geolocation = venue.geolocation;
					displayDetail.offerImageUri = offer.offerImageUri;
					displayDetail.validPeriod = offer.validPeriod;
					Address address = venue.address;
					displayDetail.address = address.address1 + " "
							+ address.address2 + " " + address.city + " "
							+ address.stateProv + " " + address.postcode;
					displayDetail.staticId = venue.staticId;
					displayDetail.sysId = venue.sysId;
					// ArrayList<ContactDetail> listContactDetails =
					// venue.contactDetails;
					// for (int j = 0; j < listContactDetails.size(); j++) {
					// ContactDetail contact = listContactDetails.get(j);
					// if (contact.type.equalsIgnoreCase("CONTACT")) {
					// displayDetail.phone = contact.value;
					// IGLogger.d(this, " PHONE " + contact.value);
					// break;
					// }
					// }
					displayDetail.phone = venue.address.mainPhone;
					ArrayList<String> attributes = venue.attributes;
					if (attributes.size() > 0) {
						displayDetail.category = attributes.get(0);
					}
					displayDetail.valid = offer.valid;
					arrayListOfferDisplayDetail.add(displayDetail);
					addMarker(venue, arrayListOfferDisplayDetail.size() - 1);
				}
			}

		}
		// IGLogger.d(this, "total offers = " +
		// arrayListOfferDisplayDetail.size());
		// return arrayListOfferDisplayDetail;
	}

	private ArrayList<OfferDisplayDetail> getSelecteVenueOffers(int index) {
		ArrayList<OfferDisplayDetail> selectedVenueOffers = new ArrayList<OfferDisplayDetail>();
		OfferDisplayDetail displayDetail = arrayListOfferDisplayDetail
				.get(index);
		String venueName = displayDetail.venueName;
		for (OfferDisplayDetail offerDisplayDetail : arrayListOfferDisplayDetail) {
			if (offerDisplayDetail.venueName.equalsIgnoreCase(venueName)) {
				selectedVenueOffers.add(offerDisplayDetail);
			}

		}
		return selectedVenueOffers;
	}

	@Override
	public void onUpdateMapAfterUserInterection() {
		IGLogger.d(this, "onUpdateMapAfterUserInterection");
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
			popupWindow = null;
		}
	}
}
