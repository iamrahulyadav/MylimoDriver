package ranglerz.mylimodriver;

import android.*;
import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ranglerz.mylimodriver.Adapters.DriverOrdersAdapter;
import ranglerz.mylimodriver.Constants.APIURLs;
import ranglerz.mylimodriver.GetterSetters.DriverOrdersGetterSetter;
import ranglerz.mylimodriver.Services.GettingCurrentLatLngService;
import ranglerz.mylimodriver.VolleyLibraryFiles.AppSingleton;

public class DriversOrderListing extends BaseActvitvityForDrawer implements  GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    RecyclerView rv_order_history;
    LinearLayoutManager linearLayoutManager;

    Button bt_start_trip;
    TextView tv_no_order_available;

    private ProgressBar progress_bar;
    private JSONObject jsonObject;
    private JSONArray jsonArray;

    ArrayList<DriverOrdersGetterSetter> historyData;
    DriverOrdersAdapter historyDataAdapter;

    private String driver_id = "";
    private LatLng destination = null;
    private String orderId = null;
    private String carId = null;

    private GoogleApiClient googleApiClient;



    String mOrder1 = "-1";
    String mOrder2 = "-1";
    String mOrder3 = "-1";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_drivers_order_listing);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        //inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.activity_drivers_order_listing, null, false);
        mDrawerLayout.addView(contentView, 0);

        init();
        startTripButtonPress();
        turnOnGPS();



        SharedPreferences sharedPreferences  = getSharedPreferences("driver", 0);
        driver_id = sharedPreferences.getString("driver_id", null);

        Log.e("TAG", "the driver id is: " + driver_id);
        gettingMyOrderHistoryFromServer(driver_id);



        //startService(new Intent(this,CurrentLocation .class));

    }



    private void init(){

        rv_order_history = (RecyclerView) findViewById(R.id.rv_order_history);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext() , LinearLayoutManager.VERTICAL, false);
        rv_order_history.setLayoutManager(linearLayoutManager);

        bt_start_trip = (Button) findViewById(R.id.bt_start_trip);
        tv_no_order_available = (TextView) findViewById(R.id.tv_no_order_available);

        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        historyData = new ArrayList<>();

       /* historyData.add(new DriverOrdersGetterSetter("1", "32.121212", "71.23323", "pick me from the location...", "32.212121", "72.235242", "drop me to the location...",
                "Lahore", "Islamabad", "Business", "2", "20-02-18", "Web"+" 4:30 pm"+"", "", "12", "Shoaib Anwar", "myemailc.om", "32323", "1323232", "32322"));
*/
        Collections.reverse(historyData);
        historyDataAdapter = new DriverOrdersAdapter(getApplicationContext(), historyData);
        rv_order_history.setAdapter(historyDataAdapter);

        boolean isTru = checkPermission();

        Log.e("TAG", "the premission result is: " + isTru);

        if (isTru) {

            boolean isServiceRunning =  isMyServiceRunning(GettingCurrentLatLngService.class);
            Log.e("TAG", "the service Running STATUS is: " + isServiceRunning);

           // stopService(new Intent(DriversOrderListing.this, GettingCurrentLatLngService.class));
            if (!isServiceRunning) {
                startService(new Intent(this, GettingCurrentLatLngService.class));
            }
        }else {

            ActivityCompat.requestPermissions(DriversOrderListing.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);

        }
    }


    //order booking server
    private void gettingMyOrderHistoryFromServer(final String user_id){


        // Tag used to cancel the request
        String cancel_req_tag = "register";
        progress_bar.setVisibility(View.VISIBLE);
        progress_bar.bringToFront();

        StringRequest strReq = new StringRequest(Request.Method.POST, APIURLs.orders , new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("TAG", "hisptory response: " + response.toString());
                //hideDialog();
                progress_bar.setVisibility(View.GONE);

                try {

                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {

                        jsonObject = new JSONObject(response);
                        jsonArray = jsonObject.getJSONArray("order_detail");
                        Log.e("TAG", "the order for driver jsonArray: " + jsonArray);
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject finalobject = jsonArray.getJSONObject(i);

                            String reservation_id = finalobject.getString("reservation_id");
                            String fk_car_id = finalobject.getString("fk_car_id");
                            String order_id = finalobject.getString("order_id");
                            String pickup_lat = finalobject.getString("pickup_lat");
                            String pickup_lng = finalobject.getString("pickup_lng");
                            String pickup_detail = finalobject.getString("pickup_detail");
                            String destination_lat = finalobject.getString("destination_lat");
                            String destination_lng = finalobject.getString("destination_lng");
                            String destination_detail = finalobject.getString("destination_detail");
                            String pickup_city = finalobject.getString("pickup_city");
                            String destination_city = finalobject.getString("destination_city");
                            String car_type = finalobject.getString("car_type");
                            String seats = finalobject.getString("seats");
                            String date = finalobject.getString("date");
                            String time = finalobject.getString("time");
                            String fk_user_id = finalobject.getString("fk_user_id");
                            String order_status = finalobject.getString("order_status");
                            String price = finalobject.getString("price");
                            String user_id = finalobject.getString("user_id");
                            String fullname = finalobject.getString("fullname");
                            String email = finalobject.getString("email");
                            String phone = finalobject.getString("phone");
                            String pincode = finalobject.getString("pincode");
                            String cnic = finalobject.getString("cnic");
                            String day = finalobject.getString("day");

                            day = day.substring(0, 3);

                            Log.e("TAG", "the user order id " + i + " " + order_id);
                            Log.e("TAG", "User Destination Lat " + i + " " + destination_lat);
                            Log.e("TAG", "User Destination Lng " + i + " " + destination_lng);


                             historyData.add(new DriverOrdersGetterSetter(order_id, reservation_id, fk_car_id,  pickup_lat, pickup_lng, pickup_detail, destination_lat, destination_lng, destination_detail,
                                    pickup_city, destination_city, car_type, seats, date, day+" "+time, price, user_id, fullname, email, phone, pincode, cnic));
                        }


                        Log.e("TAG", "the size of history record: " + historyData.size());



                            //reversing array list to get recent on top
                            Collections.reverse(historyData);
                            historyDataAdapter = new DriverOrdersAdapter(getApplicationContext(), historyData);
                            rv_order_history.setAdapter(historyDataAdapter);

                            Log.e("TAG", "the destination lat result is lat: " + historyData.get(0).getDestination_lat());
                            Log.e("TAG", "the destination lat result is lng: " + historyData.get(0).getDestination_lng());

                            String lat = historyData.get(0).getDestination_lat();
                            String lng = historyData.get(0).getDestination_lng();


                            double dLat = Double.parseDouble(lat);
                            double dLng = Double.parseDouble(lng);
                            destination = new LatLng(dLat, dLng);
                            orderId = historyData.get(0).getOrder_id();
                            carId = historyData.get(0).getFk_car_id();

                            tv_no_order_available.setVisibility(View.GONE);
                            bt_start_trip.setVisibility(View.VISIBLE);



                    } else {


                        String errorMsg = jObj.getString("msg");
                        if (errorMsg.equals("No Orders Yet")){

                            tv_no_order_available.setVisibility(View.VISIBLE);
                            bt_start_trip.setVisibility(View.GONE);


                        }
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

                params.put("driver_id", user_id);

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

    private void startTripButtonPress(){

        bt_start_trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isGPSEnabled(getApplicationContext())) {



                    //
                    if (historyData.size()>0) {

                        if (historyData.size() == 1){
                            mOrder1 = historyData.get(0).getOrder_id();

                            Log.e("TAG", "The order id is driver 1: " + mOrder1);
                            Log.e("TAG", "The order id is driver 2: " + mOrder2);
                            Log.e("TAG", "The order id is driver 3: " + mOrder3);
                        }
                        else if (historyData.size() == 2){

                            mOrder1 = historyData.get(0).getOrder_id();
                            mOrder2 = historyData.get(1).getOrder_id();

                            Log.e("TAG", "The order id is driver 1: " + mOrder1);
                            Log.e("TAG", "The order id is driver 2: " + mOrder2);
                            Log.e("TAG", "The order id is driver 3: " + mOrder3);

                        }else if (historyData.size()==3){
                            mOrder1 = historyData.get(0).getOrder_id();
                            mOrder2 = historyData.get(1).getOrder_id();
                            mOrder3 = historyData.get(2).getOrder_id();



                            Log.e("TAG", "The order id is driver 1: " + mOrder1);
                            Log.e("TAG", "The order id is driver 2: " + mOrder2);
                            Log.e("TAG", "The order id is driver 3: " + mOrder3);

                        }


                    }else {
                        Log.e("TAG", "there is no history availabel");
                    }

                    //
                    Intent i = new Intent(DriversOrderListing.this, MpasForStartTrip.class);
                    Log.e("TAg", "the lat long of destination are: " + destination);

                    i.putExtra("latlng", destination.toString());
                    i.putExtra("driver_id", driver_id);
                    i.putExtra("car_id", carId);
                    i.putExtra("order_id_1", mOrder1);
                    i.putExtra("order_id_2", mOrder2);
                    i.putExtra("order_id_3", mOrder3);
                    startActivity(i);

                    boolean foo = isGPSEnabled(getApplicationContext());
                    Log.e("TAG", "the gps is: " + foo);
                    finish();

                }else {

                    turnOnGPS();
                }
            }
        });
    }



    // Check for permission to access Location
    private boolean checkPermission() {
        Log.d("MapActivity", "checkPermission()");
        // Ask for permission if it wasn't granted yet
        return (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startService(new Intent(this, GettingCurrentLatLngService.class));

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                   // Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    public void turnOnGPS(){


        googleApiClient = new GoogleApiClient.Builder(DriversOrderListing.this)
                        .addApi(LocationServices.API)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this).build();
        googleApiClient.connect();

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
                        LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
                result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                    @Override
                    public void onResult(LocationSettingsResult result) {
                        final Status status = result.getStatus();
                        final LocationSettingsStates state = result.getLocationSettingsStates();
                        switch (status.getStatusCode()) {
                            case LocationSettingsStatusCodes.SUCCESS:
                                // All location settings are satisfied. The client can initialize location
                                // requests here.

                                Log.e("TAG", "permision from user is the gps is permitted");
                                break;
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                // Location settings are not satisfied. But could be fixed by showing the user
                                // a dialog.
                                try {
                                    // Show the dialog by calling startResolutionForResult(),
                                    // and check the result in onActivityResult().
                                    status.startResolutionForResult(
                                            DriversOrderListing.this, 1000);
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        checkPermission();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public boolean isGPSEnabled(Context mContext)
    {
        LocationManager lm = (LocationManager)
                mContext.getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


}
