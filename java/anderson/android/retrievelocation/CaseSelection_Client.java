package anderson.android.retrievelocation;

//import java.io.File;
//import java.io.FileInputStream;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import anderson.android.utilities.Constants_Client;

public class CaseSelection_Client extends Activity {
	private ListView mCaseList;
	private Button m_button_back;
	protected ArrayAdapter<String> CaseList_adapter;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cases_selection_client);
        
        String [] mCases = {
        		 "Normal case\nLocation will be updated each 90 secs",
        		 "Very High Sensitive case\nUpdate location each 20 secs",
        		 "High Sensitive case\nUpdate location each 30 secs",
        		 "Sensitive case\nUpdate location each 60 secs",
        		 "Low Sensitive case\nUpdate location each 300 secs "
        		 
        		
        };
        
        mCaseList = (ListView) findViewById(R.id.case_selection_list);
      
        CaseList_adapter = new ArrayAdapter<String>(this,
	        	R.layout.simple_list_item_8, mCases);
        mCaseList.setAdapter(CaseList_adapter);
        mCaseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?>parent, View view, int position, long id){
					switch(position){
					case 0 : Constants_Client.MINIMUM_TIME_BETWEEN_UPDATES = 90000;
						     break;
					case 1 : Constants_Client.MINIMUM_TIME_BETWEEN_UPDATES = 20000;
				             break;
					case 2 : Constants_Client.MINIMUM_TIME_BETWEEN_UPDATES = 30000;
		                     break;
					case 3 : Constants_Client.MINIMUM_TIME_BETWEEN_UPDATES = 60000;
                    		 break;
					case 4 : Constants_Client.MINIMUM_TIME_BETWEEN_UPDATES = 300000;
           		 			 break;
						
					}
					finish();
			   }	
	  });	  
			      
			    
     /*  m_button_back.setOnClickListener(new Button.OnClickListener(){
      	 	 public void onClick(View v) {
      	 		 
      	 		Intent data = new Intent();
				data.putExtra(String.valueOf(Constants.SELECT_ACCOUNT), Constants.RESULT_NOT_OK);
								    // Activity finished ok, return the data
				setResult(Constants.RESULT_NOT_OK, data);
				finish();
      	 		finish();
      	 	 }
       });
           
     */   
	}

	
	 @Override
		protected void onResume() {
			super.onResume();
			
          
           
   }
	 
	 protected void onStop() {
	        super.onStop();
	        
	    }
}