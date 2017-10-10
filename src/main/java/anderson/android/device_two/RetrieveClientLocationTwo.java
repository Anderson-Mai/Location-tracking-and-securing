package anderson.android.device_two;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.FileNotFoundException;

import anderson.android.trackingbase.Constants;
import anderson.android.trackingbase.Global;
import anderson.android.trackingbase.R;
import anderson.android.trackingbase.Utilities;

public class RetrieveClientLocationTwo extends Activity {
private ListView mDisplayer = null;
private TextView m_current_client_location = null; 
private ArrayAdapter<String> list_adapter = null;
protected static NotificationManager mNotificationManager = null;
PendingIntent contentIntent  = null;
private final static int Notify_ID = 110;
private	String deviceName = "";

     @Override 
     public void onCreate(Bundle savedInstanceState) {  
         super.onCreate(savedInstanceState); 
         setContentView(R.layout.options_page);

         final Context m_context = getApplicationContext();     
         m_current_client_location = (TextView)findViewById(R.id.ClientCurrentLocation);
         deviceName = Global.nameAccounts.get(1);
		 String firstLetter = deviceName.substring(0,1);
		 Log.i("---COLOR", firstLetter );
		 String colorCode = Utilities.getColorForLetter(firstLetter);

		 m_current_client_location.setText(Global.nameAccounts.get(1));
		 m_current_client_location.setBackgroundColor(Color.parseColor(colorCode));

         m_current_client_location.setOnClickListener(new Button.OnClickListener(){
    	 	 public void onClick(View v) {
    	 		if (!m_current_client_location.getText().toString().trim().
    	 				              contains("*** Client is not connected ***")){
    	 		    displayLocationOnMap(0);
    	 		}
    	 	 }
	     });

	    mDisplayer = (ListView) findViewById(R.id.Displayer);
	    mDisplayer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?>parent, View view, int position, long id){
				displayLocationOnMap(position);
			}
	    });
      }   
     
      private BroadcastReceiver mMessageReceiverTwo = new BroadcastReceiver() {
   	  @Override
		  public void onReceive(Context context, Intent intent) {
			// Get extra data included in the Intent
			  String Broadcast_Command = intent.getAction();
			  if (Broadcast_Command.contentEquals(Device_Two_Constants.DEVICE_TWO_ADAPTER_UPDATED)){
				 list_adapter.notifyDataSetChanged();
				  Utilities.putToast("Latest location is changed ...", 0, 200);
				  Log.i("--- BroadCast  :  ", "DEVICE_TWO_ADAPTER_UPDATED TOUCHED" );
				 //m_current_client_location.setText(Device_One_Global.Last_Thirty_RealAddress.get(0));
			  }
		  }
   	};

	private BroadcastReceiver mClosedReceiverTwo = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Get extra data included in the Intent
			String Broadcast_Command = intent.getAction();
			if (Broadcast_Command.contentEquals(Constants.CLIENT_TWO_CLOSED_ACTION)){
				finish();
				Utilities.putToast(deviceName  + " closed its connection.", 0, 200);
			}
		}
	};

     @Override
     protected void onResume() {
  		super.onResume();
  		// Read the back up file to both last_Thirty_RealAddress

		 Utilities.LoadLocation(Device_Two_Global.Last_Thirty_RealAddress, Device_Two_Constants.ClientTwo_last_30_addresses, this);
		 Log.i("--- onResume  :  ", "TWO" );
		 Utilities.LoadLocation(Device_Two_Global.Last_Thirty, Device_Two_Constants.ClientTwo_last_30_ll, this);



  		 list_adapter = new ArrayAdapter<String>(this,
		        	R.layout.simple_list_item_5, Device_Two_Global.Last_Thirty_RealAddress );

  		 mDisplayer.setAdapter(list_adapter);
  		 registerReceiver(mMessageReceiverTwo,
	    	      new IntentFilter(Device_Two_Constants.DEVICE_TWO_ADAPTER_UPDATED));
		 registerReceiver(mClosedReceiverTwo,
				 new IntentFilter(Constants.CLIENT_TWO_CLOSED_ACTION));

      }
  // Remove all elements created for the table
     @Override protected void onPause(){

    	 super.onPause();
    	 unregisterReceiver(mMessageReceiverTwo);
		 unregisterReceiver(mClosedReceiverTwo);
    	 if (!Device_Two_Global.Last_Thirty_RealAddress.isEmpty()){
	    	 try {
				Utilities.BackUpLocations(30, Device_Two_Global.Last_Thirty_RealAddress, Device_Two_Constants.ClientTwo_last_30_addresses, this);
				Utilities.BackUpLocations(30, Device_Two_Global.Last_Thirty, Device_Two_Constants.ClientTwo_last_30_ll, this);
	    	 } catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				 Log.i("--- LAST THIRTY  :  ", "FileNotFoundException 1" );

			}
    	 }
     }	

     @Override
     protected void onDestroy() {
  		super.onDestroy();
  		 // Unregister since the activity is about to be closed.
      }  

    public void displayLocationOnMap(int pos){
    	 String selectLocData = Device_Two_Global.Last_Thirty.get(pos);
		 Log.i("--- LAST THIRTY  :  ", selectLocData );

		 String [] splitedFields = selectLocData.split(Constants.AT);
		 String selectAddressData = Device_Two_Global.Last_Thirty_RealAddress.get(pos);
		 Log.i("---REAL ADDRESS  :  ", selectAddressData );
		 String [] splitedAddress = selectAddressData.split("\n");

		 String m_longitude = splitedFields[1];
		 String m_address = splitedAddress[1];
         String m_latitude = splitedFields[0];
		 
		 m_address.replace(" ", "+");
	     Uri gmmIntentUri = Uri.parse("geo:"+ m_latitude + "," + m_longitude + "?q=" + m_address);
	     Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
	     mapIntent.setPackage("com.google.android.apps.maps");
		 if (mapIntent.resolveActivity(getPackageManager()) != null) {
			startActivity(mapIntent);
		 }
     }

 
}   
