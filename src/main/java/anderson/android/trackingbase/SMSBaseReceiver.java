package anderson.android.trackingbase;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsMessage;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import anderson.android.device_one.Device_One_Constants;
import anderson.android.device_one.Device_One_Global;
import anderson.android.device_two.Device_Two_Constants;
import anderson.android.device_two.Device_Two_Global;

public class SMSBaseReceiver extends BroadcastReceiver {

	private String formatedNumberStr = "";
	private String formatedCallStr = "";

	final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
	final String TAG = "SMSReceiver";
	public static String msg_from;
	Context m_context;
	String physicalAddress = null;
	MediaPlayer mMediaPlayer;
	String[] subject_fields = null;

	public void onReceive(Context context, Intent intent) {
		int i = -1;
		m_context = context;
		String action = intent.getAction();
		Log.d("---ACTION: ", action);
		if (intent.getAction().equals(SMS_RECEIVED_ACTION)) {
			Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
			SmsMessage msgs = null;
			if (bundle != null) {
				//---retrieve the SMS message received---
				Object[] pdus = (Object[]) bundle.get("pdus");

				msgs = SmsMessage.createFromPdu((byte[]) pdus[0]);
				msg_from = Utilities.getDigitsOnly(msgs.getOriginatingAddress());
				Log.d("---incoming number: ", msg_from);
				String incoming_msg = msgs.getDisplayMessageBody();
				Log.d("---incoming message: ", incoming_msg);

				subject_fields = null;
				subject_fields = incoming_msg.split(Constants.POUND);
				String fromName = subject_fields[3];
				String newest_location = subject_fields[2];
				//String safeDistance = subject_fields[4];

				if (incoming_msg.contains(Constants.REGISTER_PHASE) ||  incoming_msg.contains(Constants.NO_GEOFENCE_BUT_SECURED_DISTANCE)
						 || incoming_msg.contains(Constants.FENCE_CREATED_CONFIRM) || incoming_msg.contains(Constants.CLIENT_RESETTED)
						 || incoming_msg.contains(Constants.SECURE_DISTANCE_PASSED) || incoming_msg.contains(Constants.REMOVE_GEOFENCE_ACTION)
						 || incoming_msg.contains(Constants.SECURE_DISTANCE_PASSED_POSITION)|| subject_fields[0].contains(Constants.REMOVEALINE_CONFIRM)
						 || subject_fields[0].contains(Constants.CLIENTCLOSE )|| incoming_msg.contains(Constants.SUBJECT_MASK_GPS)){
					        changeRingerMode(context);
				}

				if (incoming_msg.contains(Constants.REGISTER_PHASE)) {
					for (int indx = 0; indx < Global.phoneAccounts.size(); indx++) {
						Log.d("INDEX 1: ", String.valueOf(indx));

						if (Global.phoneAccounts.get(indx).contentEquals(msg_from) &&
								(Global.nameAccounts.get(indx).contentEquals(fromName))) {
							incoming_msg = incoming_msg.replace(Constants.REGISTER_PHASE, Constants.CONFIRM_PHASE);
							Utilities.sendText(incoming_msg, msg_from);
                            return;
						}
						else
						if (!Global.phoneAccounts.get(indx).contentEquals(msg_from) ||
								(Global.nameAccounts.get(indx).contentEquals(fromName))) {
							incoming_msg = incoming_msg.replace(Constants.REGISTER_PHASE, Constants.NAME_ALREADY_INUSE);
							Utilities.sendText(incoming_msg, msg_from);
							return;
						}
						else
						if (Global.phoneAccounts.get(indx).contentEquals(msg_from) ||
								(!Global.nameAccounts.get(indx).contentEquals(fromName))) {
							incoming_msg = incoming_msg.replace(Constants.REGISTER_PHASE, Constants.DEVICE_ALREADY_INUSE);
							Utilities.sendText(incoming_msg, msg_from);
							return;
						}
					}

					Global.phoneAccounts.add(msg_from);
					Global.nameAccounts.add(fromName);
					Global.g_adapter.notifyDataSetChanged();

					Log.d(" ---incoming name  ", fromName);
					Log.d(" ---CONFIRM PAGE ", incoming_msg);
					incoming_msg = incoming_msg.replace(Constants.REGISTER_PHASE,Constants.CONFIRM_PHASE);
					Utilities.sendText(incoming_msg, msg_from);
					Utilities.BackUpDeviceList(Global.phoneAccounts, Global.nameAccounts, context);

					Intent broadcast_intent = new Intent(Constants.NEW_DEVICE_ADDED);
					broadcast_intent.putExtra(Constants.SENDER_NAME, fromName);
					context.sendBroadcast(broadcast_intent);
				}
				else if (incoming_msg.contains(Constants.NO_GEOFENCE_BUT_SECURED_DISTANCE)) {
					Utilities.displayAToast("GeoFence is not available at this location\n. Activated secured distance instead ...", context);
				}
				else if (incoming_msg.contains(Constants.FENCE_CREATED_CONFIRM)) {
					Utilities.displayAToast("GeoFence is activated", context);
				}
				else if (incoming_msg.contains(Constants.CLIENT_RESETTED)) {
					resetThisClient(fromName, msg_from, Global.phoneAccounts,  Global.nameAccounts);
					// Utilities.BackUpDeviceList(Global.phoneAccounts, Global.nameAccounts, context);
					Intent  broadcast_intent = new Intent(Device_One_Constants.DEVICE_ONE_ADAPTER_UPDATED);
					Global.my_context.sendBroadcast(broadcast_intent);
					//Global.g_adapter.notifyDataSetChanged();
				}

				else if (incoming_msg.contains(Constants.SECURE_DISTANCE_PASSED)) {
					Log.d(TAG, incoming_msg);
					for (i = 0; i < Global.phoneAccounts.size(); i++) {
						Log.d("phoneAccounts:  ", Global.phoneAccounts.get(i));
						if ((Global.phoneAccounts.get(i).contains(msg_from))&&
						   (Global.nameAccounts.get(i).contains(fromName))) {
							if (Global.alarming_tracker[i] == 0) {
								// This device is not alarming.
								//  Set alarm for this device name
								Global.alarming_tracker[i] = 1;
								Intent alarmIntent = new Intent(context, DeviceAlarmActivity.class);
								alarmIntent.putExtra(Constants.SEND_DEVICE_NUMBER, i);
								alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								context.startActivity(alarmIntent);
							}
							if (Global.presetDistance[i] > 0.0) {
								Global.presetDistance[i] = 0.0;
							}

							incoming_msg = incoming_msg.replace(Constants.SECURE_DISTANCE_PASSED, Constants.SECURE_DISTANCE_CANCEL);
							Utilities.sendText(incoming_msg, msg_from);
							return;
						}
					}

				}

				else if (incoming_msg.contains(Constants.REMOVE_GEOFENCE_ACTION)) {
					Log.d(TAG, incoming_msg);
					for (i = 0; i < Global.phoneAccounts.size(); i++) {
						Log.d("phoneAccounts:  ", Global.phoneAccounts.get(i));
						if ((Global.phoneAccounts.get(i).contains(msg_from)) &&
								(Global.nameAccounts.get(i).contains(fromName))) {
							if (Global.alarming_tracker[i] == 1) {
								Global.alarming_tracker[i] = 0;
								Global.presetDistance[i] = 0.0;
							}
							return;
						}
					}

				}
				else if (incoming_msg.contains(Constants.SECURE_DISTANCE_PASSED_POSITION)) {
					Log.d(TAG, incoming_msg);
					//String [] passIn = incoming_msg.split(Constants.POUND);
					for (i = 0; i < Global.phoneAccounts.size(); i++) {
						Log.d("phoneAccounts:  ", Global.phoneAccounts.get(i));
						if ((Global.phoneAccounts.get(i).contains(msg_from))&&
								(Global.nameAccounts.get(i).contains(fromName))) {
							if (Global.alarming_tracker[i] == 1) {
								// This device is alarming. We update the location map
								// with new lat-long
								followAClientDevice(i);
							}
							return;
						}
					}
				}

				else if (subject_fields[0].contains(Constants.REMOVEALINE_CONFIRM)){
					removeThisClient(fromName, msg_from, Global.phoneAccounts,  Global.nameAccounts);
					Utilities.BackUpDeviceList(Global.phoneAccounts, Global.nameAccounts, context);
					Global.g_adapter.notifyDataSetChanged();
				}

				else if (subject_fields[0].contains(Constants.CLIENTCLOSE )){
					String closeALine_Msg = Constants.REMOVEALINE + Constants.POUND + "OMITTED" + Constants.POUND + msg_from +Constants.POUND + fromName;
					Utilities.sendText(closeALine_Msg, msg_from);

				}
				else if (incoming_msg.contains(Constants.SUBJECT_MASK_GPS)) {

					//abortBroadcast();
					Log.d("---subject mask:  ", incoming_msg);
					// 0 = GPS_LOCATION, 1 = Time, 2 = Latitude- Longitude, 3 = phone number 1xxx xxx xxxx
					int indx;
					boolean name_register = false;
					for (indx = 0; indx < Global.nameAccounts.size(); indx++) {
						if (Global.nameAccounts.get(indx).contentEquals(fromName)) {
							//This device is already registered
							Log.d("---already register: ", fromName);
							//	Global.g_adapter.notifyDataSetChanged();
							name_register = true;
							break;
						}
					}

					if (!name_register) {
						Log.d("Name not registered: ", fromName);
						Global.phoneAccounts.add(msg_from);
						Global.nameAccounts.add(fromName);
						Utilities.BackUpDeviceList(Global.phoneAccounts, Global.nameAccounts, context);
						Intent broadcast_intent = new Intent(Constants.NEW_DEVICE_ADDED);
						broadcast_intent.putExtra(Constants.SENDER_NAME, fromName);
						// You can also include some extra data.
						context.sendBroadcast(broadcast_intent);
						Global.g_adapter.notifyDataSetChanged();
					} else {

						//Check distance
						Log.d("--- from:", msg_from);
						Log.d("--- name: ", subject_fields[3]);
						Log.d("--- new location 2: ", subject_fields[2]);
						Log.d("--- Newest Location:  ", newest_location);
						String[] late_long_fields = subject_fields[2].split(Constants.AT);
						Double[] late_longi_vals = new Double[2];
						late_longi_vals[0] = Double.parseDouble(late_long_fields[0]);
						late_longi_vals[1] = Double.parseDouble(late_long_fields[1]);
						String safeDistance = subject_fields[4];

						if (safeDistance.contentEquals(Constants.SECURED_DISTANCE_REQUEST)) {
							int numberIndex = Global.phoneAccounts.indexOf(msg_from);
							if (numberIndex > -1) {
								String[] mLocation = Global.securedPoint[numberIndex].split(Constants.POUND);
								if (Global.alarming_tracker[numberIndex] != 1) {
									double distanceBeweenTwoLocs = Utilities.distanceA_B(late_longi_vals[0], late_longi_vals[1],
											Double.parseDouble(mLocation[0]),
											Double.parseDouble(mLocation[1]));

									if (distanceBeweenTwoLocs >= Global.presetDistance[numberIndex]) {
										if (Global.alarming_tracker[numberIndex] == 0) {
											// This device is not alarming.
											//  Set alarm for this device name
											Global.alarming_tracker[numberIndex] = 1;
											Intent alarmIntent = new Intent(context, DeviceAlarmActivity.class);
											alarmIntent.putExtra(Constants.SEND_DEVICE_NUMBER, numberIndex);
											alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
											context.startActivity(alarmIntent);
											//	mMediaPlayer = new MediaPlayer();
											//	alerting(i, m_context);

											//	incoming_msg = incoming_msg.replace(Constants.SECURE_DISTANCE_PASSED, Constants.SECURE_DISTANCE_PASSED_POSITION);
											//	Utilities.sendText(incoming_msg, msg_from);
											return;
										}
									}
								}
							}
						}
						GetRealAddress getAddress = new GetRealAddress();
						getAddress.execute(late_longi_vals[0], late_longi_vals[1]);
					}
				}
				else {
					Log.d("---latest Location: ", "Constants.ADAPTER_UPDATED");
				}
			}
		}
	}

	public String locWithAddress( String loc) throws IOException, JSONException {
		String [] splitedFields = loc.split(Constants.AT);
		double latitude = Double.parseDouble(splitedFields[1]);
		double longitude = Double.parseDouble(splitedFields[2]);

		String address = Utilities.getAddressGivenLongAndLate(latitude, longitude, m_context);
		return splitedFields[0] + "\n" + address;
	}
	private void removeThisClient(String clientName, String bclientNumber,
									 ArrayList<String> phoneAccounts,  ArrayList<String> nameAccounts){
		for (int i = 0; i < phoneAccounts.size(); i++ ){
			if (phoneAccounts.get(i).contentEquals(bclientNumber) && nameAccounts.get(i).contentEquals(clientName)){
				phoneAccounts.remove(i);
				nameAccounts.remove(i);
				Global.account_Controler[i] = false;
				switch(i){
					case 0:

						Intent broadcast_intent = new Intent(Constants.CLIENT_ONE_CLOSED_ACTION);
						m_context.sendBroadcast(broadcast_intent);
						break;
					case 1:
						broadcast_intent = new Intent(Constants.CLIENT_TWO_CLOSED_ACTION);
						m_context.sendBroadcast(broadcast_intent);
						break;
					case 2:
						broadcast_intent = new Intent(Constants.CLIENT_THREE_CLOSED_ACTION);
						m_context.sendBroadcast(broadcast_intent);
						break;
					case 3:
						broadcast_intent = new Intent(Constants.CLIENT_FOUR_CLOSED_ACTION);
						m_context.sendBroadcast(broadcast_intent);
						break;
					case 4:
						broadcast_intent = new Intent(Constants.CLIENT_FIVE_CLOSED_ACTION);
						m_context.sendBroadcast(broadcast_intent);
						break;
					case 5:
						broadcast_intent = new Intent(Constants.CLIENT_SIX_CLOSED_ACTION);
						m_context.sendBroadcast(broadcast_intent);
						break;
				}
				//Intent broadcast_intent = new Intent(Device_Four_Constants.DEVICE_FOUR_ADAPTER_UPDATED);
				//broadcast_intent.putExtra(Constants.SENDER_NAME, fromName);
				//broadcast_intent.putExtra(Constants.ADDRESS, newest_location);
			}
		}
	}
	private void resetThisClient(String clientName, String bclientNumber,
								  ArrayList<String> phoneAccounts,  ArrayList<String> nameAccounts){
		for (int i = 0; i < phoneAccounts.size(); i++ ){
			if (phoneAccounts.get(i).contentEquals(bclientNumber) && nameAccounts.get(i).contentEquals(clientName)){
				switch(i){
					case 0:
						Device_One_Global.Last_Thirty.clear();
						Device_One_Global.Last_Thirty_RealAddress.clear();
						m_context.deleteFile(Device_One_Constants.ClientOne_last_30_addresses);
						m_context.deleteFile(Device_One_Constants.ClientOne_last_30_ll);
						//Utilities.BackUpLocations(Device_One_Global.Last_Thirty_RealAddress, Device_One_Constants.ClientOne_last_30_addresses, this);
						//Utilities.BackUpLocations(Device_One_Global.Last_Thirty, Device_One_Constants.ClientOne_last_30_ll, this);
						break;
					case 1:
						Device_Two_Global.Last_Thirty.clear();
						Device_Two_Global.Last_Thirty_RealAddress.clear();
						m_context.deleteFile(Device_Two_Constants.ClientTwo_last_30_addresses);
						m_context.deleteFile(Device_Two_Constants.ClientTwo_last_30_ll);
						break;
					case 2:
						break;
					case 3:
						break;
					case 4:
						break;
					case 5:
						break;
				}
				//Intent broadcast_intent = new Intent(Device_Four_Constants.DEVICE_FOUR_ADAPTER_UPDATED);
				//broadcast_intent.putExtra(Constants.SENDER_NAME, fromName);
				//broadcast_intent.putExtra(Constants.ADDRESS, newest_location);
			}
		}
	}
	private void followAClientDevice(int deviceNumb){
		String selectLocData = null;
		String selectAddressData = null;
		int LocSize = Device_One_Global.Last_Thirty.size();
		switch(deviceNumb){
			case 0:
				LocSize = Device_One_Global.Last_Thirty.size();
				selectLocData = Device_One_Global.Last_Thirty.get(LocSize);
				selectAddressData = Device_One_Global.Last_Thirty_RealAddress.get(LocSize);
				break;
			case 1:
				LocSize = Device_Two_Global.Last_Thirty.size();
				selectLocData = Device_One_Global.Last_Thirty.get(LocSize);
				selectAddressData = Device_One_Global.Last_Thirty_RealAddress.get(LocSize);
				break;
			/*case 2:
				LocSize = Device_Three_Global.Last_Thirty.size();
				selectLocData = Device_One_Global.Last_Thirty.get(LocSize);
				selectAddressData = Device_One_Global.Last_Thirty_RealAddress.get(LocSize);
				break;
			case 3:
				LocSize = Device_Three_Global.Last_Thirty.size();
				selectLocData = Device_One_Global.Last_Thirty.get(LocSize);
				selectAddressData = Device_One_Global.Last_Thirty_RealAddress.get(LocSize);
				break;
			case 4:
				LocSize = Device_Three_Global.Last_Thirty.size();
				selectLocData = Device_One_Global.Last_Thirty.get(LocSize);
				selectAddressData = Device_One_Global.Last_Thirty_RealAddress.get(LocSize);
				break;
			case 5:
				LocSize = Device_Three_Global.Last_Thirty.size();
				selectLocData = Device_One_Global.Last_Thirty.get(LocSize);
				selectAddressData = Device_One_Global.Last_Thirty_RealAddress.get(LocSize);
				break;
				*/
		}

		Log.i("--- LAST THIRTY  :  ", selectLocData );

		String [] splitedFields = selectLocData.split(Constants.AT);
		Log.i("---REAL ADDRESS  :  ", selectAddressData );
		String [] splitedAddress = selectAddressData.split("\n");

		String m_longitude = splitedFields[1];
		String m_address = splitedAddress[1];
		String m_latitude = splitedFields[0];

		m_address.replace(" ", "+");
		Uri gmmIntentUri = Uri.parse("geo:"+ m_latitude + "," + m_longitude + "?q=" + m_address);
		Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
		mapIntent.setPackage("com.google.android.apps.maps");
		if (mapIntent.resolveActivity(m_context.getPackageManager()) != null) {
			m_context.startActivity(mapIntent);
		}
	}


	class GetRealAddress extends AsyncTask<Double , Void, String> {

		@Override
		protected String doInBackground(Double... params) {

			String my_address = null;

			my_address = Utilities.getAddressGivenLongAndLate( params[0], params[1], m_context);
			return my_address;
		}

		@Override
		protected void onPostExecute(String realAddress) {

			String  addressWithTime = subject_fields[1] + realAddress;
			CheckIncomingMsg smsChecker = new CheckIncomingMsg(addressWithTime, subject_fields[2] , msg_from);
			String latestLocation = smsChecker.NewMsgHandler();
			Log.d("--- Latest Location:   ", latestLocation);
			Log.d("--- Name Size:   ", String.valueOf(Global.nameAccounts.size()));

		}

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}


    private void acceptOrCancel_Request(final String deviceName, final String devicePhone){
        AlertDialog.Builder alertbox_2 = new AlertDialog.Builder(m_context);
        String m_deviceName = "Client " + deviceName + " request to be remove from the base" ;

        alertbox_2.setTitle(m_deviceName);
        alertbox_2.setPositiveButton("ACCEPT",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0,int arg1) {
                        String removeALine_Msg = Constants.REMOVEALINE + Constants.POUND + "OMITTED" + Constants.POUND + devicePhone +Constants.POUND + deviceName;
                        Utilities.sendText(removeALine_Msg, devicePhone);
                    }
                });

        alertbox_2.setNegativeButton("CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0,int arg1) {
                        return;
                    }
                });
        alertbox_2.show();
    }

	public void changeRingerMode(Context context){

		final AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

		/**
		 * To Enable silent mode.....
		 */
		audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		audio.setStreamVolume(AudioManager.STREAM_RING, 0,0);

		/**
		 * To Enable Ringer mode.....
		 */new Handler().postDelayed(new Runnable() {
			public void run() {
				audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
				audio.setStreamVolume(AudioManager.STREAM_RING, 5, 0);
			}
		},5000);


	}

}