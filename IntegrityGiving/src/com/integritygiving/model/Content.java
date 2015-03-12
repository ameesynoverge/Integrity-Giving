package com.integritygiving.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Content implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Address address;
	public String staticId;
	public String sysId;
	public Location geolocation;
	public String name;
	public ArrayList<ContactDetail> contactDetails = new ArrayList<ContactDetail>();
	public ArrayList<String> attributes = new ArrayList<String>();
	public ArrayList<AttributeObjs> attributeObjs = new ArrayList<AttributeObjs>();
}
