package anderson.android.geofencing;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

import anderson.android.utilities.Global_Client;

/**
 * Created by Anderson Mai on 8/16/2017.
 */

public class Constants {
    public static long GEOFENCE_EXPIRATION_IN_MILLISECONDS = Long.parseLong(Global_Client.secured_Distance_Log[2]);
    public static float GEOFENCE_RADIUS_IN_METERS = Float.parseFloat(Global_Client.secured_Distance_Log[3]);

    public static final HashMap<String, LatLng> LANDMARKS = new HashMap<String, LatLng>();
    static {

        // Test
        LANDMARKS.put("SFO", new LatLng(Double.parseDouble(Global_Client.secured_Distance_Log[0]),Double.parseDouble(Global_Client.secured_Distance_Log[1])));
    }
}
