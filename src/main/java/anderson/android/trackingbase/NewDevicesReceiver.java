package anderson.android.trackingbase;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import anderson.android.device_one.Device_One_Constants;
import anderson.android.device_one.Device_One_Global;
import anderson.android.device_two.Device_Two_Constants;
import anderson.android.device_two.Device_Two_Global;

public class NewDevicesReceiver extends BroadcastReceiver {

	private String formatedNumberStr = "";
	private String formatedCallStr = "";

	final String TAG = "NewDevicesReceiver";
	String msg_from;
	Context m_context;

	public void onReceive(Context context, Intent intent) {
		// Get extra data included in the Intent
		String Broadcast_Command = intent.getAction();
		if (Broadcast_Command.contentEquals(Constants.NEW_DEVICE_ADDED)){
			Utilities.putToast("New device registering ...", 50, 200);
			String deviceName = intent.getStringExtra(Constants.SENDER_NAME);
			int size = Global.nameAccounts.size();
			for(int indx = 0; indx < size; indx++ ) {
				if (Global.nameAccounts.get(indx).contentEquals(deviceName))
					switch (indx) {
                        case 0:
                            Utilities.LoadLocation(Device_One_Global.Last_Thirty_RealAddress, Device_One_Constants.ClientOne_last_30_addresses, context);
                            Utilities.LoadLocation(Device_One_Global.Last_Thirty, Device_One_Constants.ClientOne_last_30_ll, context);
                            break;
                        case 1:
                            Utilities.LoadLocation(Device_Two_Global.Last_Thirty_RealAddress, Device_Two_Constants.ClientTwo_last_30_addresses, context);
                            Utilities.LoadLocation(Device_Two_Global.Last_Thirty, Device_Two_Constants.ClientTwo_last_30_ll, context);
                            break;
                       /* case 2:
                            Utilities.LoadLocation(Device_Three_Global.Last_Thirty_RealAddress, Device_Three_Constants.ClientThree_last_30_addresses, context);
                            Utilities.LoadLocation(Device_Three_Global.Last_Thirty, Device_Three_Constants.ClientThree_last_30_ll, context);
                            break;
                        case 3:
                            Utilities.LoadLocation(Device_Four_Global.Last_Thirty_RealAddress, Device_Four_Constants.ClientFour_last_30_addresses, context);
                            Utilities.LoadLocation(Device_Four_Global.Last_Thirty, Device_Four_Constants.ClientFour_last_30_ll, context);
                            break;
                        case 4:
                            Utilities.LoadLocation(Device_Five_Global.Last_Thirty_RealAddress, Device_Five_Constants.ClientFive_last_30_addresses, context);
                            Utilities.LoadLocation(Device_Five_Global.Last_Thirty, Device_Five_Constants.ClientFive_last_30_ll, context);
                            break;
                        case 5:
                            Utilities.LoadLocation(Device_Six_Global.Last_Thirty_RealAddress, Device_Six_Constants.ClientSix_last_30_addresses, context);
                            Utilities.LoadLocation(Device_Six_Global.Last_Thirty, Device_Six_Constants.ClientSix_last_30_ll, context);
                            break;
                            */
                    }
			}
		}

	}

}