package anderson.android.utilities;

import android.os.Environment;

public class Constants_Client {
    	public static final String EXTERNAL_STORAGE_FILE  = "LOG_FILE.txt";
	    public static final String EXTERNAL_STORAGE_PATH  = Environment.getExternalStorageDirectory().getPath() + "/";
	    public static final String ACTION_GMAIL_RECEIVED = "com.android.email.intent.action.EMAIL_RECEIVED";
	    public static final String SUBJECT_MASK_GPS = "GPS_LOCATION";
	    public static final String GMaiInforFile    = "GMaiBaseFile";
	    public static final String GMaiContactFile    = "GMaiContactFile";
	    public static final String NEWLINE = "\n";
	    public static final String POUND = "#";
	    public static final String SUBJECT = "SUBJECT";
	    public static final int SELECT_ACCOUNT = 2456;
	    public static final int RESULT_OK = 2;
	    public static final int RESULT_NOT_OK = -2;
	    public static String AT = "@";
	    public static long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 4; // 4  Meters
	    public static long MINIMUM_TIME_BETWEEN_UPDATES = 10000L; //10 seconds
	    public static long PREFER_TIME_BETWEEN_UPDATES = 1L; //
	    public static final int RETRIEVE_ON_BACKG = 111;
	    public static final String CONFIRM_PHASE = "CONFIRMPHASE081";
	    public static final String REGISTER_PHASE = "REGISTERPHASE081";
	    public static final String NAME_ALREADY_INUSE = "NAMEALREADYINUSE081";
	    public static final String NUMBER_ALREADY_INUSE = "NUMBERALREADYINUSE081";
	    public static final String NAMEANDPHONE_REGISTERED = "NAMEANDPHONEREGISTERED081";
	    public static final String PRESET_DISTANCE_PASSED = "PRESETDISTANCEPASSED081";
	    public static final String PRESET_DISTANCE = "PRESETDISTANCE081";
	    public  static final String FENCE_REQUEST = "FENCEREQUEST081";
    	public  static final String FENCE_CREATED_CONFIRM = "FENCECREATEDCONFIRM081";
	    public  static  final String SECURE_DISTANCE_PASSED = "SECUREDISTANCEPASSED081";
   	    public  static  final String KICKUP_REGISTER_ACTION = "KICKUP_REGISTER_ACTION081";
	    public static final String CLIENT_RESETTED = "CLIENTRESETTED081";
	    public static final String REMOVEALINE_CONFIRM = "REMOVEALINECONFIRM081";
	    public static final String REMOVEALINE = "REMOVEALINE081";
	    public static final String CLIENT_CLOSE = "CLIENTCLOSE081";
	    public static final String CLIENT_CLOSE_CONFIRM = "CLIENTCLOSECONFIRM081";
	    public static final String SECURE_DISTANCE_CANCEL = "SECUREDISTANCECANCEL081";
	    public static final String SECURED_DISTANCE_REQUEST = "SECUREDDISTANCEREQUEST081";
	    public static final String SET_SAFE_DISTANCE = "SETSAFEDISTANCE081";
	    public static final String NO_GEOFENCE_BUT_SECURED_DISTANCE = "NO_GEOFENCE_BUT_SECURED_DISTANCE_081";
	    public static final String GEOFENCE_REQUEST = "GEOFENCE_REQUEST_081";
	    public static final String REMOVE_GEOFENCE_ACTION = "REMOVE_GEOFENCE_081";
	    public static boolean PREVENT_SHUTDOWN = false;
	    public static final String PREVENT_SHUTDOWN_REQUEST = "PREVENT_SHUTDOWN_REQUEST_081";
	    public static final String DEVICE_ALREADY_INUSE = "DEVICE_ALREADY_INUSE_081";
	    public static final String VIDEO_RECORD_REQUEST = "VIDEO_RECORD_REQUEST_081";

}
