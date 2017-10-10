package anderson.android.trackingbase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Camera.Parameters;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import anderson.android.device_one.Device_One_Global;

public class Utilities {
    /**
     * Saves an attachment part to a file on disk
     * @param part a part of the e-mail's multipart content.
     * @throws MessagingException
     * @throws IOException
     * 
     * 
     */
  public static void display_Notification(int m_icon_image){
		
	  //  Global.icon = R.drawable.email_image;
	    Global.icon = m_icon_image;
		Context context = Global.my_context;
		Notification.Builder mBuilder =
			    new Notification.Builder(context)
			    .setSmallIcon(Global.icon)
			    .setContentTitle("Tracking Base")
			    .setContentText("");
		Intent notificationIntent = new Intent(Global.my_context, FirstSetUp_Base.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		PendingIntent contentIntent = 
						PendingIntent.getActivity(Global.my_context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(contentIntent);
		Notification notification = mBuilder.build();
	    Global.NotifyManager.notify(Global.Notify_ID, notification);
		Global.cursorId = Global.Notify_ID;
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
				Toast.makeText(Global.my_context, "SMS Sent! ", Toast.LENGTH_LONG).show();
			  } catch (Exception e) {
				Toast.makeText(Global.my_context,
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
       String GmailName = GmailPath.substring(0, GmailPath.indexOf("@"));
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
	
	public static void LoadLocation( ArrayList<String> tGmailList, String fileName, Context m_Context){
		tGmailList.clear();
		String filePath = fileName;
		String str = null;
		String tempStr = null;
		File file = m_Context.getFileStreamPath(filePath);
		FileInputStream fs = null;
		if (file.exists()) {
			try {
				fs = m_Context.openFileInput(filePath);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return;
			}

		}
		else
			return;
		            int availBytes = 0;
					try {
						availBytes = fs.available();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					if ((fs != null) && (availBytes > 0)){
						 
						byte[] buffer = new byte[availBytes];
						
							try {
								fs.read(buffer, 0, availBytes);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								try {
									fs.close();
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								return;
							}
							str = new String(buffer);
							Log.d("Load  ", "str = " + str);
						
						try {
							fs.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return;
						}
						int start = 0, end = 0;
						int counter = 0;
						while (((end = str.indexOf(Constants.POUND, start)) != -1) &&
								(counter++ < 30)){
								tempStr = str.substring(start, end);
								tGmailList.add(tempStr);
								//=============================    
								start = end + 1 ;
								str = str.substring(start);
								start = 0;
					   }
					
                  }  
	 }
	public static void LoadLocation_Two( ArrayList<String> tGmailList, String dirPath, String fileName){

		String filePath = dirPath + fileName;
		String str = null;
		String tempStr = null;
		if (!checkFile(filePath)){
			return;
		}


		FileInputStream fs;
		try {
			fs = new FileInputStream(filePath);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}


		int availBytes = 0;
		try {
			availBytes = fs.available();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if ((fs != null) && (availBytes > 0)){

			byte[] buffer = new byte[availBytes];

			try {
				fs.read(buffer, 0, availBytes);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				try {
					fs.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				return;
			}
			str = new String(buffer);
			Log.d("backup Old message", "str = " + str);

			try {
				fs.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			int start = 0, end = 0;
			int counter = 0;
			while (((end = str.indexOf(Constants.POUND, start)) != -1) &&
					(counter++ < 30)){
				tempStr = str.substring(start, end);
				tGmailList.add(tempStr);
				//=============================
				start = end + 1 ;
				str = str.substring(start);
				start = 0;
			}

		}
	}

	// Write the messages in Global.alertedmsgInfo to file
	public static void BackUpLocations( int mHisLength, ArrayList<String> tLocationList, String fileName, Context mContext) throws FileNotFoundException{
		
		    String filePath = fileName;
    	    String temp = "";
    	    int length = tLocationList.size();
    	    if (length == 0){
				mContext.deleteFile(filePath);
    	    	return;
    	    }
    	    if (length > mHisLength) {
				length = mHisLength;
				for (int i = mHisLength; i < length; i++){
					tLocationList.remove(i);
				}
			}
    	    
    	    FileOutputStream f_writer = null;
			try {
				f_writer = mContext.openFileOutput(filePath, Context.MODE_PRIVATE);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return;
			 }
			
    	     for (int i = 0; i < length; i++){
	    	    String temp_str = tLocationList.get(i);
	    	    String  Ticker_str = temp_str + Constants.POUND;
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
	// Write the messages in Global.alertedmsgInfo to file
	public static void BackUpLocations_two( ArrayList<String> tLocationList, String dirPath,  String fileName) throws FileNotFoundException{

		String filePath = dirPath + fileName;

		String temp = "";
		int length = tLocationList.size();
		if (length == 0){
			return;
		}
		if (length > 30) {
			length = 30;
		}

		if (!checkFile(filePath)){
			filePath = createFile( dirPath, fileName);
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
			String  Ticker_str = temp_str + Constants.POUND;
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


	// Write the messages in Global.alertedmsgInfo to file
    public static void AddToOldMsgsList( String tOldGmail, String filePath) throws FileNotFoundException{
    	  
    	    String  OldGmail_str = tOldGmail + Constants.POUND;
    	            
    	    FileOutputStream f_writer = new FileOutputStream(new File(filePath), true);
			
    	    if (f_writer != null){
    	       	try {
					 f_writer.write(OldGmail_str.getBytes());
					 f_writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    	    }
    	    
    }
	
    public static  void putToast(String message, int x, int y){
    	Toast toast = new Toast(Global.my_context);
    
	  toast = Toast.makeText(Global.my_context,message,Toast.LENGTH_LONG);
	  toast.setGravity(Gravity.CENTER, x, y);
	  toast.show();
    }

  /* public static void displayNotification(int iconImage, int Notify_ID){
	  

        String ns = Context.NOTIFICATION_SERVICE;
    		 NotificationManager mNotificationManager = (NotificationManager) Global.my_context.getSystemService(ns);
    	
    		Log.d("I AM HERE 5", "I AM HERE 5");
    		CharSequence tickerText = "New VV Mails";
    		long when = System.currentTimeMillis();
    		
    		Notification notification = new Notification(iconImage, tickerText, when);
    		CharSequence contentTitle = "New VV mails";
    		CharSequence contentText = "";
    		
    		Intent notificationIntent = new Intent(Global.my_context, NewIncomingMailProcess.class);
    		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    		PendingIntent contentIntent = 
    						PendingIntent.getActivity(Global.my_context, 0, notificationIntent,0);
    		
    	    
    		notification.setLatestEventInfo(Global.my_context, contentTitle, contentText, contentIntent);
    	
    		mNotificationManager.notify(Notify_ID, notification);
    		Global.mboxNotifyManager = mNotificationManager;
    	
           
    		//=========================================
    }
  */
    
  private static Notification.Builder notificationBuilder(int icon , Context mContext, int time) {
		 
	    Notification.Builder builder = 
	          new Notification.Builder(mContext);
	    builder.setSmallIcon(icon)
	           .setTicker("VV Mail");
	         
	    return builder;
	  }
 
  
  

  
  // Write the messages in Global.alertedmsgInfo to file 
  public static  void BackUpInfor( MsgInfor GMaiInfor, String backupDir){
    	    String content = "";
    	    FileOutputStream fop = null;
    	    final String BLANK = "#";
    	    
    	     Log.d("Back Up File Name :  ", GMaiInfor.MessageName);
    	     
    	     File mFile = new File(backupDir + GMaiInfor.MessageName);
    	     if (mFile.exists())
    	    	  return;
    	  
    	      content =  GMaiInfor.MessageId + BLANK + GMaiInfor.MessageName +  BLANK + GMaiInfor.MessageNumb + BLANK +   GMaiInfor.From + BLANK + GMaiInfor.To + BLANK + GMaiInfor.CC + BLANK 
    	    	+ GMaiInfor.Subject + BLANK + GMaiInfor.SentDate + BLANK + GMaiInfor.Message + BLANK + GMaiInfor.Attachments + BLANK + GMaiInfor.VideoLength +  BLANK + GMaiInfor.FullName + BLANK
    	    	+ GMaiInfor.Voice_Video_Mask + BLANK;
    	     
    	     Log.d("content buff:  ", content);
    	   
    	     String filePath = createFile(backupDir , GMaiInfor.MessageName);
    	   //  String filePath = Global.createFile(Constants.BACKUPINFORDIR , GMaiInfor.MessageName);
    	    Log.d("content buff   2:  ", content);
    	     try {
    	    	 
				  fop = new FileOutputStream(filePath);
				  
				  Log.d("content buff   3:  ", content);
			 } catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			  }
    	   
    	   Log.d("content buff   4:  ", content);
			// get the content in bytes
			byte[] contentInBytes = content.getBytes();
			 Log.d("content buff   5:  ", content);
			try {
				fop.write(contentInBytes);
				fop.flush();
	 			fop.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			 }
				    
    }
  
  // Write the messages in Global.alertedmsgInfo to file 
  public static  void ReadBackUpInfor( MsgInfor GMaiInfor, String backupDir, String FileName){
    	    String content = "";
    	    FileInputStream fip = null;
    	    final String BLANK = "#";
    	    
    	     Log.d("Back Up File Name :  ", GMaiInfor.MessageName);
    	  
    	  
    	      content =  GMaiInfor.MessageId + BLANK + GMaiInfor.MessageName +  BLANK + GMaiInfor.MessageNumb + BLANK +   GMaiInfor.From + BLANK + GMaiInfor.To + BLANK + GMaiInfor.CC + BLANK 
    	    	+ GMaiInfor.Subject + BLANK + GMaiInfor.SentDate + BLANK + GMaiInfor.Message + BLANK + GMaiInfor.Attachments + BLANK;
    	     
    	     Log.d("content buff:  ", content);
    	     String filePath = backupDir + FileName;
    	   //  String filePath = Global.createFile(Constants.BACKUPINFORDIR , GMaiInfor.MessageName);
    	    Log.d("content buff   2:  ", content);
    	     try {
    	    	 
				  fip = new FileInputStream(filePath);
				  
				 // Log.d("content buff   3:  ", content);
			 } catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			  }
    	   
    	   Log.d("content buff   4:  ", content);
			// get the content in bytes
    	   int data_len = 0;
    	    try {
				data_len = fip.available();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			byte[] contentInBytes = new byte [data_len];
			
			 Log.d("content buff   5:  ", content);
			try {
				fip.read(contentInBytes);
	 			fip.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			String dataStr = contentInBytes.toString();
			String [] m_data =  dataStr.split(BLANK);
			GMaiInfor.MessageId = m_data[0];
			GMaiInfor.MessageName = m_data[1];
			GMaiInfor.MessageNumb = m_data[2];
			GMaiInfor.From = m_data[3];
			GMaiInfor.To =  m_data[4];
			GMaiInfor.CC = m_data[5];
		    GMaiInfor.Subject = m_data[6];
			GMaiInfor.SentDate = m_data[7];
			GMaiInfor.Message = m_data[8];
			GMaiInfor.Attachments =  m_data[9];
		    	     
			
				    
    }
  
  
  public static void readVoiceFile(String absoluteWareFilePath){
      File mfile = new File(absoluteWareFilePath);
   
   	Uri muri = Uri.fromFile(mfile); 
   	MediaPlayer mp = MediaPlayer.create(Global.my_context.getApplicationContext(), muri);
   	//mp.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);  //New code - Mai
		try {
			mp.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mp.start();

      //Called when the song <span class="IL_AD" id="IL_AD5">completes</span>.....
      mp.setOnCompletionListener(new OnCompletionListener() {
          public void onCompletion(MediaPlayer mp) {
       	   mp.stop();
       	   mp.release();
       	   
          }
      });   	   
}
  
  
 public static void sendBroadcastMsg(Context m_context, int total_msg) {
  Intent m_intent = new Intent();
  m_intent.setAction("androidOne.google.apps.mybroadcast");
  m_intent.putExtra("MAX_NEWMAILS", total_msg);
  Log.d("SENT BROADCAST ", "BROADCAST");
  m_context.sendBroadcast(m_intent); 
 }
 
 
 public static boolean startContinuousAutoFocus(android.hardware.Camera mCamera ) {
	 
	    Parameters params =  mCamera.getParameters();
	   
	    List<String> focusModes = params.getSupportedFocusModes();

	    String CAF_PICTURE = Parameters.FOCUS_MODE_CONTINUOUS_PICTURE, 
	           CAF_VIDEO = Parameters.FOCUS_MODE_CONTINUOUS_VIDEO, 
	           supportedMode = focusModes
	                   .contains(CAF_PICTURE) ? CAF_PICTURE : focusModes
	                   .contains(CAF_VIDEO) ? CAF_VIDEO : "";

	    if (!supportedMode.equals("")) {

	        params.setFocusMode(supportedMode);
	        mCamera.setParameters(params);
	        return true;
	    }

	    return false;
	}
 
    public static String generateCombinedKey(String mKeyOne, String mKeyTwo){
		String combinedKey = null;
		combinedKey = mKeyOne.substring(0, 1) + mKeyTwo.substring(3,4) + mKeyOne.substring(1,2) + 
				mKeyTwo.substring(2, 3) + mKeyOne.substring(2, 3) + mKeyTwo.substring(1,2) 
				+ mKeyOne.substring(3, 4)+ mKeyTwo.substring(0,1);
		
	    return combinedKey;
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
		        FileWriter fw = new FileWriter(filename,true); //the true will append the new data
		        fw.write(textToAppend + "\n");   //appends the string to the file
		        fw.close();
	   }
	   catch(IOException ioe){
		        System.err.println("IOException: " + ioe.getMessage());
	   }

   }
    public static String getAddressGivenLongAndLate(double latitude, double longitude, Context context) {

       Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
		try {
			addresses = geocoder.getFromLocation(latitude, longitude, 1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(Global.my_context,
		             "Can not parse the latitute and longitute ...", Toast.LENGTH_LONG).show();
			return null;
		}
		
       StringBuilder sb = new StringBuilder();
       if (addresses.size() > 0) {
    	  Address address = addresses.get(0);
		  sb.append(address.getAddressLine(0)).append("\n");
       }
       else {
        	return null;
       }
      
       return sb.toString();
    }

	public static int getListFromFile(Context m_Context,
									  ArrayList<String> tGmailList, String filePath) throws IOException {

		String str = null;
		String tempStr = null;

		File file = m_Context.getFileStreamPath(filePath);

		if (file.exists()) {
			FileInputStream fs = m_Context.openFileInput(filePath);

			if (fs == null) {
				return -1;
			} else if (fs.available() == 0) {
				return -2;

			} else if ((fs != null) && (fs.available() != 0)) {
				byte[] buffer = new byte[fs.available()];
				fs.read(buffer, 0, fs.available());
				str = new String(buffer);
				// Log.d("Get data frpm backup Old message", "str = " + str);

				fs.close();
				int start = 0, end = 0;
				while ((end = str.indexOf(Constants.POUND, start)) != -1) {
					tempStr = str.substring(start, end);
					//t_nodeList.add(Utilities.GetNode(tempStr, ""));
					tGmailList.add(tempStr);
					start = end + 1;
					str = str.substring(start);
					start = 0;
				}
				return 1;
			}

		}
		return -3;
	}

	// Write the messages in Global.alertedmsgInfo to file
	public static void BackUpDeviceList(ArrayList<String> tDeviceList, ArrayList<String> tNameList, Context mContext) {
		String temp = "";

		if (tDeviceList.isEmpty()) {
			mContext.deleteFile(Constants.GMaiInforFile);
			mContext.deleteFile(Constants.GMaiContactFile);
			return;
		}

		for (int i = 0; i < tDeviceList.size(); i++) {
			String temp_str = tDeviceList.get(i);
			String Ticker_str = temp_str + Constants.POUND;
			temp += Ticker_str;
			Ticker_str = null;
		}

		FileOutputStream f_writer = null;
		try {
			f_writer = mContext.openFileOutput(Constants.GMaiInforFile, Context.MODE_PRIVATE);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		if (f_writer != null) {
			try {
				f_writer.write(temp.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			f_writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		temp = "";
		for (int i = 0; i < tNameList.size(); i++) {
			String temp_str = tNameList.get(i);
			String Ticker_str = temp_str + Constants.POUND;
			temp += Ticker_str;
			Ticker_str = null;
		}

		FileOutputStream f_writer_two = null;
		try {
			f_writer_two = mContext.openFileOutput(Constants.GMaiContactFile, Context.MODE_PRIVATE);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		if (f_writer_two != null) {
			try {
				f_writer_two.write(temp.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			f_writer_two.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public static void openSoftKeyboard(Context m_context) {
		//View view = getCurrentFocus();
		InputMethodManager imm = (InputMethodManager) m_context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
	}

	public static String skipSpacesInString(String str) {
		return str.trim().replaceAll(" +", "");
	}

	public static String reduceSpacesInString(String str) {
		return str.trim().replaceAll(" +", " ");
	}
	public static String[] getPhoneContactList(Context m_context) {
		ContentResolver cr = m_context.getContentResolver();
		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		ArrayList<String> contact_names = new ArrayList<String>();

		if (cur.getCount() > 0) {
			while (cur.moveToNext()) {
				String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
				String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				if (cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER.trim()))
						.equalsIgnoreCase("1")) {
					if (name != null) {
						Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
						while (pCur.moveToNext()) {
							String PhoneNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
							contact_names.add(name + ", " + PhoneNumber);
						}
						pCur.close();
						pCur.deactivate();
					}
				}
			}
			cur.close();
			cur.deactivate();
		}

		String[] contactList = new String[contact_names.size()];
		for (int j = 0; j < contact_names.size(); j++) {
			contactList[j] = contact_names.get(j);
		}
		return contactList;
	}


	public static String getColorForLetter(String firstLetter) {
		switch (firstLetter) {
			case "A":
			case "a":
				return ("#008000"); //Green
			case "J":
			case "j":
				return ("#00FF00");  //Lime
			case "C":
			case "c":
				return ("#808000");  //Olive
			case "D":
			case "d":
				return ("#FFFF00");  //Yellow
			case "E":
			case "e":
				return ("#000080");  //Navy
			case "F":
			case "f":
				return ("#0000FF");  //Blue
			case "T":
			case "t":
				return ("#008080");  //Teal
			case "Z":
			case "z":
				return ("#636363");  //Dark silver
			case "H":
			case "h":
				return ("#00FFFF");  //Aqua
			case "B":
			case "b":
				return ("#000000");  //Black
			case "K":
			case "k":
				return ("#C0C0C0");  //Silver
			case "L":
			case "l":
				return ("#808080");  //Gray
			case "M":
			case "m":
				return ("#800000");  //Maroon
			case "R":
			case "r":
				return ("#FF0000");  //Red
			case "O":
			case "o":
				return ("#800080");  //Purple
			case "P":
			case "p":
				return ("#FF00FF");  //Fuchsia
			case "Q":
			case "q":
				return ("#497FBF");  //Light purple
			case "N":
			case "n":
				return ("#648DCF");  //Light navy
			case "S":
			case "s":
				return ("#00AEED");  //Light blue
			case "G":
			case "g":
				return ("#00A54E");  //Light green
			case "U":
			case "u":
				return ("#8DCB41");  //Light lime
			case "V":
			case "v":
				return ("#FcF64C");  //Light yellow
			case "X":
			case "x":
				return ("#DC9000");  //Orange
			case "Y":
			case "y":
				return ("#7D7D7D");  //Orange
			case "W":
			case "w":
				return ("#D16162");
			default:
				return ("#002809");
		}
		//return Constants.anonymous;
	}

	public static void sendText(String m_text, String dest) {

		SmsManager.getDefault().sendTextMessage(dest, null, m_text, null, null);
	}

	public static boolean checkForJellyBean(){

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN){
			return true;
		}
		else {
			return false;
		}

	}
	public static String uniformNumberFormat(String t_PhoneStr) {
		String uniformStr = "";
		if (t_PhoneStr.length() == 10) {
			uniformStr = "1 " + t_PhoneStr.substring(0, 3) + "-" + t_PhoneStr.substring(3);
		} else if (t_PhoneStr.length() == 11) {
			uniformStr = t_PhoneStr.substring(0, 1) + " " + t_PhoneStr.substring(1, 4) + "-" + t_PhoneStr.substring(4);
		}
		return uniformStr;
	}



	public static String getDigitsOnly(String phoneNumber) {

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




	public static void closeSoftKeyboard(Context m_context, View view) {
		//View view = getCurrentFocus();
		if (view != null) {
			InputMethodManager imm = (InputMethodManager) m_context.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}
	public static String readAndCheck(int rawFileId, String mAreaCodeStr, Context mContext) {
		int mAreaCodeInt = Integer.parseInt(mAreaCodeStr);
		InputStream inputStream = mContext.getResources().openRawResource(rawFileId);

		InputStreamReader inputreader = new InputStreamReader(inputStream);
		BufferedReader buffreader = new BufferedReader(inputreader);

		String line = "";
		String cityStr = null;

		try {
			while ((line = buffreader.readLine()) != null) {
				String temp_AreaCodeStr = line.substring(0, 3);

				if (temp_AreaCodeStr.contentEquals(mAreaCodeStr)) {
					cityStr = line.substring(line.indexOf("-") + 2);
					return cityStr;
				} else if (Integer.parseInt(temp_AreaCodeStr) > mAreaCodeInt) {
					return cityStr;
				}
				line = "";
			}
		} catch (IOException e) {
			return cityStr;
		}
		return cityStr;
	}
	public static List<Address> getStringFromLocation(double lat, double lng){

		String address = String
				.format(Locale.ENGLISH, "http://maps.googleapis.com/maps/api/geocode/json?latlng=%1$f,%2$f&sensor=true&language="
						+ Locale.getDefault().getCountry(), lat, lng);
		HttpGet httpGet = new HttpGet(address);
		HttpClient client = new DefaultHttpClient();
		HttpResponse response = null;
		StringBuilder stringBuilder = new StringBuilder();

		List<Address> retList = null;
		try {
			response = client.execute(httpGet);
		} catch (IOException e) {
			e.printStackTrace();
		}

		HttpEntity entity = response.getEntity();
		InputStream stream = null;

		try {
			stream = entity.getContent();
		} catch (IOException e) {
			e.printStackTrace();
		}

		int b;
		try {
			while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
		} catch (IOException e) {
			e.printStackTrace();
		}

		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(stringBuilder.toString());


			retList = new ArrayList<Address>();

			if ("OK".equalsIgnoreCase(jsonObject.getString("status"))) {
				JSONArray results = jsonObject.getJSONArray("results");
				for (int i = 0; i < results.length(); i++) {
					JSONObject result = results.getJSONObject(i);
					String indiStr = result.getString("formatted_address");
					Address addr = new Address(Locale.getDefault());
					addr.setAddressLine(0, indiStr);
					retList.add(addr);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return retList;
	}

	public static double distanceA_B(double latA, double longA, double latB, double longB){

		Location locationA = new Location("point A");
		locationA.setLatitude(latA);
		locationA.setLongitude(longA);

		Location locationB = new Location("point B");
		locationB.setLatitude(latB);
		locationB.setLongitude(longB);

		double distance = locationB.distanceTo(locationA);
		return (distance);
	}

	public static void adjustHistoryList(int m_hisLen, ArrayList<String> listOne, ArrayList<String> listTwo){
		if (listOne.size() > m_hisLen){
			for ( int i = m_hisLen; i <listOne.size(); i++ ){
				listOne.remove(i);
				listTwo.remove(i);
			}
		}
	}

}