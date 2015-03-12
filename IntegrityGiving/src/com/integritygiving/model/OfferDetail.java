package com.integritygiving.model;

import java.io.Serializable;
import java.util.ArrayList;

public class OfferDetail implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ArrayList<Offer> offers = new ArrayList<Offer>();
	public Venue venue;
}
