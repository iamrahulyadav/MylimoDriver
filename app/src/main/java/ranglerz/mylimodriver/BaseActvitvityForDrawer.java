package ranglerz.mylimodriver;


import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.design.widget.NavigationView;

import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ranglerz.mylimodriver.Constants.APIURLs;
import ranglerz.mylimodriver.Services.GettingCurrentLatLngService;
import ranglerz.mylimodriver.VolleyLibraryFiles.AppSingleton;


public class  BaseActvitvityForDrawer extends AppCompatActivity {

    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    MenuItem navLoginRegister;
    MenuItem navUsername;
    MenuItem navOrders;

    TextView tv_order_number;


    ProgressBar progress_bar;

    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initialize facebook sdk
       /* FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();*/
        setContentView(R.layout.activity_base_actvitvity_for_drawer);
        view = new View(this);


        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        progress_bar.bringToFront();


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.shitstuff) ;
        mNavigationView.setItemIconTintList(null);

        // get menu from navigationView
        Menu menu = mNavigationView.getMenu();

        // find MenuItem you want to change
        navUsername = menu.findItem(R.id.nav_item_home);
        navOrders = menu.findItem(R.id.nav_item_my_orders);
        View actionView = MenuItemCompat.getActionView(navOrders);
        tv_order_number = (TextView) actionView.findViewById(R.id.tv_order_number);
        tv_order_number.setVisibility(View.GONE);




        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawerLayout.closeDrawers();


                if (menuItem.getItemId() == R.id.nav_item_home) {
                    //home activity
                    //Toast.makeText(getApplicationContext(), "DashBoard", Toast.LENGTH_SHORT).show();

                }

                if (menuItem.getItemId() == R.id.nav_item_my_orders) {

                    Intent i = new Intent(BaseActvitvityForDrawer.this, MyHistoryScreen.class);
                    startActivity(i);




                }
                if (menuItem.getItemId() == R.id.nav_item_about){

                    Intent i = new Intent(BaseActvitvityForDrawer.this, AboutUs.class);
                    startActivity(i);




                }
                if (menuItem.getItemId() == R.id.nav_item_contact_us){

                    Intent i = new Intent(BaseActvitvityForDrawer.this, ContactUs.class);
                    startActivity(i);

                }

                if (menuItem.getItemId() == R.id.nav_item_logout){

                    //calling logout method

                    SharedPreferences sharedPreferences = getSharedPreferences("driver", 0);

                    String driver_id  = sharedPreferences.getString("driver_id", null);
                    Log.e("TAG", "The driver id is : " + driver_id);
                    navUsername.setTitle(driver_id);



                    logout(driver_id);

                }

                /*if (menuItem.getItemId() == R.id.feedback){

                    //Intent i = new Intent(BaseActvitvityForDrawer.this, Feedback.class);
                   // startActivity(i);

                }*/

                return false;
            }

        });

        /**
         * Setup Drawer Toggle of the Toolbar
         */

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout, toolbar,R.string.app_name,
                R.string.app_name);

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();



            SharedPreferences upComingOrderNumber = getSharedPreferences("order", 0);
            int order = upComingOrderNumber.getInt("ordernumber", 0);
            Log.e("TAg", "the order from sharepre: " + order);
            if (order!=0){
                String currentOrdrs = tv_order_number.getText().toString();

                //here shoulb be visibility Visibile
                tv_order_number.setVisibility(View.GONE);
                tv_order_number.setText(""+order);
                    SharedPreferences.Editor editor = upComingOrderNumber.edit();
                    editor.clear();
                    editor.putInt("ordernumber", order);


            }




    }//end on Create

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getSharedPreferences("driver", 0);

        String fullname  = sharedPreferences.getString("driverName", null);
        Log.e("TAG", "driver name is: " + fullname);
        navUsername.setTitle(fullname);


        Log.e("TAG", "the current availabel orders are: " + tv_order_number.getText());



    }


    private void logout(final String driver_id){



        // Tag used to cancel the request
        String cancel_req_tag = "register";
        progress_bar.setVisibility(View.VISIBLE);

        StringRequest strReq = new StringRequest(Request.Method.POST, APIURLs.driver_logout , new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("TAG", "Logout Response: " + response.toString());
                //hideDialog();
                progress_bar.setVisibility(View.GONE);
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {


                        Intent i = new Intent(BaseActvitvityForDrawer.this, LoginActivity.class);
                        startActivity(i);

                        SharedPreferences sharedPreferences = getSharedPreferences("driver", 0);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.commit();
                        finish();



                        stopService(new Intent(BaseActvitvityForDrawer.this, GettingCurrentLatLngService.class));

                        //logut sucessfully

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



                params.put("driver_id", driver_id);


                return params;
            }
        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, cancel_req_tag);
    }//end of logout service


}



