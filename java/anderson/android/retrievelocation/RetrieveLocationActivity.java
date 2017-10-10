package anderson.android.retrievelocation;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import anderson.android.utilities.Constants_Client;
import anderson.android.utilities.Global_Client;
import anderson.android.utilities.Utilities_Client;

/***
 * Anderson Mai. 09/08/2015
 * Retrieve location and text it to the Location base
 */

public class RetrieveLocationActivity extends Activity {

	protected LocationManager locationManager;
	protected String mlocProvider = null;
	protected Context context;
	//static double latitude;
	//static double longitude;
	private Button m_starter;
	private Button m_mainpage;
	private TextView m_displayer;
	static int count = 0;
	public static MyLocationListener locationL;
	static Geocoder gc = null;
	static Geocoder fwd_gc = null;
	List<Address> addresses = null;
	private final static int Notify_ID = 110;
	int counter = 0;
	double recent_latitude = 1111;
	double recent_longitude = 1111;
	PendingIntent contentIntent = null;
	String old_address = "no_address";
	private int same_address = 0;
	private double adjusted_distance = 0;
	private static boolean first_start = true;

	private final String TAG = "RetrieveLocationAct";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.location_in_progress_client);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		// start_notify(R.drawable.bluecursor);

		//gc = new Geocoder(this, Locale.getDefault());
		//fwd_gc = new Geocoder(this, Locale.US);

		// m_starter = (Button)findViewById(R.id.Starter);
	/*	m_mainpage = (Button) findViewById(R.id.MainPage);

		m_mainpage.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				//if (android.os.Build.VERSION.SDK_INT >= 23) {
				//	if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
				//			checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
						// TODO: Consider calling
						//    Activity#requestPermissions
						// here to request the missing permissions, and then overriding
						//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
						//                                          int[] grantResults)
						// to handle the case where the user grants the permission. See the documentation
						// for Activity#requestPermissions for more details.
				//		return;
				//	}
			//	}
				locationManager.removeUpdates(locationL);
				if ( Global_Client.NotifyManager != null){
		 	        Global_Client.NotifyManager.cancelAll();
		 		 }
		 	   Intent m_intent = new Intent(getApplicationContext(), SelectionPage_Client.class);
			   startActivity(m_intent);
		 	   finish();
		 	 }

	    });

         */
		m_displayer = (TextView) findViewById(R.id.Displayer);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		Criteria hdCrit = new Criteria();

		hdCrit.setHorizontalAccuracy(Criteria.ACCURACY_FINE);
		hdCrit.setVerticalAccuracy(Criteria.ACCURACY_FINE);
		hdCrit.setSpeedAccuracy(Criteria.ACCURACY_FINE);
        hdCrit.setAccuracy(Criteria.ACCURACY_FINE);
		hdCrit.setPowerRequirement(Criteria.POWER_LOW);
		hdCrit.setAltitudeRequired(false);
		hdCrit.setBearingRequired(false);
		hdCrit.setCostAllowed(true);

		// Can not get a hold of provider
		mlocProvider = locationManager.getBestProvider(hdCrit, true);
		if (mlocProvider == null) {
			Log.i("TAG", "Provider is null");
			return;
		} else {
			Log.v("TAG", "Provider: " + mlocProvider);
		}

		locationL = new MyLocationListener();
		locationManager.requestLocationUpdates(
				LocationManager.GPS_PROVIDER,
				Constants_Client.MINIMUM_TIME_BETWEEN_UPDATES,
				Constants_Client.MINIMUM_DISTANCE_CHANGE_FOR_UPDATES,
				RetrieveLocationActivity.locationL
		);

		Global_Client.g_Loc_Manager = locationManager;
	}

	@Override
	protected void onResume() {
		super.onResume();
		//if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN){
		//display_Notification(R.drawable.icon_track);
		//moveTaskToBack(true);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private class MyLocationListener implements LocationListener {
		class m_point {
			double latitude;
			double longitude;
			float accuracy;
		}

		public void onLocationChanged(Location location) {
			String late_long_Address = processThenSend(location);

			Utilities_Client.displayAToast("Address: " + late_long_Address, getApplicationContext());
		}

		public void onStatusChanged(String s, int i, Bundle b) {
			//Utilities.displayAToast("Provider status changed ...", RetrieveLocationActivity.this);
		}

		public void onProviderDisabled(String s) {
			Utilities_Client.displayAToast("Provider disabled by the user. GPS turned off", RetrieveLocationActivity.this);
		}

		@Override
		public void onProviderEnabled(String s) {
			Utilities_Client.displayAToast("Provider enabled by the user. GPS turned on",
					RetrieveLocationActivity.this);
		}
	}

	protected String getIPfrom3GNetwork() {

		String ipAddress = null;
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						ipAddress = inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
		}
		return ipAddress;
	}

	protected String getIPfromWifi() {
		WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
		WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
		int ip = wifiInfo.getIpAddress();
		String ipAddress = Formatter.formatIpAddress(ip);

		return "Wifi: " + ipAddress;
	}


	protected String checkAvailableConnection() {
		ConnectivityManager connMgr = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		final android.net.NetworkInfo wifi = connMgr
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		final android.net.NetworkInfo mobile = connMgr
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		if (wifi.isAvailable()) {

			return getIPfromWifi();

		} else if (mobile.isAvailable()) {

			return GetLocalIpAddress();

		} else {
			return "No network available";
		}
	}

	private String GetLocalIpAddress() {
   /* try {
        for (Enumeration<NetworkInterface> en = NetworkInterface
                .getNetworkInterfaces(); en.hasMoreElements();) {
            NetworkInterface intf = en.nextElement();
            for (Enumeration<InetAddress> enumIpAddr = intf
                    .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                InetAddress inetAddress = enumIpAddr.nextElement();
                if (!inetAddress.isLoopbackAddress()) {
                    return "3G : " + inetAddress.getHostAddress().toString();
                }
            }
        }
    } catch (SocketException ex) {
        return "ERROR Obtaining IP";
    }
    return "No IP Available";

    */
		String ipAddress = null;
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						ipAddress = "3G :" + inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
		}
		return ipAddress;
	}

	@Override
	public void onBackPressed() {
		return;
	}

	public double distanceBeweenTwoLocs(double latA, double longA, double latB, double longB) {
		Log.i("Latitude: ", String.valueOf(latB));
		Log.i("Longitude: ", String.valueOf(longB));

		Location locationA = new Location("point A");
		locationA.setLatitude(latA);
		locationA.setLongitude(longA);

		Location locationB = new Location("point B");
		locationB.setLatitude(latB);
		locationB.setLongitude(longB);

		double distance = locationA.distanceTo(locationB);

		return distance;
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


	private String processThenSend(Location mLocation) {
		Global_Client.my_context = getApplicationContext();

		double m_latitude = mLocation.getLatitude();
		double m_longitude = mLocation.getLongitude();
		String resultStr = "";
		float mAccuracy = mLocation.getAccuracy();
		Utilities_Client.displayAToast("Accuracy: " + String.valueOf(mAccuracy), this);

		String timeStamp = new SimpleDateFormat("EEE MMM dd yyyy  HH:mm:ss").format(Calendar.getInstance().getTime());
		String locationStr = String.valueOf(m_latitude) + "@" + String.valueOf(m_longitude);
		resultStr = timeStamp + "\n";

		if ((Global_Client.recentLat == 0.0) && (Global_Client.recentLong == 0.0)) {
			if (mAccuracy > 10){
				return " Not accuracy ";
			}
			Global_Client.recentLat = m_latitude;
			Global_Client.recentLong = m_longitude;
			new SendingXYPos_Client().execute(resultStr, locationStr, Global_Client.phoneAccounts[1], Global_Client.phoneAccounts[2], null);
			return m_latitude + "," + m_longitude;
		}

		double distanceAtoB = Utilities_Client.distanceA_B(m_latitude, m_longitude,
				Global_Client.recentLat,Global_Client.recentLong );
		Utilities_Client.displayAToast("Final short  distance:" + String.valueOf(distanceAtoB), Global_Client.my_context);
		Log.d("Final short distance: ",String.valueOf(distanceAtoB) );
		Log.i(TAG,"Accu: " +String.valueOf(mAccuracy));

		if (Global_Client.setSafeDistance ) {
			if (distanceAtoB < Constants_Client.MINIMUM_DISTANCE_CHANGE_FOR_UPDATES){
				if (++same_address > 6) {
					same_address = 0;
					new SendingXYPos_Client().execute(resultStr, locationStr, Global_Client.phoneAccounts[1], Global_Client.phoneAccounts[2], null);
				}
				return "Same address ...";
			}
			double distanceBeweenTwoLocs = Utilities_Client.distanceBeweenTwoLocs(m_latitude, m_longitude,
					Double.parseDouble(Global_Client.secured_Distance_Log[0]),
					Double.parseDouble(Global_Client.secured_Distance_Log[1]), mAccuracy, distanceAtoB);
			Utilities_Client.displayAToast("Final long  distance:" + String.valueOf(distanceBeweenTwoLocs), Global_Client.my_context);
			Log.d("Final long  distance: ",String.valueOf(distanceBeweenTwoLocs) );
			if (distanceBeweenTwoLocs >= Double.parseDouble(Global_Client.secured_Distance_Log[2])) {
				String distancePassedPhase = Constants_Client.SECURE_DISTANCE_PASSED + Constants_Client.POUND + Global_Client.phoneAccounts[0]
						+ Constants_Client.POUND + Global_Client.phoneAccounts[1] + Constants_Client.POUND + Global_Client.phoneAccounts[2];
				SmsManager.getDefault().sendTextMessage(Global_Client.phoneAccounts[1], null, distancePassedPhase, null, null);
				Utilities_Client.displayAToast("Secured distance passed ...", this);
				Global_Client.setSafeDistance = false;
				return "---Secured distance passed ---";
			}
			new SendingXYPos_Client().execute(resultStr, locationStr, Global_Client.phoneAccounts[1], Global_Client.phoneAccounts[2], null);

		} else {
			if (distanceAtoB > Constants_Client.MINIMUM_DISTANCE_CHANGE_FOR_UPDATES){
				new SendingXYPos_Client().execute(resultStr, locationStr, Global_Client.phoneAccounts[1], Global_Client.phoneAccounts[2], null);
				same_address = 0;
			}
			else {
				if (++same_address < 6) {
					return "Same address ...";

				}
				else {
					same_address = 0;
					new SendingXYPos_Client().execute(resultStr, locationStr, Global_Client.phoneAccounts[1], Global_Client.phoneAccounts[2],null);

				}
			}

		}
		Global_Client.recentLat = m_latitude;
		Global_Client.recentLong = m_longitude;
		return m_latitude + "," + m_longitude;
	}

}

