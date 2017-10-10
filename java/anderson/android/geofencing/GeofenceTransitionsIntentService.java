package anderson.android.geofencing;

import android.app.IntentService;
import android.content.Intent;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

import anderson.android.utilities.Constants_Client;
import anderson.android.utilities.Global_Client;
import anderson.android.utilities.Utilities_Client;

/**
 * Created by Anderson Mai on 8/16/2017.
 * Receiver to use with GeoFence
 */

public class GeofenceTransitionsIntentService extends IntentService {

    protected static final String TAG = "GeofenceTransitionsIS";

    public GeofenceTransitionsIntentService() {
        super(TAG);  // use TAG to name the IntentService worker thread
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(TAG, "GeoFence turn...");
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = " Errors";
            Log.e(TAG, errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ){

        }
        else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT){
            String distancePassedPhase = Constants_Client.SECURE_DISTANCE_PASSED + Constants_Client.POUND + Global_Client.phoneAccounts[0]
                    + Constants_Client.POUND + Global_Client.phoneAccounts[1] + Constants_Client.POUND + Global_Client.phoneAccounts[2];
            SmsManager.getDefault().sendTextMessage(Global_Client.phoneAccounts[1], null, distancePassedPhase, null, null);
            Utilities_Client.displayAToast(" --- Secured distance passed ...", this);
            Global_Client.setSafeDistance = false;

            Log.e(TAG, " --- GeoFence distance passed ... ");
        }
        else {
            // Log the error.
            Log.e(TAG, "Invalid Transition type ...");
        }
    }
    private static String getGeofenceTransitionDetails(GeofencingEvent event) {
        String transitionString =
                GeofenceStatusCodes.getStatusCodeString(event.getGeofenceTransition());
        List triggeringIDs = new ArrayList();
        for (Geofence geofence : event.getTriggeringGeofences()) {
            triggeringIDs.add(geofence.getRequestId());
        }
        return String.format("%s: %s", transitionString, TextUtils.join(", ", triggeringIDs));
    }



}
