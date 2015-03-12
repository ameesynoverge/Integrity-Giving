package service;


import java.util.List;

import notification.ServiceHandler;
import notification.note_ig;

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
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.Toast;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.LocationClient;
import com.integrity.giving.R;
import com.integritygiving.activities.FindOfferChangedActivity;
import com.integritygiving.activities.LandingActivity;
import com.integritygiving.activities.OfferListActivity;
import com.integritygiving.activities.OfferListActivityForNoti;
import com.integritygiving.activities.RedeemActivity;
import com.integritygiving.configuration.Configuration;


/**
 * Created by PaulTR on 5/26/14.
 */
public class GeofencingService extends IntentService {

	private NotificationManager mNotificationManager;
	public NotificationCompat.Builder builder;
	JSONObject dataObj ;   
    public int Occurance=0;   
    Uri alarmSound;
    private static final int NOTIFY_ME_ID=1337;
    private NotificationManager mgr=null;
        
	public GeofencingService(String name) {
		super(name);
	}
   
	public GeofencingService() {
		this("Geofencing Service");  
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}


	@Override
	protected void onHandleIntent(Intent intent) {

		GeofencingEvent event = GeofencingEvent.fromIntent(intent);
		
		alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		 builder = new NotificationCompat.Builder( this
				  ); builder.setSmallIcon( R.drawable.ic_launcher ); builder.setDefaults(
				  Notification.DEFAULT_ALL ); builder.setOngoing( true);
				  
				 
		if (event != null) {
  			
		if (event.hasError()) {
				//onError(event.getErrorCode());
			} else {
				int transition = event.getGeofenceTransition();
				
				if (transition == Geofence.GEOFENCE_TRANSITION_ENTER  
						|| transition == Geofence.GEOFENCE_TRANSITION_DWELL
						|| transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
					String[] geofenceIds = new String[event
							.getTriggeringGeofences().size()];
					for (int index = 0; index < event.getTriggeringGeofences()
							.size(); index++) {
						geofenceIds[index] = event.getTriggeringGeofences()
								.get(index).getRequestId();
					}

					if (transition == Geofence.GEOFENCE_TRANSITION_ENTER
							|| transition == Geofence.GEOFENCE_TRANSITION_DWELL) {
						
						 List<Geofence> geofences = LocationClient.getTriggeringGeofences(intent);
			              //  MessageActionEnum transitionType = getTransitionString(transition);
			                // Send Notification
			                String[] geofenceId = extractGeofenceRequestsId(geofences);
			               // sendNotification(transitionType, geofenceIds);
					       
			                String GEOID="";      
					        for(int i=0;i<geofenceId.length;i++)
					        {   
					        	GEOID=String.valueOf(geofenceId[i]);
					            System.out.println("GEOID "+geofenceId[i]);      
					        }
					        Configuration.enterGeoFirst=true;
					        try {
								GetOfferListJson(GEOID);
								
								Thread.sleep(200);
								Occurance=0;
							    new	GeoOfferAsyncTask().execute();
								
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} 
					
					
					} else if (transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
						//onExitedGeofences(geofenceIds);
						
						Configuration.Longitude="";
						Configuration.Latitude="";
						
						Configuration.GeoExit=Boolean.FALSE;
						Configuration.Current_Lat="";
						Configuration.Current_Lng="";
						
						
					}
				}else{
				  //Toast.makeText(getApplicationContext(),"Responce from Handle Event"+transition, Toast.LENGTH_SHORT).show();
				}
			} 

		}  
	}
	
	
	private JSONObject GetOfferListJson(String geofenceId) throws JSONException {
		// TODO Auto-generated method stub
	
		
		dataObj = new JSONObject();		
    	    	
    	JSONArray facingId = new JSONArray();	 
    	
    	facingId.put(geofenceId);
    	
    	dataObj.put("nodeIds", facingId);
    
    	System.out.println("Dataobj"+dataObj);
    	
    	return dataObj;
		
    }


	public class GeoOfferAsyncTask extends AsyncTask<Void, Void, Void>{

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

			String Geofence_ACC_URL = Configuration.IG_GeoOffer+Configuration.Token+"";
			// Making a request to url and getting response
					
			DefaultHttpClient client = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(Geofence_ACC_URL);
			System.out.println(Geofence_ACC_URL);  
			StringEntity se;
			try {
				   
				Thread.sleep(2000);
				JSONObject obj=dataObj;
				se = new StringEntity(dataObj.toString());  
				se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,"application/json"));

				httppost.setEntity(se);    

				HttpResponse response1 = client.execute(httppost);
                
				int statusCode = response1.getStatusLine().getStatusCode();   
				String token = null;
				if (statusCode == 401) { 
			        System.out.println("Offer 401 :-"+jsonStr);   			
				}else{
					   
					HttpEntity entity = response1.getEntity();   
				    if(entity != null) {
				        jsonStr = EntityUtils.toString(entity);  
				        System.out.println("Offer RESPONCE:-"+jsonStr);         
				     }   
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
		
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			
			try {
			
			 if(jsonStr != null){
		        	JSONObject jsonObj = new JSONObject(jsonStr);     
		        	
		        	JSONArray availableOffers=jsonObj.getJSONArray("availableOffers");
		            
		        	for(int i=0;i<availableOffers.length();i++){
		        		Configuration.enterGeoFirst=false;
		        		
		        		 JSONObject json_obj = availableOffers.getJSONObject(i);
	                       
						 JSONArray geolocation = json_obj.getJSONArray("offers");   
						 
						 for(int j=0;j<geolocation.length();j++){
							 JSONObject obj = geolocation.getJSONObject(i);   
							 String  id = obj.getString("id");
							 Occurance++;
							
						 }
											
						if(Occurance != 0){
							ShowNotification(jsonStr);
						}
		        	}          
		       
		        	//System.out.println(availableOffers);
		        }
				
			} catch (Exception e) {
				// TODO: handle exception
			}
			//super.onPostExecute(result);
	
			super.onPostExecute(result);
	}

	}
	
	
	private void ShowNotification(String Responce) {
		// TODO Auto-generated method stub
				
		Intent intent=null;
		
		if(Occurance > 1){
		     intent=new Intent(this,OfferListActivityForNoti.class);   
		}else{
			 intent=new Intent(this,RedeemActivity.class);	
		}   
		 // intent=new Intent(this,OfferListActivity.class);   
		
		 NotificationCompat.Builder mBuilder =   
			             new NotificationCompat.Builder(this)      
			            .setSmallIcon(R.drawable.app_icon)    
			            .setContentTitle("Notification from IntegrityGiving")          
			            .setAutoCancel(true)   
			            .setSound(alarmSound)   
			            .setContentText("You have " + Occurance + " offers.");       
		    	      //.setContentText("You have  new  offers.");
		 
			    // Creates an explicit intent for an Activity in your app
			     
			    // The stack builder object will contain an artificial back stack for the
			    // started Activity.
			    // This ensures that navigating backward from the Activity leads out of
			    // your application to the Home screen.
			    TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);      
			    // Adds the back stack for the Intent (but not the Intent itself)
			    stackBuilder.addParentStack(FindOfferChangedActivity.class);   
			    // Adds the Intent that starts the Activity to the top of the stack
			    
			    intent.putExtra("notification", "true");  
			    intent.putExtra("notificationResponce", Responce);
			    stackBuilder.addNextIntent(intent);
			    PendingIntent resultPendingIntent =
			            stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
			    mBuilder.setContentIntent(resultPendingIntent);
			    mNotificationManager =
			        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

			    // mId allows you to update the notification later on.
			    mNotificationManager.notify(NOTIFY_ME_ID, mBuilder.build());
			    
			    Occurance=0;
	    
	}

	private String[] extractGeofenceRequestsId(List<Geofence> geofences) {
	        String[] geofenceIds = new String[geofences.size()];
	        for (int index = 0; index < geofences.size(); index++) {
	            Geofence geofence = geofences.get(index);
	            geofenceIds[index] = geofence.getRequestId();
	            
	        }
	        return geofenceIds;
	    }

		 
	protected void onEnteredGeofences(String[] strings) {

		// do something!
		Toast.makeText(getApplicationContext(), "Geofence Enter", Toast.LENGTH_SHORT).show();
		builder.setContentTitle( "Geofence Transition" ); builder.setContentText(
				  "IG Entering Geofence" ); mNotificationManager.notify( 1, builder.build()); 
				  
	}

	protected void onExitedGeofences(String[] strings) {

		// do something!
		Toast.makeText(getApplicationContext(), "Geofence Exit", Toast.LENGTH_SHORT).show();
		builder.setContentTitle( "Geofence Transition" ); builder.setContentText(
				  "IG Exit Geofence" ); mNotificationManager.notify( 1, builder.build() );
	}

	protected void onError(int i) {
		Toast.makeText(getApplicationContext(), "Geofence Error", Toast.LENGTH_SHORT).show();
		builder.setContentTitle( "Geofence Transition" ); builder.setContentText(
				  "IG Error Geofence" ); mNotificationManager.notify( 1, builder.build() );
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		onHandleIntent(intent);
		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mNotificationManager.cancel(1);
	}
}
