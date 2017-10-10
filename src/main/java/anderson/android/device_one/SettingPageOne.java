package anderson.android.device_one;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import anderson.android.trackingbase.Constants;
import anderson.android.trackingbase.Global;
import anderson.android.trackingbase.R;
import anderson.android.trackingbase.Utilities;

public class SettingPageOne extends Activity {
private Button m_closeButton = null;
private Button m_nextButton  = null;
private Button m_resetButton = null;
private TextView m_addressDisplay = null;
private EditText m_securedDistance  = null;
private TextView m_nameDisplayer = null;
private EditText m_recordLength = null;
private String EXPIRE_TIME= "1000000";

     @Override
     public void onCreate(Bundle savedInstanceState) {  
         super.onCreate(savedInstanceState); 
         setContentView(R.layout.settings_page); 
         
         final Context m_context = getApplicationContext();
		 m_addressDisplay =  (TextView)findViewById(R.id.address_display);
		 if (!Device_One_Global.Last_Thirty_RealAddress.isEmpty()) {
			 m_addressDisplay.setText(Device_One_Global.Last_Thirty_RealAddress.get(0));
		 }
		 else {
			 m_addressDisplay.setText("Location is not ready ...");

		 }
		 m_nameDisplayer =  (TextView)findViewById(R.id.name_display);
		 m_nameDisplayer.setText(Global.nameAccounts.get(0));
		 String colorCode = Utilities.getColorForLetter(Global.nameAccounts.get(0).substring(0,1));
		 m_nameDisplayer.setBackgroundColor(Color.parseColor(colorCode));

		 m_closeButton = (Button) findViewById(R.id.S_Done);
		 m_closeButton .setOnClickListener(new Button.OnClickListener(){
	    	 	 public void onClick(View v) {
	    	 		finish();
	    	 	 }
		 });

		 m_securedDistance = (EditText)findViewById(R.id.securedDistance);
		 m_recordLength = (EditText)findViewById(R.id.recordLength);

		 m_nextButton = (Button) findViewById(R.id.Button_Next);
		 m_nextButton.setOnClickListener(new Button.OnClickListener(){
	    	 	 public void onClick(View v) {
	    	 		// check the distance every time the location is changed

	    	 		String distanceStr = m_securedDistance.getText().toString().trim();
					String historyLen =  m_recordLength.getText().toString().trim();

					 if (Device_One_Global.Last_Thirty.size() < 2){
						 Utilities.putToast("Warming up period. Please try again in a few minutes", 50, 100);
						 return;
					 }

	    	 		if ( distanceStr.isEmpty() || (Double.parseDouble(distanceStr) < 10)){
						distanceStr = "0";
	    	 			Log.i("Distance: ", "Preset distance is 0");
	    	 			Utilities.putToast("Preset distance is 0.\n GeoFence is not activated.", 50, 100);
	    	 		 }
	    	 		 else {
						  String [] Late_Long = Device_One_Global.Last_Thirty.get(0).split(Constants.AT);
						  Utilities.putToast( "Sent to :  " +  Global.phoneAccounts.get(0), 50, 100);
						  Global.GEOFENCE_REQUEST_text = Constants.GEOFENCE_REQUEST + Constants.POUND + Global.nameAccounts.get(0) +  Constants.POUND +
								distanceStr +  Constants.POUND + Late_Long[0] + Constants.POUND + Late_Long[1] + Constants.POUND + EXPIRE_TIME;
						  Utilities.sendText(Global.GEOFENCE_REQUEST_text, Global.phoneAccounts.get(0));
						  Global.presetDistance[0] = Double.parseDouble(distanceStr);
						  Global.securedPoint[0] = Device_One_Global.Last_Thirty.get(0);
						  Log.i(" TAG: ",Global.GEOFENCE_REQUEST_text );
					}

					 int m_hisLen = Integer.parseInt(historyLen);
					 if (m_hisLen > 1 ){
							Device_One_Global.historyLength = m_hisLen;
							Utilities.adjustHistoryList(Device_One_Global.historyLength, Device_One_Global.Last_Thirty, Device_One_Global.Last_Thirty_RealAddress);
					 }
					 finish();
	    	 	 }
		 });   	
		 
		  m_resetButton = (Button) findViewById(R.id.Reset_Button);
			 m_resetButton .setOnClickListener(new Button.OnClickListener(){
		    	 	 public void onClick(View v) {
		    	 	//	Global.checkMailAlarmManager.cancel(Global.checkMailIntent);
		    	 		m_securedDistance.setText("");
		    	 	 }
			 });   
     }   
     
     @Override
     protected void onResume() {
  		super.onResume();
     }

     @Override
     protected void onDestroy() {
    	 // Unregister since the activity is about to be closed.
  		super.onDestroy();
      }
}   

