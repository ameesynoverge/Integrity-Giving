package com.integritygiving.model;

import java.io.Serializable;
import java.util.ArrayList;

public class MarketOffersList implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6702084134502020355L;
	public Venue venue;
	public ArrayList<Offer> offers = new ArrayList<Offer>();
}
