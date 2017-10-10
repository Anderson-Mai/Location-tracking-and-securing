package anderson.android.trackingbase;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.IBinder;

/* Check for new mails. Concatenate parts of a file
 * Author : Anderson Mai. Created: 06/20/2014
 */

public class FlashingIntent extends IntentService {
    public NotificationManager mNotificationManager;    
		public FlashingIntent() {
		super("FlashingIntend");
	}
		 
    @Override
    public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
   }  
   
   @Override
   public void onDestroy() {
			super.onDestroy();
   }
		      
   @Override
   protected void onHandleIntent(Intent intent) {
		    // Context m_contect = getApplicationContext();
	         Utilities.display_Notification(R.drawable.blue_circle);
			 try {
				Thread.sleep(1000);
		 	 } catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		   
		     Utilities.display_Notification(R.drawable.tracking);
	}
}