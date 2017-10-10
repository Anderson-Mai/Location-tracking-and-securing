package anderson.android.retrievelocation;

import android.util.Log;

import anderson.android.utilities.Constants_Client;
import anderson.android.utilities.Global_Client;
import anderson.android.utilities.Utilities_Client;

public class MailService_Client {
   // private String m_subject;
	private String timeStamp = "";
	private String XY_pos = "";
    private String pass = "";
    private String to = "";
	private String deviceName = "";
	private String safeDistanceFlag = "";

	MailService_Client(String m_TimeStamp, String m_XY_pos, String m_to, String m_deviceName,String m_safeDistanceFlag){
		XY_pos =  m_XY_pos;
		to = m_to;
		deviceName = m_deviceName;
		timeStamp = m_TimeStamp;
		safeDistanceFlag = m_safeDistanceFlag;
	}

public String sendEmail(){
	        Global_Client.sequence++;
	        String text = Constants_Client.SUBJECT_MASK_GPS + Constants_Client.POUND + timeStamp + Constants_Client.POUND
					+ XY_pos + Constants_Client.POUND + deviceName + Constants_Client.POUND + safeDistanceFlag;
	        Utilities_Client.sendText(text, to);
	        Log.i("---Sent:  ", text);
            return text;
    }
}