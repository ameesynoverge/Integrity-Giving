package com.integritygiving.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Venue implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Content content;
	public Address address;
	public Distance distance;
	public String staticId;
	public String sysId;
	public Location geolocation;
	public String name;
	public ArrayList<String> attributes = new ArrayList<String>();
	public ArrayList<AttributeObjs> attributeObjs = new ArrayList<AttributeObjs>();
	public ArrayList<ContactDetail> contactDetails = new ArrayList<ContactDetail>();
}
