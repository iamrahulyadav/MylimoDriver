package ranglerz.mylimodriver.Notifications;

/**
 * Created by Shoaib Anwar on 14-Feb-18.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.WorkerThread;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import ranglerz.mylimodriver.AvailableOrdersActivity;
import ranglerz.mylimodriver.DriversOrderListing;
import ranglerz.mylimodriver.R;


public class MyFirebaseMessagingService extends FirebaseMessagingService {


    private static final String TAG = "MyFirebaseMsgService";

    @WorkerThread
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Displaying data in log
        //It is optional

       /* Log.e(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
        Log.e(TAG, "Notification Remote Message: " + remoteMessage);*/

        //Calling method to generate notification
        sendNotification(remoteMessage.getData().get("title"));

        Log.e("TAG", "the notication Body is: " + remoteMessage.getData().get("title"));
    }

    @Override
    public void handleIntent(Intent intent) {
        super.handleIntent(intent);


        String title = intent.getExtras().getString("gcm.notification.title");
        String body = intent.getExtras().getString("gcm.notification.body");

        Log.e("TAG: ", "Key is haree: " + title);
        Log.e("TAG: ", "Key is body: " + body);



        SharedPreferences upComingOrderNumber = getSharedPreferences("order", 0);
        int curentNumber = upComingOrderNumber.getInt("ordernumber", 0);
        SharedPreferences.Editor editor = upComingOrderNumber.edit();
        editor.putInt("ordernumber", curentNumber+1);
        editor.commit();




            for (String key : intent.getExtras().keySet()) {
                Object value = intent.getExtras().get(key);
                Log.e("TAG: ", "Key is haree: " + key + " Value is here: " + value);
                if (key.equals("title")){
                    NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancelAll();
                    sendNotification("New Order");
                }
            }

    }




    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(String messageBody) {



        Intent intent = new Intent(this, DriversOrderListing.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

                Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                R.drawable.cartype_icon_black);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.cartype_icon_black)
               // .setLargeIcon(icon)
                .setContentTitle("Mylimo")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

}
