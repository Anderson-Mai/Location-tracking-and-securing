package anderson.android.trackingbase;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class Global{

	public static PendingIntent checkMailIntent = null;
	public static AlarmManager checkMailAlarmManager = null;
	public static PendingIntent checkDistanceIntent = null;
	public static AlarmManager checkDistanceAlarmManager = null;
	public static ArrayList<String> phoneAccounts = new ArrayList<String>();
	public static ArrayList<String> nameAccounts = new ArrayList<String>();
	public static String [] securedPoint = {"", "", "", "", "", "", "", "", "", ""};
	public static Context my_context = null;
	public static String EMAIL_PATTERN = "gmail.com";
	public static final String LATEST_LOCATION = "LATEST_LOCATION";
	public static ArrayAdapter<String> list_adapter = null;
	//public static ListView mDisplayer = null;
	public static long fenceDiameterOne = 0;
	public static Double presetDistance[]= {0.0, 0.0, 0.0, 0.0,0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
	public static int icon = 0;
	public static int cursorId = 0;
	public static int Notify_ID = 100;
	public static NotificationManager NotifyManager = null;
	public static PendingIntent flashingIntent = null;
	public static AlarmManager flashingAlarmManager = null;
	public static int counter = 0;
	public static boolean alarming = false;
	public static boolean INTEND_STARTED = false;
	public static boolean QUICK_RETRIEVE = true;
	public static float acceptedAccuracy = 80;
	public static boolean CLIENT_IS_READY = false;
	public static int ACCOUNT_INDEX = 0;
	public static String mPhoneNumber = "";
	public static String confirmPhase =  "";
	public static int [] alarming_tracker = new int[Constants.Max_Accounts];
	public static CustomList_One g_adapter;
	public static boolean [] account_Controler = {true, true, true,true, true,true};
	public static String GEOFENCE_REQUEST_text  = "";

	public static  String GetGmailName(String GmailPath ){
	       String GmailName = GmailPath.substring(0, GmailPath.indexOf("@"));
	       return GmailName;
	 }
	
	 public String createFilename(String dir_Path, String fileName){
	 	   File newDir = new File (dir_Path);
	 	   if (!newDir.exists()){
	 				if (newDir.mkdirs()){
	 					newDir.setExecutable(true);
	 		 			newDir.setReadable(true);
	 		 			newDir.setWritable(true);
	 					Log.d("CREATE DIRPATH:   ",dir_Path);
	 				}
	 				else {
	 					
	 					return null;
	 					
	 				}
	 	   }
	 	    String file_Path = dir_Path + fileName;
	  	    File newFile = new File (file_Path);
	  	    if (newFile.exists()){
	  		   return (newFile.getAbsolutePath());
	  		 
	  	   }
	 	   try {
	 			if (newFile.createNewFile()){
	 				newFile.setExecutable(true);
		 			newFile.setReadable(true);
		 			newFile.setWritable(true);
	 				Log.d("CREATE FILE:   ", file_Path);
	 				
	 			}
	 			else{
	 				Log.d("CANNOT CREATE FILE:   ", file_Path);
	 				return null;
	 			}
	 					
	 		} catch (IOException e) {
	 				// TODO Auto-generated catch block
	 				e.printStackTrace();
	 		 }
	 	  return (newFile.getAbsolutePath());
	 	 // return (file_Path);
	    }

	 	
}

	
	



