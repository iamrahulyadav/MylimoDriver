package ranglerz.mylimodriver.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import android.support.v7.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import ranglerz.mylimodriver.AvailableOrdersActivity;
import ranglerz.mylimodriver.GetterSetters.DriverOrdersGetterSetter;
import ranglerz.mylimodriver.R;

/**
 * Created by Shoaib Anwar on 14-Feb-18.
 */

public class DriverOrdersAdapter extends RecyclerView.Adapter<DriverOrdersAdapter.MyViewHolder>  {

    private ArrayList<DriverOrdersGetterSetter> userHisotry;
    private Context mContext;

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private boolean isLoadingAdded = false;
    private boolean isIdFojnd = false;

    private int lastPosition = -1;

    public DriverOrdersAdapter( Context context , ArrayList<DriverOrdersGetterSetter> adList ) {
        this.userHisotry = adList;
        this.mContext = context;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {


        protected TextView tv_date_time;
        protected TextView  tv_pickup_city;
        protected TextView  tv_pickup_detail;
        protected TextView  tv_dropoff_city;
        protected TextView  tv_dropoff_detail;
        protected  TextView tv_user_full_name;
        protected Button bt_view_detail;

        protected TextView tv_reservation_id;
        protected TextView tv_user_cnic;
        protected TextView tv_user_phone;
        protected TextView tv_user_email;
        protected TextView tv_user_id;
        protected TextView tv_price;
        protected TextView tv_seats;
        protected TextView tv_car_type;
        protected TextView tv_dropoff_latlng;
        protected TextView tv_pickup_latlng;
        protected TextView tv_order_id;
        private TextView tv_car_id;





        public MyViewHolder(final View view) {
            super(view);



            tv_date_time = (TextView) view.findViewById(R.id.tv_date_time) ;
            tv_pickup_city = (TextView) view.findViewById(R.id.tv_pickup_city) ;
            tv_pickup_detail = (TextView) view.findViewById(R.id.tv_pickup_detail) ;
            tv_dropoff_city = (TextView) view.findViewById(R.id.tv_dropoff_city) ;
            tv_dropoff_detail = (TextView) view.findViewById(R.id.tv_dropoff_detail) ;
            tv_user_full_name = (TextView) view.findViewById(R.id.tv_user_full_name) ;
            bt_view_detail = (Button) view.findViewById(R.id.bt_view_detail) ;

              tv_reservation_id = (TextView) view.findViewById(R.id.tv_reservation_id) ;
              tv_user_cnic = (TextView) view.findViewById(R.id.tv_user_cnic) ;
              tv_user_phone = (TextView) view.findViewById(R.id.tv_user_phone) ;
              tv_user_email = (TextView) view.findViewById(R.id.tv_user_email) ;
              tv_user_id = (TextView) view.findViewById(R.id.tv_user_id) ;
              tv_price = (TextView) view.findViewById(R.id.tv_price) ;
              tv_seats = (TextView) view.findViewById(R.id.tv_seats) ;
              tv_car_type = (TextView) view.findViewById(R.id.tv_car_type) ;
              tv_dropoff_latlng = (TextView) view.findViewById(R.id.tv_dropoff_latlng) ;
              tv_pickup_latlng = (TextView) view.findViewById(R.id.tv_pickup_latlng) ;
              tv_order_id = (TextView) view.findViewById(R.id.tv_order_id) ;
            tv_car_id = (TextView) view.findViewById(R.id.tv_car_id);


        }
    }




    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_driver_orders_layout, null);
        MyViewHolder mh = new MyViewHolder(v);
        return mh;


    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {


           final DriverOrdersGetterSetter ad = userHisotry.get(position);

            String date = ad.getDate();
            String[] calend = date.split("-");
            int month = Integer.parseInt(calend[1]);
            int dayofWeek = Integer.parseInt(calend[2]);
            String  day = calend[2];


            Calendar cal=Calendar.getInstance();
            SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
            cal.set(Calendar.MONTH, month -1);

            String month_name = month_date.format(cal.getTime());



            // Log.e("TAg", "the day is: " + dayname);
            month_name = month_name.substring(0,3);
            date = month_name + " " + day;

            String dateTime = date+ "," + " " + ad.getTime();
            holder.tv_date_time.setText(dateTime);
            holder.tv_pickup_city.setText(ad.getPickup_city());
            if (ad.getPickup_detail().length()<1){
                holder.tv_pickup_detail.setText("Not Provided");
            }else {
                holder.tv_pickup_detail.setText(ad.getPickup_detail());
            }
            holder.tv_dropoff_city.setText(ad.getDestination_city());
            if (ad.getDestination_detail().length()<1){
                holder.tv_dropoff_detail.setText("Not Provided");
            }
            else {
                holder.tv_dropoff_detail.setText(ad.getDestination_detail());
            }

            holder.tv_reservation_id.setText((ad.getReservationID()));
            holder.tv_user_id.setText((ad.getUser_id()));
            holder.tv_user_email.setText((ad.getEmail()));
            holder.tv_price.setText((ad.getPrice()));
            holder.tv_seats.setText((ad.getSeats()));
            holder.tv_car_type.setText(ad.getCar_type());
            //converting string lat and long to LatLng object
            double pickupLat = Double.parseDouble(ad.getPickup_lat());
            double pickupLng = Double.parseDouble(ad.getPickup_lng());
            double destinationLat = Double.parseDouble(ad.getDestination_lat());
            double destinationLng = Double.parseDouble(ad.getDestination_lng());
           final LatLng picupLatLng = new LatLng(pickupLat, pickupLng);
            LatLng destinationLatLng = new LatLng(destinationLat, destinationLng);

            holder.tv_pickup_latlng.setText(picupLatLng.toString());
            holder.tv_dropoff_latlng.setText(destinationLatLng.toString());
            holder.tv_user_phone.setText(ad.getPhone());
            holder.tv_user_full_name.setText(ad.getFullname());
            holder.tv_car_id.setText(ad.getFk_car_id());
            holder.bt_view_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent i = new Intent(mContext, AvailableOrdersActivity.class);
                    i.putExtra("date",  holder.tv_date_time.getText().toString());
                    i.putExtra("time", ad.getTime());
                    i.putExtra("fullname", ad.getFullname());
                    i.putExtra("userid", ad.getUser_id());
                    i.putExtra("phone", ad.getPhone());
                    i.putExtra("seats", ad.getSeats());
                    i.putExtra("cnic", ad.getCnic());
                    i.putExtra("pickupLatlng", picupLatLng.toString());
                    i.putExtra("pickupcity", ad.getPickup_city());
                    i.putExtra("pickupdetail", ad.getPickup_detail());
                    i.putExtra("destinationcity", ad.getDestination_city());
                    i.putExtra("destinationdetail", ad.getDestination_detail());
                    i.putExtra("reservation_id", ad.getReservationID());
                    i.putExtra("orderid", ad.getOrder_id());
                    i.putExtra("cartype", ad.getCar_type());
                    i.putExtra("car_id", ad.getFk_car_id());


                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(i);

                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return userHisotry.size();
    }

    @Override
    public int getItemViewType(int position) {


        return (position == userHisotry.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }


    public String timeFormteIn12Hr(String time){

        DateFormat f1 = new SimpleDateFormat("kk:mm");
        Date d = null;
        try {
            d = f1.parse(time);
            DateFormat f2 = new SimpleDateFormat("h:mm a");
            time = f2.format(d).toUpperCase(); // "12:18am"

        } catch (ParseException e) {

            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return time;

    }

}
