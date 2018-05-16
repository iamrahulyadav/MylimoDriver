package ranglerz.mylimodriver.Services;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
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
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ranglerz.mylimodriver.AvailableOrdersActivity;
import ranglerz.mylimodriver.Constants.APIURLs;
import ranglerz.mylimodriver.LoginActivity;
import ranglerz.mylimodriver.R;
import ranglerz.mylimodriver.VolleyLibraryFiles.AppSingleton;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;

public class GettingCurrentLatLngService extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener{

    public static Double lat, lang;
    public static LatLng userCurrentLocation;

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;

    Handler handler;

    private int sendingLatLngtime = 30;
    Handler mHandlerSendingLatLng;

    String DRIVER_ID, LAT, LNG;

    public static final int REQUEST_PERMISSION_CODE = 30;

    public GettingCurrentLatLngService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        googleApiClientCode();
        //useHandler();

        return super.onStartCommand(intent, flags, startId);


    }

    public void googleApiClientCode() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();

        } else
            Toast.makeText(this, "Not Connected!", Toast.LENGTH_SHORT).show();

    }

    //


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        settingRequest();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Connection Suspended!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection Failed!", Toast.LENGTH_SHORT).show();
        if (connectionResult.hasResolution()) {

        } else {
            Log.i("Current Location", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    /*Method to get the enable location settings dialog*/
    public void settingRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);    // 10 seconds, in milliseconds
        mLocationRequest.setFastestInterval(1000);   // 1 second, in milliseconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.
                        getLocation();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.

                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        break;
                }
            }

        });
    }


    public void getLocation() {
        Log.e("tag", "check permission 4344");


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (mLastLocation != null) {
            lat = mLastLocation.getLatitude();
            lang = mLastLocation.getLongitude();


            userCurrentLocation = new LatLng(lat, lang);

            Log.e("tag", "check permission");


            //getAddress(this , lat , lang);
            Log.e("tag ", "The current lat are shoaib ajsflkjasl flkajdlf " + lat);
            Log.e("tag ", "The current lng are " + lang);


            SharedPreferences sharedPreferences = getSharedPreferences("driver", 0);

            if (sharedPreferences!=null){

                String driver_id = sharedPreferences.getString("driver_id", "");
                Log.e("TAG", "the driver id is: " + driver_id);
                //updationg user current location on serve

                String lati = String.valueOf(lat);
                String lngi = String.valueOf(lang);
                updateLanLngToServer(driver_id, lati, lngi);

            }

           // getAddressApi();
            //getCitiesService();


        } else {
                /*if there is no last known location. Which means the device has no data for the loction currently.
                * So we will get the current location.
                * For this we'll implement Location Listener and override onLocationChanged*/
            Log.i("Current Location", "No data for location found");

            if (!mGoogleApiClient.isConnected())
                mGoogleApiClient.connect();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);


    }
    }


    /*When Location changes, this method get called. */
    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;

        lat =  mLastLocation.getLatitude();
        lang = mLastLocation.getLongitude();

        Log.e("tag " , "lat onLocationChange "+lat);
        Log.e("tag " , "lang onLocationChange "+lang);
    }

    //Thread for starting mainActivity
    private Runnable mRunnableStartMainActivity = new Runnable() {
        @Override
        public void run() {
            Log.d("Handler", " Calls location chage runaper");

            handler = new Handler();
            handler.postDelayed(this, 10000);
            getLocation();

        }
    };


    //handler for the starign activity
    Handler newHandler;
    public void useHandler(){

        newHandler = new Handler();
        newHandler.postDelayed(mRunnableStartMainActivity, 1000);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mHandlerSendingLatLng.removeCallbacks(mRunnableStartMainActivity);
    }

    private void sendingUpdateOfCurrentLocationToServer(final String driver_id, final String ride_lat, final String ride_lng){


        // Tag used to cancel the request
        String cancel_req_tag = "register";

        Log.e("TAG", "to send lat are: " + ride_lat);
        Log.e("TAG", "to send lng are: " + ride_lng);

        StringRequest strReq = new StringRequest(Request.Method.POST, APIURLs.driver_location , new Response.Listener<String>() {

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

                //hideDialog();

            }
        }) {



            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url


                Map<String, String> params = new HashMap<String, String>();

                params.put("driver_id", driver_id);
                params.put("current_lat", ride_lat);
                params.put("current_lng", ride_lng);



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

    public boolean isInternetOn() {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {


            return true;

        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {


            return false;
        }
        return false;
    }

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

                mHandlerSendingLatLng.removeCallbacks(mRunableSendLatLng);
                sendingLatLngtime = 30;

                Log.e("TAG", " here are the latlng lat: " + LAT);
                Log.e("TAG", " here are the latlng lng: " + LNG);
                Log.e("TAG", " here are the latlng: " + DRIVER_ID);

                if (isInternetOn()) {

                    sendingUpdateOfCurrentLocationToServer(DRIVER_ID, LAT, LNG);

                    stopService(new Intent(GettingCurrentLatLngService.this, GettingCurrentLatLngService.class));
                    startService(new Intent(GettingCurrentLatLngService.this, GettingCurrentLatLngService.class));
                }
            }
        }
    };

    private void updateLanLngToServer(String id, String lat, String lng){

        DRIVER_ID = id;
        LAT = lat;
        LNG = lng;
        mHandlerSendingLatLng = new Handler();
        mHandlerSendingLatLng.postDelayed(mRunableSendLatLng, 1000);

    }

}



