package anderson.android.trackingbase;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class KickOffClientConnection extends Activity {
	private int ret_val = 0;
	private Button mContactSubmitter;
	private Button mHome;
	private Button mAddButton;
	private Button mResetButton;
	private EditText mPhoneNumber;

	private LinearLayout mListView;

	private EditText mNameEntry = null;
	private Context mContext;
	private Button mListMenu;
	public static final String CONTACT_NUMBER = "contact_number";

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kickoff_client);

    	mContext = this;
    	mPhoneNumber = (EditText)findViewById(R.id.client_number);
		
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(mPhoneNumber, InputMethodManager.SHOW_IMPLICIT);
		
		mNameEntry = (EditText) findViewById(R.id.NameEntry);
		mNameEntry.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				mNameEntry.setText("");
				return true;
			}
		});
		mNameEntry.requestFocus();


       // mGmailInbox = (ListView) findViewById(R.id.gmailContactInbox);
        mContactSubmitter = (Button)findViewById(R.id.submitButton);
        mContactSubmitter.setVisibility(View.GONE);
        mHome =  (Button)findViewById(R.id.button_home);
		mHome.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
                finish();
			}
		});
       
        mAddButton = (Button) findViewById(R.id.button_add);

		mAddButton.setOnClickListener(new Button.OnClickListener(){
   	 	 public void onClick(View v) {
   	 	
	   	 	    /* InputMethodManager imm = (InputMethodManager)
	   	 		 getSystemService(Context.INPUT_METHOD_SERVICE);
	   	 		 imm.hideSoftInputFromInputMethod(null, DEFAULT_KEYS_DISABLE);
	   	 		 imm.hideSoftInputFromWindow(mYourMsg.getWindowToken(), 0);
	   	 		 */
   	 		     String t_NameStr = mNameEntry.getText().toString().trim();
			     if (t_NameStr.isEmpty()){
					 t_NameStr = Constants.anonymous;
				 }
				 String	 t_PhoneStr = mPhoneNumber.getText().toString().trim();
			     for ( int i = 0; i < Global.phoneAccounts.size(); i++){
					 if (Global.phoneAccounts.get(i).contains(t_PhoneStr)){
						 Utilities.putToast(t_PhoneStr + " is registered", 20, 200);
						 return;
					 }
				 }

			 for ( int i = 0; i < Global.nameAccounts.size(); i++){
				 if (Global.nameAccounts.get(i).contains(t_NameStr)){
					 Utilities.putToast(t_NameStr + " is registered", 20, 200);
					 return;
				 }
			 }
			    // t_PhoneStr = Utilities.uniformNumberFormat(Utilities.getDigitsOnly(t_PhoneStr));
			     String outMsg = Constants.KICKUP_REGISTER_ACTION + Constants.POUND + t_PhoneStr + Constants.POUND + t_NameStr ;
			     Utilities.sendText(outMsg, t_PhoneStr);

	   	 	 }
   	 	 
        });
	}
   
	 @Override
		protected void onResume() {
			super.onResume();
	 }
 
	 protected void onPause() {
	        super.onPause();

	    }
	}
		