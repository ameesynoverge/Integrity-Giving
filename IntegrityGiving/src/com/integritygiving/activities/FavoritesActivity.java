package com.integritygiving.activities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.integrity.giving.R;
import com.integritygiving.adapter.FavoritesListAdapter;
import com.integritygiving.logger.IGLogger;
import com.integritygiving.manager.NetworkManager;
import com.integritygiving.model.Address;
import com.integritygiving.model.AttributeObjs;
import com.integritygiving.model.ContactDetail;
import com.integritygiving.model.FavoriteOfferResponse;
import com.integritygiving.model.ListOffers;
import com.integritygiving.model.Login;
import com.integritygiving.model.OfferDisplayDetail;
import com.integritygiving.model.OfferRedemptionDetail;
import com.integritygiving.model.Venue;
import com.integritygiving.utils.Utils;
import com.tz.sdk.core.TZOfferList;

public class FavoritesActivity extends BaseActivity implements OnClickListener {

	private ListView listFavorites;
	private FavoritesListAdapter adapter;
	private LinearLayout layoutViewRedeemed;
	private ArrayList<OfferDisplayDetail> arrayListOffers;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater.from(this).inflate(R.layout.activity_favorites,
				getRootLayout(), true);
		listFavorites = (ListView) findViewById(R.id.list_favorites);
		layoutViewRedeemed = (LinearLayout) findViewById(R.id.layout_view_redeemed);
		layoutViewRedeemed.setOnClickListener(this);

		if (new NetworkManager(getApplicationContext()).isInternetConnected()) {
			Login login = Utils.getInstance().getLogin();
			new FavoriteListAsyncTast().execute(Utils.getInstance().getToken(),
					"1", login.getUserName(), login.getPassword());
		} else {
			alertManager.showNetworkErrorToast(FavoritesActivity.this);
		}
	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {
		case R.id.layout_view_redeemed:
			startActivity(new Intent(this, RedeemedOffersListActivity.class));
			break;

		}

	}

	private class FavoriteListAsyncTast extends TZOfferList {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			alertManager.showLoadingProgress(FavoritesActivity.this);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			IGLogger.d(this, "response = " + result);
			alertManager.hideLoadingProgress();
			if (result != null) {
				// writeStringAsFile(result);
				try {
					// OfferListStatuses offerList = new OfferListStatuses();
					FavoriteOfferResponse favoriteOffers = new FavoriteOfferResponse();
					Gson gson = new Gson();
					favoriteOffers = gson.fromJson(result,
							favoriteOffers.getClass());
					if (favoriteOffers.token != null) {
						Utils.getInstance().setToken(favoriteOffers.token);
					}
					arrayListOffers = getOffers(favoriteOffers);
					if (arrayListOffers.size() > 0) {
						loadData(arrayListOffers);
					} else {
						alertManager.showToast(FavoritesActivity.this,
								"You have no favorite offers.");
					}
				} catch (JsonSyntaxException exception) {
					alertManager.showToast(FavoritesActivity.this,
							"Server response error.");
				}

			} else {
				alertManager.showToast(FavoritesActivity.this, "Server error.");
			}
		}
	}

	private void loadData(ArrayList<OfferDisplayDetail> offerList) {
		// offerList
		adapter = new FavoritesListAdapter(this, arrayListOffers);
		listFavorites.setAdapter(adapter);
		listFavorites.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				OfferDisplayDetail displayDetail = (OfferDisplayDetail) adapter
						.getItem(arg2);

				if (displayDetail.valid) {
					Intent intent = new Intent(FavoritesActivity.this,
							RedeemActivity.class);
					intent.putExtra("offer", displayDetail);
					startActivity(intent);
				}
			}
		});

	}

	public static void writeStringAsFile(final String fileContents) {
		try {
			String fileName = "data.txt";
			FileWriter out = new FileWriter(new File(
					Environment.getDataDirectory(), fileName));
			out.write(fileContents);
			out.close();
		} catch (IOException e) {
		}
	}

	private ArrayList<OfferDisplayDetail> getOffers(
			FavoriteOfferResponse offerResponse) {
		IGLogger.d(this,
				"no of fav offers" + offerResponse.favoriteOffers.size());
		ArrayList<OfferDisplayDetail> arrayListOfferDisplayDetail = new ArrayList<OfferDisplayDetail>();
		int k=0;
		if (offerResponse.favoriteOffers.size() > 0) {
			for (ListOffers favoriteOffer : offerResponse.favoriteOffers) {
				for (int i = 0; i < favoriteOffer.offers.size(); i++) {
					OfferRedemptionDetail offerRedemptionDetail = favoriteOffer.offers
							.get(i);
					Venue venue = favoriteOffer.venue;
					OfferDisplayDetail displayDetail = new OfferDisplayDetail();
					displayDetail.id = offerRedemptionDetail.offer.id;
					displayDetail.name = offerRedemptionDetail.offer.name;
					displayDetail.description = offerRedemptionDetail.offer.description;
					displayDetail.venueName = venue.name;
					displayDetail.geolocation = venue.geolocation;
					displayDetail.offerImageUri = offerRedemptionDetail.offer.offerImageUri;
					displayDetail.validPeriod = offerRedemptionDetail.offer.validPeriod;
					Address address = venue.address;
					displayDetail.address = address.address1 + " "
							+ address.address2 + " " + address.city + " "
							+ address.stateProv + " " + address.postcode;
					displayDetail.staticId = venue.staticId;
					displayDetail.sysId = venue.sysId;
					ArrayList<ContactDetail> listContactDetails = venue.contactDetails;
					// for (int j = 0; j < listContactDetails.size(); j++) {
					//
					// ContactDetail contact = listContactDetails.get(j);
					// if (contact.type.equalsIgnoreCase("CONTACT")) {
					// displayDetail.phone = contact.value;
					// IGLogger.d(this, " PHONE " + contact.value);
					// break;
					// }
					// }
					displayDetail.phone = venue.address.mainPhone;   
					// ArrayList<String> attributes = venue.attributes;
					// if (attributes.size() > 0) {
					// displayDetail.category = attributes.get(0);
					// }
					ArrayList<AttributeObjs> attributesObj = venue.attributeObjs;
					if (attributesObj.size() > 0) {
						displayDetail.category = attributesObj.get(0).displayName;
					}
					k++;
					System.out.println("K is " + k);
					Utils.getInstance().daysLeft(displayDetail);
					displayDetail.valid = offerRedemptionDetail.offer.valid;
					arrayListOfferDisplayDetail.add(displayDetail);
				}
			}

		}
		return arrayListOfferDisplayDetail;
	}
}
