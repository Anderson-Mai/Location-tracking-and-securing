package anderson.android.retrievelocation;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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

import static anderson.android.utilities.Utilities_Client.getPhoneNumberOnly;
import static android.content.Context.LOCATION_SERVICE;

/***
 * Anderson Mai. 09/08/2017
 * Start Location Client right after completed booting
 */

public class BootTimeReceiver extends BroadcastReceiver {

	private String formatedNumberStr = "";
	private String formatedCallStr = "";

	final String BOOT_COMPLETED_ACTION = "android.intent.action.BOOT_COMPLETED";
	final String TAG = "BootTimeReceiver";
	String msg_from;
	Context m_context;

	public void onReceive(Context context, Intent intent) {
		m_context = context;
		String action = intent.getAction();
		Log.d("---ACTION: ", action);
		if (intent.getAction().equals(BOOT_COMPLETED_ACTION)) {
			Intent firstStart = new Intent(context, FirstSetUp_Client.class);
			firstStart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(firstStart);

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

		//  Global.icon = R.drawable.email_image;
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

}