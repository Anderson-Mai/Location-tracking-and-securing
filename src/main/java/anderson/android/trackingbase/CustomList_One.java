package anderson.android.trackingbase;

/**
 * Created by Anderson_Mai on 1/28/2016.
 */

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import java.util.ArrayList;

public class CustomList_One extends ArrayAdapter<String> {

    customButtonListener customListner;
    ViewHolder viewHolder;
    boolean reset_flag = true;

    public interface customButtonListener {
        public void onButtonOneClickListener(int position);
        public void onButtonTwoClickListener(int position);
        public void onButtonThreeClickListener(int position);
        public void onButtonFourClickListener(int position);
        public void onButtonFiveClickListener(int position);
        public void onButtonZeroClickListener(int position);
    }

    public void setCustomButtonListner(customButtonListener listener) {
        this.customListner = listener;
    }

    private ArrayList<String> data = new ArrayList<String>();
    private Context context;

    public CustomList_One(Context context, ArrayList<String> dataItem) {
        super(context, R.layout.list_single_one, dataItem);
        this.context = context;
        this.data = dataItem;

    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater =  LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.list_single_one, null);
            viewHolder = new ViewHolder();

            viewHolder.button_Del = (Button) convertView
                    .findViewById(R.id.deleteButton);

            viewHolder.button_Device = (Button) convertView
                    .findViewById(R.id.deviceName);

            viewHolder.button_Setup = (Button) convertView
                    .findViewById(R.id.setupButton);

            viewHolder.button_Location = (Button) convertView
                    .findViewById(R.id.locationButton);

            viewHolder.button_secure_Location = (Button) convertView
                    .findViewById(R.id.secureDistanceButton);

            if (Global.presetDistance[position] > 0.0){
                viewHolder.button_secure_Location.setBackgroundResource(R.drawable.secured_icon);
            }
            else {
                viewHolder.button_secure_Location.setBackgroundResource(R.drawable.unsecured_icon);
            }
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final String device_name  = getItem(position);
        String firstLetter = device_name.substring(0,1);
        Log.i("---COLOR", firstLetter );
        String colorCode = Utilities.getColorForLetter(firstLetter);

        viewHolder.button_Device.setText(device_name);
        viewHolder.button_Device.setBackgroundColor(Color.parseColor(colorCode));
        //viewHolder.button_Device.setBackgroundColor(Color.CYAN);
        viewHolder.button_Device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customListner != null) {
                    customListner.onButtonOneClickListener(position);
                }

            }
        });

        viewHolder.button_Device.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View Arg0) {
                if (customListner != null) {
                    customListner.onButtonZeroClickListener(position);
                }
                return true;
            }
        });
        viewHolder.button_Device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customListner != null) {
                    customListner.onButtonOneClickListener(position);
                }

            }
        });
        viewHolder.button_Location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customListner != null) {
                    customListner.onButtonTwoClickListener(position);
                }

            }
        });
        viewHolder.button_Setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (customListner != null) {
                        customListner.onButtonThreeClickListener(position);
                    }
            }

        });

        viewHolder.button_secure_Location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customListner != null) {
                    customListner.onButtonFourClickListener(position);
                }
                if (Global.presetDistance[position]> 0.0){
                     viewHolder.button_secure_Location.setBackgroundResource(R.drawable.secured_icon);
                }
                else {
                    viewHolder.button_secure_Location.setBackgroundResource(R.drawable.unsecured_icon);
                }
            }

        });

        viewHolder.button_Del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customListner != null) {
                    customListner.onButtonFiveClickListener(position);
                }
            }

        });
        return convertView;
    }

    public class ViewHolder {
        Button button_Device;
        Button button_Del;
        Button button_Setup;
        Button button_Location;
        Button button_secure_Location;
    }

}