package anderson.android.retrievelocation;

import android.os.AsyncTask;
import android.widget.Toast;

import anderson.android.utilities.Global_Client;

public class SendingXYPos_Client extends AsyncTask<String, Void, String> {

  	      @Override
  	      protected String doInBackground(String... params) {
  	    	  
  	    	  MailService_Client mMailService = new MailService_Client( params[0], params[1], params[2],params[3],params[4]);
			  return mMailService.sendEmail();
  	      }      

  	      @Override
  	      protected void onPostExecute(String  result) {
  	    	Toast.makeText(Global_Client.my_context,
					"Sent location #" + String.valueOf(Global_Client.sequence),
					Toast.LENGTH_LONG).show();
  	    	Global_Client.waiting = false;
  	      }

  	      @Override
  	      protected void onPreExecute() {
  	    	/*Toast.makeText(Global.my_context,
					"Start sending email ...",
					Toast.LENGTH_LONG).show();
  	    */
  	    	  Global_Client.waiting = true;
  	      }

  	      @Override
  	      protected void onProgressUpdate(Void... values) {
  	      }
  }