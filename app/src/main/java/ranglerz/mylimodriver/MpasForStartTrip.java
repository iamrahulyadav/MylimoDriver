package ranglerz.mylimodriver;

import android.*;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ranglerz.mylimodriver.Adapters.DriverOrdersAdapter;
import ranglerz.mylimodriver.Constants.APIURLs;
import ranglerz.mylimodriver.GetterSetters.DriverOrdersGetterSetter;
import ranglerz.mylimodriver.VolleyLibraryFiles.AppSingleton;

public class MpasForStartTrip extends FragmentActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;

    private static final int REQUEST_FINE_LOCATION = 11;

    Button bt_end_ride;


    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    LatLng latLng;


    Location myCurrentLocation;
    Location myStaticCurrentLocation;
    Marker mCurrLocationMarker;

    private int timer = 3;
    Handler mHandler;

    private int sendingLatLngtime = 30;
    Handler mHandlerSendingLatLng;


    double latitude; // latitude
    double longitude; // longitude

    MapHelper mapHelper;
    LatLng mLatlngDropoff;
    String mPickupAddress, mDropOffLocation;


    PolylineOptions lineOptions;
    Polyline polyline = null;

    String mDriver_id = "";
    String mCarId = "";
    String mRide_id = "";

    String mOrder1 = "-1";
    String mOrder2 = "-1";
    String mOrder3 = "-1";



    private ProgressBar progress_bar;

    private  LatLng mCurrentLatLng = null;

    //****************
    Double dhaLahoreLat,dhaLahoreLng;

    //***********

    SharedPreferences sharedPreferencesRide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mpas_for_start_trip);


        bt_end_ride = (Button) findViewById(R.id.bt_end_ride);
        bt_end_ride.bringToFront();
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        progress_bar.bringToFront();



        //cLocation();

    }//end of onCreate


    @Override
    protected void onResume() {
        super.onResume();

        sharedPreferencesRide = getSharedPreferences("driving", 0);

        if (sharedPreferencesRide!=null){
            mDriver_id = sharedPreferencesRide.getString("d_id", null);

            if (mDriver_id!=null){

                mDriver_id = sharedPreferencesRide.getString("d_id", null);
                mCarId = sharedPreferencesRide.getString("car_id", null);

                mOrder1 = sharedPreferencesRide.getString("order_id_1", null);
                mOrder2 = sharedPreferencesRide.getString("order_id_2", null);
                mOrder3 = sharedPreferencesRide.getString("order_id_3", null);


                String latlng = sharedPreferencesRide.getString("latlng", null);

                Log.e("TAg", "user destination latlng: " + latlng);
                String[] latlngSplitArray = latlng.split(",");
                String lat = latlngSplitArray[0];
                lat = lat.substring(10);
                String lng = latlngSplitArray[1];
                lng = lng.substring(0, lng.length() - 1);
                Log.e("TAg", "the pickup latn lng: " + latlng);
                Log.e("TAg", "the pickup lat are: " + lat);
                Log.e("TAg", "the pickup lng are: " + lng);
                double dLat = Double.parseDouble(lat);
                double dLng = Double.parseDouble(lng);

                //mLatlngPickup = new LatLng(33.738045, 73.084488);
                mLatlngDropoff = new LatLng(dLat, dLng);


            }
            else {

                SharedPreferences.Editor editor = sharedPreferencesRide.edit();
                Intent intent = getIntent();
                String latlng = intent.getExtras().getString("latlng", "");
                mDriver_id = intent.getExtras().getString("driver_id", "");
                mCarId = intent.getExtras().getString("car_id", "");

                mOrder1 = intent.getExtras().getString("order_id_1", "-1");
                mOrder2 = intent.getExtras().getString("order_id_2", "-1");
                mOrder3 = intent.getExtras().getString("order_id_3", "-1");


                editor.putString("d_id", mDriver_id);
                editor.putString("car_id", mCarId);
                editor.putString("latlng", latlng);
                editor.putString("order_id_1", mOrder1);
                editor.putString("order_id_2", mOrder2);
                editor.putString("order_id_3", mOrder3);
                editor.commit();


                Log.e("TAg", "user destination latlng: " + latlng);
                String[] latlngSplitArray = latlng.split(",");
                String lat = latlngSplitArray[0];
                lat = lat.substring(10);
                String lng = latlngSplitArray[1];
                lng = lng.substring(0, lng.length() - 1);
                Log.e("TAg", "the pickup latn lng: " + latlng);
                Log.e("TAg", "the pickup lat are: " + lat);
                Log.e("TAg", "the pickup lng are: " + lng);
                double dLat = Double.parseDouble(lat);
                double dLng = Double.parseDouble(lng);

                //mLatlngPickup = new LatLng(33.738045, 73.084488);
                mLatlngDropoff = new LatLng(dLat, dLng);


            }
        }


        turnOnGPS();


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        createNetErrorDialog();
        checkPermission();

        mapFragment.getMapAsync(this);

        mapHelper = new MapHelper();


        useHandler();

        endRide();

    }

    public void inisialization(LatLng current){


        Log.e("TAG", "the values for current latlng: " + current);
        Log.e("TAG", "the values for current latlng mLatLng: " + mLatlngDropoff);

        calculateShorDistance(current, mLatlngDropoff );

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        setMyLocationEnable();


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
//            mMap.setMyLocationEnabled(true);
            return;
        }

        mMap.setMyLocationEnabled(true);
        buildGoogleApiClient();

        mGoogleApiClient.connect();
        ///

    }


    ///

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            //place marker at current position
            //mGoogleMap.clear();
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();

            myCurrentLocation = mLastLocation;
            myStaticCurrentLocation = mLastLocation;

            mapHelper.setLatitude(latitude);
            mapHelper.setLongitude(longitude);

            Log.e("latlang" , "latitudeCustomer "+latitude);
            Log.e("latlang" , "longitudeCustomer "+longitude);
            latLng = new LatLng(latitude , longitude);
           /* MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Location");
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.cartype_icon));

            mMap.addMarker(markerOptions);*/


            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng).zoom(12.f).build();

            mMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));


        }

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000); //1 seconds
        mLocationRequest.setFastestInterval(1000); //1 seconds
        mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if(mGoogleApiClient.isConnected()){

            final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

            if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                // Call your Alert message

                turnOnGPS();
            }
            else {
                inisialization(latLng);
                //starting ride
                if (mRide_id.length() == 0) {

                    startRide();
                }

                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }

        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    // Check for permission to access Location
    private boolean checkPermission() {
        Log.d("MapActivity", "checkPermission()");
        // Ask for permission if it wasn't granted yet
        return (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED );
    }
    // Asks for permission
    private void askPermission() {
        Log.d("MapActivity", "askPermission()");
        ActivityCompat.requestPermissions(
                this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_FINE_LOCATION
        );
    }

    public void setMyLocationEnable(){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M && checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
        }else {
            mMap.setMyLocationEnabled(true);


        }
    }



    //calculation distance
    public void calculateShorDistance(LatLng pickup, LatLng dropOff){

        String url = getUrl(pickup, dropOff);
        Log.d("onMapClick", url.toString());
        FetchUrl FetchUrl = new FetchUrl();
        // Start downloading json data from Google Directions API
        FetchUrl.execute(url);
        //   setingTextAndTimeInTextView(dhaDistance);

    }//end of calculate distance



    //rouding double
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    //market for Pickup Location
    public void addingMarketForPickLocation(LatLng pickup, String pickupTitle){
        mMap.addMarker(new MarkerOptions()
                .position(pickup)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.cartype_icon))
                .title(pickupTitle));

    }

    //market for DropOff Location
    public void addingMarketForDropOffLocation(LatLng drobOff, String dropOffTitle){
        mMap.addMarker(new MarkerOptions()
                .position(drobOff)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker_des))
                .title(dropOffTitle));

    }

    public double shortDistance(double fromLong, double fromLat, double toLong, double toLat){
        double d2r = Math.PI / 180;
        double dLong = (toLong - fromLong) * d2r;
        double dLat = (toLat - fromLat) * d2r;
        double a = Math.pow(Math.sin(dLat / 2.0), 2) + Math.cos(fromLat * d2r)
                * Math.cos(toLat * d2r) * Math.pow(Math.sin(dLong / 2.0), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = 6367000 * c;
        return Math.round(d);
    }



    //distance between two points

    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }


    //Thread for starting mainActivity
    private Runnable mRunnableStartMainActivity = new Runnable() {
        @Override
        public void run() {
            Log.d("Handler", " Calls");
            timer--;
            mHandler = new Handler();
            mHandler.postDelayed(this, 1000);

            if (timer == 0) {
                LatLng currentLatLng = new LatLng(latitude, longitude);
                mapHelper.setScr(currentLatLng);

                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatlngPickup, 14.0f));

                //addingMarketForPickLocation(latLng, mPickupAddress);
                addingMarketForDropOffLocation(mLatlngDropoff, mDropOffLocation);

                mHandler.removeCallbacks(mRunnableStartMainActivity);

            }
        }
    };



    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        onMapReady(mMap);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }



    //***********************

    private String getUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    //converting time into hrs and day
    public String timeConvert(int time) {
        return time/24/60 + ":" + time/60%24 + ':' + time%60;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

       /* Toast toast =  Toast.makeText(this, "Location Changed " + location.getLatitude()
                + location.getLongitude(), Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();*/

        //


        myCurrentLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        double lat =  location.getLatitude();
        double lng = location.getLongitude();
        Log.e("TAg", "  the change location is: " + lat);
        Log.e("TAg", "  the change location is: " + lng);
        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon((BitmapDescriptorFactory.fromResource(R.drawable.cartype_icon_black)));
        mCurrLocationMarker = mMap.addMarker(markerOptions);
        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
        LatLng currentLatLng = new LatLng(lat, lng);
        Log.e("TAG", "abc test CurrentLATLNG: " + latLng);
        Log.e("TAG", " abc test Static LatLng: " + currentLatLng);
        mCurrentLatLng = currentLatLng;
        latitude = lat;
        longitude = lng;
        //calculateShorDistance(mLatlngDropoff, latLng);



    }

    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask",jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask","Executing routes");
                Log.d("ParserTask",routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;

            lineOptions = null;


            // Traversing through all the routes
            if (result.size()!=0){
                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList<>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(10);
                    lineOptions.color(ContextCompat.getColor(getApplicationContext(), R.color.buttonColor));

                    Log.d("onPostExecute","onPostExecute lineoptions decoded");

                }
            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                if (polyline!=null){
                     polyline.remove();
                    polyline =  mMap.addPolyline(lineOptions);
                }else {
                    polyline = mMap.addPolyline(lineOptions);
                }

            }
            else {
                Log.d("onPostExecute","without Polylines drawn");
            }
        }
    }


    //handler for the starign activity
    Handler newHandler;
    public void useHandler(){

        newHandler = new Handler();
        newHandler.postDelayed(mRunnableStartMainActivity, 1000);

    }


    private void updateLanLngToServer(){

        mHandlerSendingLatLng = new Handler();
        mHandlerSendingLatLng.postDelayed(mRunableSendLatLng, 1000);

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mRunnableStartMainActivity);
//        mHandlerSendingLatLng.removeCallbacks(mRunableSendLatLng);
    }


    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    protected void createNetErrorDialog() {

        if (isNetworkAvailable()==false){


            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("You need a network connection to use this application. Please turn on mobile network or Wi-Fi in Settings.")
                    .setTitle("Unable to connect")
                    .setCancelable(false)
                    .setPositiveButton("Settings",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent i = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                                    startActivity(i);
                                }
                            }
                    )
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    MpasForStartTrip.this.finish();
                                }
                            }
                    );
            AlertDialog alert = builder.create();
            alert.show();
        }else {
            //remainging
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();


    }

    public void turnOnGPS(){
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(MpasForStartTrip.this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            mGoogleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            //**************************
            builder.setAlwaysShow(true); //this is the key ingredient
            //**************************

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result.getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All location settings are satisfied. The client can initialize location
                            // requests here.
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(
                                        MpasForStartTrip.this, 1000);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        }


    }



    public LatLng convertStringToLatlng(String latlngString){


        String abcdedf =   latlngString;
        String[] latlong =  abcdedf.split(",");
        String latitude = (latlong[0]);
        String longitude = (latlong[1]);

        Log.e("TAG", "latitude: " + latitude);
        Log.e("TAG", "logitue: " + longitude);

        String[] mLat = latitude.split("\\(");
        String latLeft = (mLat[0]);
        String LatRight = (mLat[1]);
        Log.e("TAG", "latleft: " + latLeft);
        Log.e("TAG", "latright: " + LatRight);

        String[] mLng = longitude.split("\\)");
        String lngleft = (mLng[0]);
        //String lngright = (mLng[1]);
        Log.e("TAG", "lngleft: " + lngleft);

        double myLatitude = Double.parseDouble(LatRight);
        double myLongitude = Double.parseDouble(lngleft);
        // Log.e("TAG", "lngright: " + lngright);

        LatLng latLng = new LatLng(myLatitude, myLongitude);

        Log.e("TAG", "final Latlng: " + latLng);

        return latLng;

    }

    public void reFreshMap(){


    }


    private void endRide(){
        bt_end_ride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                endRideAlertDialog();

            }
        });
    }


    private void startRide(){

        //calling service for end ride
        double dLat = latLng.latitude;
        double dLng = latLng.longitude;
        String lat = String.valueOf(dLat);
        String lng = String.valueOf(dLng);


        SharedPreferences sharedPrefRideStarted = getSharedPreferences("ridestarted", 0);
        String ride_status = sharedPrefRideStarted.getString("ride_status", "-1");
        Log.e("TAG", "the ride status is: " + ride_status);
        if (sharedPrefRideStarted!=null){
            if (ride_status.equals("1")){


                Toast.makeText(this, "Continuing Your Ride", Toast.LENGTH_SHORT).show();

            }
            else {
                rideStarted("1", lat, lng, mDriver_id, mCarId);
            }
        }



    }

    private void endRideAlertDialog(){

        final Dialog confirmationDiloag = new Dialog(MpasForStartTrip.this);

        confirmationDiloag.requestWindowFeature(Window.FEATURE_NO_TITLE);
        confirmationDiloag.setContentView(R.layout.order_confirmation_alert);
        TextView tv_description = (TextView) confirmationDiloag.findViewById(R.id.tv_description);
        Button bt_no = (Button) confirmationDiloag.findViewById(R.id.bt_no);
        Button bt_yes = (Button) confirmationDiloag.findViewById(R.id.bt_yes);

        bt_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmationDiloag.dismiss();
            }
        });

        //submitting order
        bt_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmationDiloag.dismiss();

                //calling end ride api

                SharedPreferences sharedPrefRideStarted = getSharedPreferences("ridestarted", 0);

                String myRide_id = sharedPrefRideStarted.getString("mRide_id", "-1");


                rideEndServiecCalling(myRide_id);



            }
        });

        confirmationDiloag.show();

    }


    private void rideStarted(final String ride_status, final String ride_lat, final String ride_lng, final String fk_driver_id, final String fk_car_id){


        // Tag used to cancel the request
        String cancel_req_tag = "register";
        progress_bar.setVisibility(View.VISIBLE);
        progress_bar.bringToFront();


        StringRequest strReq = new StringRequest(Request.Method.POST, APIURLs.start_ride , new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("TAG", "Ride Started Response: " + response.toString());
                //hideDialog();
                progress_bar.setVisibility(View.GONE);

                try {

                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {

                        String message =  jObj.getString("msg");

                        if (message.equals("Ride Starts Now !")){
                            Toast.makeText(MpasForStartTrip.this, "Your Ride Started", Toast.LENGTH_LONG).show();
                            mRide_id = jObj.getString("ride_id");
                            Log.e("TAG", "the ride id is: " + mRide_id);

                                double dLat = latLng.latitude;
                                double dLng = latLng.longitude;
                                String lat = String.valueOf(dLat);
                                String lng = String.valueOf(dLng);

                                sendingUpdateOfCurrentLocationToServer(mRide_id, lat, lng);

                                //calling location update
                                updateLanLngToServer();


                                SharedPreferences sharedPrefRideStarted = getSharedPreferences("ridestarted", 0);
                                SharedPreferences.Editor editor = sharedPrefRideStarted.edit();
                                editor.putString("ride_status", ride_status);
                                editor.putString("ride_lat", ride_lat);
                                editor.putString("ride_lng", ride_lat);
                                editor.putString("fk_driver_id", fk_driver_id);
                                editor.putString("fk_car_id", fk_car_id);
                                editor.putString("mRide_id", mRide_id);
                                editor.commit();


                        }

                    } else {

                        String errorMsg = jObj.getString("msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                //hideDialog();
                progress_bar.setVisibility(View.GONE);
            }
        }) {



            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url


                Map<String, String> params = new HashMap<String, String>();

                    params.put("ride_status", ride_status);
                    params.put("ride_lat", ride_lat);
                    params.put("ride_lng", ride_lng);
                    params.put("fk_driver_id", fk_driver_id);
                    params.put("fk_car_id", fk_car_id);

                params.put("order_id_1", mOrder1);
                params.put("order_id_2", mOrder2);
                params.put("order_id_3", mOrder3);


                return params;
            }
        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, cancel_req_tag);

    }//end of order service


    //Thread for starting mainActivity
    private Runnable mRunableSendLatLng = new Runnable() {
        @Override
        public void run() {
            Log.d("Handler", " Calls");
            sendingLatLngtime--;

            Log.e("TAG", "the timmer for start ride is: " + sendingLatLngtime);
            mHandlerSendingLatLng = new Handler();
            mHandlerSendingLatLng.postDelayed(this, 1000);

            if (sendingLatLngtime == 0) {
                LatLng currentLatLng = new LatLng(latitude, longitude);
                mapHelper.setScr(currentLatLng);

                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatlngPickup, 14.0f));

                mHandler.removeCallbacks(mRunnableStartMainActivity);
                sendingLatLngtime = 30;

                String lat = String.valueOf(latitude);
                String lng = String.valueOf(longitude);
                Log.e("TAG", " here are the latlng lat: " + lat);
                Log.e("TAG", " here are the latlng lng: " + lng);
                Log.e("TAG", " here are the latlng: " + mCurrentLatLng);

                sendingUpdateOfCurrentLocationToServer(mRide_id, lat, lng);
            }
        }
    };


    private void sendingUpdateOfCurrentLocationToServer(final String ride_id, final String ride_lat, final String ride_lng){


        // Tag used to cancel the request
        String cancel_req_tag = "register";

        Log.e("TAG", "to send lat are: " + ride_lat);
        Log.e("TAG", "to send lng are: " + ride_lng);

        StringRequest strReq = new StringRequest(Request.Method.POST, APIURLs.update_latlng_on_server , new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("TAG", "Update LatLng Respose: " + response.toString());
                //hideDialog();


                try {

                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {



                    } else {

                        String errorMsg = jObj.getString("msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                //hideDialog();

            }
        }) {



            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url


                Map<String, String> params = new HashMap<String, String>();

                params.put("ride_id", ride_id);
                params.put("ride_lat", ride_lat);
                params.put("ride_lng", ride_lng);



                return params;
            }
        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, cancel_req_tag);

    }//end of update service service


    //end ride
    private void rideEndServiecCalling(final String endRide){


        // Tag used to cancel the request
        String cancel_req_tag = "register";
        progress_bar.setVisibility(View.VISIBLE);
        progress_bar.bringToFront();


        StringRequest strReq = new StringRequest(Request.Method.POST, APIURLs.end_ride, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("TAG", "Ride Finished Response: " + response.toString());
                //hideDialog();
                progress_bar.setVisibility(View.GONE);

                try {

                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (error) {

                        String message =  jObj.getString("msg");
                        if (message.equals("Ride Fisnished !.")){

                            SharedPreferences.Editor editor = sharedPreferencesRide.edit();
                            editor.clear();
                            editor.commit();

                            SharedPreferences sharedPrefRideStarted = getSharedPreferences("ridestarted", 0);
                            SharedPreferences.Editor rideStarted = sharedPrefRideStarted.edit();
                            rideStarted.clear();
                            rideStarted.commit();


                            Toast.makeText(MpasForStartTrip.this, "Ride Has Been Finish", Toast.LENGTH_SHORT).show();
                            finish();
                            Intent i = new Intent(MpasForStartTrip.this, MyHistoryScreen.class);
                            startActivity(i);



                        }

                        else if (message.equals("Ride Not Exist !.")){


                            Toast.makeText(MpasForStartTrip.this, "Ride Not Exist !.", Toast.LENGTH_SHORT).show();



                        }

                    } else {

                        String errorMsg = jObj.getString("msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                //hideDialog();
                progress_bar.setVisibility(View.GONE);
            }
        }) {



            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url


                Map<String, String> params = new HashMap<String, String>();

                SharedPreferences sharedPreferences = getSharedPreferences("driver", 0);
                String driver_id = sharedPreferences.getString("driver_id", "no");



                Log.e("TAG","here are the ides for driver id is: " + driver_id);
                Log.e("TAG","here are the ides for ride id: " + endRide);
                Log.e("TAG","here are the ides for ride id 1: " + mOrder1);
                Log.e("TAG","here are the ides for ride id 2: " + mOrder2);
                Log.e("TAG","here are the ides for ride id 3: " + mOrder3);

                    params.put("driver_id", driver_id);
                    params.put("ride_id", endRide);

                    params.put("order_id_1", mOrder1);
                    params.put("order_id_2", mOrder2);
                    params.put("order_id_3", mOrder3);


                return params;
            }
        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, cancel_req_tag);

    }//end of end ride service


}//end of class