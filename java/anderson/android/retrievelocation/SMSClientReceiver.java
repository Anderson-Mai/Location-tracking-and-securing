package anderson.android.retrievelocation;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import anderson.android.utilities.Constants;
import anderson.android.utilities.Constants_Client;
import anderson.android.utilities.Global_Client;
import anderson.android.utilities.Utilities_Client;

import static anderson.android.utilities.Global_Client.mGoogleApiClient;
import static anderson.android.utilities.Utilities_Client.getPhoneNumberOnly;
import static android.content.Context.LOCATION_SERVICE;

/***
 *  Anderson Mai. 06/09/2015
 *  Receive commands and data from Location base via SMS
 */

public class SMSClientReceiver extends BroadcastReceiver {

	private String formatedNumberStr = "";
	private String formatedCallStr = "";

	final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
	final String TAG = "SMSReceiver";
	String msg_from;
	Context m_context;

	public void onReceive(final Context context, Intent intent) {
		m_context = context;
		String action = intent.getAction();
		if (intent.getAction().equals(SMS_RECEIVED_ACTION)) {
			Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
			SmsMessage msgs = null;
			if (bundle != null) {
				//---retrieve the SMS message received---
				try {
					Object[] pdus = (Object[]) bundle.get("pdus");
					msgs = SmsMessage.createFromPdu((byte[]) pdus[0]);
					String incoming_msg = msgs.getDisplayMessageBody();
					msg_from = msgs.getOriginatingAddress();

					String[] field_data = incoming_msg.split(Constants_Client.POUND);
					String baseCommand = field_data[0];
					if (!baseCommand.contentEquals(Constants_Client.KICKUP_REGISTER_ACTION) &&
					        !baseCommand.contentEquals(Constants_Client.CONFIRM_PHASE) &&
							!baseCommand.contentEquals(Constants_Client.SECURE_DISTANCE_CANCEL) &&
							!baseCommand.contentEquals(Constants_Client.REMOVEALINE) &&
							!baseCommand.contentEquals(Constants_Client.NAME_ALREADY_INUSE) &&
							!baseCommand.contentEquals(Constants_Client.NUMBER_ALREADY_INUSE) &&
							!baseCommand.contentEquals(Constants_Client.SECURED_DISTANCE_REQUEST)&&
							!baseCommand.contentEquals(Constants_Client.GEOFENCE_REQUEST) &&
                            !baseCommand.contentEquals(Constants_Client.NAMEANDPHONE_REGISTERED)&&
							!baseCommand.contentEquals(Constants_Client.PREVENT_SHUTDOWN_REQUEST)){
						     return;
					}

					changeRingerMode(context);

					Log.d(" --- sender: ", msg_from);

					if (baseCommand.contains(Constants_Client.KICKUP_REGISTER_ACTION)) {
						    Utilities_Client.displayAToast("Receive KICKUP_REGISTER_ACTION", context);
							Global_Client.phoneAccounts[0] = field_data[1];
							Global_Client.phoneAccounts[1] = msg_from;
							Global_Client.phoneAccounts[2] = field_data[2].toUpperCase();
							Utilities_Client.requestForConnection(Global_Client.phoneAccounts[2],  Global_Client.phoneAccounts[1]);
							StoreDeviceInfor(Global_Client.phoneAccounts, context);
							Log.d(TAG ,"send request for connecting ");
						    return;
					}
					else
					    if (!msg_from.contains(Global_Client.phoneAccounts[1])){
							return;
					}
					if (baseCommand.contains(Constants_Client.CONFIRM_PHASE)|| (baseCommand.contains(Constants_Client.NAMEANDPHONE_REGISTERED)) &&
                            		(incoming_msg.contentEquals(Global_Client.confirmPhase)) ) {
						Utilities_Client.displayAToast("Receive CONFIRM", context);
						if (!Global_Client.phoneAccounts[1].contains(getPhoneNumberOnly(msg_from)) &&
								(!Global_Client.phoneAccounts[2].contains(field_data[3]))){
								  return;
							}
						StoreDeviceInfor(Global_Client.phoneAccounts, context);
						LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

						Global_Client.startTime = System.currentTimeMillis();
						int locationMode = checkAndTurnLocation(context);
						if ((locationMode == Settings.Secure.LOCATION_MODE_HIGH_ACCURACY ) || (locationMode == Settings.Secure.LOCATION_MODE_SENSORS_ONLY)){
						//if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
							Intent m_intent = new Intent(context, UpdateLocationActivity.class);
							m_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							context.startActivity(m_intent);
						} else {
							Intent m_intent = new Intent(context, RetrieveLocationActivity.class);
							m_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							context.startActivity(m_intent);
						}
						Log.d(TAG, "MATCH");
					}
					else if (baseCommand.contains(Constants_Client.SECURE_DISTANCE_CANCEL)
							&& field_data[3].contains(Global_Client.phoneAccounts[2])
							&& Global_Client.phoneAccounts[0].contains(field_data[1]) &&
							msg_from.contains( Global_Client.phoneAccounts[1])){
						    Global_Client.setSafeDistance = false;
						    Global_Client.secured_distance = 0.0;
							WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
							if(wifiManager.isWifiEnabled()){
								wifiManager.setWifiEnabled(false);
							}
							Intent m_intent = new Intent(Constants_Client.REMOVE_GEOFENCE_ACTION);
							context.sendBroadcast(m_intent);
						    return;
					}
					else if (baseCommand.contains(Constants_Client.REMOVEALINE)
							&& field_data[3].contains(Global_Client.phoneAccounts[2])
							&& Global_Client.phoneAccounts[0].contains(field_data[2]) &&
							msg_from.contains( Global_Client.phoneAccounts[1])){
						Utilities_Client.displayAToast(Global_Client.phoneAccounts[2] +
								" is closed by the base", context);

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

						if (mGoogleApiClient != null) {
							if (mGoogleApiClient.isConnected()) {
								Global_Client.CLOSE_FLAG = true;
								// send an action to run the  stopLocationUpdates();
								Intent m_intent = new Intent(Constants.STOP_LOCATION_UPDATE_ACTION);
								context.sendBroadcast(m_intent);
							}
						}

						String text = Constants_Client.REMOVEALINE_CONFIRM + Constants_Client.POUND + "OMITTED" + Constants_Client.POUND
								+ Global_Client.phoneAccounts[1] + Constants_Client.POUND + Global_Client.phoneAccounts[2];
						Utilities_Client.sendText(text, Global_Client.phoneAccounts[1]);
						Global_Client.phoneAccounts[0] = "";
						Global_Client.phoneAccounts[1] = "";
						Global_Client.phoneAccounts[2] = "";
						StoreDeviceInfor(Global_Client.phoneAccounts, m_context);
						m_context.deleteFile(Constants_Client.GMaiInforFile);
						WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
						if(!wifiManager.isWifiEnabled()){
							wifiManager.setWifiEnabled(true);
						}
						new Handler().postDelayed(new Runnable() {
							public void run() {
								Intent m_intent = new Intent(Constants.CLOSE_APP_ACTION);
								m_context.sendBroadcast(m_intent);
							}
						},5000);


					}
					else if (baseCommand.contains(Constants_Client.NAME_ALREADY_INUSE)
							&& incoming_msg.contains(Global_Client.phoneAccounts[2])
							&& incoming_msg.contains(Global_Client.phoneAccounts[1])) {
						    Utilities_Client.displayAToast(Global_Client.phoneAccounts[2] +
								" is registered in another device.\n Please select another name.", context);
                            //Global_Client.phoneAccounts[0] = "";
                           // Global_Client.phoneAccounts[1] = "";
                           // Global_Client.phoneAccounts[2] = "";
					}
					else if (baseCommand.contains(Constants_Client.DEVICE_ALREADY_INUSE)
							&& incoming_msg.contains(Global_Client.phoneAccounts[2])
							&& incoming_msg.contains(Global_Client.phoneAccounts[1])) {
						    Utilities_Client.displayAToast(Global_Client.phoneAccounts[0] +
								"is already in use  with the base.\n Please register with another number.", context);
                           // Global_Client.phoneAccounts[0] = "";
                           // Global_Client.phoneAccounts[1] = "";
                           // Global_Client.phoneAccounts[2] = "";
					}
					/*else if (baseCommand.contains(Constants_Client.CLIENT_CLOSE_CONFIRM)
							&& incoming_msg.contains(Global_Client.phoneAccounts[2])
							&& incoming_msg.contains(Global_Client.phoneAccounts[1])) {
						Utilities_Client.displayAToast(Global_Client.phoneAccounts[0] +
								"Location Client is closing ...", context);

						if (Global_Client.mGoogleApiClient != null) {
							if (Global_Client.mGoogleApiClient.isConnected()) {
								Global_Client.CLOSE_FLAG = true;
								// send an action to run the  stopLocationUpdates();
								Intent m_intent = new Intent(Constants.STOP_LOCATION_UPDATE_ACTION);
								context.sendBroadcast(m_intent);
							}
						}
						new Handler().postDelayed(new Runnable() {
							public void run() {
								Intent m_intent = new Intent(Constants.CLOSE_APP_ACTION);
								m_context.sendBroadcast(m_intent);
							}
						},20000);

					}
					*/

					else if (baseCommand.contains(Constants_Client.PREVENT_SHUTDOWN_REQUEST)) {
						Constants_Client.PREVENT_SHUTDOWN = true;
						Log.d("Update", "--- PREVENT_SHUTDOWN_REQUEST ----");
					}
					else if (baseCommand.contains(Constants_Client.VIDEO_RECORD_REQUEST)) {
						Constants_Client.PREVENT_SHUTDOWN = true;
					}
					else if (baseCommand.contains(Constants_Client.GEOFENCE_REQUEST)) {
                            Log.d("Update", "--- GEOFORCE REQUEST ----");
                            // FENCE_REQUEST, device name, distance, latitude, longitude, expired time
                            Global_Client.secured_Distance_Log[0] = field_data[2];
                            Global_Client.secured_Distance_Log[1] = field_data[3];
                            Global_Client.secured_Distance_Log[2] = field_data[4];
                            Global_Client.secured_Distance_Log[3] = field_data[5];

                            if (!field_data[1].contains(Global_Client.phoneAccounts[2])){
                                return;
                            }
							final WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
						    if (!wifiManager.isWifiEnabled()) {
								wifiManager.setWifiEnabled(true);
							}

							new Handler().postDelayed(new Runnable() {
								public void run() {
									if (!Utilities_Client.isNetworkAvailable(m_context)){
										Log.d("Update", "---Send START_SECURED DISTANCE _ACTION");
										String text = Constants_Client.NO_GEOFENCE_BUT_SECURED_DISTANCE + Constants_Client.POUND + "OMITTED" + Constants_Client.POUND
												+ Global_Client.phoneAccounts[1] + Constants_Client.POUND + Global_Client.phoneAccounts[2];
										Utilities_Client.sendText(text, Global_Client.phoneAccounts[1]);
									}
									else {
										Intent m_intent = new Intent(Constants.START_GEOFENCE_ACTION);
										context.sendBroadcast(m_intent);
									}
								}
							},4000);
					}
					else {
							Log.d(" --- MATCH ", " NOT MATCH");
					}
				} catch (Exception e) {
							Log.d("Exception caught", e.getMessage());
				}
			}
		}
	}

	public void sendText(String m_text, String dest) {

		SmsManager.getDefault().sendTextMessage(dest, null, m_text, null, null);
	}

	public String locWithAddress(String loc) {
		String[] splitedFields = loc.split(Constants_Client.AT);
		double latitude = Double.parseDouble(splitedFields[1]);
		double longitude = Double.parseDouble(splitedFields[2]);
		String address = Utilities_Client.getAddressGivenLongAndLate(latitude, longitude);
		return splitedFields[0] + "\n" + address;

	}

	// Write the messages in Global.alertedmsgInfo to file
	private void StoreGMaiInfor(String[] GMaiInfor, Context mContext) {
		String temp = "";
		int length = GMaiInfor.length;
		if (length == 2) {
			temp = GMaiInfor[0] + "\n" + GMaiInfor[1] + "\n";
			FileOutputStream f_writer = null;
			try {
				f_writer = mContext.openFileOutput(Constants_Client.GMaiInforFile, Context.MODE_PRIVATE);

			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			if (f_writer != null) {
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

	// Write the messages in Global.alertedmsgInfo to file
	private void StoreDeviceInfor(String[] DeviceInfor, Context mContext) {
		String temp = "";
		int length = DeviceInfor.length;
		if (length == 3) {
			temp = DeviceInfor[0] + "\n" + DeviceInfor[1] + "\n" + DeviceInfor[2] + "\n";
			FileOutputStream f_writer = null;
			try {
				f_writer = mContext.openFileOutput(Constants_Client.GMaiInforFile, Context.MODE_PRIVATE);

			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			if (f_writer != null) {
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
	public void display_Notification(int m_icon_image) {

		String ns = Context.NOTIFICATION_SERVICE;
		Global_Client.NotifyManager = (NotificationManager)Global_Client.my_context.getSystemService(ns);
		Global_Client.icon = m_icon_image;

		Notification.Builder mBuilder =
				new Notification.Builder(Global_Client.my_context)
						.setSmallIcon(Global_Client.icon)
						.setContentTitle("Tracking Client");
		Intent notificationIntent = new Intent(Global_Client.my_context, LastPage.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		PendingIntent contentIntent =
				PendingIntent.getActivity(Global_Client.my_context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(contentIntent);
		Notification notification = mBuilder.build();
		Global_Client.NotifyManager.notify(Global_Client.Notify_ID, notification);
		Global_Client.cursorId = Global_Client.Notify_ID;

	}

	private boolean isNetworkAvailable(Context mContext) {
		ConnectivityManager connMgr
				= (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

			Network[] networks = connMgr.getAllNetworks();
			NetworkInfo networkInfo;
			for (Network mNetwork : networks) {
				networkInfo = connMgr.getNetworkInfo(mNetwork);
				if (networkInfo != null && ((networkInfo.getType() == ConnectivityManager.TYPE_WIFI)|| (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE)) && networkInfo.isConnected()) {
					return true;
				}
			}
			return false;

		}
		else {
			final NetworkInfo wifi =
					connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if( wifi.isAvailable() && wifi.getDetailedState() == NetworkInfo.DetailedState.CONNECTED){
				return true;
			}

			final NetworkInfo mobile =
					connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if( mobile.isAvailable() && mobile.getDetailedState() == NetworkInfo.DetailedState.CONNECTED ){
				return true;
			}
		}
			return false;
	}
	public int checkAndTurnLocation(Context context) {
		int locationMode = 0;
		String locationProviders;
		//Equal or higher than API 19/KitKat

		try {
			locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
		} catch (Settings.SettingNotFoundException e) {
			e.printStackTrace();
		}
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if (locationMode ==  Settings.Secure.LOCATION_MODE_HIGH_ACCURACY) {
			if (wifiManager.isWifiEnabled()) {
				wifiManager.setWifiEnabled(false);
			}

		}
		else  if (locationMode ==  Settings.Secure.LOCATION_MODE_SENSORS_ONLY){
			if (!wifiManager.isWifiEnabled()) {
				wifiManager.setWifiEnabled(true);
			}
		}
		else  if (locationMode ==  Settings.Secure.LOCATION_MODE_OFF){
			if (!wifiManager.isWifiEnabled()) {
				wifiManager.setWifiEnabled(true);
			}
		}
		return locationMode;
	}
	public void changeRingerMode(Context context){

		final AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

		/**
		 * To Enable silent mode.....
		 */
		audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		audio.setStreamVolume(AudioManager.STREAM_RING,0,0);


		/**
		 * To Enable Ringer mode.....
		 */new Handler().postDelayed(new Runnable() {
			public void run() {
				audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
				audio.setStreamVolume(AudioManager.STREAM_RING,5, 0);
			}
		},5000);


	}
}