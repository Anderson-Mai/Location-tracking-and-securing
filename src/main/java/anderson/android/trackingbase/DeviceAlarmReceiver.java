package anderson.android.trackingbase;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import anderson.android.device_one.Device_One_Global;
import anderson.android.device_two.Device_Two_Global;

//import anderson.android.device_one.DeviceOneCheckCurrentLocation;
//import anderson.android.device_seven.DeviceSevenCheckCurrentLocation;

public class DeviceAlarmReceiver extends BroadcastReceiver {
	final String TAG = "DeviceAlarmReceiver";
	String msg_from;
	Context m_context = null;
	MediaPlayer mMediaPlayer;

	@Override
	public void onReceive(Context context, Intent intent) {
		// Get extra data included in the Intent
		m_context = context;
		String Broadcast_Command = intent.getAction();
		if (Broadcast_Command.contentEquals(Constants.SET_ALARM_ACTION)) {
			Utilities.putToast("Alarming ...", 0, 200);
			final int device_number = intent.getIntExtra(Constants.SEND_DEVICE_NUMBER, -1);
			if (device_number == -1) {
				return;
			}
			followAClientDevice(device_number);

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

}