package anderson.android.trackingbase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import anderson.android.device_one.Device_One_Constants;
import anderson.android.device_one.Device_One_Global;
import anderson.android.device_two.Device_Two_Constants;
import anderson.android.device_two.Device_Two_Global;

public class CheckIncomingMsg {
	
	final String TAG = "CheckIncomingMsg";
    public NotificationManager mNotificationManager;
	private String newest_location = "";
	private String newest_address = "";
	private String sentFrom = "";
    public static PendingIntent m_periodicIntentSender ;
    ArrayList<MsgInfor> mInforList  = null;
	private String m_address = "";

	public CheckIncomingMsg(String newAddress, String m_newLocation, String m_sentFrom){
			newest_location = m_newLocation;
			sentFrom = m_sentFrom;
		    newest_address = newAddress;
		    m_address = newAddress;
	}
						      
    public String NewMsgHandler() {
	    	  Log.d("Check sms", "CHECH NEW SMS");
	    	  if (newest_location == null){
	    		  Log.d(TAG, "NULL"); 
	    		  return newest_location;
	    	  }
	    	  else {
				  int i;
				  Log.d("sentFrom:  ", sentFrom );
				  for(i = 0; i < Global.phoneAccounts.size(); i++){
					  Log.d("phoneAccounts:  ", Global.phoneAccounts.get(i) );
					  if (Global.phoneAccounts.get(i).contains(sentFrom)){
						  int length = 0;
						 // String m_address = newest_address;
						  Log.d("Devide registed :  ", String.valueOf(i));
						  switch(i){
							  case  0:
								  Log.d(TAG , "for  device 1: "+newest_location);

								  if (!Device_One_Global.Last_Thirty.isEmpty()){

									  length = Device_One_Global.Last_Thirty.size();
										  if (length < Device_One_Global.historyLength){
											  Device_One_Global.Last_Thirty.add(0, newest_location);
											  Device_One_Global.Last_Thirty_RealAddress.add(0, m_address);
											  Log.d(TAG , "for  device 1 < historyLength");
										  }
										  else {
											  Log.d(TAG , "for  device 1 => historyLength");
											  Device_One_Global.Last_Thirty.remove(length - 1);
											  Device_One_Global.Last_Thirty.add(0, newest_location);
											  Device_One_Global.Last_Thirty_RealAddress.remove(length - 1);
											  Device_One_Global.Last_Thirty_RealAddress.add(0, m_address);
										  }

										  Log.d("Check emails", " PASSED CHECH EMAIL - INTEND");

								  }
								  else {
									  Log.d("Check emails", " Device 1  empty");
									  Device_One_Global.Last_Thirty.add(0, newest_location);
									  Device_One_Global.Last_Thirty_RealAddress.add(0, m_address);
								  }

								  try {
									  Utilities.BackUpLocations(Device_One_Global.historyLength, Device_One_Global.Last_Thirty_RealAddress, Device_One_Constants.ClientOne_last_30_addresses, Global.my_context);
									  Utilities.BackUpLocations(Device_One_Global.historyLength,Device_One_Global.Last_Thirty, Device_One_Constants.ClientOne_last_30_ll, Global.my_context);
								  } catch (FileNotFoundException e) {
									  e.printStackTrace();
								  }

								  Intent  broadcast_intent = new Intent(Device_One_Constants.DEVICE_ONE_ADAPTER_UPDATED);
								  // You can also include some extra data.
								  Global.my_context.sendBroadcast(broadcast_intent);

								  broadcast_intent = new Intent(Constants.ACTION_NEW_ADDRESS);
								  broadcast_intent.putExtra(Constants.ACTION_NEW_ADDRESS, 0);
								  // You can also include some extra data.
								  Global.my_context.sendBroadcast(broadcast_intent);
								  Log.d("---latest Location: ", "BROAD CASTED 1");
								  Log.d("Check emails", " PASSED CHECH EMAIL - Divice  1");
								  return Device_One_Global.Last_Thirty_RealAddress.get(0);

							  case 1:

								  Log.d(TAG , "for  device 2: "+newest_location);

								  if (!Device_Two_Global.Last_Thirty.isEmpty()){

									  length = Device_Two_Global.Last_Thirty.size();
									  if (length < 30){
										  Device_Two_Global.Last_Thirty.add(0, newest_location);
										  Device_Two_Global.Last_Thirty_RealAddress.add(0, m_address);
										  Log.d(TAG , "for  device 1 < 30");
									  }
									  else {
										  Log.d(TAG , "for  device 1 => 30");
										  Device_Two_Global.Last_Thirty.remove(length - 1);
										  Device_Two_Global.Last_Thirty.add(0, newest_location);
										  Device_Two_Global.Last_Thirty_RealAddress.remove(length - 1);
										  Device_Two_Global.Last_Thirty_RealAddress.add(0, m_address);
									  }

									  Log.d("Check emails", " PASSED CHECH EMAIL - INTEND");

								  }
								  else {
									  Log.d("Check emails", " Device 2  empty");
									  Device_Two_Global.Last_Thirty.add(0, newest_location);
									  Device_Two_Global.Last_Thirty_RealAddress.add(0, m_address);
								  }

								  try {
									  Utilities.BackUpLocations(30,Device_Two_Global.Last_Thirty_RealAddress, Device_Two_Constants.ClientTwo_last_30_addresses, Global.my_context);
									  Utilities.BackUpLocations(30,Device_Two_Global.Last_Thirty, Device_Two_Constants.ClientTwo_last_30_ll, Global.my_context);
								  } catch (FileNotFoundException e) {
									  e.printStackTrace();
								  }

								  Intent  broadcast_intent_2 = new Intent(Device_Two_Constants.DEVICE_TWO_ADAPTER_UPDATED);
								  // You can also include some extra data.
								  Global.my_context.sendBroadcast(broadcast_intent_2);
								  broadcast_intent = new Intent(Constants.ACTION_NEW_ADDRESS);
								  broadcast_intent.putExtra(Constants.ACTION_NEW_ADDRESS, 1);
								  Log.d("---latest Location: ", "BROAD CASTED 1");
								  Log.d("Check emails", " PASSED CHECH EMAIL - Divice  2");
								  return Device_Two_Global.Last_Thirty_RealAddress.get(0);

							  case 2:
							  case 3:
							  case 4:
							  case 5:
								  break;
						  }
						  break;
					  }
				  }
		    	  return null;
	    	  }
   }

protected boolean get_GmailAccountInfor(String Gmail[]) throws IOException 
{
		
		String str = null;
	    String tempStr = null; 	
	    File file = Global.my_context.getFileStreamPath(Constants.GMaiInforFile);
	if  (file.exists()){	
			FileInputStream fs = Global.my_context.openFileInput(Constants.GMaiInforFile);
	
			if (fs == null){
				return false;
			}
			if (fs.available() == 0){
		
				return false;
				
			}
			if ((fs != null) && (fs.available() != 0)){
				byte[] buffer = new byte[fs.available()];
				
					fs.read(buffer, 0, fs.available());
					str = new String(buffer);
					Log.d("onStart CHECK HERE:", "str = " + str);
				
				fs.close();
				int start = 0, end = 0, i = 0;
				while ((end = str.indexOf("\n", start)) != -1){
					tempStr = str.substring(start, end);
					Gmail[i++] = tempStr;
					//=============================    
					start = end + 1 ;
					str = str.substring(start);
					start = 0;
					
				}
			  }
			return true;
	}
	
	return false;
	
  }

public String locWithAddress( String loc) throws IOException, JSONException {
	 String [] splitedFields = loc.split(Constants.AT);
	 double latitude = Double.parseDouble(splitedFields[1]);
	 double longitude = Double.parseDouble(splitedFields[2]);
	 
	 String address = Utilities.getAddressGivenLongAndLate(latitude, longitude,Global.my_context );
	 return splitedFields[0] + "\n" + address;
	
}


}