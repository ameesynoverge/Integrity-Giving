
package notification;


/*  For offer in region
 * http://fe.integritygiving.com:8093/roads-tafe/user/proximitySession?action=enterV2&ticket=ST-147-C1mp3oTbE6js7NI5NRcT-cas,body is {
  "nodeIds" : [
    "en.geoPoint.1569525956.1423669991"
  ]
}
 * 
 * */

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import service.GeofencingService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationStatusCodes;
import com.integritygiving.configuration.Configuration;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.widget.ToggleButton;

	public class note_ig extends android.app.Service implements LocationListener, GooglePlayServicesClient.ConnectionCallbacks,
	GooglePlayServicesClient.OnConnectionFailedListener,
	LocationClient.OnAddGeofencesResultListener,
	LocationClient.OnRemoveGeofencesResultListener{ 
		
		//http://android-er.blogspot.in/2011/04/start-service-to-send-notification.html
		int i=0,iCount=0;
		DefaultHttpClient client;
		String uname,password,msgDisplay,token;
		HttpResponse res;   
		XmlPullParser parser;   
		XmlPullParserFactory factory;
		Boolean flag;
		int trigger;   
		Uri alarmSound;
		String total1,total;  
		ArrayList<String> title;
		String link,method;
		public static SharedPreferences prefs;
		static Editor editor;
		private static final int NOTIFY_ME_ID=1337;
		boolean f,internet,flag_result;
		SharedPreferences sharedpreferences;  
		 public static final String MyPREFERENCES = "MyPrefs" ;
		 public static final String Password = "Password"; 
	     public static final String UserName= "UserName"; 
	 	 public static final String Token= "Token";
	 	public static final String ProductPruchase="Productpruchase"; 
	 	 private PreferenceManager preferenceManager;
	     protected LocationManager locationManager;
	     Double Lat,Lng;
	     JSONObject dataObj=null;
	     Boolean CallApi=false;
	     private final static String FENCE_ID = "com.ptrprograms.geofence";
	 	private final int RADIUS = 100;

	 	private ToggleButton mToggleButton;
	 	private Geofence mGeofence;
	 	private LocationClient mLocationClient;
	 	private Intent mIntent;
	 	private PendingIntent mPendingIntent;
	 	protected ArrayList<Geofence> mGeofenceList;
			

      public String LatArray[],LngArray[],RadiousArray[],SysIdArray[],LatTemp[],LngTemp[];
		
		@Override
		public void onCreate() {
			// TODO Auto-generated method stub
			
			super.onCreate();
			flag=true;
			prefs = PreferenceManager.getDefaultSharedPreferences(this); 
			
			//	method();
			alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
					
			 locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			 
			 int minTime = 60;
			// The minimum distance (in meters) traveled until you will be notified
			float minDistance = 3;
			Criteria criteria = new Criteria();
			criteria.setPowerRequirement(Criteria.POWER_LOW);
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
			criteria.setAltitudeRequired(true);   
			criteria.setBearingRequired(true);   
			criteria.setCostAllowed(true);
			criteria.setSpeedRequired(true);
			// Get the best provider from the criteria specified, and false to say it can turn the provider on if it isn't already
			String bestProvider = locationManager.getBestProvider(criteria, false);
			 
			 //locationManager.requestLocationUpdates(bestProvider, minTime, minDistance, (android.location.LocationListener) this);
			 
			 locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0,this);
			 locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, (android.location.LocationListener) this);
			
			   // Empty list for storing geofences.
		        mGeofenceList = new ArrayList<Geofence>();
		        
				mLocationClient = new LocationClient( this, this, this );
			 	mIntent = new Intent( getApplicationContext(), GeofencingService.class );
				mPendingIntent = PendingIntent.getService( this, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT );
				
				createLocationRequest();  
				   
			Timer tt=new Timer();
			TimerTask min =new TimerTask() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					
					try {
						onCallFirst();   
					} catch (Exception e) {
						// TODO Auto-generated catch block     
						e.printStackTrace();        
					}  
				}
			};
			tt.schedule(min, 2,1000*60);  
		}

		@SuppressWarnings("deprecation")
		@Override
		public void onStart(Intent intent, int startId) {
			// TODO Auto-generated method stub
			super.onStart(intent, startId);
			flag=true;
			try {
				 mLocationClient.connect();
				 //onCallFirst();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		
		public void onCallFirst() throws InterruptedException, JSONException{
			Boolean flag1=internet();
			if(flag1){
				
					client=new DefaultHttpClient();
					
					Thread.sleep(2000);
					Context ctx = getApplicationContext();
					SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx); 
				    String nm=prefs.getString(UserName, ""); 
					
					sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
					
					String namae=sharedpreferences.getString(UserName,"");
					String Passwo=sharedpreferences.getString(Password,"");	
					String token=sharedpreferences.getString(Token,"");
					String ProductPurchase=sharedpreferences.getString(ProductPruchase, "");
					      
					    
					System.out.println("Name and PWD " + namae + " " + Passwo + nm);
					if(Boolean.valueOf(ProductPurchase)){
						try {
							if(Lat != null){
						    CreateNotificationGeoFence(Lat,Lng);
						    if(CallApi){   
					    	   new LocationAsyncTask().execute();      
						  }  
					    }
					} catch (JSONException e) {
						// TODO Auto-generated catch block   
						e.printStackTrace();  
					}	
				 }
			}
		}
		public boolean internet(){
			boolean flag = false;
			ConnectivityManager cm=(ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
			if(cm!=null){
				NetworkInfo[] info=cm.getAllNetworkInfo();
				if(info!=null){
					if (info[0].getState() == NetworkInfo.State.CONNECTED ||info[1].getState() == NetworkInfo.State.CONNECTED  )
					{
						flag=true;

					}else{
						flag=false;
					}
				}

			}
			return flag;
		}
		
		
				
		private JSONObject CreateNotificationGeoFence(Double lat , Double lng) throws JSONException, InterruptedException {
			// TODO Auto-generated method stub
			
			dataObj = new JSONObject();		
	    	
	    	JSONObject mediaTypeId = new JSONObject();	    
	    	dataObj.put("limit", 20);	 
	    	dataObj.put("maxdist", JSONObject.NULL);  
	    		    	
	    	JSONObject facingId = new JSONObject();	 
	    	
	    	//23.037978,72.512249
	    	//Configuration.Latitude=String.valueOf(lat);72.5135443545878 72.5135443545878
	    	//Configuration.Longitude=String.valueOf(Lng);23.038382004015148 23.038382004015148 
	    	
	    	 
	    	DecimalFormat df = new DecimalFormat("##.######");
	       // System.out.print(df.format(Configuration.Longitude));
	         Thread.sleep(3000);
	    	
	    	if(Configuration.Longitude.equals("") && Configuration.Latitude.equals("")){
	    		Configuration.Latitude=String.valueOf(Lat);   
				Configuration.Longitude=String.valueOf(Lng);	
				CallApi=true;
	    	}else if(Configuration.Latitude.equals(Configuration.Current_Lat) && Configuration.Longitude.equals(Configuration.Current_Lng)){
	    		CallApi=false;
	    	}else if(!Configuration.Latitude.equals(Configuration.Current_Lat) && !(Configuration.Longitude.equals(Configuration.Current_Lng))){
	    		
	    		
	    		Location locationA = new Location("Location A");
	    		locationA.setLatitude(Double.valueOf(Configuration.Latitude));
	    		locationA.setLongitude(Double.valueOf(Configuration.Longitude));

	    		Location locationB = new Location("Location B");
	    		locationB.setLatitude(Double.valueOf(Configuration.Current_Lat));
	    		locationB.setLongitude(Double.valueOf(Configuration.Current_Lng));

	    		int metres = Math.round(locationA.distanceTo(locationB));
	    		
	    		System.out.println("Meters Are "+ metres);
	    		
	    		Configuration.Latitude=String.valueOf(Lat);
				Configuration.Longitude=String.valueOf(Lng);	
				CallApi=true;   
	    	} 
	    	   
	    	facingId.put("longitude", Configuration.Longitude);
	    	facingId.put("latitude", Configuration.Latitude);
		
	    	mediaTypeId.put("coord", facingId);	
	    	
	    	dataObj.put("nearestTo", mediaTypeId);
	    	        	
	    	System.out.println("Dataobj"+dataObj);
	    	
	    	return dataObj;
		}
	
		public class LocationAsyncTask extends AsyncTask<Void, Void, Void>{

			String jsonStr;
			
			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
			}
			
			@Override
			protected Void doInBackground(Void... arg0) {
				// TODO Auto-generated method stub
				
				ServiceHandler sh = new ServiceHandler();

				if(Configuration.Token.equals("")){
					Configuration.Token=sharedpreferences.getString(Token, "");
				}
				
				String Geofence_ACC_URL = Configuration.IG_GeoUrl+Configuration.Token+"";
				// Making a request to url and getting response
						
				DefaultHttpClient client = new DefaultHttpClient();  
				HttpPost httppost = new HttpPost(Geofence_ACC_URL);
				System.out.println(Geofence_ACC_URL);  
				StringEntity se;
				try {   
					
					Thread.sleep(5000);      
					
					se = new StringEntity(dataObj.toString());
					se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,  
							"application/json"));  

					httppost.setEntity(se);  

					HttpResponse response1 = client.execute(httppost);   
                    
					int statusCode = response1.getStatusLine().getStatusCode();   
					String token = null;     
					if (statusCode == 401) {
										
					}else{
						
						HttpEntity entity = response1.getEntity();   
					    if(entity != null) {
					        jsonStr = EntityUtils.toString(entity);
					    }
						//jsonStr=response1.toString();
						//System.out.println(jsonStr);
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} 
			
				
				System.out.println("WEB SERVICE RESPONCE Location " + jsonStr);
				
				if (jsonStr != null) {   
				try {
					
					JSONObject jsonObj = new JSONObject(jsonStr);  
                     
					JSONArray jsonObjUser=jsonObj.getJSONArray("geofences");
					LatArray=new String[jsonObjUser.length()];
					LngArray=new String[jsonObjUser.length()];
					RadiousArray=new String[jsonObjUser.length()];
					SysIdArray=new String[jsonObjUser.length()];
					
					 for(int i=0;i<jsonObjUser.length();i++){
						
						 JSONObject json_obj = jsonObjUser.getJSONObject(i);

	                       
						 JSONObject geolocation = json_obj.getJSONObject("geolocation");
						 
						 String  latitude = geolocation.getString("latitude");
						 String  longitude = geolocation.getString("longitude");
						 
						 String geofenceradius=json_obj.getString("geofenceradius");
						 String sysid=json_obj.getString("sysid");
						 LatArray[i]=latitude;
						 LngArray[i]=longitude;
						 RadiousArray[i]=geofenceradius;
						 SysIdArray[i]=sysid;
						 
						 System.out.println("RESPONCE  "+ latitude + " " +longitude  + geofenceradius + sysid);
						 
					 }			
				
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				
				}
								
				return null;
			}
			
			
			@Override
			protected void onPostExecute(Void result) {
				// TODO Auto-generated method stub
				
				
				if (jsonStr != null) {
					try {
					
						verifyPlayServices();     
						Start();   
					    //StartTemp();
						
						//populateGeofenceList();   
						
						//Start_PArshwa();
						
					}catch(Exception e){
						e.printStackTrace();   
					}
				}
				
			
				super.onPostExecute(result);
		}		

		}
		
		
		private void verifyPlayServices() {
			switch ( GooglePlayServicesUtil.isGooglePlayServicesAvailable( this ) ) {
				case ConnectionResult.SUCCESS: {
					break;
				}
				case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED: {
				 
				}
				default: {
				
				}
			}
		}
		

		@Override
		public IBinder onBind(Intent intent) {
			// TODO Auto-generated method stub
			return null;
		}
		
		
		
		protected void createLocationRequest() {
		    LocationRequest mLocationRequest = new LocationRequest();
		    mLocationRequest.setInterval(10000);
		    mLocationRequest.setFastestInterval(5000);
		    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		}

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			
			
			createLocationRequest();
			
			Lat=location.getLatitude();
			Lng=location.getLongitude();	   
		  
						
			DecimalFormat df = new DecimalFormat("##.##");
	        
			df.format(Double.valueOf(Lat));
	 	    df.format(Double.valueOf(Lng));
			   			  
			//Configuration.Latitude=String.valueOf(Lat);
			//Configuration.Longitude=String.valueOf(Lng);
			
			Configuration.Current_Lat=String.valueOf(Lat);
			Configuration.Current_Lng=String.valueOf(Lng);	
			
			System.out.println("Lat Long is "+ Lat + " " + Lng);   
			
		}

		
		private void Start() {
			// TODO Auto-generated method stub
			
			String Sysid=SysIdArray[i];
			System.out.println("Sysid is " + Sysid);
			Geofence.Builder builder = new Geofence.Builder();  
			ArrayList<Geofence> geofences = new ArrayList<Geofence>();
			
			//23.037978, 72.512249
			
			for(int i=1;i<LatArray.length;i++){
				try {
										
					double GeoLat=Double.valueOf(LatArray[i]);
					double GeoLng=Double.valueOf(LngArray[i]);
					float GeoRad=Float.valueOf(RadiousArray[i]);
					
					DecimalFormat df = new DecimalFormat("##.######");
				    				        
				   df.format(Double.valueOf(GeoLat));
				   df.format(Double.valueOf(GeoLng));
										
					System.out.println("GeoLAt"+GeoLat + " Long" + GeoLng + " Red" + GeoRad);
				
					mGeofence = builder.setRequestId( FENCE_ID )
							.setCircularRegion(GeoLat,GeoLng, GeoRad)
			 				
							//.setCircularRegion( 23.037978,72.512249,RADIUS)
							.setTransitionTypes( Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT )
							.setExpirationDuration(Geofence.NEVER_EXPIRE)
							.setRequestId(SysIdArray[i])    
							.build();     
					   
					geofences.add(mGeofence);
					   
				} 
				
				catch(Exception e){
					e.printStackTrace();
				}
			}
			
			mLocationClient.addGeofences( geofences, mPendingIntent, this);
		}
		
		private void Start_PArshwa() {
			// TODO Auto-generated method stub
			
			String Sysid=SysIdArray[i];
			System.out.println("Sysid is " + Sysid);
			Geofence.Builder builder = new Geofence.Builder();  
			ArrayList<Geofence> geofences = new ArrayList<Geofence>();
			
			// pakvan  23.037250, 72.518974 
			
			// Prshwa  23.037978,72.512249
			
			// icici bank 23.035469, 72.510706 
			//Galaxy 23.044430, 72.529672
						
			LatTemp=new String[2];
			LngTemp=new String[2];
			
			LatTemp[0]="23.037978";
			LngTemp[0]="72.512249";
			
			LatTemp[1]="23.037250";
			LngTemp[1]="72.518974";
			
			LatTemp[1]="23.035469";
			LngTemp[1]="72.510706";
						
			for(int i=1;i<LatTemp.length;i++){
				try {
					
					mGeofence = builder.setRequestId( FENCE_ID )
							.setCircularRegion( Double.valueOf(LatTemp[i]),Double.valueOf(LngTemp[i]), Float.valueOf(RadiousArray[i]))
			 				.setTransitionTypes( Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT )
							.setExpirationDuration(Geofence.NEVER_EXPIRE)
							.setRequestId(SysIdArray[i])  
							.build();
					   
					geofences.add(mGeofence);
					
				} 
				
				catch(Exception e){
					e.printStackTrace();
				}
			}
			
			mLocationClient.addGeofences( geofences, mPendingIntent, this);
		}
		
		
			
		
		 public void populateGeofenceList() {
		      			 
			 String Sysid=SysIdArray[i];
			 System.out.println("Sysid is " + Sysid);
				
			 for(int i=1;i<LatArray.length;i++){

		            mGeofenceList.add(new Geofence.Builder()
		                    // Set the request ID of the geofence. This is a string to identify this
		                    // geofence.
		                    .setRequestId(FENCE_ID)

		                    // Set the circular region of this geofence.
		                    .setCircularRegion(
		                    		 Double.valueOf(LatArray[i]),Double.valueOf(LngArray[i]), Float.valueOf(RadiousArray[i]))

		                    // Set the expiration duration of the geofence. This geofence gets automatically
		                    // removed after this period of time.
		                    .setExpirationDuration(Geofence.NEVER_EXPIRE)

		                    // Set the transition types of interest. Alerts are only generated for these
		                    // transition. We track entry and exit transitions in this sample.
		                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
		                            Geofence.GEOFENCE_TRANSITION_EXIT)
		                    .setRequestId(SysIdArray[i])  
		                    // Create the geofence.
		                    .build());
		        }
			 
			 mLocationClient.addGeofences( mGeofenceList, mPendingIntent, this);
		    }
		 
		
		private void StartTemp() {
			// TODO Auto-generated method stub
			
			String Sysid=SysIdArray[i];
			System.out.println("Sysid is " + Sysid);
			Geofence.Builder builder = new Geofence.Builder();  
			ArrayList<Geofence> geofences = new ArrayList<Geofence>();   
			
			// Chocalate room 23.027173, 72.506144
			
			// Prshwa  23.037978,72.512249 
			
			//for(int i=1;i<LatArray.length;i++){   
				try {
					
					mGeofence = builder.setRequestId( FENCE_ID )
							.setCircularRegion( 23.037978,72.512249,RADIUS)
							.setTransitionTypes( Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT )
							.setExpirationDuration( Geofence.NEVER_EXPIRE)
							.setRequestId(SysIdArray[i])
							.build();
					
					geofences.add(mGeofence);
					} 
				
				catch(Exception e){
					e.printStackTrace();
				}
			//}
			
			mLocationClient.addGeofences( geofences, mPendingIntent, this);
		}
		
		
		
		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onRemoveGeofencesByPendingIntentResult(int status,
				PendingIntent arg1) {
			// TODO Auto-generated method stub
			if( status == LocationStatusCodes.SUCCESS ) {
				stopService( mIntent );
			}
			
		}
  
		@Override  
		public void onRemoveGeofencesByRequestIdsResult(int status, String[] arg1) {
			// TODO Auto-generated method stub
		
			if( status == LocationStatusCodes.SUCCESS ) {
				stopService( mIntent );
			}
		}

		@Override
		public void onAddGeofencesResult(int status, String[] arg1) {
			// TODO Auto-generated method stub
			
			
			if( status == LocationStatusCodes.SUCCESS ) {
				Intent intent = new Intent( mIntent );
				startService( intent );
			}
			
		}

		@Override
		public void onConnectionFailed(ConnectionResult arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onConnected(Bundle arg0) {
			// TODO Auto-generated method stub
			
			 if (!mLocationClient.isConnecting()) {
			        mLocationClient.getLastLocation();  
			    }
			
		}

		@Override
		public void onDisconnected() {
			// TODO Auto-generated method stub
			
		}
		
		

	}


