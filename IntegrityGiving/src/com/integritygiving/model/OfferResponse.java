package com.integritygiving.model;

import java.io.Serializable;
import java.util.ArrayList;

public class OfferResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ArrayList<OfferDetail> availableOffers = new ArrayList<OfferDetail>();
	public String token;
}
