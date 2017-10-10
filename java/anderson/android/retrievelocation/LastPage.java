package anderson.android.retrievelocation;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import anderson.android.utilities.Constants;
import anderson.android.utilities.Constants_Client;
import anderson.android.utilities.Global_Client;
import anderson.android.utilities.Utilities_Client;

/***
 * Anderson Mai.09/08/2015
 * The last page involves closing, resetting and starting Location client processes
 */

public class LastPage extends Activity {
	 Button m_closeButton;
	 Button m_resetButton;
	 Button m_startButton;

	 LinearLayout mGMailSetupSection = null;
	 final String BLANK = "#";
	 Toast toast;
	 private final String TAG = "LastPage";

		
	 @Override
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  setContentView(R.layout.lastsetup_client);

	  Global_Client.my_context = this;

		 if(Global_Client.NotifyManager != null){
			 Global_Client.NotifyManager.cancelAll();
			 Global_Client.NotifyManager.cancel(Global_Client.cursorId);
			 Global_Client.NotifyManager = null;
		 }

		 m_startButton = (Button) findViewById(R.id.firstPageButton);
		 m_startButton.setOnClickListener(new Button.OnClickListener(){
			 public void onClick(View v) {
				if (( Global_Client.phoneAccounts != null) && (!Global_Client.phoneAccounts[2].isEmpty())&& (!Global_Client.phoneAccounts[2].isEmpty())){
					Utilities_Client.requestForConnection(Global_Client.phoneAccounts[2], Global_Client.phoneAccounts[1]);
				}
				else {
					 Intent  mIntent = new Intent(getApplicationContext(), FirstSetUp_Client.class);
					 startActivity(mIntent);
					 finish();

				}
			 }
		 });


	  m_closeButton = (Button) findViewById(R.id.closeButton);
		 m_closeButton.setOnClickListener(new Button.OnClickListener(){
			 public void onClick(View v) {

                 if ( (Global_Client.phoneAccounts != null) &&(!Global_Client.phoneAccounts[1].isEmpty())) {
                     String text = Constants_Client.CLIENT_CLOSE + Constants_Client.POUND + "OMITTED" + Constants_Client.POUND
                             + Global_Client.phoneAccounts[1] + Constants_Client.POUND + Global_Client.phoneAccounts[2] + Constants_Client.POUND + "NO";
                     Utilities_Client.sendText(text, Global_Client.phoneAccounts[1]);
					 finish();
                 }
                 else {
                        finish();
				 }

			 }
		 });

		 m_resetButton = (Button) findViewById(R.id.resetButton);
		 m_resetButton.setOnClickListener(new Button.OnClickListener(){
			 public void onClick(View v) {
				 confirmReset();
				 if (!Global_Client.phoneAccounts[1].isEmpty()) {
					 String text = Constants_Client.CLIENT_RESETTED + Constants_Client.POUND + Global_Client.phoneAccounts[1] + Constants_Client.POUND
							 + "OMITTED" + Constants_Client.POUND + Global_Client.phoneAccounts[2];
					 Utilities_Client.sendText(text, Global_Client.phoneAccounts[1]);
				 }
			 }
		 });

	  String announce_str = "";
	  String possibleEmail = "";


 }
	 


  @Override
  protected void onStart() {
       super.onStart();
  }
     
  // Remove all elements created for the table
  @Override protected void onStop(){
   	 super.onStop();
	  unregisterReceiver(doCloseApp);
  }

  
  // Write the messages in Global.alertedmsgInfo to file 
  private void StoreGMaiInfor(String [] GMaiInfor){
    	    String temp = "";
    	    int length = GMaiInfor.length;
    	    if (length == 2){
    	    	temp = GMaiInfor[0] + "\n" + GMaiInfor[1] + "\n";
    	        FileOutputStream f_writer = null;
    	        try {
    	        	f_writer = openFileOutput(Constants_Client.GMaiInforFile, Context.MODE_PRIVATE);
			
    	        } catch (FileNotFoundException e1) {
				     // TODO Auto-generated catch block
				    e1.printStackTrace();
			    }
			
    	        if (f_writer != null){
    	        	try {
					    f_writer.write(temp.getBytes());
				    } catch (IOException e) {
				         // TODO Auto-generated catch block
					     e.printStackTrace();
		            }
    	        }
    	    
    	        try {
				   f_writer.close();
			    } catch (IOException e) {
				    // TODO Auto-generated catch block
				    e.printStackTrace();
			   } 
         }
    }
    
 
  

    protected void cleanDir(String dirPath){
    	
        File directory = new File(dirPath);
        
          // Get all files in directory
        	File[] files = directory.listFiles();

        	if ((files != null) && (files.length > 0)){
                // Delete all files
        		for (File file : files) {
        			// Delete each file
        			file.delete();
        		}
        		cleanDir(dirPath);
        	}
        	
        }

   
    private boolean checkNetwork(){
		 boolean network = true;
		 if  (!isNetworkAvailable() ){
		    for (int i = 0; i < 10000000; i++){
		    	// delayed time prior checking the network availability
		    }
	        if  (!isNetworkAvailable() ){
	        	network = false;
	    	    AlertDialog.Builder alertbox = new AlertDialog.Builder(LastPage.this);
				alertbox.setTitle("   No network is available");
				  // alertbox.setMessage("Please bring up the network and retry");
				alertbox.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0,int arg1) {
								finish();
					}
				});
				alertbox.show();  
		   }
	   }
	   return network;
   }

	private boolean isNetworkAvailable() {
		ConnectivityManager connMgr
				= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		final NetworkInfo wifi =
				connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		final NetworkInfo mobile =
				connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);


		if( wifi.isAvailable() && wifi.getDetailedState() == NetworkInfo.DetailedState.CONNECTED){
			return true;
		}
		else if( mobile.isAvailable() && mobile.getDetailedState() == NetworkInfo.DetailedState.CONNECTED ){
			return true;
		}
		else
		{
			return false;
		}
	}

	private void confirmTheBase(String mDeviceName, String otherParty){
		Global_Client.confirmPhase =  Constants_Client.CONFIRM_PHASE + Global_Client.phoneAccounts[1] + "_" + Global_Client.phoneAccounts[2];
		SmsManager.getDefault().sendTextMessage(otherParty, null, Global_Client.confirmPhase, null, null);
	}

	@Override
	protected void onResume() {
		super.onResume();

		registerReceiver(doCloseApp,
				new IntentFilter(Constants.CLOSE_APP_ACTION));

		if (Global_Client.g_Loc_Manager != null){
			if (RetrieveLocationActivity.locationL != null){
				Global_Client.g_Loc_Manager.removeUpdates(RetrieveLocationActivity.locationL);
			}
			Global_Client.g_Loc_Manager = null;
		}

		if (Global_Client.NotifyManager != null){
			Global_Client.NotifyManager.cancelAll();
			Global_Client.NotifyManager.cancel(Global_Client.Notify_ID);
			Global_Client.NotifyManager = null;
		}
		/* if (Global_Client.mGoogleApiClient != null) {
			if (Global_Client.mGoogleApiClient.isConnected()) {
				// send an action to run the  stopLocationUpdates();
				Global_Client.mGoogleApiClient.disconnect();
				Intent m_intent = new Intent(Constants.STOP_LOCATION_UPDATE_ACTION);
				sendBroadcast(m_intent);
			}
		}
		*/

	}


	private void confirmReset(){

		AlertDialog.Builder alertbox_2 = new AlertDialog.Builder(LastPage.this);
		alertbox_2.setTitle("      Reset the app");

		alertbox_2.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0,int arg1){
						deleteFile(Constants_Client.GMaiInforFile);
						//cleanDir(Constants_Client.BACKUPINFORDIR);
						//Global.Last_Thirty.clear();
						//Global.Last_Thirty_RealAddress.clear();
						return;
					}
				});

		alertbox_2.setNegativeButton("NO",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0,int arg1) {
						return;
					}
				});

		alertbox_2.show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (Global_Client.NotifyManager != null){
					 Global_Client.NotifyManager.cancelAll();
					 Global_Client.NotifyManager = null;
					// Global_Client.NotifyManager.cancel(Global_Client.Notify_ID);
				 }
		//System.exit(0);
	}

	private void display_Notification(int m_icon_image) {

		//  Global.icon = R.drawable.email_image;
		String ns = Context.NOTIFICATION_SERVICE;
		Global_Client.NotifyManager = (NotificationManager)getSystemService(ns);
		Global_Client.icon = m_icon_image;

		Notification.Builder mBuilder =
				new Notification.Builder(this)
						.setSmallIcon(Global_Client.icon)
						.setContentTitle("Tracking Client");
		Intent notificationIntent = new Intent(this, LastPage.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		PendingIntent contentIntent =
				PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(contentIntent);
		Notification notification = mBuilder.build();
		Global_Client.NotifyManager.notify(Global_Client.Notify_ID, notification);
		Global_Client.cursorId = Global_Client.Notify_ID;

	}
	// Write the messages in Global.alertedmsgInfo to file
	private void StoreDeviceInfor(String [] DeviceInfor){
		String temp = "";
		int length = DeviceInfor.length;
		if (length == 3){
			temp = DeviceInfor[0] + "\n" + DeviceInfor[1] + "\n" + DeviceInfor[2] + "\n" ;
			FileOutputStream f_writer = null;
			try {
				f_writer = openFileOutput(Constants_Client.GMaiInforFile, Context.MODE_PRIVATE);

			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			if (f_writer != null){
				try {
					f_writer.write(temp.getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
		}

			try {
			f_writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

	private BroadcastReceiver doCloseApp = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(Constants.CLOSE_APP_ACTION)) {
                Log.i(TAG, "--- Received CLOSE_APP_ACTION");
                ComponentName component = new ComponentName(getApplicationContext(), SMSClientReceiver.class);
                getPackageManager()
                        .setComponentEnabledSetting(component,
                                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                                PackageManager.DONT_KILL_APP);
                finish();
            }
		}
	};
} /* End of SetUp */
