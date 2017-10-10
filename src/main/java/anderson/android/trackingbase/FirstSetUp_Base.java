package anderson.android.trackingbase;

import android.accounts.Account;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;

public class FirstSetUp_Base extends Activity {
	 Button m_submitButton; 
	 Button m_cancelButton;
	// TextView mChanged;
	 LinearLayout mGmailSetup;
	 EditText mGMailAddress; 
	 EditText mGmailPassword;
	 EditText mOtherPartyEmail;
	 LinearLayout mGMailSetupSection = null;
	 final String BLANK = "#";
	 Toast toast;
	 Account[] account;                                                                                                                                               
	 Pattern emailPattern;
	 private ProgressBar  mSendingProgressBar= null;
	 final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
	 final String TAG = "FirstSetUp_Base";
		
	 @Override
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  setContentView(R.layout.firstsetup_base);




	  Global.my_context = this;
	  m_submitButton = (Button) findViewById(R.id.submitButton);
		 m_submitButton.setOnClickListener(new Button.OnClickListener(){
			 public void onClick(View v) {
				 Intent mIntent = new Intent(getApplicationContext(), MainActivity.class);
				 startActivity(mIntent);
			 }
		 });
	  m_cancelButton = (Button) findViewById(R.id.cancelButton);
	  m_cancelButton.setOnClickListener(new Button.OnClickListener(){
		 	 public void onClick(View v) {
		 		 finish();
		 	 }
	  });

	  String announce_str = "";
	  String possibleEmail = "";

		 	
	  mGMailSetupSection = (LinearLayout)findViewById(R.id.GMailSetupSection);
	 // try {
	//	  if (get_GmailAccountInfor(Global.phoneAccounts)){
	//			 mGMailSetupSection.setVisibility(View.GONE);
	//			 Intent m_intent = new Intent(getApplicationContext(), SelectionPage.class);
	//			 startActivity(m_intent);
	//			 finish();
	//	  }
	//	  else
		 {
			  //=====================================================================
			//  account = AccountManager.get(getBaseContext()).getAccountsByType(Constants_Client.ACCOUNT_TYPE_GOOGLE);
			  TelephonyManager tMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
			  String mPhoneNumber = tMgr.getLine1Number();
			  if (mPhoneNumber == null ){

				    mGMailSetupSection = (LinearLayout)findViewById(R.id.GMailSetupSection);
					mGMailSetupSection.setVisibility(View.GONE);
					announce_str = "Can not detect the SIM/ Phone number";
					
				    toast =  Toast.makeText(getApplicationContext(),announce_str, Toast.LENGTH_LONG);
				    toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 240);
				    toast.show();
				    return;
			  }

			  else {
				  announce_str = "SIM/ Phone number is detected";
				  Global.mPhoneNumber = mPhoneNumber;
				  Global.confirmPhase =  Constants.CONFIRM_PHASE + Global.mPhoneNumber;
				  toast =  Toast.makeText(getApplicationContext(),announce_str, Toast.LENGTH_LONG);
				  toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 240);
				  toast.show();
				  Intent mIntent = new Intent(getApplicationContext(), MainActivity.class);
				  startActivity(mIntent);

				//  possibleEmail = mPhoneNumber;
				//  mGMailAddress = (EditText)findViewById(R.id.GmailAddress);
				//  mGMailAddress.setText(possibleEmail);
		      }
			
		  }
	 // } catch (IOException e1) {
	//	// TODO Auto-generated catch block
	//	e1.printStackTrace();
	//    }
 }
	 

		
		

  @Override
  protected void onStart() {
       super.onStart();
  }
     
  // Remove all elements created for the table
  @Override protected void onStop(){
   	 super.onStop();
  }

  
  // Write the messages in Global.alertedmsgInfo to file 
  private void StoreGMaiInfor(String [] GMaiInfor){
    	    String temp = "";
    	    int length = GMaiInfor.length;
    	    if (length == 2){
    	    	temp = GMaiInfor[0] + "\n" + GMaiInfor[1] + "\n";
    	        FileOutputStream f_writer = null;
    	        try {
    	        	f_writer = openFileOutput(Constants.GMaiInforFile, Context.MODE_PRIVATE);
			
    	        } catch (FileNotFoundException e1) {
				     // TODO Auto-generated catch block
				    e1.printStackTrace();
			    }
			
    	        if (f_writer != null){
    	        	try {
					    f_writer.write(temp.getBytes());
				    } catch (IOException e) {
				         // TODO Auto-generated catch block
					     e.printStackTrace();
		            }
    	        }
    	    
    	        try {
				   f_writer.close();
			    } catch (IOException e) {
				    // TODO Auto-generated catch block
				    e.printStackTrace();
			   } 
         }
    }

    protected boolean get_GmailAccountInfor(String Gmail[]) throws IOException 
    {
  		String str = null;
  	    String tempStr = null; 	
  	    File file = getBaseContext().getFileStreamPath(Constants.GMaiInforFile);
		if  (file.exists()){	
 			FileInputStream fs = openFileInput(Constants.GMaiInforFile);
		
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

    protected void cleanDir(String dirPath){
    	
        File directory = new File(dirPath);
        
          // Get all files in directory
        	File[] files = directory.listFiles();

        	if ((files != null) && (files.length > 0)){
                // Delete all files
        		for (File file : files) {
        			// Delete each file
        			file.delete();
        		}
        		cleanDir(dirPath);
        	}
        	
        }
   
    private boolean checkNetwork(){
		 boolean network = true;
		 if  (!isNetworkAvailable() ){
		    for (int i = 0; i < 10000000; i++){
		    	// delayed time prior checking the network availability
		    }
	        if  (!isNetworkAvailable() ){
	        	network = false;
	    	    AlertDialog.Builder alertbox = new AlertDialog.Builder(FirstSetUp_Base.this);
				alertbox.setTitle("   No network is available");
				  // alertbox.setMessage("Please bring up the network and retry");
				alertbox.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0,int arg1) {
								finish();
					}
				});
				alertbox.show();  
		   }
	   }
	   return network;
   }

	private boolean isNetworkAvailable() {
		ConnectivityManager connMgr
				= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		final NetworkInfo wifi =
				connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		final NetworkInfo mobile =
				connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);


		if( wifi.isAvailable() && wifi.getDetailedState() == NetworkInfo.DetailedState.CONNECTED){
			return true;
		}
		else if( mobile.isAvailable() && mobile.getDetailedState() == NetworkInfo.DetailedState.CONNECTED ){
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	public  boolean hasPermissions(Context context, String[] permissions) {
		PackageManager pm = getPackageManager();
		for (String permission : permissions) {
			if (pm.checkPermission (permission,getPackageName() ) != PackageManager.PERMISSION_GRANTED) {
				return false;
			}
		}
		return true;
	}

} /* End of SetUp */
