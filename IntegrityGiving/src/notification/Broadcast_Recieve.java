package notification;


import java.util.Timer;
import java.util.TimerTask;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Broadcast_Recieve extends BroadcastReceiver {

	@Override
	public void onReceive(final Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		
		Timer tt=new Timer();
		TimerTask min =new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Log.d("Parser", "Inside the Log=============================>?");
				//Log.d("NT", "User Name "+veriable_file.n_username);
				Log.d("Network", "Inside the BroadCast Receiver Notification Project");
				Intent serviceIntent = new Intent(arg0,note_ig.class);       
		        arg0.startService(serviceIntent);   
			
			}
		};
		tt.schedule(min, 1,1000*50);
		tt.cancel();
	
	}

}
