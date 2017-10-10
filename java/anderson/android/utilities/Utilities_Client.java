package anderson.android.utilities;

import android.app.Notification;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static java.lang.Math.abs;

public class Utilities_Client {
    /**
     * Saves an attachment part to a file on disk
     * @param part a part of the e-mail's multipart content.
     * @throws MessagingException
     * @throws IOException
     * 
     * 
     */
	
	private static Notification.Builder notificationBuilder(int icon , Context mContext, int time) {
		 
	    Notification.Builder builder = 
	          new Notification.Builder(mContext);
	    builder.setSmallIcon(icon)
	           .setTicker("VV Mail");
	         
	    return builder;
	  }
 

	 public static void cleanDir(File file)
		    	throws IOException{
		 
		    	if(file.isDirectory()){
		 
		    		//directory is empty, then delete it
		    		if (file.list().length == 0){
		               return;
		    		}
		    		else{
		 
		    		   //list all the directory contents
		        	   String files[] = file.list();
		 
		        	   for (String temp : files) {
		        		   deleteAFile( temp);
		        	   }
		        	     
		        	   }
		 
		        	   //check the directory again, if empty then delete it
		        	   if (file.list().length == 0){
		           	     return;
		        	   }
		    		}
		 
		  
		    }
		
	 
	public static void clearDir (String  dirPath){
		
		File directory = new File(dirPath);
		 
    	//make sure directory exists
    	if(!directory.exists()){
           return;
 
        }
    	else
        	if (directory.isDirectory()){
	    		//directory is empty, then delete it
	    		if (directory.list().length == 0){
	               return;
	    		}
	    		else{
	    		   //list all the directory contents
	        	   String files[] = directory.list();
	 
	        	   for (String temp : files) {
	        		   Log.i("Delete file: ",  temp );
	        		   deleteAFile( dirPath + "/" + temp);
	        	   }
	        	     
	    		}
        	}
        }

	public static void send(String destPhoneNumber, String messbody)
	{        
		
		 try {
			    SmsManager smsManager = SmsManager.getDefault();
				smsManager.sendTextMessage(destPhoneNumber, null , messbody , null, null);
				Toast.makeText(Global_Client.my_context, "SMS Sent! ", Toast.LENGTH_LONG).show();
			  } catch (Exception e) {
				Toast.makeText(Global_Client.my_context,
					"SMS faild, please try again later!",
					Toast.LENGTH_LONG).show();
				e.printStackTrace();
			  }

	}
	
	
	 public static String createFile( String dirPath, String fileName){
	 	   File newDir = new File (dirPath);
	 	   if (!newDir.exists()){
	 				newDir.mkdirs();
	 				newDir.setExecutable(true);
	 				newDir.setReadable(true);
	 				newDir.setWritable(true);
	 	   }
	 	 String file_Path = dirPath + fileName;
	 	
	  	   File newFile = new File (file_Path);
	  	   if (newFile.exists()){
	 	        	newFile.delete(); 
	 	         }
	 	   try {
	 			newFile.createNewFile();
	 		    newFile.setExecutable(true);
	 		    newFile.setReadable(true);
	 			newFile.setWritable(true);
	 	  } catch (IOException e) {
	 			// TODO Auto-generated catch block
	 			e.printStackTrace();
	 		}
	 	//  return (file_Path);
	 	   
	 	return (newFile.getAbsolutePath());
	 }
	 
	 public static boolean checkFile( String file_Path){
	  	   File newFile = new File (file_Path);
	  	   if (newFile.exists()){
	 	        	return true;
	 	         }
	 	return (false);
	 }
	 
 public static String GetGmailName(String GmailPath ){
       String GmailName = GmailPath.substring(0, GmailPath.indexOf("@") + 1);
       return GmailName;
 }
 public static boolean deleteAFile( String file_Path){
	 
	 File newFile = new File (file_Path);
	   if ((newFile != null) &&(newFile.exists())){
	        	newFile.delete(); 
	         } 
	   else
		   return false;
	   
	 return true;
 }
	
	
 public static void copyFileUsingStream(File source, File dest) throws IOException {
	    InputStream is = null;
	    OutputStream os = null;
	    
	    // BufferedWriter bw=new BufferedWriter(new FileWriter(dest));
	   
		    try {
		        is = new FileInputStream(source);
		        os = new FileOutputStream(dest);
		        byte[] buffer = new byte[1024];
		        int length;
		        while ((length = is.read(buffer)) > 0) {
		            os.write(buffer, 0, length);
		        }
		    } finally {
		        is.close();
		        os.close();
		    }
       }
	
	
   
	

	 // Write the messages in Global.alertedmsgInfo to file 
	public static void BackUpLocations( ArrayList<String> tLocationList, String backupDir, String fileName) throws FileNotFoundException{
		
		    String filePath = createFile(backupDir , fileName);
		
    	    String temp = "";
    	    int length = tLocationList.size();
    	    if (length == 0){
    	    	return;
    	    }
    	    
    	    FileOutputStream f_writer = null;
    	    
			try {
				f_writer =  new FileOutputStream(filePath);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return;
			 }
			
    	     for (int i = 0; i < length; i++){
	    	    String temp_str = tLocationList.get(i);
	    	    String  Ticker_str = temp_str + Constants_Client.POUND;
	    	    temp += Ticker_str;
	    	    Ticker_str = null;	   
    	     }
   
    	     Log.d("BackupStocks", "Stocks are = " + temp);
    	            
    	     try {
					 f_writer.write(temp.getBytes());
			 } catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			 }
    	    
    	    
    	    try {
				f_writer.close();
			 } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
        
    }

    public static  void putToast(String message, int x, int y){
    	Toast toast = new Toast(Global_Client.my_context);
    
	  toast.setGravity(Gravity.CENTER_HORIZONTAL, x, y);
	  toast = Toast.makeText(Global_Client.my_context,message,Toast.LENGTH_LONG);
	  toast.show();
    }


    public static void displayAToast (String msg, Context mContext){
      Toast toast = new Toast(mContext);
	  toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 240);
	  toast = Toast.makeText(mContext, msg,Toast.LENGTH_LONG);
	  
	  toast.show();
    }
   
    public static String createFolder( String root, String dirPath){
	 	   File newDir = new File (root + "/" + dirPath);
	 	   if (!newDir.exists()){
	 				newDir.mkdirs();
	 				newDir.setExecutable(true);
	 				newDir.setReadable(true);
	 				newDir.setWritable(true);
	 	   }
	 	  return newDir.getAbsolutePath();
    }
    
    public static void appendTextToFile(String filePath,   String  textToAppend){
   
    	try {
		        String filename= filePath;
			    Writer fw = new BufferedWriter(new FileWriter(filename, true));
		        fw.write(textToAppend + "\n");   //appends the string to the file
		        fw.close();
	   }
	   catch(IOException ioe){
		        System.err.println("IOException: " + ioe.getMessage());
	   }

   }

    public static String getAddressGivenLongAndLate(double latitude, double longitude){
    
        Geocoder geocoder = new Geocoder(Global_Client.my_context,Locale.getDefault());
        List<Address> addresses = null;
		try {
			addresses = geocoder.getFromLocation(latitude, longitude, 1);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			 Toast.makeText(Global_Client.my_context,  
		             "Can not parse the latitute and longitute ...", Toast.LENGTH_LONG).show();  
			return null;
		}
		
       StringBuilder sb = new StringBuilder();
       if (addresses.size() > 0) {
    	  Address address = addresses.get(0);
         // for (int i = 0; i < address.getMaxAddressLineIndex(); i++){
			//  Log.i("Test:", address.getAddressLine(i));
        //	 sb.append(address.getAddressLine(i)).append("\n");
       //   }
		   sb.append(address.getAddressLine(0)).append("\n");
		  // sb.append(address.getLocality()).append("\n");
		  // sb.append(address.getPostalCode()).append("\n");
		  // sb.append(address.getCountryName());
       }
       else {
        	return null;
       }
      
       return sb.toString();
    }
	public static  boolean get_AccountInfor(String DeviceInfor[]) throws IOException
	{
		String str = null;
		String tempStr = null;
		File file = Global_Client.my_context.getFileStreamPath(Constants_Client.GMaiInforFile);
		long length = 0;
		if  (file.exists() && (length = file.length()) > 0){
			FileInputStream fs = Global_Client.my_context.openFileInput(Constants_Client.GMaiInforFile);
			if (fs == null) {
				return false;
			}

			byte[] buffer = new byte[fs.available()];
			fs.read(buffer, 0, fs.available());
			str = new String(buffer);
			fs.close();
			int start = 0, end = 0, i = 0;
			while ((end = str.indexOf("\n", start)) != -1){
				tempStr = str.substring(start, end);
				DeviceInfor[i++] = tempStr;
				start = end + 1 ;
				str = str.substring(start);
				start = 0;

			}
			return true;
		}

		return false;

	}

	public static double distanceBeweenTwoLocs(double latA, double longA, double latB, double longB,  float accuracy, double recent_distance){

		double distance = distanceA_B(latA, longA, latB, longB);
		if (Global_Client.lastDistance > 1) {
			if (distance > Global_Client.lastDistance) {
				if ((distance - Global_Client.lastDistance) <= ((recent_distance / 2) * 3)) {
					Global_Client.lastDistance = distance;
				} else {
					Global_Client.lastDistance = Global_Client.lastDistance + recent_distance;
				}
			} else if (distance < Global_Client.lastDistance) {
				if ((Global_Client.lastDistance - distance) <= (recent_distance / 2) * 3) {
					Global_Client.lastDistance = distance;
				} else {
					Global_Client.lastDistance = Global_Client.lastDistance - recent_distance;

				}
			} else {
				Global_Client.lastDistance = distance;
			}
			if (Global_Client.lastDistance < 0) {
				Global_Client.lastDistance = 1;
			}

			return (Global_Client.lastDistance);
		}
		  return distance;
	}

	public static double distanceA_B(double latA, double longA, double latB, double longB){

		Location locationA = new Location("point A");
		locationA.setLatitude(latA);
		locationA.setLongitude(longA);

		Location locationB = new Location("point B");
		locationB.setLatitude(latB);
		locationB.setLongitude(longB);

		double distance = locationB.distanceTo(locationA);
		Utilities_Client.displayAToast("First get location:" + String.valueOf(distance), Global_Client.my_context);
		/*final float[] results= new float[1];
		Location.distanceBetween(latA, longA, latB, longB, results);
		double distance = results[0];
         */
		return (distance);
	}

    public static boolean checkForNewLoc(double latA, double longA, double latB, double longB){

		if ((abs(latA - latB) > 0.0001) || (abs(longA - longB)> 0.0001)){
			return true;
		}
		return   false;
	}

	public static boolean checkForNewLoc_2(double latA, double longA, double latB, double longB){

		if ((abs(latA - latB) > 0.00001) || (abs(longA - longB)> 0.00001)){
			return true;
		}
		return   false;
	}

	public static void requestForConnection(String mDeviceName, String otherParty){
		Global_Client.confirmPhase =  Constants_Client.REGISTER_PHASE + Constants_Client.POUND + "OMITTED" + Constants_Client.POUND + otherParty + Constants_Client.POUND + mDeviceName;
		SmsManager.getDefault().sendTextMessage(otherParty, null, Global_Client.confirmPhase, null, null);
		Log.d(" ---CONFIRM:  ", "SEnd request ");
		Utilities_Client.displayAToast("Send REGISTER_ACTION", Global_Client.my_context);
		Global_Client.confirmPhase = Global_Client.confirmPhase.replace(Constants_Client.REGISTER_PHASE, Constants_Client.CONFIRM_PHASE);
	}

	public static String getPhoneNumberOnly(String phoneNumber) {

		if ((phoneNumber == null) || phoneNumber.isEmpty()) {
			return null;
		}

		String phoneNumberStr = "";
		for (int i = 0; i < phoneNumber.length(); i++) {
			char c = phoneNumber.charAt(i);
			if (Character.isDigit(c)) {
				String intStr = Character.toString(c);
				phoneNumberStr += intStr;
			}
		}
		return phoneNumberStr;
	}
	public static void sendText(String m_text, String dest) {
		Log.d("Sent: ", m_text);
		SmsManager.getDefault().sendTextMessage(dest, null, m_text, null, null);
	}

	public static double distanceAnalyze(List<Double> lastFive, double newDistance) {
		double longestDistance = 0.0;
		double shortestDistance = 9999999999.0;
		double secondLongestDis = 0.0;
		double secondShortestDis = 0.0;
		int maxIndex = lastFive.size();
		if ((maxIndex > 0) && (maxIndex < 10)){
			if(newDistance <lastFive.get(lastFive.size() -1) )
			lastFive.add(newDistance);
			return newDistance;
		}
		double latest_distance = lastFive.get(maxIndex - 1);
		double adjustedDistance = latest_distance;
		double previous_distance = lastFive.get(maxIndex - 2);

		for (int i = 0; i < maxIndex; i++) {
			double distance = lastFive.get(i);
			if (distance > longestDistance) {
				secondLongestDis = longestDistance;
				longestDistance = distance;
			}
			if (distance < shortestDistance) {
				secondShortestDis = shortestDistance;
				shortestDistance = distance;
			}
		}
		if ((latest_distance > previous_distance) && (newDistance > latest_distance)) {
			double estimatedDistance = longestDistance + abs(latest_distance - previous_distance);
			if (newDistance > estimatedDistance) {
				 adjustedDistance = longestDistance;
			}
			else{
				adjustedDistance = newDistance;
			}
		} else if ((latest_distance <= previous_distance) && (newDistance < previous_distance)) {
			double estimatedDistance = latest_distance + (latest_distance - previous_distance);
			if (newDistance < estimatedDistance) {
				adjustedDistance = estimatedDistance;
			} else{
				adjustedDistance =  newDistance;
			}
		}
		if (maxIndex == 5){
			lastFive.remove(0);
		}
		lastFive.add(adjustedDistance);
		return adjustedDistance;
	}

	public static boolean checkAccuracy(double mAccuracy,  double distance){
		if ( distance < 20 ){
			if (mAccuracy > 7) {
				return false;
			}
		}

		else if ( distance < 50 ){
			if (mAccuracy > 10) {
				return false;
			}
		}
		else if ( distance < 100 ){
			if (mAccuracy > 15) {
				return false;
			}
		}
		else if (distance < 200){
			if (mAccuracy > 20) {
				return false;
			}
		}
		else if (distance < 300){
			if (mAccuracy > 25) {
				return false;
			}
		}
		else if (distance < 400){
			if (mAccuracy > 30) {
				return false;
			}
		}
		else if (distance < 500){
			if (mAccuracy > 35) {
				return false;
			}
		}
		else if (distance < 600){
			if (mAccuracy > 40) {
				return false;
			}
		}
		else if (distance < 700){
			if (mAccuracy > 45) {
				return false;
			}
		}
		else if (distance < 800){
			if (mAccuracy > 50) {
				return false;
			}
		}
		else if (distance < 900){
			if (mAccuracy > 55) {
				return false;
			}
		}
		else if (distance < 1000){
			if (mAccuracy > 60) {
				return false;
			}
		}

		return true;

	}

	public static boolean isNetworkAvailable(Context mContext) {
		ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (activeNetwork != null) { // connected to the internet
			if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
				// connected to wifi
				Toast.makeText(mContext, activeNetwork.getTypeName(), Toast.LENGTH_SHORT).show();
				return true;
			} else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
				// connected to the mobile provider's data plan
				Toast.makeText(mContext, activeNetwork.getTypeName(), Toast.LENGTH_SHORT).show();
				return true;
			}
		}

		return false;

	}
}