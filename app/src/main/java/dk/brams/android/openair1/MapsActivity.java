package dk.brams.android.openair1;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "TBR:";
    private static final int STEP_SIZE = 1;
    private static final float DEFAULT_LINE_WIDTH = 10f;
    private static final float SPECIAL_LINE_WIDTH= 25f;

    private GoogleMap mMap;
    private LatLng mCenter = null;
    private int mStep_direction = 1;
    private ArrayList<LatLng> mCoordList = new ArrayList<>();
    private int mOutlineColor;
    private float mOutlineWidth;

    private Marker mAirportMarker = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        mapFragment.getMapAsync(this);
    }


    public void addPosToCoordList(LatLng pos) {
        mCoordList.add(pos);
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


        mAirportMarker = mMap.addMarker(new MarkerOptions().position(KRNO).title("KRNO Airport"));


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(KRNO, 8));

        String[] openAirCommands = {

                "* Reno CTR",
                "AC C",
                "AN RENO-C",
                "AL SFC",
                "AH 8400 ft",
                "V X=39:29.9 N 119:46.1 W",
                "DC 5",
                "",

                "* Reno TMA xx",
                "AC C",
                "V X=39:29.9 N 119:46.1W",
                "DA 10,270,290",
                "DA 7,290,320",
                "DA 10,320,200",
                "V D=-",
                "DA 5,200,270",
                "",


                "AC R",
                "AN R-4806 W",
                "AL 0",
                "AH UNLIM",
                "DP 36:41:00 N 115:56:10 W    c29",
                "DP 37:06:00 N 115:56:10 W    c30",
                "DP 37:06:00 N 115:35:00 W    c31",
                "DP 37:16:55 N 115:35:00 W    c32",
                "DP 37:16:55 N 115:18:10 W    c34",
                "DP 36:38:08 N 115:18:10 W    c35",
                "DP 36:25:40 N 115:18:10 W    c36",
                "DP 36:25:40 N 115:23:20 W    c37",
                "DP 36:35:00 N 115:37:00 W    c38",
                "DP 36:35:00 N 115:53:00 W    c39",
                "DP 36:35:45 N 115:56:10 W    c40",
                "",

                "AC D",
                "AN NAS-FALLON",
                "AL 0",
                "AH 6400ft",
                "V X=39:25.0 N 118:42.0 W",
                "DC 6",
                "",

                "AC D",
                "AN LAKE TAHOE",
                "AL 0",
                "AH 8800ft",
                "V X=38:53.6 N 119:59.7 W",
                "DC 5"
        };


        for (String cmd : openAirCommands) {
            if (cmd.equals("") || cmd.equals("*")) {

                plotAndReset();
            } else {
                parseCommand(cmd);
            }
        }
        plotAndReset();


    }

    private void plotAndReset() {

        // Remove marker from display - otherwise it will become cluttered
        mAirportMarker.setVisible(false);

        // Create a Google Maps Polygon and populate it with coordinates interpreted form the OpenAir spec.
        PolygonOptions polyOptions = new PolygonOptions();
        for (LatLng pos : mCoordList) {
            polyOptions.add(pos);
        }

        // Use outline only to keep cluttering at a minimum, color of outline is defined by Airspace class
        polyOptions.strokeColor(ContextCompat.getColor(getApplicationContext(), mOutlineColor));
        polyOptions.strokeWidth(mOutlineWidth);

        // Display polygon
        Polygon polygon = mMap.addPolygon(polyOptions);

        // Reset internal storage
        mCoordList = new ArrayList<>();
        mOutlineColor = R.color.Yellow;
        mOutlineWidth=DEFAULT_LINE_WIDTH;
    }


    /**
     * Parse coordinates in the String format from OpenAir specification and return a LatLng object.
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
            Log.e(TAG, "parseCoordinateString: Cannot parse coordinate String: " + coordString);
            return null;
        }
    }


    public void parseCommand(String cmd) {

        // First pattern matches two groups - the main command and the rest of the line
        String pattern = "(AN|AC|AL|AH|DC|DA|DP|V|\\*) ([\\w\\d\\s\\:\\.\\=\\+\\-\\,]*)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(cmd);

        if (m.find()) {
            String command = m.group(1).toUpperCase();
            String rest = m.group(2).trim().toUpperCase();

            LatLng pos = null;
            int radius;
            int fromDeg;
            int toDeg;

            switch (command) {
                case "*":
                    // Comment - do nothing
                    Log.d(TAG, "parseCommand: Comment");
                    break;

                case "AC":
                    // Airspace Class - expect a simple one letter argument

                    Log.d(TAG, "parseCommand: Airspace Class");

                    mOutlineColor = R.color.Yellow;
                    mOutlineWidth=DEFAULT_LINE_WIDTH;
                    if (rest.equals("B")) {
                        mOutlineColor = R.color.DodgerBlue;
                    } else if (rest.equals("C")) {
                        mOutlineColor = R.color.MediumVioletRed;
                    } else if (rest.equals("D")) {
                        mOutlineColor = R.color.MediumSlateBlue;
                    } else if (rest.equals("R")) {
                        mOutlineColor = R.color.MediumSlateBlueTransp;
                        mOutlineWidth=SPECIAL_LINE_WIDTH;
                    } else {
                        Log.e(TAG, "Airspace argunt problem: " + rest);
                    }
                    break;


                case "AN":
                    // Airport name, expect string parameter
                    // not implem,ented yet

                    Log.d(TAG, "parseCommand: Airport Name");
                    break;

                case "AL":
                    // Altitude Low, expect a parameters like "3500 ft" or "SFC"
                    // Not implemented yet

                    Log.d(TAG, "parseCommand: Altitude Low");
                    break;


                case "AH":
                    // Altitude High, expect a parameters like "35000ft" or "35000 ft"
                    // Not implemented yet

                    Log.d(TAG, "parseCommand: Altitude High");
                    break;


                case "DC":
                    Log.d(TAG, "parseCommand: Draw Circle");

                    // Draw Circle command - expect an integer argument
                    radius = Integer.parseInt(rest) * 1852;
                    pos = null;
                    if (mCenter != null) {
                        for (int deg = 0; deg < 360; deg++) {
                            pos = SphericalUtil.computeOffset(mCenter, radius, deg);
                            addPosToCoordList(pos);
                        }
                    }
                    break;

                case "V":
                    // Variable Assignment Command
                    // The pattern matches a variable name and the value argument from the rest of the line above

                    Log.d(TAG, "parseCommand: Variable assignment");

                    String assignPattern = "([\\w]+)\\s*=([\\s\\w\\d\\:\\.\\+\\-]*)";
                    r = Pattern.compile(assignPattern);
                    m = r.matcher(rest);

                    if (m.find()) {
                        if (m.group(1).equals("D")) {
                            // Variable name D means this is a Direction assignment
                            Log.d(TAG, String.format("Direction command, sign: %s", m.group(2)));
                            if (m.group(2).equals("+")) {
                                mStep_direction = 1;
                            } else {
                                mStep_direction = -1;
                            }

                        } else {
                            // A position variable assignment, any variable name us supported although I have only seen X used
                            Log.d(TAG, String.format("Variable assignment: %s identified, remaining arguments: %s", m.group(1), m.group(2)));

                            pos = parseCoordinateString(rest);
                            if (pos != null) {
                                Log.d(TAG, "Setting mCenter to: " + pos);
                                mCenter = pos;

                            } else {
                                // If we cannot parse this as a position, we need to look into this later
                                Log.e(TAG, "parseCommand: Unsupported assignment...");
                            }
                        }

                    } else {
                        // We did not find anything useful in the arugument string after the name

                        Log.d(TAG, "parseCommand: Variable argument parsing error");
                    }

                    break;


                case "DA":
                    // Draw Arc Command
                    // Pattern matches three comma separated integer aruments

                    Log.d(TAG, "parseCommand: Draw Arc command");

                    String threeArgsPattern = "([\\d]+)\\s*\\,\\s*([\\d]+)\\s*\\,\\s*([\\d]+)";
                    r = Pattern.compile(threeArgsPattern);
                    m = r.matcher(rest);

                    if (m.find()) {
                        radius = Integer.parseInt(m.group(1)) * 1852;
                        fromDeg = Integer.parseInt(m.group(2));
                        toDeg = Integer.parseInt(m.group(3));
                        drawArcFromTo(radius, fromDeg, toDeg);
                    } else {
                        // We did not find the expected three integers in the argument string
                        Log.e(TAG, "parseCommand: Draw arc parameters not recognized");
                    }
                    break;

                case "DP":
                    // Define Point Command
                    // Pattern matches a potential coordinate string

                    Log.d(TAG, "parseCommand: Draw Point Commannd");

                    String coordPattern = "([\\d\\:\\. \\w]+)";
                    r = Pattern.compile(coordPattern);
                    m = r.matcher(rest);
                    if (m.find()) {
                        pos = parseCoordinateString(m.group(1));
                        addPosToCoordList(pos);

                        Log.d(TAG, "Got a coordinate : " + pos);

                    } else {
                        Log.e(TAG, "parseCommand: Problem parsing DP argument");
                    }
                    break;


                case "DB":
                    // Draw Between Command
                    Log.d(TAG, "parseCommand: Draw between command");

                    // Pattern matches two possible coordinates separated by a comma
                    String betweenPattern = "([\\d\\:\\. \\w]+) *, *([\\d\\:\\. \\w]+)";
                    r = Pattern.compile(betweenPattern);
                    m = r.matcher(rest);

                    if (m.find()) {
                        LatLng pos1, pos2;
                        pos1 = parseCoordinateString(m.group(1));
                        pos2 = parseCoordinateString(m.group(2));
                        Log.d(TAG, "parseCommand: Got two coordinates : " + pos1 + " and " + pos2);

                        if (pos1 != null && pos2 != null) {
                            fromDeg = (int) SphericalUtil.computeHeading(mCenter, pos1);
                            toDeg = (int) SphericalUtil.computeHeading(mCenter, pos2);
                            radius = (int) SphericalUtil.computeDistanceBetween(mCenter, pos1);
                            drawArcFromTo(radius, fromDeg, toDeg);
                        }
                    } else {
                        Log.e(TAG, "parseCommand: Problem parsing draw between arguments");
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

    private void drawArcFromTo(int radius, int fromDeg, int toDeg) {
        if (mCenter != null) {
            double x, y;
            LatLng newPos;
            int degrees = fromDeg;
            int step = mStep_direction * STEP_SIZE;
            do {
                newPos = SphericalUtil.computeOffset(mCenter, radius, degrees);
                addPosToCoordList(newPos);
                degrees += step;
                if (Math.abs(((degrees + 360) % 360) - toDeg) < STEP_SIZE)
                    break;
            } while (true);

        }
    }

    public double compasToMathDegrees(double compass) {
        return (double) (((90 - compass) + 360) % 360);
    }
}
