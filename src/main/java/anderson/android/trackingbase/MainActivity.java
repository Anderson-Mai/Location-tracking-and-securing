package anderson.android.trackingbase;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private Button [] m_DeviceButton = new Button[5];
    private LinearLayout[] m_DeviceLL = new LinearLayout[5];
    private TextView[] m_DeviceText = new TextView[5];
    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainpage);

        TelephonyManager tMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number();
        if (mPhoneNumber == null ){
            String announce_str = "Can not detect the SIM/ Phone number";

            toast =  Toast.makeText(getApplicationContext(),announce_str, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 240);
            toast.show();
            return;
        }
        Global.mPhoneNumber = mPhoneNumber;


        m_DeviceLL[0] = (LinearLayout)findViewById(R.id.client_one);
        m_DeviceLL[1] = (LinearLayout)findViewById(R.id.client_two);
        m_DeviceLL[2] = (LinearLayout)findViewById(R.id.client_three);
        m_DeviceLL[3] = (LinearLayout)findViewById(R.id.client_four);
        m_DeviceLL[4] = (LinearLayout)findViewById(R.id.client_five);
       // m_DeviceLL[0].setVisibility(TextView.VISIBLE);

        m_DeviceButton[0] = (Button)findViewById(R.id.client_one_button);
        m_DeviceButton[1] = (Button)findViewById(R.id.client_two_button);
        m_DeviceButton[2] = (Button)findViewById(R.id.client_three_button);
        m_DeviceButton[3] = (Button)findViewById(R.id.client_four_button);
        m_DeviceButton[4] = (Button)findViewById(R.id.client_five_button);

        m_DeviceText[0] = (TextView)findViewById(R.id.tracking_client_one);
        m_DeviceText[1] = (TextView)findViewById(R.id.tracking_client_two);
        m_DeviceText[2] = (TextView)findViewById(R.id.tracking_client_three);
        m_DeviceText[3] = (TextView)findViewById(R.id.tracking_client_four);
        m_DeviceText[4] = (TextView)findViewById(R.id.tracking_client_five);

    }

}
