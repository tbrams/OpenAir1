package dk.brams.android.openair1;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "TBR:Main2";

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        OpenAirParser openAirParser = new OpenAirParser(getApplicationContext(), mMap);

        // Focus the map in Reno, Nevada using the parse coordinate utility from OpenAirParser
        LatLng locKRNO = openAirParser.parseCoordinateString("39:29.9 N 119:46.1W");
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locKRNO, 8));

        // Get the sample input file, and display the processed content on the map
        OpenAirFile openAirFile = new OpenAirFile();
        openAirParser.parseCommands(openAirFile.getCommands());
    }

}
