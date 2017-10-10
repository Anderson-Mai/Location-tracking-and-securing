package anderson.android.device_two;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import anderson.android.device_one.Device_One_Global;
import anderson.android.trackingbase.Constants;
import anderson.android.trackingbase.Global;
import anderson.android.trackingbase.R;
import anderson.android.trackingbase.Utilities;

public class SettingPageTwo extends Activity {
	private Button m_closeButton = null;
	private Button m_nextButton  = null;
	private Button m_resetButton = null;
	private TextView m_addressDisplay = null;
	private EditText m_securedDistance  = null;
	private TextView m_nameDisplayer = null;
	private String EXPIRE_TIME= "1000000";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_page);

		final Context m_context = getApplicationContext();
		m_addressDisplay =  (TextView)findViewById(R.id.address_display);
		m_addressDisplay.setText(Device_Two_Global.Last_Thirty_RealAddress.get(0));

		m_nameDisplayer =  (TextView)findViewById(R.id.name_display);
		m_nameDisplayer.setText(Global.nameAccounts.get(1));
		String colorCode = Utilities.getColorForLetter(Global.nameAccounts.get(1).substring(0,1));
		m_nameDisplayer.setBackgroundColor(Color.parseColor(colorCode));

		m_closeButton = (Button) findViewById(R.id.S_Done);
		m_closeButton .setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				finish();
			}
		});

		m_securedDistance = (EditText)findViewById(R.id.securedDistance);

		m_nextButton = (Button) findViewById(R.id.Button_Next);
		m_nextButton.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				// check the distance every time the location is changed

				String distanceStr = m_securedDistance.getText().toString().trim();
				if (distanceStr.isEmpty()){
					distanceStr = "0";
					Log.i("Distance: ", "Preset distance is 0");
					Utilities.putToast("Preset distance is 0.\n No secured distance will be checked.", 50, 100);
					return;
				}
				else {
					if (Double.parseDouble(distanceStr) < 10){
						Utilities.putToast("Secured distance must be longer than 10 metters", 50, 100);
						return;
					}
					Utilities.putToast( "Sent to :  " +  Global.phoneAccounts.get(0), 50, 100);
					// String text = Constants.FENCE_REQUEST + Constants.POUND + Global.nameAccounts.get(0) +  Constants.POUND +
					String text = Constants.SECURED_DISTANCE_REQUEST + Constants.POUND + Global.nameAccounts.get(1) +  Constants.POUND +
							distanceStr +  Constants.POUND + Device_Two_Global.Last_Thirty.get(0) + Constants.POUND + EXPIRE_TIME;
					Utilities.sendText(text, Global.phoneAccounts.get(0));
					Global.presetDistance[1] = Double.parseDouble(distanceStr);
					Global.securedPoint[1] = Device_Two_Global.Last_Thirty.get(0);
					Log.i(" TAG: ",text );
					finish();
				}

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

