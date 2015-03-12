package com.integritygiving.configuration;

import com.google.android.gms.location.LocationClient;

public class Configuration {

	public static boolean DEV_MODE = true;
	public static String Latitude="";
	public static String Longitude="";
	public static String Username="";
	
	public static String Current_Lat;
	public static String Current_Lng;
	
	// dev urls.
	public static final String IG_DONATE_CREATE_ACC_URL = "http://dev.integritygiving.com/index.php/fundraisers/donation.html";
	public static final String IG_BASE_URL = "http://dev.integritygiving.com/rest/";
	public static String IG_GeoUrl="http://fe.integritygiving.com:8093/roads-tafe/node/geoPoint?action=geosearch&ticket=";
	
	public static String IG_GeoOffer="http://fe.integritygiving.com:8093/roads-tafe/user/proximitySession?" +
			"action=enterV2&ticket=";
	
	// prodctions urls.
	
	public static String Token="";
	
	//public static final String IG_DONATE_CREATE_ACC_URL ="http://www.integritygiving.com/index.php/fundraisers/donation.html";
	//public static final String IG_BASE_URL ="https://rest.integritygiving.com:444/";
	public static final String IG_CREATE_ACCOUNT = "IGuser/createaccount";
	public static final String IG_USER = "IGuser/";
	public static final String IG_MARKETS_ALL = "markets/all";
	public static final String IG_USER_DETAIL = "IGuser/";
	public static final String IG_DETAILS = "details";
	
	
	public static LocationClient mLocationClient;
	public static boolean GeoExit=Boolean.TRUE;
	public static boolean enterGeoFirst;
	public static boolean CallFirst=false;
     
}
