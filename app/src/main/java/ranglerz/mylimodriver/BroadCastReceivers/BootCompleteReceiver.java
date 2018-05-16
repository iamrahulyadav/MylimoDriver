package ranglerz.mylimodriver.BroadCastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import ranglerz.mylimodriver.Services.GettingCurrentLatLngService;


public class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("driver", 0);
        if (sharedPreferences!=null){
            String driver_id = sharedPreferences.getString("driver_id", "");
            if (driver_id.length()>1){

                Intent service = new Intent(context, GettingCurrentLatLngService.class);
                context.startService(service);
            }
        }


    }

}
