package com.integritygiving.adapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.integrity.giving.R;
import com.integritygiving.activities.AttributesListActivity;
import com.integritygiving.activities.BaseActivity;
import com.integritygiving.activities.OfferListActivity;
import com.integritygiving.adapter.AttributesAdapter;
import com.integritygiving.constants.Constants;
import com.integritygiving.logger.IGLogger;
import com.integritygiving.manager.NetworkManager;
import com.integritygiving.model.Attribute;
import com.integritygiving.model.AttributesList;
import com.integritygiving.model.Login;
import com.integritygiving.model.Market;
import com.integritygiving.model.MarketList;
import com.integritygiving.model.MarketOffersResponse;
import com.integritygiving.model.OfferRequest;
import com.integritygiving.model.OfferResponse;
import com.integritygiving.utils.Utils;
import com.tz.sdk.core.TZGetAllMarkets;
import com.tz.sdk.core.TZGetAttributes;
import com.tz.sdk.core.TZGetMarketOffers;
import com.tz.sdk.core.TZGetOffers;

public class FindOfferChangedActivity extends BaseActivity implements
		OnClickListener {

	private MarketList marketList;
	private String attribute = "";
	private Spinner spinnerMarket;
	private String marketName;
	private LinearLayout layoutFindOffers;
	private EditText editSearch;
	private ArrayAdapter<String> arrayMarket;
	private TextView textList;
	private TextView textMap;
	private final int REQUEST_CODE_SELECT_ATTRIBUTE = 1001;
	private ListView listViewAttributes;
	private List<Attribute> ListAttributes;
	private AttributesAdapter attributesAdapter;
	private com.integritygiving.model.Location mapZoomLocation;
	private boolean showCategorySortOption = false;
	private int offerScreenMode = Constants.OFFER_SCREEN_MODE_LIST;
	private AttributesList attributesList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater.from(this).inflate(R.layout.activity_find_offer_changed,
				getRootLayout(), true);
		listViewAttributes = (ListView) findViewById(R.id.list_attributes);
		textList = (TextView) findViewById(R.id.text_list);
		textMap = (TextView) findViewById(R.id.text_map);
		textList.setOnClickListener(this);
		textMap.setOnClickListener(this);
		textList.performClick();
		listViewAttributes.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position < attributesAdapter.getCount() - 1) {
					attributesAdapter.setCheckedPosition(position);
					// if (position == 0) {
					// attribute = "";
					// } else {
					// attribute = ((TextView) view
					// .findViewById(R.id.text_attribute)).getText()
					// .toString();
					// IGLogger.d(this, "attribute =" + attribute);
					// }
					attribute = ListAttributes.get(position).value;
					attributesAdapter.notifyDataSetChanged();
				} else {
					Intent intent = new Intent(getApplicationContext(),
							AttributesListActivity.class);
					intent.putExtra(Constants.EXTRA_ATTRIBUTES, attributesList);
					startActivityForResult(intent,
							REQUEST_CODE_SELECT_ATTRIBUTE);
				}
			}
		});
		layoutFindOffers = (LinearLayout) findViewById(R.id.layout_find_offers);
		editSearch = (EditText) findViewById(R.id.edit_search);
		spinnerMarket = (Spinner) findViewById(R.id.spinner_market);
		layoutFindOffers.setOnClickListener(this);
		spinnerMarket.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0) {
					marketName = String.valueOf(parent
							.getItemAtPosition(position));
					if (ListAttributes.get(0).displayName
							.equalsIgnoreCase(getString(R.string.text_all))) {
						ListAttributes.remove(0);
						attributesAdapter.decreaseCheckedPos();
						attributesAdapter.notifyDataSetChanged();
						attribute = ListAttributes.get(attributesAdapter
								.getCheckedPosition()).value;

					}
					// attributesAdapter.setCheckedPosition(0);
				} else {
					if (!ListAttributes.get(0).displayName
							.equalsIgnoreCase(getString(R.string.text_all))) {
						Attribute attributeAll = new Attribute();
						attributeAll.displayName = getString(R.string.text_all);
						attributeAll.value = "";
						ListAttributes.add(0, attributeAll);
						attributesAdapter.increaseCheckedPos();
						attributesAdapter.notifyDataSetChanged();
						attribute = ListAttributes.get(attributesAdapter
								.getCheckedPosition()).value;
					}
					// attributesAdapter.setCheckedPosition(0);
					marketName = marketList.marketList.get(position - 1).marketName;
					mapZoomLocation = marketList.marketList.get(position - 1).geolocation;

				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		// attribute = getString(R.string.text_casual_dining);
		IGLogger.d(this, "category = " + attribute);
		ArrayList<Attribute> arrayListAttributes = Utils.getInstance()
				.getAttributes();
		if (arrayListAttributes == null || arrayListAttributes.size() == 0) {
			if (new NetworkManager(getApplicationContext())
					.isInternetConnected()) {
				Login login = Utils.getInstance().getLogin();
				if (login != null) {
					new GetCategory().execute(Utils.getInstance().getToken(),
							login.getUserName(), login.getPassword());
				} else {
					new GetCategory().execute(Utils.getInstance().getToken(),
							null, null);
				}
			} else {
				alertManager
						.showNetworkErrorToast(FindOfferChangedActivity.this);
			}
		} else {
			attributesList = new AttributesList();
			attributesList.attributeList.addAll(arrayListAttributes);
			loadListData();
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.layout_find_offers:
			new RequestDataAsyncTask().execute();
			break;
		case R.id.text_list:
			textList.setEnabled(false);
			textMap.setEnabled(true);
			textList.setBackgroundColor(getResources().getColor(
					R.color.color_green));
			textMap.setBackgroundColor(getResources().getColor(
					R.color.color_gray));
			offerScreenMode = Constants.OFFER_SCREEN_MODE_LIST;
			break;
		case R.id.text_map:
			textMap.setEnabled(false);
			textList.setEnabled(true);
			textMap.setBackgroundColor(getResources().getColor(
					R.color.color_green));
			textList.setBackgroundColor(getResources().getColor(
					R.color.color_gray));
			offerScreenMode = Constants.OFFER_SCREEN_MODE_MAP;
			break;
		}
	}

	private class RequestDataAsyncTask extends
			AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			alertManager.showLoadingProgress(FindOfferChangedActivity.this);

		}

		@Override
		protected String doInBackground(String... params) {
			String json = null;
			try {
				OfferRequest request = new OfferRequest();
				request.featuredOnly = false;
				String searchCriteria = editSearch.getText().toString();
				if (searchCriteria != null && searchCriteria.length() > 0) {
					request.searchCriteria = searchCriteria;
				}
				if (attribute.equalsIgnoreCase(getString(R.string.text_all))) {
					attribute = null;
					showCategorySortOption = true;
				}
				// if (attribute != null) {
				// request.attributeObjs = new ArrayList<String>();
				// request.attributeObjs.add(attribute);
				// showCategorySortOption = false;
				// }
				// else {
				// request.attributeObjs = null;
				// }
				request.type = "offer";
				if (marketName.equalsIgnoreCase("Near me")) {
					Location currentLcoation = Utils.getInstance()
							.getCurrentLocation(getApplicationContext());
					if (currentLcoation == null) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								alertManager
										.showToast(
												FindOfferChangedActivity.this,
												"Unable to fetch the current location.");
							}
						});
						return null;
					}
					com.integritygiving.model.Location location = new com.integritygiving.model.Location();
					location.latitude = currentLcoation.getLatitude();
					location.longitude = currentLcoation.getLongitude();
					mapZoomLocation = location;
					// location.latitude = 33.3990891;
					// location.longitude = -111.6846103;
					IGLogger.d(this, "location.longitude " + location.longitude);
					request.fromLocation = location;
				} else {
					request.marketName = marketName;
				}

				Gson gson = new Gson();
				json = gson.toJson(request);
				// String json = createRequestJson(request);
				if (attribute == null) {
					String temp = json.substring(1, json.length());
					String append = "{\"attributeObjs\"" + ":[],";
					json = append + temp;
				} else {
					String temp = json.substring(1, json.length());
					String append = "{\"attributeObjs\"" + ":[{\"value\":\""
							+ attribute + "\"}],";
					json = append + temp;
					showCategorySortOption = false;
				}
				if (searchCriteria == null || searchCriteria.length() == 0) {
					String temp = json.substring(1, json.length());
					String append = "{\"searchCriteria\"" + ":null,";
					json = append + temp;
				}
				String temp = json.substring(1, json.length());
				String append = "{\"venueIds\"" + ":[],";
				json = append + temp;
				temp = json.substring(1, json.length());
				append = "{\"attributes\"" + ":null,";
				json = append + temp;
				IGLogger.d(this, json);
			} catch (Exception e) {
				e.printStackTrace();
			}

			IGLogger.d(this, "changed body = " + json);
			// json =
			// "{\"fromLocation\": {\"latitude\":33.3990891 ,\"longitude\":-111.6846103},\"searchRadius\": 100,\"attributes\":null,\"venueIds\": null,\"type\": \"offer\",\"featuredOnly\": false, \"searchCriteria\": null}";
			return json;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			alertManager.hideLoadingProgress();
			IGLogger.d(this, "result = " + result);
			if (result != null) {
				if (new NetworkManager(getApplicationContext())
						.isInternetConnected()) {
					// Login login = Utils.getInstance().getLogin();
					Login login = Utils.getInstance().getLogin();
					if (marketName.equalsIgnoreCase("Near me")) {
						if (login != null) {
							new GetOffers().execute(Utils.getInstance()
									.getToken(), result, login.getUserName(),
									login.getPassword());
						} else {
							new GetOffers().execute(Utils.getInstance()
									.getToken(), result, null, null);
						}
					} else {
						if (login != null) {
							new GetMarketOffers().execute(Utils.getInstance()
									.getToken(), result, login.getUserName(),
									login.getPassword());
						} else {
							new GetMarketOffers().execute(Utils.getInstance()
									.getToken(), result, null, null);
						}
					}

				} else {
					alertManager
							.showNetworkErrorToast(FindOfferChangedActivity.this);
				}
			}
		}
	}

	private class GetCategory extends TZGetAttributes {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			alertManager.showLoadingProgress(FindOfferChangedActivity.this);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			IGLogger.d(this, "result = " + result);
			alertManager.hideLoadingProgress();
			if (result != null) {
				parseCategories(result);
			} else {
				alertManager.showToast(FindOfferChangedActivity.this,
						"Unable to fetch the category.");
			}
			// setLastSearchResult();
		}
	}

	private void parseCategories(String result) {
		try {
			Gson gson = new Gson();
			attributesList = gson.fromJson(result, AttributesList.class);
			if (attributesList.token != null
					&& attributesList.token.length() > 0) {
				Utils.getInstance().setToken(attributesList.token);
			}
			if (attributesList.attributeList != null
					&& attributesList.attributeList.size() > 0) {
				Utils.getInstance().setAttributes(attributesList.attributeList);
				loadListData();
			}
		} catch (Exception e) {

		}
	}

	private void loadListData() {
		// ListAttributes = attributesList.attributeList.subList(0, 5);
		// attributesList.attributeList.
		ListAttributes = new ArrayList<Attribute>();
		for (int i = 0; i < attributesList.attributeList.size() && i < 5; i++) {
			ListAttributes.add(attributesList.attributeList.get(0));
			attributesList.attributeList.remove(0);
		}
		Attribute attributeMore = new Attribute();
		attributeMore.displayName = "More";
		ListAttributes.add(attributeMore);
		attributesAdapter = new AttributesAdapter(getApplicationContext(),
				ListAttributes, 0, true);
		listViewAttributes.setAdapter(attributesAdapter);
		attribute = ListAttributes.get(0).displayName;
		IGLogger.d(this, "attributesList size = "
				+ attributesList.attributeList.size());
		if (new NetworkManager(getApplicationContext()).isInternetConnected()) {
			new GetAllMarket().execute("");
		}
	}

	public Address getLocationFromAddress(String strAddress) {
		IGLogger.d(this, "address " + strAddress);
		Geocoder coder = new Geocoder(this);
		List<Address> address = null;
		try {
			address = coder.getFromLocationName(strAddress, 5);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (address == null) {
			return null;
		}
		// Address ad = new Address(Locale.US);
		// latitude\":33.3990891 ,\"longitude\":-111.6846103
		// ad.setLatitude(33.3990891);
		// ad.setLongitude(111.6846103);
		// address.add(ad);
		Address location = address.get(0);
		IGLogger.d(this,
				location.getLatitude() + " : " + location.getLongitude());
		return location;
	}

	private class GetOffers extends TZGetOffers {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			alertManager.showLoadingProgress(FindOfferChangedActivity.this);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			alertManager.hideLoadingProgress();
			IGLogger.d(this, "offers = " + result);
			if (result != null) {
				try {
					OfferResponse offerResponse = new OfferResponse();
					Gson gson = new Gson();
					offerResponse = gson.fromJson(result,
							offerResponse.getClass());
					if (offerResponse.token != null) {
						Utils.getInstance().setToken(offerResponse.token);
					}
					Intent intent = new Intent(FindOfferChangedActivity.this,
							OfferListActivity.class);
					intent.putExtra(Constants.EXTRA_SHOW_CATEGORY_SORT_OPTION,
							showCategorySortOption);
					intent.putExtra(Constants.EXTRA_OFFER_SCREEN_MODE,
							offerScreenMode);
					intent.putExtra(Constants.EXTRA_MAP_ZOOM_LOCATION,
							mapZoomLocation);
					intent.putExtra("offers", offerResponse);
					startActivity(intent);
				} catch (JsonSyntaxException e) {

				}
			} else {
				alertManager.showToast(FindOfferChangedActivity.this,
						"Offers are not available.");
			}
		}
	}

	private class GetMarketOffers extends TZGetMarketOffers {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			alertManager.showLoadingProgress(FindOfferChangedActivity.this);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			alertManager.hideLoadingProgress();
			IGLogger.d(this, "offers = " + result);
			if (result != null) {
				// FavoriteOfferResponse offerResponse = new OfferResponse();
				try {
					Gson gson = new Gson();
					MarketOffersResponse marketOffersResponse = gson.fromJson(
							result, MarketOffersResponse.class);
					if (marketOffersResponse.token != null) {
						Utils.getInstance()
								.setToken(marketOffersResponse.token);
					}
					Intent intent = new Intent(FindOfferChangedActivity.this,
							OfferListActivity.class);
					intent.putExtra(Constants.EXTRA_OFFER_SCREEN_MODE,
							offerScreenMode);
					IGLogger.d(this, "offerScreenMode = " + offerScreenMode);
					intent.putExtra(Constants.EXTRA_MAP_ZOOM_LOCATION,
							mapZoomLocation);
					intent.putExtra(Constants.EXTRA_SHOW_CATEGORY_SORT_OPTION,
							showCategorySortOption);
					intent.putExtra("market_offers", marketOffersResponse);
					startActivity(intent);
				} catch (JsonSyntaxException e) {

				}
			} else {
				alertManager.showToast(FindOfferChangedActivity.this,
						"Offers are not available.");
			}
		}
	}

	private class GetAllMarket extends TZGetAllMarkets {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			alertManager.showLoadingProgress(FindOfferChangedActivity.this);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			alertManager.hideLoadingProgress();
			if (result != null) {
				try {
					Gson gson = new Gson();
					marketList = gson.fromJson(result, MarketList.class);
					if (marketList.marketList != null
							&& marketList.marketList.size() > 0) {
						ArrayList<String> arrayListmarket = new ArrayList<String>();
						arrayListmarket.add("Near me");
						for (Market market : marketList.marketList) {
							arrayListmarket.add(market.displayName);
							IGLogger.d(this, "marketName = "
									+ market.displayName);
						}
						arrayMarket = new ArrayAdapter<String>(
								FindOfferChangedActivity.this,
								android.R.layout.simple_spinner_item,
								arrayListmarket);
						spinnerMarket.setAdapter(arrayMarket);
						IGLogger.d(this, "marketList size = "
								+ marketList.marketList.size());
					}
				} catch (JsonSyntaxException e) {
					e.printStackTrace();
				}
			}
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_SELECT_ATTRIBUTE
				&& resultCode == RESULT_OK) {
			// String attribute = data
			// .getStringExtra(Constants.EXTRA_SELECTED_ATTRIBUTE);
			Attribute selectedAttribute = (Attribute) data
					.getSerializableExtra(Constants.EXTRA_SELECTED_ATTRIBUTE);
			int pos = data.getIntExtra("pos", -1);
			if (pos != -1) {
				IGLogger.d(this, "pos = " + pos);
				Attribute temp = attributesList.attributeList.remove(pos);
				IGLogger.d(this, "delete temp name = " + temp.displayName);
			}
			setSelectedAttribute(selectedAttribute);
			IGLogger.d(this, "selected category = "
					+ selectedAttribute.displayName);
		}
	}

	private void setSelectedAttribute(Attribute selectedAttribute) {
		int selectedPosition = 0;
		if (ListAttributes.get(0).displayName
				.equalsIgnoreCase(getString(R.string.text_all))) {
			ListAttributes.add(1, selectedAttribute);
			selectedPosition = 1;
		} else {
			ListAttributes.add(0, selectedAttribute);
		}
		int deletePosition = ListAttributes.size() - 2;
		Attribute deletedAttribute = ListAttributes.remove(deletePosition);
		attributesList.attributeList.add(0, deletedAttribute);
		attributesAdapter.setCheckedPosition(selectedPosition);
		attributesAdapter.notifyDataSetChanged();
		attribute = ListAttributes.get(selectedPosition).value;

	}
	// private void setSelectedAttribute(Attribute selectedAttribute) {
	// boolean isAllAttribteAdded = false;
	// Attribute deletedAttribute = null;
	// boolean isSelectedAttributeAdded = false;
	// int selectedPosition = 0;
	// for (int i = 0; i < ListAttributes.size() - 1; i++) {
	// Attribute attribute = ListAttributes.get(i);
	// if (i == 0) {
	// if (attribute.displayName
	// .equalsIgnoreCase(getString(R.string.text_all))) {
	// isAllAttribteAdded = true;
	// }
	// }
	// if (selectedAttribute.displayName
	// .equalsIgnoreCase(attribute.displayName)) {
	// isSelectedAttributeAdded = true;
	// if (i == 0) {
	// isSelectedAttributeAdded = true;
	// } else {
	// deletedAttribute = ListAttributes.remove(i);
	// if (isAllAttribteAdded) {
	// ListAttributes.add(1, selectedAttribute);
	// selectedPosition = 1;
	// } else {
	// ListAttributes.add(0, selectedAttribute);
	// }
	// }
	// break;
	// }
	// }
	// if (!isSelectedAttributeAdded) {
	// if (isAllAttribteAdded) {
	// ListAttributes.add(1, selectedAttribute);
	// selectedPosition = 1;
	// } else {
	// ListAttributes.add(0, selectedAttribute);
	// }
	// deletedAttribute = ListAttributes.remove(ListAttributes.size() - 2);
	// }
	// attributesList.attributeList.add(0, deletedAttribute);
	// attributesAdapter.setCheckedPosition(selectedPosition);
	// attributesAdapter.notifyDataSetChanged();
	// attribute = ListAttributes.get(selectedPosition).value;
	// }

	// private boolean validateAddressField() {
	// boolean status = false;
	// city = editCity.getText().toString().trim();
	// zipCode = editZipCode.getText().toString().trim();
	// if (zipCode != null && zipCode.length() > 0) {
	// status = true;
	// } else if ((city != null && city.length() > 0)
	// && (state != null && state.length() > 0)) {
	// return true;
	// } else {
	// status = false;
	// }
	//
	// return status;
	// }

	// private boolean isAttributeAvailable(String attribute) {
	// for (int i = 0; i < arrayListAttributes.size(); i++) {
	// if (attribute.equalsIgnoreCase(arrayListAttributes.get(i))) {
	// return true;
	// }
	// }
	// return false;
	// }

	// private void loadAttributes() {
	// arrayListAttributes = new ArrayList<String>();
	// arrayListAttributes.add(getString(R.string.text_casual_dining));
	// arrayListAttributes.add(getString(R.string.text_clothing_apparel));
	// arrayListAttributes.add(getString(R.string.text_fast_food_eateries));
	// arrayListAttributes.add(getString(R.string.text_automative));
	// arrayListAttributes.add(getString(R.string.text_beauty_spa));
	// arrayListAttributes.add(getString(R.string.text_more));
	//
	// }
	// private void setLastSearchResult() {
	// if (searchInput != null && setData) {
	// searchView.setQuery(searchInput.search, false);
	// if (spinnerCategory.getCount() >= searchInput.category) {
	// spinnerCategory.setSelection(searchInput.category);
	// }
	// spinnerDistance.setSelection(searchInput.distance);
	// checkCurrentLocation.setChecked(searchInput.currentLocation);
	// city = searchInput.city;
	// zipCode = searchInput.zipCode;
	// editCity.setText(searchInput.city);
	// spinnerStates.setSelection(searchInput.state);
	// editZipCode.setText(searchInput.zipCode);
	// }
	// }
	// private void parseCategories(String result) {
	// JSONObject response;
	// try {
	// response = new JSONObject(result);
	// try {
	// String token = response.getString("token");
	// if (token != null && token.length() > 0) {
	// Utils.getInstance().setToken(token);
	// }
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	// JSONObject attribute = response.getJSONObject("attributes");
	//
	// JSONArray root = attribute.getJSONArray("ROOT");
	// categories = new ArrayList<String>();
	// for (int i = 0; i < root.length(); i++) {
	// categories.add(root.getString(i));
	// IGLogger.d(this, "attribute = " + root.getString(i));
	// }
	// Collections.sort(categories, new Comparator<String>() {
	// @Override
	// public int compare(String s1, String s2) {
	// return s1.compareToIgnoreCase(s2);
	// }
	// });
	// adapter = new AttributesAdapter(getApplicationContext(),
	// categories);
	// IGLogger.d(this, "categories =" + categories.size());
	// // listAttributes.setAdapter(adapter);
	// // listAttributes.set
	// // spinnerCategory.setAdapter(attributes);
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	//
	// }

	// private class GetCategory extends TZCategoty {
	//
	// @Override
	// protected void onPreExecute() {
	// super.onPreExecute();
	// alertManager.showLoadingProgress(FindOfferChangedActivity.this);
	// }
	//
	// @Override
	// protected void onPostExecute(String result) {
	// super.onPostExecute(result);
	// IGLogger.d(this, "result = " + result);
	// alertManager.hideLoadingProgress();
	// if (result != null) {
	// parseCategories(result);
	// } else {
	// alertManager.showToast(FindOfferChangedActivity.this,
	// "Unable to fetch the category.");
	// }
	// if (new NetworkManager(getApplicationContext())
	// .isInternetConnected()) {
	// new GetAllMarket().execute("");
	// }
	// setLastSearchResult();
	// }
	// }
}