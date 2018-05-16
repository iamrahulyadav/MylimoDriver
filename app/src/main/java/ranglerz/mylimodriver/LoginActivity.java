package ranglerz.mylimodriver;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ranglerz.mylimodriver.Constants.APIURLs;
import ranglerz.mylimodriver.Services.GettingCurrentLatLngService;
import ranglerz.mylimodriver.VolleyLibraryFiles.AppSingleton;

import static android.icu.lang.UProperty.INT_START;

public class LoginActivity extends AppCompatActivity implements LocationListener{

    Button bt_register, bt_login;
    TextView tv_mobile_number, tv_khofiya_code, tv_forgot_password;
    EditText et_mobile_no, et_pincode;
    private ProgressBar progress_bar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
        btRegistrationClickHanlder();
        tvForgotPinClickHandler();
        btLoginHandler();

        Intent i = new Intent(LoginActivity.this, DriversOrderListing.class);
        //startActivity(i);
        //finish();

    }

    private void init() {

        Typeface tf = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/shoaib.ttf");
        bt_register = (Button) findViewById(R.id.bt_register);
        bt_login = (Button) findViewById(R.id.bt_login);
        tv_mobile_number = (TextView) findViewById(R.id.tv_mobile_number);
        tv_mobile_number.setTypeface(tf);
        tv_khofiya_code = (TextView) findViewById(R.id.tv_khofiya_code);
        tv_khofiya_code.setTypeface(tf);

        tv_forgot_password = (TextView) findViewById(R.id.tv_forgot_password);

        et_mobile_no = (EditText) findViewById(R.id.et_mobile_no);
        et_pincode = (EditText) findViewById(R.id.et_pincode);
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        progress_bar.bringToFront();


    }

    private void btRegistrationClickHanlder(){

        bt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               /* Intent gettingMobileNoActivity = new Intent(MyLoginActivity.this, GettingMobileNumberActivity.class);
                gettingMobileNoActivity.putExtra("registeration", "regi");
                startActivity(gettingMobileNoActivity);
                finish();*/
            }
        });
    }

    public void tvForgotPinClickHandler(){

        tv_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               /* Intent gettingMobileNoActivity = new Intent(MyLoginActivity.this, GettingMobileNumberActivity.class);
                gettingMobileNoActivity.putExtra("forgotpass", "forgotpass");
                startActivity(gettingMobileNoActivity);
                finish();*/
            }
        });
    }

    private void btLoginHandler(){

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (progress_bar.getVisibility() == View.GONE) {
                    final Animation animShake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);

                    String mobileNumber = et_mobile_no.getText().toString();
                    String pinCode = et_pincode.getText().toString();

                    if (mobileNumber.length() < 11) {
                        et_mobile_no.setError("Invalid Mobile No.");
                        et_mobile_no.setAnimation(animShake);

                    } else if (pinCode.length() < 4) {
                        et_pincode.setError("Invalid Pin");
                        et_pincode.setAnimation(animShake);
                    } else {

                        SharedPreferences sharedPreferences = getSharedPreferences("udid", 0);
                        String udid = sharedPreferences.getString("udid", null);
                        Log.e("TAG", "the user device udid is: " + udid);

                        loginingDriverService(mobileNumber, pinCode, udid);

                    }

                }else {


                }

            }
        });
    }

    //logining user serverice
    private void loginingDriverService(final String userphone, final String userpin, final String udid){

        // Tag used to cancel the request
        String cancel_req_tag = "register";
        progress_bar.setVisibility(View.VISIBLE);
        progress_bar.bringToFront();


        StringRequest strReq = new StringRequest(Request.Method.POST, APIURLs.loginURL , new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("TAG", "Login Response: " + response.toString());
                //hideDialog();
                progress_bar.setVisibility(View.GONE);
                if (response.length()>1) {

                    if (response.contains("div")){
                        Toast.makeText(LoginActivity.this, "Problem Connecting Server", Toast.LENGTH_SHORT).show();
                    }
                    else {

                    try {

                        JSONObject jObj = new JSONObject(response);
                        boolean error = jObj.getBoolean("error");

                        if (!error) {

                            String driver_id = jObj.getString("driver_id");
                            String driverName = jObj.getString("name");
                            String driverEmail = jObj.getString("email");
                            String driverPhone = jObj.getString("phone");
                            String driverPincode = jObj.getString("pincode");
                            String driverCNIC = jObj.getString("cnic");
                            String car_reg_no = jObj.getString("car_reg_no");
                            String license_no = jObj.getString("license_no");


                            Log.e("TAG", "the driver resutl id: " + driver_id);
                            Log.e("TAG", "the driver resutl Name: " + driverName);
                            Log.e("TAG", "the driver resutl Email: " + driverEmail);
                            Log.e("TAG", "the driver resutl Phone: " + driverPhone);
                            Log.e("TAG", "the driver resutl Pincode: " + driverPincode);
                            Log.e("TAG", "the driver resutl CNIC: " + driverCNIC);

                            Log.e("TAG", "the driver resutl Registration No: " + car_reg_no);
                            Log.e("TAG", "the driver resutl License No: " + license_no);


                            //adding data
                            SharedPreferences sharedPreferences = getSharedPreferences("driver", 0);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.clear();
                            editor.putString("driver_id", driver_id);
                            editor.putString("driverName", driverName);
                            editor.putString("driverEmail", driverEmail);
                            editor.putString("driverPhone", driverPhone);
                            editor.putString("driverPincode", driverPincode);
                            editor.putString("driverCNIC", driverCNIC);
                            editor.putString("car_reg_no", car_reg_no);
                            editor.putString("license_no", license_no);
                            editor.commit();
                            Intent orderBookingScreen = new Intent(LoginActivity.this, DriversOrderListing.class);
                            startActivity(orderBookingScreen);
                            finish();

                        } else {

                            String errorMsg = jObj.getString("msg");

                            Toast.makeText(getApplicationContext(), "Server Connectivity Error", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }}//end of if for server connecting faile
                }else {
                    Toast.makeText(getApplicationContext(), "Server Not Responding", Toast.LENGTH_LONG).show();
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



                params.put("phone", userphone);
                params.put("pincode", userpin);
                params.put("driver_ud_id", udid);


                return params;
            }
        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, cancel_req_tag);
    }//end of registration service

    @Override
    public void onBackPressed() {
        if (progress_bar.getVisibility() == View.VISIBLE){

        }else {
            super.onBackPressed();
        }
    }



    @Override
    public void onLocationChanged(Location location) {
        Log.e("TAG", "the change locatio is: " + location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

        Log.e("TAG", "provider status : " + s);

    }

    @Override
    public void onProviderEnabled(String s) {

        Log.e("TAG", "provider is : " + s);

    }

    @Override
    public void onProviderDisabled(String s) {

        Log.e("TAG", "provider is disable : " + s);

    }
}
