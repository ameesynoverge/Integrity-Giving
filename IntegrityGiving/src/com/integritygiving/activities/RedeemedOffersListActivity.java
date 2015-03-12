package com.integritygiving.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.integrity.giving.R;
import com.integritygiving.adapter.RedeemedOffersAdapter;
import com.integritygiving.logger.IGLogger;
import com.integritygiving.manager.NetworkManager;
import com.integritygiving.model.Address;
import com.integritygiving.model.AttributeObjs;
import com.integritygiving.model.ContactDetail;
import com.integritygiving.model.ListOffers;
import com.integritygiving.model.Login;
import com.integritygiving.model.OfferDisplayDetail;
import com.integritygiving.model.OfferRedemptionDetail;
import com.integritygiving.model.RedeemedOfferResponse;
import com.integritygiving.model.Venue;
import com.integritygiving.utils.Utils;
import com.tz.sdk.core.TZOfferList;

public class RedeemedOffersListActivity extends BaseActivity {

	private ListView listRedeemedOffers;
	private RedeemedOffersAdapter adapter;
	

	 public static final String MyPREFERENCES = "MyPrefs" ;
	 public static final String Password = "Password"; 
    public static final String UserName= "UserName"; 
    
    SharedPreferences sharedpreferences;
	String  Uname,password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater.from(this).inflate(R.layout.activity_offer_redeemed,
				getRootLayout(), true);
		

		 sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
		
		 Uname=sharedpreferences.getString(UserName, "");
		 password=sharedpreferences.getString(Password, "");	
		 
		listRedeemedOffers = (ListView) findViewById(R.id.list_redeemed_offers);
		if (new NetworkManager(getApplicationContext()).isInternetConnected()) {
			Login login = Utils.getInstance().getLogin();
			new RedeemOfferListtAsyncTast().execute(Utils.getInstance()
					.getToken(), "2", Uname, password);
		} else {
			alertManager.showNetworkErrorToast(RedeemedOffersListActivity.this);
		}
	}

	private class RedeemOfferListtAsyncTast extends TZOfferList {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			alertManager.showLoadingProgress(RedeemedOffersListActivity.this);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			IGLogger.d(this, "response = " + result);
			alertManager.hideLoadingProgress();
			if (result != null) {
				try {
					RedeemedOfferResponse redeemedOfferResponse = new RedeemedOfferResponse();
					Gson gson = new Gson();
					redeemedOfferResponse = gson.fromJson(result,
							redeemedOfferResponse.getClass());
					if (redeemedOfferResponse.redeemedOffers.size() > 0) {
						ArrayList<OfferDisplayDetail> arrayListOffers = getOffers(redeemedOfferResponse);
						loadData(arrayListOffers);
					} else {
						alertManager.showToast(RedeemedOffersListActivity.this,
								"You have no redeemed offers.");
					}
				} catch (JsonSyntaxException exception) {
				}
			} else {
				alertManager.showToast(RedeemedOffersListActivity.this,
						"Server error.");
			}
		}
	}

	private void loadData(ArrayList<OfferDisplayDetail> arrayListOffers) {
		// offerList
		adapter = new RedeemedOffersAdapter(this, arrayListOffers);
		listRedeemedOffers.setAdapter(adapter);
		listRedeemedOffers.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
			}
		});
	}

	private ArrayList<OfferDisplayDetail> getOffers(
			RedeemedOfferResponse redeemedOfferResponse) {
		IGLogger.d(this, "no of redeemed offers"
				+ redeemedOfferResponse.redeemedOffers.size());
		ArrayList<OfferDisplayDetail> arrayListOfferDisplayDetail = new ArrayList<OfferDisplayDetail>();
		if (redeemedOfferResponse.redeemedOffers.size() > 0) {
			for (ListOffers redeemdOffer : redeemedOfferResponse.redeemedOffers) {
				for (int i = 0; i < redeemdOffer.offers.size(); i++) {
					OfferRedemptionDetail offerRedemptionDetail = redeemdOffer.offers
							.get(i);
					Venue venue = redeemdOffer.venue;
					OfferDisplayDetail displayDetail = new OfferDisplayDetail();
					displayDetail.id = offerRedemptionDetail.offer.id;
					displayDetail.name = offerRedemptionDetail.offer.name;
					displayDetail.description = offerRedemptionDetail.offer.description;
					displayDetail.venueName = venue.name;
					displayDetail.validPeriod = offerRedemptionDetail.offer.validPeriod;
					displayDetail.offerImageUri = offerRedemptionDetail.offer.offerImageUri;
					Address address = venue.address;
					displayDetail.address = address.address1 + " "
							+ address.address2 + " " + address.city + " "
							+ address.postcode;
					displayDetail.staticId = venue.staticId;
					displayDetail.sysId = venue.sysId;
					ArrayList<ContactDetail> listContactDetails = venue.contactDetails;
					for (int j = 0; j < listContactDetails.size(); j++) {

						ContactDetail contact = listContactDetails.get(j);
						if (contact.type.equalsIgnoreCase("PHONE")) {
							displayDetail.phone = contact.value;
							IGLogger.d(this, " PHONE " + contact.value);
							break;
						}
					}
					// ArrayList<String> attributes = venue.attributes;
					// if (attributes.size() > 0) {
					// displayDetail.category = attributes.get(0);
					// }
					ArrayList<AttributeObjs> attributesObj = venue.attributeObjs;
					if (attributesObj.size() > 0) {
						displayDetail.category = attributesObj.get(0).displayName;
					}
					Utils.getInstance().daysLeft(displayDetail);
					displayDetail.valid = offerRedemptionDetail.offer.valid;
					displayDetail.lastRedemption = Utils.getInstance()
							.formateDate(offerRedemptionDetail.lastRedemption);
					displayDetail.redeemDate = Utils.getInstance()
							.convertStringToDate(
									offerRedemptionDetail.lastRedemption);
					arrayListOfferDisplayDetail.add(displayDetail);
				}
			}
		}
		Collections.sort(arrayListOfferDisplayDetail, new DateComprator());
		return arrayListOfferDisplayDetail;
	}

	private class DateComprator implements Comparator<OfferDisplayDetail> {

		@Override
		public int compare(OfferDisplayDetail offer1, OfferDisplayDetail offer2) {
			long date1 = offer1.redeemDate;
			long date2 = offer2.redeemDate;

			if (date1 < date2) {
				return 1;
			}
			if (date1 > date2) {
				return -1; // Fails on NaN however, not sure what you want
			}
			return 0;
		}

	}
}
