package anderson.android.trackingbase;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

import anderson.android.device_one.Device_One_Global;
import anderson.android.device_two.Device_Two_Global;

//import anderson.android.device_one.DeviceOneCheckCurrentLocation;
//import anderson.android.device_seven.DeviceSevenCheckCurrentLocation;

public class DeviceAlarmActivity extends Activity {
	final String TAG = "DeviceAlarmReceiver";
	String msg_from;
	Context m_context = null;
	MediaPlayer mMediaPlayer;

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

		// Get extra data included in the Intent
		Intent mIntent = getIntent();
		int device_numb = mIntent.getIntExtra(Constants.SEND_DEVICE_NUMBER, -1);
		 if (device_numb == -1){
			return;
		}
		alerting(device_numb, this);
	}

	private void alerting(final int alertedDevice, Context mContext){
        Log.i("TAG", "alertDevice : " +  String.valueOf(alertedDevice));
		AlertDialog.Builder alertbox_2 = new AlertDialog.Builder(mContext);
        Log.i("TAG", "Alert Name : " +  Global.nameAccounts.get(alertedDevice));
		String deviceName = "Track the " + Global.nameAccounts.get(alertedDevice) ;
		Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
		mMediaPlayer = new MediaPlayer();
		try {
			mMediaPlayer.setDataSource(mContext, alert);
		} catch (IOException e) {
			e.printStackTrace();
		}
		final AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
		//if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
			mMediaPlayer.setLooping(true);
			try {
				mMediaPlayer.prepare();
			} catch (IOException e) {
				e.printStackTrace();
			}
			mMediaPlayer.start();
		//}
		alertbox_2.setTitle(deviceName);
		alertbox_2.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0,int arg1) {
						mMediaPlayer.stop();
						mMediaPlayer.reset();
						mMediaPlayer.release();
						// Start real time location tracking: We are chasing the beauty who steals my BMW ...
						followAClientDevice(alertedDevice);
						finish();
					}
				});

		alertbox_2.setNegativeButton("SKIP",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0,int arg1) {
						mMediaPlayer.stop();
						mMediaPlayer.reset();
						mMediaPlayer.release();
						mMediaPlayer = null;
						Global.alarming = false;
						arg0.dismiss();
						finish();

					}
				});

		alertbox_2.show();
	}

	private void followAClientDevice(int deviceNumb){
		String selectLocData = null;
		String selectAddressData = null;
		int LocSize = 0;
		switch(deviceNumb){
			case 0:
				selectLocData = Device_One_Global.Last_Thirty.get(LocSize);
				selectAddressData = Device_One_Global.Last_Thirty_RealAddress.get(LocSize);
				break;
			case 1:
				selectLocData = Device_Two_Global.Last_Thirty.get(LocSize);
				selectAddressData = Device_Two_Global.Last_Thirty_RealAddress.get(LocSize);
				break;
			/* case 2:
				LocSize = Device_Three_Global.Last_Thirty.size();
				selectLocData = Device_Three_Global.Last_Thirty.get(LocSize);
				selectAddressData = Device_Three_Global.Last_Thirty_RealAddress.get(LocSize);
				break;
			case 3:
				LocSize = Device_Four_Global.Last_Thirty.size();
				selectLocData = Device_Four_Global.Last_Thirty.get(LocSize);
				selectAddressData = Device_Four_Global.Last_Thirty_RealAddress.get(LocSize);
				break;
			case 4:
				LocSize = Device_Five_Global.Last_Thirty.size();
				selectLocData = Device_Five_Global.Last_Thirty.get(LocSize);
				selectAddressData = Device_Five_Global.Last_Thirty_RealAddress.get(LocSize);
				break;
			case 5:
				LocSize = Device_Six_Global.Last_Thirty.size();
				selectLocData = Device_Six_Global.Last_Thirty.get(LocSize);
				selectAddressData = Device_Six_Global.Last_Thirty_RealAddress.get(LocSize);
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
		if (mapIntent.resolveActivity(getPackageManager()) != null) {
			startActivity(mapIntent);
		}
	}


}