package anderson.android.trackingbase;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import anderson.android.device_one.Device_One_Global;
import anderson.android.device_two.Device_Two_Global;




/**
 * Created by Anderson_Mai on 10/8/2017.
 */

public class Display_All extends FragmentActivity implements OnMapReadyCallback {
    private List<Marker> markers;

   private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.m_map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap m_map) {
        Marker localmarker = null;
        if (Global.phoneAccounts.size()> 0) {
            markers = new ArrayList<Marker>();
            GoogleMap t_map = m_map;
            String [] late_long = null;
            String addrs = "";
            for (int i = 0; i < Global.phoneAccounts.size(); i++) {
                Log.i("TESTING", "LOOP:  " + String.valueOf(i));
                switch (i) {
                    case 0:
                        addrs = Device_One_Global.Last_Thirty_RealAddress.get(0);
                        late_long = Device_One_Global.Last_Thirty.get(0).split(Constants.AT);
                        break;
                    case 1:
                        addrs = Device_Two_Global.Last_Thirty_RealAddress.get(0);
                        late_long = Device_One_Global.Last_Thirty.get(0).split(Constants.AT);
                        break;
                    case 2:

                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    case 5:
                        break;

                }

                Log.i("TESTING", "latitude = " + late_long[0] + " longitude = " + late_long[1]);
                localmarker = t_map.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(late_long[0]), Double.parseDouble(late_long[1])))
                        .title(Global.nameAccounts.get(i))
                        .snippet(addrs)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        .alpha(0.7f)
                );
                markers.add(localmarker);

            }
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Marker m : markers) {
                builder.include(m.getPosition());
            }
            LatLngBounds bounds = builder.build();
            int padding = 0; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            t_map.moveCamera(cu);
        }

    }
}
