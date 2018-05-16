package ranglerz.mylimodriver;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class SplashScreen extends AppCompatActivity {

    private static int SplashScreenTimeOut = 3000;//3 seconds8
    private int timer = 3;
    Handler mHandler;
    //TextView tv_please_wait;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mHandler = new Handler();
        useHandler();
        // tv_please_wait = (TextView) findViewById(R.id.tv_please_wait);



    }

    //Thread for starting mainActivity
    private Runnable mRunnableStartMainActivity = new Runnable() {
        @Override
        public void run() {
            Log.d("Handler", " Calls");
            timer--;
            mHandler = new Handler();
            mHandler.postDelayed(this, 1000);

            if (timer == 2) {
                //tv_please_wait.setText("Please Wait...");
            }
            if (timer == 1) {
                //tv_please_wait.setText("Please Wait.");
            }
            if (timer == 0) {

                SharedPreferences sharedPreferences = getSharedPreferences("driver", 0);
                if (sharedPreferences!=null){
                    String driver_id =  sharedPreferences.getString("driver_id", "null");
                    if (!driver_id.equals("null")){

                        SharedPreferences sharedPreferencesDriving = getSharedPreferences("driving", 0);
                        if (sharedPreferencesDriving!=null){
                           String mDriver_id = sharedPreferencesDriving.getString("d_id", null);
                           Log.e("TAG", "the driver id is: " + mDriver_id);
                            if (mDriver_id!=null){

                                Intent i = new Intent(SplashScreen.this, MpasForStartTrip.class);
                                startActivity(i);
                                finish();

                            }else {

                                Intent i = new Intent(SplashScreen.this, DriversOrderListing.class);
                                startActivity(i);
                                finish();
                            }
                        }

                    }
                    else {

                        Intent i = new Intent(SplashScreen.this, LoginActivity.class);

                        startActivity(i);
                        finish();
                    }
                }
            }
        }
    };


    //handler for the starign activity
    Handler newHandler;
    public void useHandler(){

        newHandler = new Handler();
        newHandler.postDelayed(mRunnableStartMainActivity, 1000);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mRunnableStartMainActivity);
    }
}//***************** Shoaib Anwar ********************