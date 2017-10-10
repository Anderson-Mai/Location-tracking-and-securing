package anderson.android.trackingbase;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import anderson.android.device_one.Device_One_Global;
import anderson.android.device_two.Device_Two_Global;

public class ViewSetUpCurrentLocation extends Activity{
	final String TAG = "CheckCurrentLocation";

    private String navigate_method = "NOT_READY";
    private TextView mViewTextView = null;
    private Button mBikingButton = null;
    private Button mDrivingButton = null;
	private Button mWalkingButton = null;
	private String selectLocData = "";
	private int passed_selected_device = -1;
	private String m_selectLocData = "";
	private String  m_selectAddressData = "";
    private TextView mClientName = null;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_view_setup);


		Intent g_Intent = getIntent();
		passed_selected_device = g_Intent.getIntExtra(Constants.SELECTED_DEVICE_STR, -1);
		if (passed_selected_device == -1){
			Utilities.putToast("System error. Please retry", 0, 200);
			return;
		}
        mClientName = (TextView)findViewById(R.id.ClientName);
		switch(passed_selected_device) {
			case 0:
				m_selectLocData = Device_One_Global.Last_Thirty.get(0);
				m_selectAddressData = Device_One_Global.Last_Thirty_RealAddress.get(0);
                mClientName.setText(Global.nameAccounts.get(0));
                String firstLetter = Global.nameAccounts.get(0).substring(0,1);
                Log.i("---COLOR", firstLetter );
                String colorCode = Utilities.getColorForLetter(firstLetter);
                mClientName.setBackgroundColor(Color.parseColor(colorCode));
				break;
			case 1:
				m_selectLocData = Device_Two_Global.Last_Thirty.get(0);
				m_selectAddressData = Device_Two_Global.Last_Thirty_RealAddress.get(0);
                mClientName.setText(Global.nameAccounts.get(1));
                firstLetter = Global.nameAccounts.get(1).substring(0,1);
                Log.i("---COLOR", firstLetter );
                colorCode = Utilities.getColorForLetter(firstLetter);
                mClientName.setBackgroundColor(Color.parseColor(colorCode));
				break;
			/* case 2:
				m_selectLocData = Device_Three_Global.Last_Thirty.get(0);
				m_selectAddressData = Device_Three_Global.Last_Thirty_RealAddress.get(0);
                mClientName.setText(Global.nameAccounts.get(2));
                firstLetter = Global.nameAccounts.get(2).substring(0,1);
                Log.i("---COLOR", firstLetter );
                colorCode = Utilities.getColorForLetter(firstLetter);
                mClientName.setBackgroundColor(Color.parseColor(colorCode));
				break;
			case 3:
				m_selectLocData = Device_Four_Global.Last_Thirty.get(0);
				m_selectAddressData = Device_Four_Global.Last_Thirty_RealAddress.get(0);
				break;
			case 4:
				m_selectLocData = Device_Five_Global.Last_Thirty.get(0);
				m_selectAddressData = Device_Five_Global.Last_Thirty_RealAddress.get(0);
				break;
			case 5:
				m_selectLocData = Device_Six_Global.Last_Thirty.get(0);
				m_selectAddressData = Device_Six_Global.Last_Thirty_RealAddress.get(0);
				break;
				*/
		}

        mViewTextView = (TextView)findViewById(R.id.ClientCurrentLocation);
		mViewTextView.setText(m_selectAddressData);

		mViewTextView.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				displayLocationOnMap(m_selectLocData, m_selectAddressData, "NONE" );
			}
		});

		mBikingButton = (Button) findViewById(R.id.Biking_Button);
		mBikingButton.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				displayLocationOnMap(m_selectLocData, m_selectAddressData, "&mode=b" );
			}
		});

		mDrivingButton = (Button) findViewById(R.id.Driving_Button);
		mDrivingButton.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				displayLocationOnMap(m_selectLocData, m_selectAddressData, "&mode=d" );
			}
		});

		mWalkingButton = (Button) findViewById(R.id.Walking_Button);
		mWalkingButton.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				displayLocationOnMap(m_selectLocData, m_selectAddressData, "&mode=w" );
			}
		});
   }

	    @Override
	     protected void onDestroy() {
	    	 // Unregister since the activity is about to be closed.
	  		  super.onDestroy();
	      }

	public void displayLocationOnMap(final String ll_location, final String realAddress, String mode){
		String selectLocData = ll_location;
		String selectAddressData = realAddress;

		Log.i("--- LAST THIRTY  :  ", selectLocData );

		String [] splitedFields = selectLocData.split(Constants.AT);

		Log.i("---REAL ADDRESS  :  ", selectAddressData );
		String [] splitedAddress = selectAddressData.split("\n");

		String m_longitude = splitedFields[1];
		String m_address = splitedAddress[1];
		String m_latitude = splitedFields[0];

		m_address.replace(" ", "+");
		Uri gmmIntentUri = null;
		if (mode.contentEquals("NONE")){
			gmmIntentUri = Uri.parse("google.navigation:q=a+" + m_address);
		}
		else{
			gmmIntentUri = Uri.parse("google.navigation:q=a+" + m_address + mode);
		}
		Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
		mapIntent.setPackage("com.google.android.apps.maps");
		if (mapIntent.resolveActivity(getPackageManager()) != null) {
			startActivity(mapIntent);
		}

	}

	private BroadcastReceiver doReceiveNewAddress = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Constants.ACTION_NEW_ADDRESS)) {
				Utilities.putToast("Get ACTION_NEW_ADDRESS", 0, 200);
				int device_update = intent.getIntExtra(Constants.ACTION_NEW_ADDRESS, -1);
				if (device_update == -1){
					return;
				}
				String selectLocData = "";
				String selectAddressData = "";

				switch(device_update) {
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

				m_selectLocData = selectLocData;
				m_selectAddressData =  selectAddressData;

				mViewTextView.setText(m_selectAddressData);

			}
		}
	};

	@Override
	public void onResume() {
		registerReceiver(doReceiveNewAddress,
				new IntentFilter(Constants.ACTION_NEW_ADDRESS));

		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(doReceiveNewAddress);

	}
}
