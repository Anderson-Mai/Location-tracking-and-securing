package anderson.android.utilities;
//import android.app.Activity;

import android.app.NotificationManager;
import android.content.Context;
import android.location.LocationManager;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;

public class Global_Client {
    public static String[] phoneAccounts = null;
    public static String[] secured_Distance_Log = new String[4] ;
    public static double[] first_Five_Distance = new double[4] ;
    public static Context my_context;
    public static int sequence = 0;
    public static boolean waiting = false;
    public static float acceptedAccuracy = 20;
    public static boolean QUICK_RETRIEVE = true;
    public static int unaccuracy_count = 0;
    public static NotificationManager NotifyManager = null;
    public static int currentapiVersion = android.os.Build.VERSION.SDK_INT;
    public static LocationManager g_Loc_Manager = null;
    public static int cursorId = -1;
    public final static int Notify_ID = 100;
    public static int icon = -1;
    public volatile static String deviceName = "";
    public volatile static String confirmPhase = "";
    public volatile static double pos_Longitude = 0.0;
    public volatile static double pos_Latitude = 0.0;
    public volatile static Boolean setSafeDistance = false;
    public volatile static double secured_distance = 0;
    public volatile static Boolean CloseFlag = false;
    public volatile static Boolean setSafeDistancePosition = false;

    public volatile static GoogleApiClient mGoogleApiClient = null;
    public volatile static LocationRequest mLocationRequest  = null;
    public volatile static Boolean CLOSE_FLAG = false;
    public volatile static double recentLat = 0.0;
    public volatile static double recentLong = 0.0;
    public volatile static double lastDistance = 0.0;
    public volatile static double different_distance = 9999.0;
    public volatile static double recent_distance = 00;
    public volatile static int counter = 0;
    public volatile static int counter_two = 0;
    public volatile static String EXTERNAL_STORAGE_FILE_PATH = "";
    public volatile static long startTime = 0L;
}

	



