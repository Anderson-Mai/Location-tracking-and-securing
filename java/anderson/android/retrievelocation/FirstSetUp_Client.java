package anderson.android.retrievelocation;

import android.accounts.Account;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;

import anderson.android.utilities.Constants_Client;
import anderson.android.utilities.Global_Client;
import anderson.android.utilities.Utilities_Client;

import static anderson.android.utilities.Utilities_Client.get_AccountInfor;

/***
 * Anderson Mai. 09/06/2015
 *  Setting up for Location client
 */

public class FirstSetUp_Client extends Activity implements MediaRecorder.OnInfoListener{
	 Button m_submitButton; 
	 Button m_cancelButton;
	 LinearLayout mGmailSetup;
	 EditText mDeviceName;
	 EditText mGmailPassword;
	 EditText mOtherPartyEmail;
	 LinearLayout mGMailSetupSection = null;
	 final String BLANK = "#";
	 Toast toast;
	 Account[] account;                                                                                                                                               
	 Pattern emailPattern;
	 private ProgressBar  mSendingProgressBar= null;
	 final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
     MediaRecorder recorder = null;

	 @Override
	 public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  setContentView(R.layout.firstsetup_client);

		 Global_Client.phoneAccounts = new String[3];
		 IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
		 filter.addAction(Intent.ACTION_SCREEN_OFF);
		 BroadcastReceiver mReceiver = new ScreenReceiver();
		 registerReceiver(mReceiver, filter);

		 Global_Client.EXTERNAL_STORAGE_FILE_PATH = Utilities_Client.createFile(Constants_Client.EXTERNAL_STORAGE_PATH, Constants_Client.EXTERNAL_STORAGE_FILE);
		 if (Global_Client.g_Loc_Manager != null){
			 if (RetrieveLocationActivity.locationL != null){
				 Global_Client.g_Loc_Manager.removeUpdates(RetrieveLocationActivity.locationL);
			 }
			 Global_Client.g_Loc_Manager = null;
		 }

		 if(Global_Client.NotifyManager != null){
			 Global_Client.NotifyManager.cancelAll();
			 Global_Client.NotifyManager = null;
		 }

		 if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			 int PERMISSION_ALL = 1;
			 String[] PERMISSIONS = {android.Manifest.permission.CALL_PHONE, android.Manifest.permission.READ_SMS, android.Manifest.permission.RECEIVE_SMS, android.Manifest.permission.RECORD_AUDIO,
					// android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION };
			                  android.Manifest.permission.ACCESS_FINE_LOCATION };
			 if (!hasPermissions(this, PERMISSIONS)) {
				 requestPermissions(PERMISSIONS, PERMISSION_ALL);
			 }
		 }
		 ComponentName component=new ComponentName(getApplicationContext(), SMSClientReceiver.class);
		 getPackageManager()
				 .setComponentEnabledSetting(component,
						 PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
						 PackageManager.DONT_KILL_APP);

		 Global_Client.my_context = this;
		 // Location return more accuracy while without Wifi

	  mDeviceName = (EditText)findViewById(R.id.deviceName);
	  mOtherPartyEmail = (EditText)findViewById(R.id.anotherPartyEmail);
	  m_submitButton = (Button) findViewById(R.id.submitButton);
	  m_cancelButton = (Button) findViewById(R.id.cancelButton);
	  m_cancelButton.setOnClickListener(new Button.OnClickListener(){
		 	 public void onClick(View v) {
		 		 finish();
		 	 }
	  });

  	      mGMailSetupSection = (LinearLayout)findViewById(R.id.GMailSetupSection);
		 //
		////	 if (get_GmailAccountInfor(Global_Client.phoneAccounts)) {
		//		 mOtherPartyEmail.setText(Global_Client.phoneAccounts[1]);
		//		 mDeviceName.setText(Global_Client.phoneAccounts[2]);
//
	//		 }
	//

		 m_submitButton.setOnClickListener(new Button.OnClickListener(){
			 public void onClick(View v) {
				 String deviceName = (mDeviceName.getText().toString().trim()).toUpperCase();
				 if (deviceName.isEmpty()) {
					 toast = Toast.makeText(getApplicationContext(), "Please set a name for your device. ", Toast.LENGTH_LONG);
					 toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 50, 240);
					 toast.show();
					 return;
				 }

				 String otherParty = mOtherPartyEmail.getText().toString().trim();
				 Log.i("Other party: ", otherParty);
				 Log.i("Device Name : ", deviceName);
				 if (otherParty.length() != 11) {
					 toast = Toast.makeText(getApplicationContext(), "Invalid base phone number", Toast.LENGTH_LONG);
					 toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 50, 240);
					 toast.show();
					 return;
				 }

				 TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
				 String mPhoneNumber = tMgr.getLine1Number();
				 if (mPhoneNumber == null) {
					 mGMailSetupSection = (LinearLayout) findViewById(R.id.GMailSetupSection);
					 mGMailSetupSection.setVisibility(View.GONE);
					 String announce_str = "Can not detect your SIM card. ";

					 toast = Toast.makeText(getApplicationContext(), announce_str, Toast.LENGTH_LONG);
					 toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 240);
					 toast.show();
					 return;
				 }

				 Global_Client.phoneAccounts[0] = mPhoneNumber;
				 Global_Client.phoneAccounts[1] = otherParty;
				 Global_Client.phoneAccounts[2] = deviceName;
				 Utilities_Client.requestForConnection(Global_Client.phoneAccounts[2],  Global_Client.phoneAccounts[1]);
				 Utilities_Client.displayAToast("my_number: " + mPhoneNumber, Global_Client.my_context);
				 //StoreDeviceInfor(Global_Client.phoneAccounts);

			 }
		 });
		 DisplayMetrics displayMetrics = new DisplayMetrics();
		 getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		 int height = displayMetrics.heightPixels;
		 int width = displayMetrics.widthPixels;
		 if  (width < 260) {
			 display_Notification(R.drawable.icon_track);
			 moveTaskToBack(true);
		 }


 }

	@Override
	protected void onStart() {
		super.onStart();

	}

	// Remove all elements created for the table
	@Override protected void onStop(){
		super.onStop();
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
    


    /* protected void cleanDir(String dirPath){
    	
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
    */

	@Override
	protected void onResume() {
		super.onResume();
		Global_Client.CLOSE_FLAG = false;
		try {
			if (get_AccountInfor(Global_Client.phoneAccounts)) {
                mOtherPartyEmail.setText(Global_Client.phoneAccounts[1]);
                mDeviceName.setText(Global_Client.phoneAccounts[2]);
            }
		} catch (IOException e) {
			e.printStackTrace();
		}

		WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		if(wifiManager.isWifiEnabled()){
			wifiManager.setWifiEnabled(false);
		}
	}

	private boolean hasPermissions(Context context, String[] permissions) {
		PackageManager pm = getPackageManager();
		for (String permission : permissions) {
			if (pm.checkPermission (permission,getPackageName() ) != PackageManager.PERMISSION_GRANTED) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		Log.d("Focus debug", "Focus changed is touched!");
		if( Constants_Client.PREVENT_SHUTDOWN) {
			Log.d("Focus debug", "Focus changed !");
			if (!hasFocus) {
				Log.d("Focus debug", "Lost focus !");
				Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
				sendBroadcast(closeDialog);
			}
		}
	}
	public void display_Notification(int m_icon_image) {

		//  Global.icon = R.drawable.email_image;
		String ns = Context.NOTIFICATION_SERVICE;
		Global_Client.NotifyManager = (NotificationManager) getSystemService(ns);
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

    public void recordSound(String filePath){

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(filePath);
        recorder.setMaxDuration(20000);
        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        recorder.start();   // Recording is now started
    /* code here */
    }

    @Override
    public void onInfo(MediaRecorder mediaRecorder, int what, int i1) {
        if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
            recorder.stop();
            recorder.reset();   // You can reuse the object by going back to setAudioSource() step
            recorder.release(); // Now the object cannot be reused
            Log.e("FirstSetUp","Maximum Duration Reached");
        }
        else if (what == MediaRecorder.MEDIA_RECORDER_ERROR_UNKNOWN) {
            Log.e("FirstStepUp","ERROR");
            recorder.stop();
            recorder.reset();   // You can reuse the object by going back to setAudioSource() step
            recorder.release(); // Now the object cannot be reused
        }
    }

} /* End of SetUp */
