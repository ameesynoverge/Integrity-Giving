package com.integritygiving.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Spinner;
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
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.integrity.giving.R;
import com.integritygiving.adapter.OfferListAdapter;
import com.integritygiving.configuration.Configuration;
import com.integritygiving.constants.Constants;
import com.integritygiving.logger.IGLogger;
import com.integritygiving.model.Address;
import com.integritygiving.model.AttributeObjs;
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

public class OfferListActivityForNoti extends BaseActivity implements OnClickListener {

	private ListView listOffers;
	private OfferListAdapter adapter;
	private GoogleMap map;
	private SearchView searchView;
	private Spinner spinnerSortingParam;
	private final int SORT_BY_DISTANCE = 0;
	private final int SORT_BY_CATEGORY = 1;
	private final int SORT_BY_VENUE = 2;
	private int sortOrder = 0;
	private ArrayList<OfferDisplayDetail> arrayListOfferDisplayDetail;
	// private GoogleMap map;
	private PopupWindow popupWindow;
	private View parent;
	private String extra;
	// private MarketOffersResponse marketOfferResponse;
	private boolean isMarketOfferShown = false;
	private ArrayAdapter<CharSequence> adapterSortingParam;
	private boolean showCategorySortOption;
	private RelativeLayout layoutSortOffer;
	private int offerScreenMode;
	private Location mapZoomLocation;
	OfferResponse offerResponse;
	
	 public static final String MyPREFERENCES = "MyPrefs" ;
	 public static final String Password = "Password"; 
     public static final String UserName= "UserName"; 
    
	public static SharedPreferences prefs;
	SharedPreferences sharedpreferences;
	String  Uname,password;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater.from(this).inflate(R.layout.activity_offer_list,
				getRootLayout(), true);
		layoutSortOffer = (RelativeLayout) findViewById(R.id.layout_sort);
		spinnerSortingParam = (Spinner) findViewById(R.id.spinner_sorting_param);   
		searchView = (SearchView) findViewById(R.id.search_offer);
		listOffers = (ListView) findViewById(R.id.list_offers);
		isMarketOfferShown = getIntent().getBooleanExtra(
				Constants.EXTRA_MARKET_OFFERS_SHOWN, false);
		offerScreenMode = getIntent().getIntExtra(
				Constants.EXTRA_OFFER_SCREEN_MODE, 1);
		
		int mode=offerScreenMode;
		
		arrayListOfferDisplayDetail = (ArrayList<OfferDisplayDetail>) getIntent().getExtras().get("offer_from_map");
		
		Intent intent = getIntent();   
		extra = intent.getStringExtra("notification");
		String notificationResponce=intent.getStringExtra("notificationResponce");  
		
		
		prefs = PreferenceManager.getDefaultSharedPreferences(this); 
		
		sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
		
		 Uname=sharedpreferences.getString(UserName, "");
		 password=sharedpreferences.getString(Password, "");			
				
		 
		if (arrayListOfferDisplayDetail == null) {
			imageMap.setVisibility(View.VISIBLE);
			imageMap.setOnClickListener(this);
			mapZoomLocation = (Location) getIntent().getSerializableExtra(
					Constants.EXTRA_MAP_ZOOM_LOCATION);
			map = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();
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
			map.setOnMarkerClickListener(markerClickListener);
			setMapZoomLocation();
		}
		showCategorySortOption = getIntent().getBooleanExtra(
				Constants.EXTRA_SHOW_CATEGORY_SORT_OPTION, true);
		if (arrayListOfferDisplayDetail == null) {
			
			if (extra != null
					&& extra.equals("true")
					&& (intent.getFlags() & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) == 0) {
			
				 ShowNotificationOffer(notificationResponce);
				 getOffersNotification(offerResponse);
				
			}else{
			findViewById(R.id.layout_search_view).setVisibility(View.VISIBLE);
			OfferResponse offerResponseOffer = (OfferResponse) getIntent().getSerializableExtra("offers");
			if (offerResponseOffer == null) {
				MarketOffersResponse marketOfferResponse = (MarketOffersResponse) getIntent()
						.getSerializableExtra("market_offers");
				getOffers(marketOfferResponse);
				isMarketOfferShown = true;
			} else {
				getOffers(offerResponseOffer);
			}
			}
		}
		if (arrayListOfferDisplayDetail != null
				&& arrayListOfferDisplayDetail.size() > 0) {
			adapter = new OfferListAdapter(this, arrayListOfferDisplayDetail,
					isMarketOfferShown);
			listOffers.setAdapter(adapter);
			listOffers.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> adapterView, View arg1,
						int position, long arg3) {
					OfferDisplayDetail displayDetail = (OfferDisplayDetail) adapterView
							.getItemAtPosition(position);
					Intent intent = new Intent(OfferListActivityForNoti.this,
							RedeemActivity.class);
					intent.putExtra("offer", displayDetail);
					startActivity(intent);
				}
			});
		}
		searchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				IGLogger.d(this, "onQueryTextSubmit");
				InputMethodManager inputManager = (InputMethodManager) OfferListActivityForNoti.this
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.hideSoftInputFromWindow(
						searchView.getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText) {

				if (adapter == null) {
					return false;
				}
				adapter.getFilter().filter(newText);
				return false;
			}
		});

		IGLogger.d(this, "isMarketOfferShown = " + isMarketOfferShown
				+ " showCategorySortOption = " + showCategorySortOption);
		if (isMarketOfferShown) {
			if (showCategorySortOption) {
				// adapterSortingParam = ArrayAdapter.createFromResource(this,
				// R.array.array_sorting_param_market_offers,
				// android.R.layout.simple_spinner_item);
				adapterSortingParam = ArrayAdapter.createFromResource(this,
						R.array.array_sorting_param_market_offers,
						R.layout.view_spinner_text);
			}
		} else {
			if (showCategorySortOption) {
				// adapterSortingParam = ArrayAdapter.createFromResource(this,
				// R.array.array_sorting_param,
				// android.R.layout.simple_spinner_item);
				adapterSortingParam = ArrayAdapter.createFromResource(this,
						R.array.array_sorting_param_market_offers,
						R.layout.view_spinner_text);
			} else {
				// adapterSortingParam = ArrayAdapter.createFromResource(this,
				// R.array.array_sorting_param_without_category,
				// android.R.layout.simple_spinner_item);
				adapterSortingParam = ArrayAdapter.createFromResource(this,
						R.array.array_sorting_param_market_offers,
						R.layout.view_spinner_text);
			}
		}
		if (adapterSortingParam != null) {
			spinnerSortingParam.setAdapter(adapterSortingParam);
			spinnerSortingParam
					.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> parent,
								View view, int position, long id) {
							if (sortOrder != position) {
								sortOrder = position;
								sortOffers();
							}
						}

						@Override
						public void onNothingSelected(AdapterView<?> parent) {

						}
					});
		} else {
			layoutSortOffer.setVisibility(View.GONE);
		}

		if (offerScreenMode == Constants.OFFER_SCREEN_MODE_MAP) {
			toggleScreenView();
			// imageMap.performLongClick();
		}
	}

	   
/*	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		 
		 Intent intent=null;
		
		 intent=new Intent(this,FindOfferChangedActivity.class);  
		
		 startActivity(intent);
		 
	   Login login=new Login();
	   login.setUserName(Uname);
	   login.setPassword(password);	
	}*/
	
	private void ShowNotificationOffer(String notificationResponce) {
		// TODO Auto-generated method stub
		
		
		if (notificationResponce != null) {
			try {
				offerResponse = new OfferResponse();
				Gson gson = new Gson();
				offerResponse = gson.fromJson(notificationResponce,
						offerResponse.getClass());   
				if (offerResponse.token != null) {
					Utils.getInstance().setToken(offerResponse.token);
				}
				/*Intent intent = new Intent(OfferListActivityNotification.this,
						OfferListActivityNotification.class);
				intent.putExtra(Constants.EXTRA_SHOW_CATEGORY_SORT_OPTION,
						showCategorySortOption);
				intent.putExtra(Constants.EXTRA_OFFER_SCREEN_MODE,
						offerScreenMode);
				intent.putExtra(Constants.EXTRA_MAP_ZOOM_LOCATION,
						mapZoomLocation);
				intent.putExtra("offers", offerResponse);
				startActivity(intent);*/
			} catch (JsonSyntaxException e) {

			}
		} else {
			//alertManager.showToast(OfferListActivity.this,"Offers are not available.");
		}
		
	}
	
	private void setMapZoomLocation() {
		//CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(	mapZoomLocation.latitude, mapZoomLocation.longitude));
		try {
			CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(Double.valueOf(Configuration.Latitude), Double.valueOf(Configuration.Longitude)));
			CameraUpdate zoom = CameraUpdateFactory.zoomTo(8);
			map.moveCamera(center);
			map.animateCamera(zoom);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

	@Override
	public void onClick(View view) {
		IGLogger.d(this, "onClick");
		switch (view.getId()) {
		case R.id.image_map:
			toggleScreenView();
			break;

		}
	}

	private void toggleScreenView() {
		if (findViewById(R.id.layout_offer_list).getVisibility() == View.VISIBLE) {
			findViewById(R.id.layout_offer_list).setVisibility(View.GONE);
			// listOffers.setVisibility(View.GONE);
			findViewById(R.id.layout_map).setVisibility(View.VISIBLE);
			imageMap.setImageResource(R.drawable.icon_menu_green);
		} else {
			findViewById(R.id.layout_offer_list).setVisibility(View.VISIBLE);
			// listOffers.setVisibility(View.VISIBLE);
			findViewById(R.id.layout_map).setVisibility(View.GONE);
			imageMap.setImageResource(R.drawable.seeonmap_green);
		}
	}

	private void addMarker(Venue venue, int index) {
		// if (map == null) {
		// return;
		// }
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

	
	private void getOffersNotification(OfferResponse offerResponse) {
		IGLogger.d(this, "get offer other response");
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
					displayDetail.venueName = venue.name;
					displayDetail.validPeriod = offer.validPeriod;
					displayDetail.offerImageUri = offer.offerImageUri;
					Address address = venue.address;
					displayDetail.address = address.address1 + " "
							+ address.address2 + " " + address.city + " "
							+ address.stateProv + " " + address.postcode;
					displayDetail.staticId = venue.staticId;
					displayDetail.sysId = venue.sysId;
					displayDetail.geolocation = venue.geolocation;
					//IGLogger.d(this, venue.content.staticId + " : "+ venue.content.sysId);
					//String value = venue.distance.value;
					displayDetail.distance ="";
					displayDetail.metric = "";
					ArrayList<ContactDetail> listContactDetails = venue.contactDetails;
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
					displayDetail.phone ="";
					// ArrayList<String> attributes = venue.content.attributes;
					// if (attributes.size() > 0) {
					// String attribute = attributes.get(0);
					// if (attribute == null) {
					// displayDetail.category = "";
					// } else {
					// displayDetail.category = attributes.get(0);
					// }
					// }
					/*ArrayList<AttributeObjs> attributesObj = venue.content.attributeObjs;
					if (attributesObj.size() > 0) {
						displayDetail.category = attributesObj.get(0).displayName;
					}*/
					arrayListOfferDisplayDetail.add(displayDetail);
					addMarker(venue, arrayListOfferDisplayDetail.size() - 1);
				}

			}
		}

	}
	private void getOffers(OfferResponse offerResponse) {
		IGLogger.d(this, "get offer other response");
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
					// ArrayList<String> attributes = venue.content.attributes;
					// if (attributes.size() > 0) {
					// String attribute = attributes.get(0);
					// if (attribute == null) {
					// displayDetail.category = "";
					// } else {
					// displayDetail.category = attributes.get(0);
					// }
					// }
					ArrayList<AttributeObjs> attributesObj = venue.content.attributeObjs;
					if (attributesObj.size() > 0) {
						displayDetail.category = attributesObj.get(0).displayName;
					}
					arrayListOfferDisplayDetail.add(displayDetail);
					addMarker(venue, arrayListOfferDisplayDetail.size() - 1);
				}

			}
		}
		Collections.sort(arrayListOfferDisplayDetail, new DistanceComprator());
	}

	private void getOffers(MarketOffersResponse marketOffersResponse) {
	//	IGLogger.d(this, "no of market offers"	+ marketOffersResponse.availableOffers.size());
		// ArrayList<OfferDisplayDetail> arrayListOfferDisplayDetail = new
		// ArrayList<OfferDisplayDetail>();   
		if (marketOffersResponse.availableOffers.size() > 0) {
			arrayListOfferDisplayDetail = new ArrayList<OfferDisplayDetail>();
			for (MarketOffersList marketOffer : marketOffersResponse.availableOffers) {
				for (int i = 0; i < marketOffer.offers.size(); i++) {
					Offer offer = marketOffer.offers.get(i);
					Venue venue = marketOffer.venue;
					OfferDisplayDetail displayDetail = new OfferDisplayDetail();
					displayDetail.id = offer.id;
					displayDetail.name = offer.name;
					displayDetail.description = offer.description;  
					displayDetail.venueName = venue.name;
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
					ArrayList<AttributeObjs> attributesObj = venue.attributeObjs;
					if (attributesObj.size() > 0) {
						displayDetail.category = attributesObj.get(0).displayName;
					}
					if (displayDetail.category == null) {
						displayDetail.category = "";
					}
					displayDetail.valid = offer.valid;
					arrayListOfferDisplayDetail.add(displayDetail);
					addMarker(venue, arrayListOfferDisplayDetail.size() - 1);
				}
			}

		}
		// return arrayListOfferDisplayDetail;
		if (arrayListOfferDisplayDetail != null
				&& arrayListOfferDisplayDetail.size() > 0) {
			Collections.sort(arrayListOfferDisplayDetail,
					new CategoryComprator());
		}
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

				popupWindow = new PopupWindow(OfferListActivityForNoti.this);
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
					Intent intent = new Intent(OfferListActivityForNoti.this,
							OfferListActivityForNoti.class);
					// intent.putParcelableArrayListExtra("offer_from_map",
					// getSelecteVenueOffers(index));
					intent.putExtra("offer_from_map",
							getSelecteVenueOffers(index));
					intent.putExtra(Constants.EXTRA_MARKET_OFFERS_SHOWN,
							isMarketOfferShown);
					intent.putExtra(Constants.EXTRA_SHOW_CATEGORY_SORT_OPTION,
							showCategorySortOption);
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

	private class VenueComprator implements Comparator<OfferDisplayDetail> {

		@Override
		public int compare(OfferDisplayDetail offer1, OfferDisplayDetail offer2) {
			int comparison = offer1.venueName.compareTo(offer2.venueName);
			if (comparison != 0) {
				return comparison;
			}
			return 0;
		}

	}

	private class CategoryComprator implements Comparator<OfferDisplayDetail> {

		@Override
		public int compare(OfferDisplayDetail offer1, OfferDisplayDetail offer2) {
			// if (offer1.category == null) {
			// offer1.category = "";
			// }
			// if (offer2.category == null) {
			// offer2.category = "";
			// }
			int comparison = offer1.category
					.compareToIgnoreCase(offer2.category);
			if (comparison != 0) {
				return comparison;
			}
			return 0;
		}
	}

	private class DistanceComprator implements Comparator<OfferDisplayDetail> {

		@Override
		public int compare(OfferDisplayDetail offer1, OfferDisplayDetail offer2) {
			float distance1 = Float.valueOf(offer1.distance);
			float distance2 = Float.valueOf(offer2.distance);
			if (distance1 < distance2) {
				return -1;
			}
			if (distance1 > distance2) {
				return 1; // Fails on NaN however, not sure what you want
			}
			return 0;
		}

	}

	private void sortOffers() {
		int order;
		if (isMarketOfferShown) {
			order = sortOrder + 1;
		} else {
			if (showCategorySortOption) {
				order = sortOrder;
			} else {
				if (sortOrder == 1) {
					order = sortOrder + 1;
				} else {
					order = sortOrder;
				}
			}
		}
		switch (order) {
		case SORT_BY_DISTANCE:
			if (arrayListOfferDisplayDetail != null
					&& arrayListOfferDisplayDetail.size() > 0) {
				Collections.sort(arrayListOfferDisplayDetail,
						new DistanceComprator());
				adapter.setData(arrayListOfferDisplayDetail);
				adapter.notifyDataSetChanged();
			}
			break;
		case SORT_BY_CATEGORY:
			if (arrayListOfferDisplayDetail != null
					&& arrayListOfferDisplayDetail.size() > 0) {
				Collections.sort(arrayListOfferDisplayDetail,
						new CategoryComprator());
				adapter.setData(arrayListOfferDisplayDetail);
				adapter.notifyDataSetChanged();
			}
			break;
		case SORT_BY_VENUE:
			if (arrayListOfferDisplayDetail != null
					&& arrayListOfferDisplayDetail.size() > 0) {
				Collections.sort(arrayListOfferDisplayDetail,
						new VenueComprator());
				adapter.setData(arrayListOfferDisplayDetail);
				adapter.notifyDataSetChanged();
			}
			break;
		default:
			break;
		}

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
	// OnMarkerClickListener mOnMarkerClickListener = new
	// OnMarkerClickListener() {
	//
	// @Override
	// public boolean onMarkerClick(Marker marker) {
	// int position = Integer.parseInt(marker.getTitle());
	// View popUpView = LayoutInflater.from(getApplicationContext())
	// .inflate(R.layout.view_map_marker, null);
	// View parent = popUpView.findViewById(R.id.layout_parent);
	//
	// final PopupWindow popupWindow = new PopupWindow(
	// OfferListActivity.this);
	// parent.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// popupWindow.dismiss();
	// startActivity(new Intent(OfferListActivity.this,
	// RedeemActivity.class));
	// }
	// });
	// popupWindow.setContentView(popUpView);
	// popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
	// popupWindow.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
	// popupWindow.showAtLocation(findViewById(R.id.layout_parent),
	// Gravity.CENTER, 0, 0);
	// return true;
	// }
	// };
}
