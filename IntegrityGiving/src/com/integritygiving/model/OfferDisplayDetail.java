package com.integritygiving.model;

import java.io.Serializable;
import java.util.ArrayList;

public class OfferDisplayDetail implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String name;
	public String id;
	public String description;
	public String offerImageUri;
	public float estimatedValue;
	public ArrayList<String> validPeriod = new ArrayList<String>();
	public String address;
	public String distance;
	public String metric;
	public String phone;
	public String staticId;
	public String sysId;
	public String category;
	public String venueName;
	public boolean valid;
	public long daysLeft;
	public String lastRedemption;
	public long redeemDate;
	public Location geolocation;
	public int offersCount;
}
