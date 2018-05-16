package ranglerz.mylimodriver;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

import ranglerz.mylimodriver.Utilities.Utility;

public class DriverHistoryDetail extends AppCompatActivity {

    public static final int REQUEST_PERMISSION_CODE = 30;

    ImageView iv_upload_cnic;
    TextView tv_upload_text;
    private String  userChoosenTask;
    private Button bt_start_trip;

    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    Uri imageUri = null;
    Bitmap bitmap1;

    String pickupLatlng = "";

    TextView tv_user_full_name;
    TextView tv_date_time;
    TextView tv_pickup_city;
    TextView tv_pickup_detail;
    TextView tv_destinatoin_city;
    TextView tv_destinatoin_detail;
    TextView tv_number_of_seats;
    TextView tv_car_type;
    TextView tv_mobile_number;
    TextView tv_user_cnic;
    TextView tv_latlng;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_history_detail);

        init();
        uploadImageClickHandler();
        statTripHandler();
        /*showMap();


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            // only for gingerbread and newer versions
            if(checkPermission()){
                //permision granted

                Intent i = new Intent(AvailableOrdersActivity.this, GettingCurrentLatLngService.class);
                //startService(i);

            }else {
                requestPermission();
            }


        }else {

            Intent i = new Intent(AvailableOrdersActivity.this, GettingCurrentLatLngService.class);
            //startService(i);

        }*/



    }//end of on Creatre

    private void init(){
        iv_upload_cnic = (ImageView) findViewById(R.id.iv_upload_cnic);
        tv_upload_text = (TextView) findViewById(R.id.tv_upload_text);
        bt_start_trip = (Button) findViewById(R.id.bt_start_trip);
        // bt_show_map = (Button) findViewById(R.id.bt_show_map);

        tv_user_full_name = (TextView) findViewById(R.id.tv_user_full_name);
        tv_date_time = (TextView) findViewById(R.id.tv_date_time);
        tv_pickup_city = (TextView) findViewById(R.id.tv_pickup_city);
        tv_pickup_detail = (TextView) findViewById(R.id.tv_pickup_detail);
        tv_destinatoin_city = (TextView) findViewById(R.id.tv_destinatoin_city);
        tv_destinatoin_detail = (TextView) findViewById(R.id.tv_destinatoin_detail);
        tv_number_of_seats = (TextView) findViewById(R.id.tv_number_of_seats);
        tv_car_type = (TextView) findViewById(R.id.tv_car_type);
        tv_mobile_number = (TextView) findViewById(R.id.tv_mobile_number);
        tv_user_cnic = (TextView) findViewById(R.id.tv_user_cnic);
        tv_latlng = (TextView) findViewById(R.id.tv_latlng);

        //getting data from intent
        Intent intent = getIntent();
        String date = intent.getExtras().getString("date", "");
        String time = intent.getExtras().getString("time", "");
        String fullname = intent.getExtras().getString("fullname", "");
        String userid = intent.getExtras().getString("userid", "");
        String phone = intent.getExtras().getString("phone", "");
        String seats = intent.getExtras().getString("seats", "");
        String cnic = intent.getExtras().getString("cnic", "");
        pickupLatlng = intent.getExtras().getString("pickupLatlng", "");
        String pickupcity = intent.getExtras().getString("pickupcity", "");
        String pickupdetail = intent.getExtras().getString("pickupdetail", "");
        String destinationcity = intent.getExtras().getString("destinationcity", "");
        String destinationdetail = intent.getExtras().getString("destinationdetail", "");
        String reservation_id = intent.getExtras().getString("reservation_id", "");
        String orderid = intent.getExtras().getString("orderid", "");
        String carType = intent.getExtras().getString("cartype", "");


        Log.e("TAG", "the detail data date: " + date);
        Log.e("TAG", "the detail data time: " + time);
        Log.e("TAG", "the detail data fullname: " + fullname);
        Log.e("TAG", "the detail data userid: " + userid);
        Log.e("TAG", "the detail data phone: " + phone);
        Log.e("TAG", "the detail data seats: " + seats);
        Log.e("TAG", "the detail data cnic: " + cnic);
        Log.e("TAG", "the detail data pickupLatlng: " + pickupLatlng);
        Log.e("TAG", "the detail data pickupcity: " + pickupcity);
        Log.e("TAG", "the detail data pickupdetail: " + pickupdetail);
        Log.e("TAG", "the detail data destinationcity: " + destinationcity);
        Log.e("TAG", "the detail data destinationdetail: " + destinationdetail);
        Log.e("TAG", "the detail data reservation_id: " + reservation_id);
        Log.e("TAG", "the detail data orderid: " + orderid);
        Log.e("TAG", "the detail data carType: " + carType);


        tv_user_full_name.setText(fullname);
        tv_latlng.setText(pickupLatlng);
        tv_date_time.setText(date+", " + time);
        tv_pickup_city.setText(pickupcity);
        if (pickupdetail.length()<1) {
            tv_pickup_detail.setText("Not Provided");
        }else {
            tv_pickup_detail.setText(pickupdetail);
        }
        tv_destinatoin_city.setText(destinationcity);
        if (destinationdetail.length()<1) {
            tv_destinatoin_detail.setText("Not Provided");
        }else
        {
            tv_destinatoin_detail.setText(destinationdetail);
        }
        tv_number_of_seats.setText(seats);
        tv_car_type.setText(carType);
        tv_user_cnic.setText(cnic);
        tv_mobile_number.setText(phone);


    }

    // upload image click lisnter
    private void uploadImageClickHandler(){

        iv_upload_cnic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectImage();
            }
        });
    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Select Photo From Gallery",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(DriverHistoryDetail.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result= Utility.checkPermission(DriverHistoryDetail.this);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask ="Take Photo";
                    if(result)
                        cameraIntent();

                } else if (items[item].equals("Select Photo From Gallery")) {
                    userChoosenTask ="Select Photo From Gallery";
                    if(result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    private void cameraIntent()
    {


        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("TAg", "The Request code is: " + requestCode);

        //  if (resultCode == Activity.RESULT_OK) {
        if (requestCode == SELECT_FILE)
            onSelectFromGalleryResult(data);
        else if (requestCode == REQUEST_CAMERA)
            onCaptureImageResult(data);
    }

    //selecting image from galary
    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("", "onSelectFromGalleryResult: license file");
            imageUri = data.getData();
        }


     /*   Long tsLong = System.currentTimeMillis() / 1000;
        timestamp1 = tsLong.toString();*/

        try {
            //profileImg.setVisibility(View.VISIBLE);

            if (imageUri!=null) {
                bitmap1 = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                int h = 400; // height in pixels
                int w = 450; // width in pixels
                bitmap1 = Bitmap.createScaledBitmap(bitmap1, w, h, true);

                iv_upload_cnic.setImageBitmap(bitmap1);
                tv_upload_text.setVisibility(View.GONE);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //getting image form camera
    private void onCaptureImageResult(Intent data) {

        try {

            bitmap1 = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            int h = 400; // height in pixels
            int w = 450; // width in pixels
            bitmap1 = Bitmap.createScaledBitmap(bitmap1, w, h, true);
            Log.e("TAg", "selected image bitmap: " + bitmap1);
            iv_upload_cnic.setImageBitmap(bitmap1);

            tv_upload_text.setVisibility(View.GONE);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void statTripHandler(){
        bt_start_trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(DriverHistoryDetail.this, MapsActivity.class);
                i.putExtra("latlng", pickupLatlng);
                startActivity(i);
            }
        });
    }

   /* private void init(){

        bt_show_map = (Button) findViewById(R.id.bt_show_map);
    }


    private void showMap(){

        bt_show_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(AvailableOrdersActivity.this, MapsActivity.class);
                startActivity(i);
            }
        });
    }


    public boolean checkPermission() {

        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);

        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED ;

    }


    private void requestPermission() {

        ActivityCompat.requestPermissions(AvailableOrdersActivity.this, new String[]
                {
                        ACCESS_FINE_LOCATION
                }, REQUEST_PERMISSION_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case REQUEST_PERMISSION_CODE:

                if (grantResults.length > 0) {

                    boolean CameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean ReadContactsPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (CameraPermission && ReadContactsPermission) {


                        //Toast.makeText(Home.this, "Permission Granted", Toast.LENGTH_LONG).show();
                    }
                    else {
                        //Toast.makeText(Home.this,"Permission Denied",Toast.LENGTH_LONG).show();

                    }
                }

                break;
        }
    }


*/

}
