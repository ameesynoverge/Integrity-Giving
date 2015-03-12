package com.integritygiving.model;

import java.io.Serializable;
import java.util.ArrayList;

public class AttributesList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3563054481843679536L;
	public ArrayList<Attribute> attributeList = new ArrayList<Attribute>();
	public String token;
}
