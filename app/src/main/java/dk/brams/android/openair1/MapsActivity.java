package dk.brams.android.openair1;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    public static final String TAG = "TBR:";

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


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng KRNO = parseCoordinateString("39:29.9 N 119:46.1W");


        mMap.addMarker(new MarkerOptions().position(KRNO).title("KRNO Airport"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(KRNO));

        String[] openAirCommands = {
            "V X=39:29.9 N 119:46.1W",
            "DA 10,270,290",
            "DA 7,290,320",
            "DA 10,320,200",
            "V D=-",
            "DA 5,200,270",
            "DB 55:15:34 N 014:24:53 E, 54:55:00 N 014:21:27 E"};

        String sampleCommand = "DA 10,270,290";

        String sampleCoord="55:15:34 N 014:24:53 E";

        for (String cmd: openAirCommands) {
            parseCommand(cmd);
        }

    }


    /**
     * Parse coordinates in the format used in the OpenAir language and return a LatLng object.
     *
     * Uses a Regular Expression to parse the components of a coordinate string, convert into double
     * and create a LatLng object that can be used in Google Maps.
     *
     * @param coordString for example "39:29.9 N 119:46.1W" or "39 : 29:9 N 119:46 :1W" for KRNO airport
     * @return LatLng object
     */
    public LatLng parseCoordinateString(String coordString) {
        String pattern = "([\\d]+) *: *([\\d]+) *[:.] *([\\d])+ *([NS]) *([\\d]+) *: *([\\d]+) *[:.] *([\\d]+) *([EW])";


        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(coordString);
        if (m.find()) {
            // Given a string like "39:29.9 N 119:46.1W" we will get 8 matches:
            // "39", "29", "9", "N" and "119", "46", "1", "W" starting at index 1

            Double lat, lon;
            lat = Double.parseDouble(m.group(1)) + Double.parseDouble(m.group(2)) / 60 + Double.parseDouble(m.group(3)) / 3600;
            lon = Double.parseDouble(m.group(5)) + Double.parseDouble(m.group(6)) / 60 + Double.parseDouble(m.group(7)) / 3600;

            if (m.group(4).toUpperCase().equals("S")) lat = lat * -1;
            if (m.group(8).toUpperCase().equals("W")) lon = lon * -1;

            return new LatLng(lat, lon);

        } else {
            Log.e(TAG, "parseCoordinateString: Cannot parse coordinate: " + coordString);
            return null;
        }
    }


    public void parseCommand(String cmd) {
        String pattern = "(\\w+)([\\w\\d\\s\\:\\.\\=\\+\\-\\,]*)";



        Pattern r = Pattern.compile(pattern);

        Matcher m = r.matcher(cmd);

        if (m.find()) {
            // We should have the command in first group and the arguments etc in the second

            String command = m.group(1).toUpperCase();
            String rest = m.group(2).trim().toUpperCase();

            Log.d(TAG, String.format("%s -> %s, %s", cmd, command, rest));

            switch (command) {
                case "V":
                    // Variable Assignment Command
                    String assignPattern="([\\w]+)\\s*=([\\s\\w\\d\\:\\.\\+\\-]*)";
                    r = Pattern.compile(assignPattern);
                    m = r.matcher(rest);
                    if (m.find()){

                        if (m.group(1).equals("D")) {
                            Log.d(TAG, "parseCommand: "+String.format("Direction command, sign: %s", m.group(2)));
                        } else {
                            // Position variable
                            Log.d(TAG, "parseCommand: "+String.format("Variable assignment. Variable: %s, rest: %s", m.group(1), m.group(2)));

                            LatLng pos = parseCoordinateString(rest);
                            if (pos != null) {
                                Log.d(TAG, "parseCommand: pos: "+pos);
                            } else {
                                Log.d(TAG, "parseCommand: Might be some other kind of assignment...");
                            }
                        }

                    } else {
                        Log.d(TAG, "parseCommand: Variable parsing error");
                    }

                    break;


                case "DA":
                    // Draw Arc Command
                    String directionPattern = "([\\d]+)\\s*\\,\\s*([\\d]+)\\s*\\,\\s*([\\d]+)";
                    r = Pattern.compile(directionPattern);
                    m = r.matcher(rest);

                    if (m.find()) {
                        int radius = Integer.parseInt(m.group(1));
                        int fromDeg = Integer.parseInt(m.group(2));
                        int toDeg = Integer.parseInt(m.group(3));
                        Log.d(TAG, String.format("Radius: %d, from: %d to %d", radius, fromDeg, toDeg));
                    } else {
                            Log.d(TAG, "parseCommand: Draw arc parameters not recognized");
                    }
                    break;

                case "DB":
                    Log.d(TAG, "parseCommand: Draw between command");
                    String betweenPattern="([\\d\\:\\. \\w]+)\\s*,\\s*([\\d\\:\\. \\w]+)";
                    r = Pattern.compile(betweenPattern);
                    m = r.matcher(rest);

                    if (m.find()) {
                        LatLng pos1, pos2;
                        pos1 = parseCoordinateString(m.group(1));
                        pos2 = parseCoordinateString(m.group(2));
                        Log.d(TAG, "parseCommand: Got two coordinates : "+pos1+" and "+pos2);
                    } else {
                        Log.e(TAG, "parseCommand: Problem parsing draw between arguments" );
                    }
                    break;

                 default:
                    Log.d(TAG, "parseCommand: not recognized");
                    break;
            }




        } else {
            Log.e(TAG, "parseCommand: Cannot parse command: " + cmd);
        }



    }
}
