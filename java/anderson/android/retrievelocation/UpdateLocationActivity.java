package anderson.android.retrievelocation;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import anderson.android.geofencing.GeoFenceActivity;
import anderson.android.utilities.Constants;
import anderson.android.utilities.Constants_Client;
import anderson.android.utilities.Global_Client;
import anderson.android.utilities.Utilities_Client;


/**
 * Using location settings.
 * <p/>
 * Uses the {@link com.google.android.gms.location.SettingsApi} to ensure that the device's system
 * settings are properly configured for the app's location needs. When making a request to
 * Location services, the device's system settings may be in a state that prevents the app from
 * obtaining the location data that it needs. For example, GPS or Wi-Fi scanning may be switched
 * off. The {@code SettingsApi} makes it possible to determine if a device's system settings are
 * adequate for the location request, and to optionally invoke a dialog that allows the user to
 * enable the necessary settings.
 * <p/>
 * This sample allows the user to request location updates using the ACCESS_FINE_LOCATION setting
 * (as specified in AndroidManifest.xml). The sample requires that the device has location enabled
 * and set to the "High accuracy" mode. If location is not enabled, or if the location mode does
 * not permit high accuracy determination of location, the activity uses the {@code SettingsApi}
 * to invoke a dialog without requiring the developer to understand which settings are needed for
 * different Location requirements.
 */

/***
 * Anderson Mai. 07/2017
 * Update the client location and text it to the Location base
 */
public class UpdateLocationActivity extends Activity implements
        ConnectionCallbacks,
        OnConnectionFailedListener,
        LocationListener {

    protected static final String TAG = "UpdateLocationActivity";

    /**
     * Constant used in the location settings dialog.
     */
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Keys for storing activity state in the Bundle.
    protected final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    protected final static String KEY_LOCATION = "location";
    protected final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string";

    /**
     * Provides the entry point to Google Play services.
     */
    //protected GoogleApiClient mGoogleApiClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;

    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    protected LocationSettingsRequest mLocationSettingsRequest;

    /**
     * Represents a geographical location.
     */
    protected Location mCurrentLocation;

    // UI Widgets.
    protected TextView mLastUpdateTimeTextView;
    protected TextView mLatitudeTextView;
    protected TextView mLongitudeTextView;
    protected TextView mLocationInadequateWarning;

    // Labels.
    protected String mLatitudeLabel;
    protected String mLongitudeLabel;
    protected String mLastUpdateTimeLabel;

    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    protected Boolean mRequestingLocationUpdates;

    /**
     * Time when the location was updated represented as a String.
     */
    protected String mLastUpdateTime;
    private int same_address = 0;
    private String old_address = "ZZZZZ";
    private Context context = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        context = this;
        // mRequestingLocationUpdates = false;
        mRequestingLocationUpdates = true;
        mLastUpdateTime = "";

        registerReceiver(doStopLocationUpdate,
                new IntentFilter(Constants.STOP_LOCATION_UPDATE_ACTION));
        registerReceiver( doStartGeoFence,
                new IntentFilter(Constants.START_GEOFENCE_ACTION));

        // Kick off the process of building the GoogleApiClient, LocationRequest, and
        // LocationSettingsRequest objects.
        buildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();

        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(Global_Client.mGoogleApiClient);
            if (mCurrentLocation != null) {
                double m_latitude = mCurrentLocation.getLatitude();
                double m_longitude = mCurrentLocation.getLongitude();
                String last_address = Utilities_Client.getAddressGivenLongAndLate(m_latitude, m_longitude);
                String timeStamp = new SimpleDateFormat("EEE MMM dd yyyy  HH:mm:ss").format(Calendar.getInstance().getTime());
                String locationStr = String.valueOf(m_latitude) + "@" + String.valueOf(m_longitude);

                String resultStr = timeStamp + "\n";
                try {
                    Utilities_Client.get_AccountInfor(Global_Client.phoneAccounts);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                new SendingXYPos_Client().execute(resultStr, locationStr, Global_Client.phoneAccounts[1], Global_Client.phoneAccounts[2], null);
            }
        }
        Log.i(TAG, "in onConnected(), starting location updates");
        startLocationUpdates();
    }

    /**
     * Updates fields based on data stored in the bundle.
     *
     * @param savedInstanceState The activity state saved in the Bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(KEY_REQUESTING_LOCATION_UPDATES)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        KEY_REQUESTING_LOCATION_UPDATES);
            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(KEY_LOCATION)) {
                // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(KEY_LAST_UPDATED_TIME_STRING)) {
                mLastUpdateTime = savedInstanceState.getString(KEY_LAST_UPDATED_TIME_STRING);
            }
            updateUI();
        }
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        Global_Client.mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        Global_Client.mLocationRequest = mLocationRequest;
        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
       // mLocationRequest.setSmallestDisplacement(10);
    }

    /**
     * Uses a {@link com.google.android.gms.location.LocationSettingsRequest.Builder} to build
     * a {@link com.google.android.gms.location.LocationSettingsRequest} that is used for checking
     * if a device has the needed location settings.
     */
    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    /**
     * The callback invoked when
     * {@link com.google.android.gms.location.SettingsApi#checkLocationSettings(GoogleApiClient,
     * LocationSettingsRequest)} is called. Examines the
     * {@link com.google.android.gms.location.LocationSettingsResult} object and determines if
     * location settings are adequate. If they are not, begins the process of presenting a location
     * settings dialog to the user.
     */


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");
                        // Nothing to do. startLocationupdates() gets called in onResume again.
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "User chose not to make required location settings changes.");
                        mRequestingLocationUpdates = false;
                        updateUI();
                        break;
                }
                break;
        }
    }

    /**
     * Handles the Stop Updates button, and requests removal of location updates.
     */
    public void stopUpdatesButtonHandler(View view) {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        stopLocationUpdates();
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {

        LocationServices.SettingsApi.checkLocationSettings(
                Global_Client.mGoogleApiClient,
                mLocationSettingsRequest
        ).setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");
                        LocationServices.FusedLocationApi.requestLocationUpdates(
                        Global_Client.mGoogleApiClient, mLocationRequest, UpdateLocationActivity.this);

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                       // Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                        //        "location settings ");
                        //try {
                            // Show the dialog by calling startResolutionForResult(), and check the
                            // result in onActivityResult().
                         //   status.startResolutionForResult(UpdateLocationActivity.this, REQUEST_CHECK_SETTINGS);
                      //  } catch (IntentSender.SendIntentException e) {
                      //      Log.i(TAG, "PendingIntent unable to execute request.");
                     //   }
                        Intent m_intent = new Intent(context, RetrieveLocationActivity.class);
                        m_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(m_intent);

                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        String errorMessage = "Location settings are inadequate, and cannot be " +
                                "fixed here. Fix in Settings.";
                        Log.e(TAG, errorMessage);
                       // Toast.makeText(UpdateLocationActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                      //  mRequestingLocationUpdates = false;
                        Intent m_intent_two= new Intent(context, RetrieveLocationActivity.class);
                        m_intent_two.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(m_intent_two);
                        finish();

                   // default :    updateUI();
                }
                updateUI();
            }
        });

    }

    /**
     * Updates all UI fields.
     */
    private void updateUI() {
        updateLocationUI();
    }


    /**
     * Sets the value of the UI fields for the location latitude, longitude and last update time.
     */
    private void updateLocationUI() {

        if (mCurrentLocation != null) {
            double baseLatitude = mCurrentLocation.getLatitude();
            double baseLongitude = mCurrentLocation.getLongitude();
           // GetRealAddress getAddress =  new GetRealAddress();
          //  getAddress.execute(baseLatitude, baseLongitude);
            String late_long_Address = "";
            late_long_Address = processThenSend(mCurrentLocation);
            Utilities_Client.displayAToast(late_long_Address, Global_Client.my_context);

            /*String realAddress = Utilities_Client.getAddressGivenLongAndLate(baseLatitude, baseLongitude);
            String late_long_Address = "";
            late_long_Address = processThenSend(mCurrentLocation, realAddress) + "\n" + realAddress;
            Utilities_Client.displayAToast(late_long_Address, Global_Client.my_context);
            */
        }
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    public  void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        LocationServices.FusedLocationApi.removeLocationUpdates(
                Global_Client.mGoogleApiClient,
                this
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                //mRequestingLocationUpdates = false;
               // setButtonsEnabledState();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!Global_Client.CLOSE_FLAG){
            Global_Client.mGoogleApiClient.connect();
        }
        else {
            finish();
        }

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
    @Override
    public void onResume() {
        super.onResume();

      //  if (Global_Client.CloseFlag){
          display_Notification(R.drawable.icon_track);
         //   Global_Client.CloseFlag = false;
     //   }
     //   else {
     //       display_Notification(R.drawable.tracking_points);
      //      Global_Client.CloseFlag = true;
     //  }


        moveTaskToBack(true);

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
    }

    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(Location location) {

        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateLocationUI();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    /**
     * Stores activity data in the Bundle.
     */
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(KEY_REQUESTING_LOCATION_UPDATES, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(KEY_LOCATION, mCurrentLocation);
        savedInstanceState.putString(KEY_LAST_UPDATED_TIME_STRING, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
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

    private BroadcastReceiver doStopLocationUpdate = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.STOP_LOCATION_UPDATE_ACTION)) {
                Log.d("Update","---Received STOP_LOCATION_UPDATE_ACTION");
                stopLocationUpdates();
                Global_Client.mGoogleApiClient = null;
                Global_Client.CLOSE_FLAG = true;

                Intent m_intent = new Intent(Constants.CLOSE_APP_ACTION);
                Log.d("Update","---Send CLOSE_APP_ACTION");
                sendBroadcast(m_intent);
                unregisterReceiver(doStopLocationUpdate);
            }

        }
    };

    private BroadcastReceiver doStartGeoFence = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.START_GEOFENCE_ACTION)) {
                Log.d("Update","---Received START_GEOFENCE_ACTION");

                Intent fenceIntent = new Intent (context, GeoFenceActivity.class);
                context.startActivity(fenceIntent);
            }

        }
    };

    private String processThenSend(Location mLocation) {

        Global_Client.my_context = getApplicationContext();
        if (Global_Client.counter == 1000) {
            Global_Client.counter = 0;
        }
        Global_Client.counter++;
        double m_latitude = mLocation.getLatitude();
        double m_longitude = mLocation.getLongitude();
        String resultStr = "";
        float mAccuracy = mLocation.getAccuracy();
        Utilities_Client.displayAToast("Accuracy: " + String.valueOf(mAccuracy), this);

        String timeStamp = new SimpleDateFormat("EEE MMM dd yyyy  HH:mm:ss").format(Calendar.getInstance().getTime());
        String locationStr = String.valueOf(m_latitude) + Constants_Client.AT + String.valueOf(m_longitude);
        resultStr = timeStamp + "\n";

        String t_String = "";
        if ((Global_Client.recentLat == 0.0) && (Global_Client.recentLong == 0.0)) {
            if ( ((System.currentTimeMillis() -  Global_Client.startTime)  < 60000) && (mAccuracy > 9)) {
                return "Warming period";
            }
            else {
                if (Global_Client.counter_two++ < 10) {
                    if ((mAccuracy > 10) || Global_Client.phoneAccounts[1].isEmpty()) {
                        return " Not accuracy ";
                    }
                    else if ((mAccuracy <= 10) && (!Global_Client.phoneAccounts[1].isEmpty())) {
                        t_String = "X,Y " + String.valueOf(m_latitude) + ", " + String.valueOf(m_longitude) + "\n";
                        //  Utilities_Client.appendTextToFile(Global_Client.EXTERNAL_STORAGE_FILE_PATH, t_String);
                        Global_Client.recentLat = m_latitude;
                        Global_Client.recentLong = m_longitude;
                        new SendingXYPos_Client().execute(resultStr, locationStr, Global_Client.phoneAccounts[1], Global_Client.phoneAccounts[2], "NO");
                        Log.e(TAG, "--- Sent first location ---");
                        return m_latitude + "," + m_longitude;
                    }
                }
                else {
                    if ((mAccuracy < 15) || Global_Client.phoneAccounts[1].isEmpty()) {
                        t_String = "X,Y " + String.valueOf(m_latitude) + ", " + String.valueOf(m_longitude) + "\n";
                        //  Utilities_Client.appendTextToFile(Global_Client.EXTERNAL_STORAGE_FILE_PATH, t_String);
                        Global_Client.recentLat = m_latitude;
                        Global_Client.recentLong = m_longitude;
                        new SendingXYPos_Client().execute(resultStr, locationStr, Global_Client.phoneAccounts[1], Global_Client.phoneAccounts[2], "NO");
                        Log.e(TAG, "--- Sent first location ---");
                        return m_latitude + "," + m_longitude;
                    }
                }
            }
        }
        else {
            if (mAccuracy > 15) {
                return " Not accuracy ";
            }
            if (Global_Client.setSafeDistance) {
                if (Utilities_Client.checkForNewLoc_2(m_latitude, m_longitude,
                        Global_Client.recentLat, Global_Client.recentLong)) {
                    new SendingXYPos_Client().execute(resultStr, locationStr, Global_Client.phoneAccounts[1], Global_Client.phoneAccounts[2], Constants_Client.SET_SAFE_DISTANCE);
                    Global_Client.recentLat = m_latitude;
                    Global_Client.recentLong = m_longitude;
                    return m_latitude + "," + m_longitude;
                }
            } else {
                if (mAccuracy < 10) {
                    same_address = 0;
                    new SendingXYPos_Client().execute(resultStr, locationStr, Global_Client.phoneAccounts[1], Global_Client.phoneAccounts[2], "NO");
                    Global_Client.recentLat = m_latitude;
                    Global_Client.recentLong = m_longitude;
                    return m_latitude + "," + m_longitude;
                }
                else {
                    if ((Global_Client.counter % 3) == 0) {
                        if (Utilities_Client.checkForNewLoc(m_latitude, m_longitude, Global_Client.recentLat, Global_Client.recentLong)) {
                            new SendingXYPos_Client().execute(resultStr, locationStr, Global_Client.phoneAccounts[1], Global_Client.phoneAccounts[2], "NO");
                            same_address = 0;
                            Global_Client.recentLat = m_latitude;
                            Global_Client.recentLong = m_longitude;
                            return m_latitude + "," + m_longitude;

                        } else {
                            if (++same_address < 5) {
                                return "Same address ...";
                            } else {
                                same_address = 0;
                                new SendingXYPos_Client().execute(resultStr, locationStr, Global_Client.phoneAccounts[1], Global_Client.phoneAccounts[2], "NO");
                                Global_Client.recentLat = m_latitude;
                                Global_Client.recentLong = m_longitude;
                                return m_latitude + "," + m_longitude;
                            }
                        }
                    }
                }
            }

        }
        return m_latitude + "," + m_longitude;
    }

    class GetRealAddress extends AsyncTask<Double , Void, String> {
        //  StockTickerDAOONE myDOA = new StockTickerDAOONE()

        @Override
        protected String doInBackground(Double... params) {

            String my_address = null;

            my_address = Utilities_Client.getAddressGivenLongAndLate( params[0], params[1]);
            return my_address;
        }

        @Override
        protected void onPostExecute(String realAddress) {

            String late_long_Address = processThenSend(mCurrentLocation)+"\n" + realAddress;
            Utilities_Client.displayAToast(late_long_Address, Global_Client.my_context);
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    public void changeRingerMode(Context context){

        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        /**
         * To Enable silent mode.....
         */
        audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);

        /**
         * To Enable Ringer mode.....
         */
        audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

    }


}