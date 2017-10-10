package anderson.android.geofencing;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.UUID;

import anderson.android.utilities.*;

/**
 * Created by Anderson Mai on 8/16/2017.
 * GeoFence activity
 */

//public class GeoFenceActivity extends Activity implements
//        GoogleApiClient.ConnectionCallbacks,
 //       GoogleApiClient.OnConnectionFailedListener,
//        ResultCallback<Status>
public class GeoFenceActivity extends Activity implements ResultCallback<Status>{
    protected ArrayList<Geofence> mGeofenceList;
    private GoogleApiClient mGoogleApiClient_GeoFence;
    private static final String GEOFENCE_REQ_ID = UUID.randomUUID().toString();
    private final String TAG= "GeoFenceActivity";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            // Empty list for storing geofences.
            mGeofenceList = new ArrayList<Geofence>();

            // Kick off the request to build GoogleApiClient.
            buildGoogleApiClient();
            Geofence m_geofence = createGeofence(Float.parseFloat(Global_Client.secured_Distance_Log[0]), Double.parseDouble(Global_Client.secured_Distance_Log[1]),
                    Double.parseDouble(Global_Client.secured_Distance_Log[2]));
            mGeofenceList.add(m_geofence);
            addGeofence(mGeofenceList);


    }
    // Create a Geofence
    private Geofence createGeofence( float radius, double latitude, double longitude) {
        Log.d(TAG, "createGeofence");
        return new Geofence.Builder()
                .setRequestId(GEOFENCE_REQ_ID)
                .setCircularRegion(latitude, longitude, radius)
                .setExpirationDuration( Geofence.NEVER_EXPIRE)
                .setTransitionTypes( Geofence.GEOFENCE_TRANSITION_ENTER
                        | Geofence.GEOFENCE_TRANSITION_EXIT )
                .build();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient_GeoFence = Global_Client.mGoogleApiClient;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mGoogleApiClient_GeoFence.isConnecting() || !mGoogleApiClient_GeoFence.isConnected()) {
            mGoogleApiClient_GeoFence.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient_GeoFence.isConnecting() || mGoogleApiClient_GeoFence.isConnected()) {
            mGoogleApiClient_GeoFence.disconnect();
        }
    }

    private void addGeofence(ArrayList<Geofence>  m_geofenceList) {
        if (!mGoogleApiClient_GeoFence.isConnected()) {
            Log.i(TAG,"Google API Client not connected!");
            return;
        }

        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient_GeoFence,
                    getGeofencingRequest(m_geofenceList),
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
        }
        Log.i(TAG,"GeoFencing added");
    }

    private void removeGeofence() {

        try {
            LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClient_GeoFence,
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
        }
        Log.i(TAG,"GeoFencing removed");
    }

    private GeofencingRequest getGeofencingRequest(ArrayList <Geofence> geofenceRequest) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceRequest);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling addgeoFences()
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void onResult(Status status) {
        if (status.isSuccess()) {
            String text = Constants_Client.FENCE_CREATED_CONFIRM + Constants_Client.POUND + "OMITTED" + Constants_Client.POUND
                    + Global_Client.phoneAccounts[1] + Constants_Client.POUND + Global_Client.phoneAccounts[2];
            Utilities_Client.sendText(text, Global_Client.phoneAccounts[1]);
            Log.i(TAG,"Geofence added -passed");
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            Log.i(TAG,"Geofence failed");

        }
    }

    private BroadcastReceiver removeGeoFence = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants_Client.REMOVE_GEOFENCE_ACTION)) {
                Log.d("Update","---Received REMOVE_GEOFENCE_ACTION)");
                removeGeofence();
                finish();
            }

        }
    };

}
