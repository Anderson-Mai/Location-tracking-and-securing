package anderson.android.trackingbase;

//import java.io.File;
//import java.io.FileInputStream;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import anderson.android.device_one.Device_One_Global;


//import java.util.List;

/* Read prices for tickers from web in every INTERVAL of time. Ccompare the price of the ticker with 
 * the low and high prices of the ticker. If either low or high price is met, notify the user and the contacts
 * in the provided list - 
 * Author : Anderson Mai
 */

public class CheckDeviceConnectionService extends IntentService {

    public NotificationManager mNotificationManager;    
    private ArrayList<String> t_alertedtickerInfo = new ArrayList<String>();
    public static int m_Notify_Id = Global.Notify_ID;
    String ns = Context.NOTIFICATION_SERVICE;
    protected ArrayList<String> mContacts = new ArrayList<String>();
	
		public CheckDeviceConnectionService() {
		super("CheckDeviceConnectionService");
	}

   @Override
   public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
   }  
   
    @Override
	public void onDestroy() {
			super.onDestroy();
	}
		      
   @Override
   protected void onHandleIntent(Intent intent) {

	   String timeStamp = new SimpleDateFormat("m").format(Calendar.getInstance().getTime());
	   int curr_Minute = Integer.parseInt(timeStamp);
	   String  [] subject_fields;
	   for (int i = 0; i < Global.nameAccounts.size(); i++){
		   subject_fields = null;
		   subject_fields = Device_One_Global.Last_Thirty_RealAddress.get(i).split("_");
		   String recent_Time_Str = subject_fields[1];
		   subject_fields = null;
		   subject_fields = recent_Time_Str.split(":");
		   String recent_Minute_Str = subject_fields[1];
		   int recently_minute = Integer.parseInt(recent_Minute_Str);
		   if (curr_Minute != recently_minute) {

			 //  Global.phoneAccounts.remove(i);
			 //  Global.nameAccounts.remove(i);
		   }
	   }
   }
}
	