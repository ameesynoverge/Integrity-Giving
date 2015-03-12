package com.integritygiving.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.util.Base64;

import com.integritygiving.logger.IGLogger;
import com.integritygiving.model.Attribute;
import com.integritygiving.model.Login;
import com.integritygiving.model.OfferDisplayDetail;
import com.integritygiving.model.SearchInput;

public class Utils {

	private static Utils _instance;
	private Login login;
	private Bitmap userimage;
	private String token;
	private boolean userstatus;
	private SearchInput searchInput;
	private boolean isProfileImageAvailable = false;
	private ArrayList<Attribute> arrayListAttributes;

	private Utils() {
	}

	public static Utils getInstance() {
		if (_instance == null) {
			_instance = new Utils();
		}
		return _instance;
	}

	public void setSearchInput(SearchInput searchInput) {
		this.searchInput = searchInput;
		IGLogger.d(this, "city = " + searchInput.city);
		IGLogger.d(this, "zipCode = " + searchInput.zipCode);
	}

	public SearchInput getSeachInput() {
		return searchInput;
	}

	public Location getCurrentLocation(Context context) {
		Location location = null;
		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		boolean isGPSEnabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);

		if (isGPSEnabled) {
			location = locationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		}
		if (location == null) {
			boolean isNetworkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			if (isNetworkEnabled) {
				location = locationManager
						.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			}
		}
		// Location location = new Location(LocationManager.NETWORK_PROVIDER);
		// location.setLatitude(33.3990891);
		// location.setLongitude(-111.6846103);
		return location;
	}

	// public Object parseJson(String response, Object object) {
	// Gson gson = new Gson();
	// object = gson.fromJson(response, object.getClass());
	// return object;
	// }

	public String readResponseFromAssets(Context context) {
		InputStream inputStream = null;
		try {
			inputStream = context.getAssets().open("a.txt");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println(inputStream);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		int i;
		try {
			i = inputStream.read();
			while (i != -1) {
				byteArrayOutputStream.write(i);
				i = inputStream.read();
			}
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return byteArrayOutputStream.toString();
	}

	public void setLogin(Login login) {
		this.login = login;
	}

	public void daysLeft(OfferDisplayDetail offer) {

		String pattern = "dd-MMM-yyyy HH:mm:ss";
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.US);
		try {
			Date endDate = dateFormat.parse(offer.validPeriod.get(1));
			Date current = new Date(System.currentTimeMillis());
			int status = current.compareTo(endDate);
			if (status == 0) {
			} else if (status < 0) {
				// valid
				long valueEndDate = endDate.getTime();
				long valueCurrentDate = current.getTime();
				long diff = valueEndDate - valueCurrentDate;
				long days = diff / (24 * 60 * 60 * 1000);
				offer.daysLeft = days;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public Login getLogin() {
		// Login login1 = new Login();
		// login1.setUserName("derek.mcdaniel@opencommercellc.com");
		// login1.setPassword("mcdaniel1");
		// return login1;
		return login;
	}

	public void setUserImage(Bitmap userimage) {
		this.userimage = userimage;
	}

	// public byte[] getBytesUserImage() {
	// ByteArrayOutputStream stream = new ByteArrayOutputStream();
	// userimage.compress(Bitmap.CompressFormat.PNG, 100, stream);
	// byte[] byteArray = stream.toByteArray();
	// return byteArray;
	// }

	public String bitMapToString(Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte[] b = baos.toByteArray();
		String temp = Base64.encodeToString(b, Base64.DEFAULT);
		return temp;
	}

	public Bitmap stringToBitMap(String encodedString) {
		try {
			byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
			Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
					encodeByte.length);
			return bitmap;
		} catch (Exception e) {
			e.getMessage();
			return null;
		}
	}

	public void setUSerStatus(boolean userstatus) {
		this.userstatus = userstatus;

	}

	public boolean getUSerState() {
		return userstatus;
	}

	public void setToken(String token) {
		this.token = token;
		clearAttributes();
	}

	public String getToken() {
		// return "ST-149-ptcF0LRZ4OvLsQMXsckn-cas";
		return token;
	}

	public String formateDate(String dateValue) {
		String formattedDate = null;
		Date date = null;
		DateFormat originalFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss",
				Locale.US);
		DateFormat targetFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
		try {
			date = originalFormat.parse(dateValue);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		formattedDate = targetFormat.format(date);
		return formattedDate;
	}

	public String getFormattedDate() {
		String formattedDate = null;
		Date date = null;
		DateFormat targetFormat = new SimpleDateFormat(
				"MM/dd/yy 'at' hh:mm a Z", Locale.US);
		date = new Date();
		formattedDate = targetFormat.format(date);
		return formattedDate;
	}

	public String getCasierFormattedDate(long timeInMillis) {
		String formattedDate = null;
		Date date = null;
		DateFormat targetFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a",
				Locale.US);
		if (timeInMillis > 0) {
			date = new Date(timeInMillis);
		} else {
			date = new Date();
		}
		formattedDate = targetFormat.format(date);
		return formattedDate;
	}

	public long convertStringToDate(String dateValue) {
		Date date = null;
		DateFormat originalFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss",
				Locale.US);
		try {
			date = originalFormat.parse(dateValue);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date.getTime();
	}

	public Address getLocationFromAddress(Context context, String strAddress) {
		IGLogger.d(this, "address " + strAddress);
		Geocoder coder = new Geocoder(context);
		List<Address> address = null;
		try {
			address = coder.getFromLocationName(strAddress, 5);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (address == null) {
			return null;
		}
		// Address ad = new Address(Locale.US);
		// latitude\":33.3990891 ,\"longitude\":-111.6846103
		// ad.setLatitude(33.3990891);
		// ad.setLongitude(111.6846103);
		// address.add(ad);
		Address location = address.get(0);
		IGLogger.d(this,
				location.getLatitude() + " : " + location.getLongitude());
		return location;
	}

	public boolean isProfileImageAvailable() {
		return isProfileImageAvailable;
	}

	public void setProfileImageAvailable(boolean isProfileImageAvailable) {
		this.isProfileImageAvailable = isProfileImageAvailable;
	}

	public void setAttributes(ArrayList<Attribute> arrayListAttributes) {
		if (this.arrayListAttributes != null) {
			this.arrayListAttributes.clear();
		} else {
			this.arrayListAttributes = new ArrayList<Attribute>();
		}
		this.arrayListAttributes.addAll(arrayListAttributes);
	}

	public ArrayList<Attribute> getAttributes() {
		return arrayListAttributes;
	}

	public void clearAttributes() {
		if (arrayListAttributes != null && arrayListAttributes.size() > 0) {
			arrayListAttributes.clear();
		}
	}
}
