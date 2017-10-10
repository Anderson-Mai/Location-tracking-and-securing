package anderson.android.trackingbase;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class SelectAccount extends Activity {
	private String GmailAccount = "";
    private ArrayList<String> mGmailAccountList = new ArrayList<String>();
	private ListView mAccountList;
	private Button m_button_back;
	protected ArrayAdapter<String> Gmails_adapter;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_list);
        
        mAccountList = (ListView) findViewById(R.id.gmailAccountList);
        m_button_back = (Button)findViewById(R.id.accountButtonBack);	
       
        Intent mIntent = getIntent();
        mGmailAccountList= mIntent.getStringArrayListExtra(String.valueOf(Constants.SELECT_ACCOUNT));
        Gmails_adapter = new ArrayAdapter<String>(this,
	        	R.layout.simple_list_item_4, mGmailAccountList);
        mAccountList.setAdapter(Gmails_adapter);
        
           	
	    mAccountList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?>parent, View view, int position, long id){
					GmailAccount = mGmailAccountList.get(position);		
					Intent data = new Intent();
					data.putExtra(String.valueOf(Constants.SELECT_ACCOUNT), position);
									    // Activity finished ok, return the data
					setResult(Constants.RESULT_OK, data);
					finish();
			   }	
	  });	  
			      
			    
       m_button_back.setOnClickListener(new Button.OnClickListener(){
      	 	 public void onClick(View v) {
      	 		 
      	 		Intent data = new Intent();
				data.putExtra(String.valueOf(Constants.SELECT_ACCOUNT), Constants.RESULT_NOT_OK);
								    // Activity finished ok, return the data
				setResult(Constants.RESULT_NOT_OK, data);
				finish();
      	 		finish();
      	 	 }
       });
           
        
	}

	
	 @Override
		protected void onResume() {
			super.onResume();
			
          
           
   }
	 
	 protected void onStop() {
	        super.onStop();
	        
	    }
}