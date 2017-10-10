package anderson.android.trackingbase;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import anderson.android.device_one.Device_One_Constants;
import anderson.android.device_one.Device_One_Global;
import anderson.android.device_one.RetrieveClientLocationOne;
import anderson.android.device_one.SettingPageOne;
import anderson.android.device_two.Device_Two_Constants;
import anderson.android.device_two.Device_Two_Global;
import anderson.android.device_two.RetrieveClientLocationTwo;
import anderson.android.device_two.SettingPageTwo;


public class DeviceActivity extends Activity implements CustomList_One.customButtonListener {
	private int ret_val = 0;
	private final int RESULT_OK = 2;
	private final int CODE_ONE = 1;
	private ListView listView;
	private LinearLayout mListView;
	private Button mHome;
	//private Button mButton_DeteleAll;
	private Button mButton_add;
	private LinearLayout eEditViewer = null;
	private TextView mModifyHeader = null;
	private RelativeLayout mTopHeaderLayout = null;
	protected CustomList_One m_adapter;
	Context mContext;
	//protected LinearLayout mOptionsBar = null;
	private boolean reset_flag = true;
	Toast toast;
	private MediaPlayer mMediaPlayer;
	private final int INTERVAL = 300000; // 5 minutes
	private int mode = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.device_page);

		ComponentName component=new ComponentName(getApplicationContext(), SMSBaseReceiver.class);
		getPackageManager()
				.setComponentEnabledSetting(component,
						PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
						PackageManager.DONT_KILL_APP);


		Global.my_context = this;
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			int PERMISSION_ALL = 1;
			String[] PERMISSIONS = {Manifest.permission.CALL_PHONE, Manifest.permission.READ_SMS, Manifest.permission.RECORD_AUDIO};

			if (!hasPermissions(this, PERMISSIONS)) {
				requestPermissions(PERMISSIONS, PERMISSION_ALL);
			}
		}

		/*TelephonyManager tMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		Global.mPhoneNumber = tMgr.getLine1Number();
		if (Global.mPhoneNumber == null ){
			String announce_str = "Can not detect the SIM/ Phone number";
			toast =  Toast.makeText(getApplicationContext(),announce_str, Toast.LENGTH_LONG);
			toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 240);
			toast.show();
			return;
		}

		Global.confirmPhase =  Constants.CONFIRM_PHASE + Global.mPhoneNumber;
		*/
		//Global.phoneAccounts[0] = mPhoneNumber;
		mContext = this;
		Global.g_adapter = m_adapter;
		mTopHeaderLayout = (RelativeLayout) findViewById(R.id.topHeaderLayout);
		listView = (ListView) findViewById(R.id.gmailContactInbox);
		/*listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
				// TODO Auto-generated method stub
				final int mPos = pos;
				final String deviceName = Global.nameAccounts.get( mPos);
				final String deviceNumber = Global.phoneAccounts.get( mPos);
				deleteAlerting(deviceName,mPos, deviceNumber);
				return true;
			}
		});
		*/
		//start_CheckDeviceConnectionService();
	}

	@Override
	protected void onResume() {
		super.onResume();
		try {
			Global.nameAccounts.clear();
			Utilities.getListFromFile(mContext,Global.nameAccounts,Constants.GMaiContactFile);
			Global.phoneAccounts.clear();
			Utilities.getListFromFile(mContext,Global.phoneAccounts,Constants.GMaiInforFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		m_adapter = new CustomList_One(this, Global.nameAccounts);
		m_adapter.setCustomButtonListner(this);
		m_adapter.notifyDataSetChanged();
		Global.g_adapter = m_adapter;
		listView.setAdapter(m_adapter);
		registerReceiver(mDeviceAlarmReceiver,
				new IntentFilter(Constants.SET_ALARM_ACTION));

		registerReceiver(mReceiveRemoveRequest,
				new IntentFilter(Constants.DEVICE_REMOVE_REQUEST));
		registerReceiver(mClosedReceiverOne,
				new IntentFilter(Constants.CLIENT_ONE_CLOSED_ACTION));
		for (int i = 0; i < Global.phoneAccounts.size(); i++){
			switch(i) {
				case 0:
					Utilities.LoadLocation(Device_One_Global.Last_Thirty_RealAddress,
							Device_One_Constants.ClientOne_last_30_addresses, this);
					Log.i("--- onResume  :  ", "TWO");
					Utilities.LoadLocation(Device_One_Global.Last_Thirty, Device_One_Constants.ClientOne_last_30_ll, this);
					break;
				case 1:
					Utilities.LoadLocation(Device_Two_Global.Last_Thirty_RealAddress,
							Device_Two_Constants.ClientTwo_last_30_addresses, this);
					Log.i("--- onResume  :  ", "TWO");
					Utilities.LoadLocation(Device_Two_Global.Last_Thirty, Device_Two_Constants.ClientTwo_last_30_ll, this);
					break;


			}
		}
	}

	protected void onStop() {
		super.onStop();
		unregisterReceiver(mDeviceAlarmReceiver);
		unregisterReceiver(mReceiveRemoveRequest);
		unregisterReceiver(mClosedReceiverOne);
		Utilities.BackUpDeviceList(Global.phoneAccounts, Global.nameAccounts, mContext);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			super.onActivityResult(requestCode, resultCode, data);
			if (data != null) {
				if (requestCode == 101) {
			  		//finish();
				 }
			}		
		}



	@Override
	public void onButtonTwoClickListener(int position) {
		switch(position){
			case 0:   Intent intent_One = new Intent (Global.my_context, RetrieveClientLocationOne.class);
				startActivity(intent_One);
				break;
			case 1:  Intent intent_Two = new Intent (Global.my_context, RetrieveClientLocationTwo.class);
				startActivity(intent_Two);
				break;
			/* case 2:  Intent intent_Three = new Intent (Global.my_context, RetrieveClientLocationThree.class);
				     startActivity(intent_Three);
				     break;
			case 3:  Intent intent_Four = new Intent (Global.my_context, RetrieveClientLocationFour.class);
				     startActivity(intent_Four);
				     break;
			case 4: Intent intent_Five = new Intent (Global.my_context, RetrieveClientLocationFive.class);
				    startActivity(intent_Five);
				    break;
			case 5: Intent intent_Six = new Intent (Global.my_context, RetrieveClientLocationSix.class);
				    startActivity(intent_Six);
				    break;
				    */

		}
	}

	@Override
	public void onButtonZeroClickListener(int mPos) {
		final String deviceName = Global.nameAccounts.get( mPos);
		final String deviceNumber = Global.phoneAccounts.get( mPos);
		deleteAlerting(deviceName,mPos, deviceNumber);
	}

	@Override
	public void onButtonOneClickListener(int position) {
		// Display the recent location
		Intent my_Intent = null;
		switch(position) {
			case 0:
				if (!Device_One_Global.Last_Thirty.isEmpty()) {
					my_Intent = new Intent(this, ViewSetUpCurrentLocation.class);
				}
				else return;
				break;
			case 1:
				if (!Device_Two_Global.Last_Thirty.isEmpty()) {
					my_Intent = new Intent(this, ViewSetUpCurrentLocation.class);
				}
				else
				    return;
				break;
			/* 	if (!Device_Three_Global.Last_Thirty.isEmpty()) {
					my_Intent = new Intent(this, ViewSetUpCurrentLocation.class);
				}
				else
				    return;
				break;
			case 3: if (!Device_Four_Global.Last_Thirty.isEmpty()) {
				       my_Intent = new Intent(this, ViewSetUpCurrentLocation.class);
			        }
			        else
				         return;
				    break;
			case 4:
				    if (!Device_Five_Global.Last_Thirty.isEmpty()) {
					   my_Intent = new Intent(this, ViewSetUpCurrentLocation.class);
				    }
					else
						return;
				    break;
			case 5:
				    if (!Device_Six_Global.Last_Thirty.isEmpty()) {
					    my_Intent = new Intent(this, ViewSetUpCurrentLocation.class);
				    }
					else
						return;
				   break;
				   */

		}

		 my_Intent.putExtra(Constants.SELECTED_DEVICE_STR, position);
		startActivity(my_Intent);
	}

	@Override
	public void onButtonThreeClickListener(int position) {
		Intent setting_intent = null;
		switch(position){
			case 0:
				    setting_intent = new Intent(Global.my_context, SettingPageOne.class);
				    startActivity(setting_intent);
				    break;

			case 1: setting_intent = new Intent(Global.my_context, SettingPageTwo.class);
				    startActivity(setting_intent);
				    break;

			/* case 2: setting_intent = new Intent(Global.my_context, SettingPageThree.class);
				    startActivity(setting_intent);
				    break;

			case 3: setting_intent = new Intent(Global.my_context, SettingPageFour.class);
				    startActivity(setting_intent);
				    break;

			case 4: setting_intent = new Intent(Global.my_context, SettingPageFive.class);
				    startActivity(setting_intent);
				    break;

			case 5: setting_intent = new Intent(Global.my_context, SettingPageSix.class);
				    startActivity(setting_intent);
				    break;
				    */
		}

	}
	@Override
	public void onButtonFourClickListener(int position) {
		final int mPos = position;
		final String deviceName = Global.nameAccounts.get( mPos);
		final String deviceNumber = Global.phoneAccounts.get( mPos);
		if (Global.presetDistance[position] > 0.0) {
			Global.presetDistance[position] = 0.0;
			cancelSecuredDistance(deviceName, mPos, deviceNumber);
		}
	}

	@Override
	public void onButtonFiveClickListener(int position) {
		final int mPos = position;

		sendDisableShutdown(mPos);
		//final String deviceName = Global.nameAccounts.get( mPos);
		//final String deviceNumber = Global.phoneAccounts.get( mPos);
		//deleteAlerting(deviceName,mPos, deviceNumber);
	}

	private BroadcastReceiver mDeviceAlarmReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Get extra data included in the Intent
			String Broadcast_Command = intent.getAction();
			if (Broadcast_Command.contentEquals(Device_One_Constants.DEVICE_ONE_STOP_ALARM)){
				mMediaPlayer.stop();
				mMediaPlayer.release();
				Global.alarming_tracker[0] = 0;
			}
			else if (Broadcast_Command.contentEquals(Device_One_Constants.DEVICE_TWO_STOP_ALARM)){
				mMediaPlayer.stop();
				mMediaPlayer.release();
				Global.alarming_tracker[1] = 0;
			}
			else if (Broadcast_Command.contentEquals(Device_One_Constants.DEVICE_THREE_STOP_ALARM)){
				mMediaPlayer.stop();
				mMediaPlayer.release();
				Global.alarming_tracker[2] = 0;
			}
			else if (Broadcast_Command.contentEquals(Device_One_Constants.DEVICE_FOUR_STOP_ALARM)){
				mMediaPlayer.stop();
				mMediaPlayer.release();
				Global.alarming_tracker[3] = 0;
			}
			else if (Broadcast_Command.contentEquals(Device_One_Constants.DEVICE_FIVE_STOP_ALARM)){
				mMediaPlayer.stop();
				mMediaPlayer.release();
				Global.alarming_tracker[4] = 0;
			}
			else if (Broadcast_Command.contentEquals(Device_One_Constants.DEVICE_SIX_STOP_ALARM)){
				mMediaPlayer.stop();
				mMediaPlayer.release();
				Global.alarming_tracker[5] = 0;
			}
			else
			if (Broadcast_Command.contentEquals(Constants.SET_ALARM_ACTION)){
				Utilities.putToast("Alarming ...", 0, 200);
				int device_number = intent.getIntExtra(Constants.SEND_DEVICE_NUMBER, -1);
				if (device_number == -1){
					return;
				}
				try {
					Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
					mMediaPlayer = new MediaPlayer();
					mMediaPlayer.setDataSource(context, alert);
					final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
					if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
						mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
						mMediaPlayer.setLooping(true);
						mMediaPlayer.prepare();
						mMediaPlayer.start();
						Global.alarming_tracker[device_number] = 1;
					}
				}
				catch(Exception e) {
				}
				alerting(device_number);

			}
		}
	};

	private BroadcastReceiver mReceiveRemoveRequest = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Get extra data included in the Intent
			String Broadcast_Command = intent.getAction();
			if (Broadcast_Command.contentEquals(Constants.DEVICE_REMOVE_REQUEST)) {
				String m_deviceName = intent.getStringExtra(Constants.SENDER_NAME);
				String m_devicePhone = intent.getStringExtra(Constants.SEND_DEVICE_NUMBER);
				acceptOrCancel_Request(m_deviceName, m_devicePhone);
			}
		}
	};

	private void alerting(final int alertedDevice){
		AlertDialog.Builder alertbox_2 = new AlertDialog.Builder(mContext);
		String deviceName = "Tracking for " + Global.nameAccounts.get(alertedDevice) ;
		alertbox_2.setTitle(deviceName);
		alertbox_2.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0,int arg1) {
						mMediaPlayer.stop();
						mMediaPlayer.release();
						// Start real time location tracking: We are chasing the beauty who stole my BMW ...
						Intent cl_intent = null;
						switch(alertedDevice){
							case 0: cl_intent = new Intent(mContext, RetrieveClientLocationOne.class);
								Global.alarming_tracker[alertedDevice] = 0;
						}
						startActivity(cl_intent);
						//cl_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						finish();
					}
				});

		alertbox_2.setNegativeButton("SKIP",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0,int arg1) {
						mMediaPlayer.stop();
						mMediaPlayer.release();
						Global.alarming_tracker[alertedDevice] = 0;
						return;
					}
				});
		Global.alarming = false;
		alertbox_2.show();
	}

	private void deleteAlerting(final String deviceName, final int position, final String devicePhone){
		AlertDialog.Builder alertbox_2 = new AlertDialog.Builder(mContext);
		String m_deviceName = "Remove " + deviceName + " from the base" ;

		alertbox_2.setTitle(m_deviceName);
		alertbox_2.setPositiveButton("PROCESS",
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

	private void cancelSecuredDistance(final String deviceName, final int position, final String devicePhone){
		AlertDialog.Builder alertbox_2 = new AlertDialog.Builder(mContext);
		String m_deviceName = "Remove secured distance for " + deviceName;

		alertbox_2.setTitle(m_deviceName);
		alertbox_2.setNegativeButton("PROCESS",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0,int arg1) {
						TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
						String mPhoneNumber = tMgr.getLine1Number();
						final String deviceName = Global.nameAccounts.get(position);
						final String deviceNumber = Global.phoneAccounts.get(position);
						String distancePassedPhase = Constants.SECURE_DISTANCE_CANCEL + Constants.POUND + deviceNumber
									+ Constants.POUND + mPhoneNumber + Constants.POUND + deviceName;
						Log.i("TAG", "T : " + distancePassedPhase);
						Utilities.sendText(distancePassedPhase, SMSBaseReceiver.msg_from);
						}
					//}
				});

		alertbox_2.setPositiveButton("CANCEL",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0,int arg1) {
						return;
					}
				});
		alertbox_2.show();
	}


	private void start_CheckDeviceConnectionService(){
		PendingIntent periodicIntentSender = PendingIntent.getService(
				this, 0, new Intent(this, CheckDeviceConnectionService.class), 0);
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 0, INTERVAL, periodicIntentSender);
	}

	public void displayLocationOnMap(int pos){
		String selectLocData = "";
		String selectAddressData = "";
		switch(pos) {
			case 0:
				selectLocData = Device_One_Global.Last_Thirty.get(0);
				selectAddressData = Device_One_Global.Last_Thirty_RealAddress.get(0);
				break;
			case 1:
				selectLocData = Device_Two_Global.Last_Thirty.get(0);
				selectAddressData = Device_Two_Global.Last_Thirty_RealAddress.get(0);
				break;
			/* case 2:
				selectLocData = Device_Three_Global.Last_Thirty.get(0);
				selectAddressData = Device_Three_Global.Last_Thirty_RealAddress.get(0);
				break;
			case 3:
				selectLocData = Device_Four_Global.Last_Thirty.get(0);
				selectAddressData = Device_Four_Global.Last_Thirty_RealAddress.get(0);
				break;
			case 4:
				selectLocData = Device_Five_Global.Last_Thirty.get(0);
				selectAddressData = Device_Five_Global.Last_Thirty_RealAddress.get(0);
				break;
			case 5:
				selectLocData = Device_Six_Global.Last_Thirty.get(0);
				selectAddressData = Device_Six_Global.Last_Thirty_RealAddress.get(0);
				break;
				*/
		}
		Log.i("--- LAST THIRTY  :  ", selectLocData );

		String [] splitedFields = selectLocData.split(Constants.AT);

		Log.i("---REAL ADDRESS  :  ", selectAddressData );
		String [] splitedAddress = selectAddressData.split("\n");

		String m_longitude = splitedFields[0];
		String m_address = splitedAddress[1];
		String m_latitude = splitedFields[1];

		m_address.replace(" ", "+");
		Uri gmmIntentUri = Uri.parse("geo:"+ m_longitude + "," + m_latitude + "?q=" + m_address);
		Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
		mapIntent.setPackage("com.google.android.apps.maps");
		startActivity(mapIntent);

	}
	boolean hasPermissions(Context context, String[] permissions) {
		PackageManager pm = getPackageManager();
		for (String permission : permissions) {
			if (pm.checkPermission (permission,getPackageName() ) != PackageManager.PERMISSION_GRANTED) {
				return false;
			}
		}
		return true;
	}



	//property for the menu item.
	static final private int CLOSE_APP = Menu.FIRST;
	static final private int HIDING_APP = 2;
	static final private int UNINSTALL_APP = 3;
    static final private int KICKOFF_CONNECTION = 4;
	static final private int SEE_ALL_DEVICES = 5;
	MenuItem itemStartSC = null;
	MenuItem itemStartAC = null;

	MenuItem itemCheckVersion = null;
	SubMenu sub = null;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (Utilities.checkForJellyBean()) {
			itemStartSC = menu.add(0, CLOSE_APP, Menu.NONE, "Close App");
			itemStartSC.setEnabled(true);
			itemStartAC = menu.add(1, HIDING_APP, Menu.NONE, "Hiding App");
			itemCheckVersion = menu.add(2, UNINSTALL_APP , Menu.NONE, " Uninstall App");
            itemCheckVersion = menu.add(3, KICKOFF_CONNECTION , Menu.NONE, " Register A Device");
			itemCheckVersion = menu.add(4, SEE_ALL_DEVICES , Menu.NONE, " See All Device Locations");
			return super.onCreateOptionsMenu(menu);
		}
		else {


			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.source_menu, menu);
			return true;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (Utilities.checkForJellyBean()) {
			switch(item.getItemId()){
				case (CLOSE_APP):
					ComponentName component=new ComponentName(getApplicationContext(), SMSBaseReceiver.class);
					getPackageManager()
							.setComponentEnabledSetting(component,
									PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
									PackageManager.DONT_KILL_APP);
					mode = -1;
					finish();

				case (HIDING_APP):
					break;

				case (UNINSTALL_APP):

					break;
                case (KICKOFF_CONNECTION):
					Intent m_intent = new Intent(this, KickOffClientConnection.class);
					startActivityForResult(m_intent,101);
                    break;

				case (SEE_ALL_DEVICES):
					Intent m_seeAll = new Intent(this, Display_All.class);
					startActivity(m_seeAll);
					break;
			}
		}
		else {
			switch (item.getItemId()) {
				case R.id.CloseApp:
					ComponentName component=new ComponentName(getApplicationContext(), SMSBaseReceiver.class);
					getPackageManager()
							.setComponentEnabledSetting(component,
									PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
									PackageManager.DONT_KILL_APP);
							mode = -1;
					finish();

				case R.id.HidingApp:

					break;
				case R.id.UninstallApp:
					break;

                case R.id.KickupClient :
					Intent m_intent = new Intent(this, KickOffClientConnection.class);
					startActivityForResult(m_intent,101);
                    break;

				case R.id.DisplayAll :
					Intent m_seeAll = new Intent(this, Display_All.class);
					startActivity(m_seeAll);
					break;
			}
		}
		return super.onOptionsItemSelected(item);
	}

	private void acceptOrCancel_Request(final String deviceName, final String devicePhone){
		AlertDialog.Builder alertbox_2 = new AlertDialog.Builder(mContext);
		String m_deviceName = "Client " + deviceName + " request to be removed from the base" ;

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

	@Override
	public void onDestroy() {

		super.onDestroy();
		if (mode == -1) {
			System.exit(1);
		}
	}

	private BroadcastReceiver mClosedReceiverOne = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Get extra data included in the Intent
			String Broadcast_Command = intent.getAction();
			if (Broadcast_Command.contentEquals(Constants.CLIENT_ONE_CLOSED_ACTION)){
				deleteFile(Device_One_Constants.ClientOne_last_30_addresses);
				deleteFile(Device_One_Constants.ClientOne_last_30_ll);
				//Utilities.putToast(deviceName  + " closed its connection.", 0, 200);
				//finish();

			}
		}
	};
	public void sendDisableShutdown(int position) {
		Intent setting_intent = null;
		String disableShutdown_Str = "";
		switch(position){
			case 0:
				Utilities.putToast( "Sent to :  " +  Global.phoneAccounts.get(0), 50, 100);
				disableShutdown_Str  = Constants.PREVENT_SHUTDOWN_REQUEST + Constants.POUND + Global.nameAccounts.get(0) +  Constants.POUND +
						"OMITTED"   + Constants.POUND + "  " + Constants.POUND + "OMITTED";
				Utilities.sendText(disableShutdown_Str, Global.phoneAccounts.get(0));
				break;

			case 1:
				Utilities.putToast( "Sent to :  " +  Global.phoneAccounts.get(1), 50, 100);
				disableShutdown_Str  = Constants.PREVENT_SHUTDOWN_REQUEST + Constants.POUND + Global.nameAccounts.get(1) +  Constants.POUND +
						"OMITTED"   + Constants.POUND + "  " + Constants.POUND + "OMITTED";
				Utilities.sendText(disableShutdown_Str, Global.phoneAccounts.get(1));
				break;

			/* case 2: setting_intent = new Intent(Global.my_context, SettingPageThree.class);
				    startActivity(setting_intent);
				    break;

			case 3: setting_intent = new Intent(Global.my_context, SettingPageFour.class);
				    startActivity(setting_intent);
				    break;

			case 4: setting_intent = new Intent(Global.my_context, SettingPageFive.class);
				    startActivity(setting_intent);
				    break;

			case 5: setting_intent = new Intent(Global.my_context, SettingPageSix.class);
				    startActivity(setting_intent);
				    break;
				    */
		}

	}
}
		