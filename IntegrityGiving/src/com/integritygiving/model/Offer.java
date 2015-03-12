package com.integritygiving.model;

import java.io.Serializable;
import java.util.ArrayList;

import com.integritygiving.activities.ValidPeriod;

public class Offer implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	// private static final long serialVersionUID = 1L;
	public String name;
	public String id="";
	public String description;
	public String offerImageUri;
	public float estimatedValue;
	public ValidPeriod validperiod;
	public ArrayList<String> validPeriod = new ArrayList<String>();
	public boolean valid;
	public int daysLeft;
}
