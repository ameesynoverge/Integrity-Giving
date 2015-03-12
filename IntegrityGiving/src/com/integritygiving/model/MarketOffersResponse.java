package com.integritygiving.model;

import java.io.Serializable;
import java.util.ArrayList;

public class MarketOffersResponse implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6431492217908905896L;
	public ArrayList<MarketOffersList> availableOffers = new ArrayList<MarketOffersList>();
	public String token;
}
