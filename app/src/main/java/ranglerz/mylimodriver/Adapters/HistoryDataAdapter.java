package ranglerz.mylimodriver.Adapters;

import android.app.Activity;
import android.content.Context;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
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

import ranglerz.mylimodriver.AvailableOrdersActivity;
import ranglerz.mylimodriver.DriverHistoryDetail;
import ranglerz.mylimodriver.R;
import ranglerz.mylimodriver.GetterSetters.HistoryData;


/**
 * Created by Shoaib Anwar on 13-Feb-18.
 */

public class HistoryDataAdapter extends RecyclerView.Adapter<HistoryDataAdapter.MyViewHolder>  {

    private ArrayList<HistoryData> userHisotry;
    private Activity mContext;

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private boolean isLoadingAdded = false;
    private boolean isIdFojnd = false;

    private int lastPosition = -1;

    public HistoryDataAdapter( Activity context , ArrayList<HistoryData> adList ) {
        this.userHisotry = adList;
        this.mContext = context;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {


        protected TextView tvDate;
        protected TextView tvTime;
        protected TextView tvFromCity;
        protected TextView tvToCity;
        protected TextView tvPrice;


        protected TextView tv_rider_seats;
        protected TextView tv_rider_destination_city;
        protected TextView tv_rider_pickup_city;
        protected TextView tv_rider_destination_detail;
        protected TextView tv_rider_destination_lng;
        protected TextView tv_rider_destination_lat;
        protected TextView tv_rider_pickup_detail;
        protected TextView tv_rider_pickup_lng;
        protected TextView tv_rider_pickup_lat;
        protected TextView tv_rider_car_type;
        protected TextView tv_rider_cnic;
        protected TextView tv_rider_user_id;
        protected TextView tv_rider_email;
        protected TextView tv_rider_phone;
        protected TextView tv_rider_full_name;
        protected TextView created_at;
        protected TextView reservation_id;
        protected TextView fk_car_id;
        protected TextView order_id;
        protected TextView fk_user_id;
        protected TextView date_slute;

        protected Button bt_view_detail;


        public MyViewHolder(final View view) {
            super(view);

            tvDate =  (TextView) view.findViewById(R.id.tv_date);
            tvTime = (TextView) view.findViewById(R.id.tv_time);
            tvFromCity = (TextView) view.findViewById(R.id.tv_from_city);
            tvToCity = (TextView) view.findViewById(R.id.tv_to_city);
            tvPrice = (TextView) view.findViewById(R.id.tv_price);
            tv_rider_seats = (TextView) view.findViewById(R.id.tv_rider_seats);
            tv_rider_destination_city = (TextView) view.findViewById(R.id.tv_rider_destination_city);
            tv_rider_pickup_city = (TextView) view.findViewById(R.id.tv_rider_pickup_city);
            tv_rider_destination_detail = (TextView) view.findViewById(R.id.tv_rider_destination_detail);
            tv_rider_destination_lng = (TextView) view.findViewById(R.id.tv_rider_destination_lng);
            tv_rider_destination_lat = (TextView) view.findViewById(R.id.tv_rider_destination_lat);
            tv_rider_pickup_detail = (TextView) view.findViewById(R.id.tv_rider_pickup_detail);
            tv_rider_pickup_lng = (TextView) view.findViewById(R.id.tv_rider_pickup_lng);
            tv_rider_pickup_lat = (TextView) view.findViewById(R.id.tv_rider_pickup_lat);
            tv_rider_car_type = (TextView) view.findViewById(R.id.tv_rider_car_type);
            tv_rider_cnic = (TextView) view.findViewById(R.id.tv_rider_cnic);
            tv_rider_user_id = (TextView) view.findViewById(R.id.tv_rider_user_id);
            tv_rider_email = (TextView) view.findViewById(R.id.tv_rider_email);
            tv_rider_phone = (TextView) view.findViewById(R.id.tv_rider_phone);
            tv_rider_full_name = (TextView) view.findViewById(R.id.tv_rider_full_name);
            created_at = (TextView) view.findViewById(R.id.created_at);
            reservation_id = (TextView) view.findViewById(R.id.reservation_id);
            fk_car_id = (TextView) view.findViewById(R.id.fk_car_id);
            order_id = (TextView) view.findViewById(R.id.order_id);
            fk_user_id = (TextView) view.findViewById(R.id.fk_user_id);
            date_slute = (TextView) view.findViewById(R.id.date_slute);

            bt_view_detail = (Button) view.findViewById(R.id.bt_view_detail);




        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_history_screen, null);
        MyViewHolder mh = new MyViewHolder(v);
        return mh;


    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {


            HistoryData ad = userHisotry.get(position);

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

            date = month_name + " " + day;

            holder.tvDate.setText(date);

            holder.tvTime.setText(ad.getTime());

            holder.tvFromCity.setText(ad.getPickup_city());
            holder.tvToCity.setText(ad.getDestination_city());
            holder.tvPrice.setText(ad.getPrice());
            holder.reservation_id.setText(ad.getReservation_id());

            holder.reservation_id.setText(ad.getReservation_id());
            holder.date_slute.setText(ad.getDate_slaut());
            holder.fk_user_id.setText(ad.getFk_car_id());
            holder.order_id.setText(ad.getOrder_id());
            holder.fk_car_id.setText(ad.getFk_car_id());
            holder.created_at.setText(ad.getCreated_at());
            holder.tv_rider_full_name.setText(ad.getFullname());
            holder.tv_rider_phone.setText(ad.getPhone());
            holder.tv_rider_email.setText(ad.getEmail());
            holder.tv_rider_user_id.setText(ad.getUser_id());
            holder.tv_rider_cnic.setText(ad.getCnic());
            holder.tv_rider_car_type.setText(ad.getCar_type());
            holder.tv_rider_pickup_lat.setText(ad.getPickup_lat());
            holder.tv_rider_pickup_lng.setText(ad.getPickup_lng());
            holder.tv_rider_pickup_detail.setText(ad.getPickup_detail());
            holder.tv_rider_destination_lat.setText(ad.getDestination_lat());

            holder.tv_rider_destination_lng.setText(ad.getPickup_lng());
            holder.tv_rider_destination_detail.setText(ad.getDestination_detail());
            holder.tv_rider_pickup_city.setText(ad.getPickup_city());
            holder.tv_rider_destination_city.setText(ad.getDestination_city());
            holder.tv_rider_seats.setText(ad.getSeats());



            holder.bt_view_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    Intent detailActivity = new Intent(mContext, DriverHistoryDetail.class);
                    detailActivity.putExtra("date",  holder.tvDate.getText().toString());
                    detailActivity.putExtra("time", holder.tvTime.getText().toString());
                    detailActivity.putExtra("fullname", holder.tv_rider_full_name.getText().toString());
                    detailActivity.putExtra("userid", holder.tv_rider_user_id.getText().toString());
                    detailActivity.putExtra("phone", holder.tv_rider_phone.getText().toString());
                    detailActivity.putExtra("seats", holder.tv_rider_seats.getText().toString());
                    detailActivity.putExtra("cnic", holder.tv_rider_cnic.getText().toString());
                    detailActivity.putExtra("pickupLatlng", holder.tv_rider_pickup_lat.getText().toString());
                    detailActivity.putExtra("pickupcity", holder.tv_rider_pickup_city.getText().toString());
                    detailActivity.putExtra("pickupdetail", holder.tv_rider_pickup_detail.getText().toString());
                    detailActivity.putExtra("destinationcity", holder.tv_rider_destination_city.getText().toString());
                    detailActivity.putExtra("destinationdetail", holder.tv_rider_destination_detail.getText().toString());
                    detailActivity.putExtra("reservation_id", holder.reservation_id.getText().toString());
                    detailActivity.putExtra("orderid", holder.order_id.getText().toString());
                    detailActivity.putExtra("cartype", holder.tv_rider_car_type.getText().toString());
                    detailActivity.putExtra("car_id", holder.fk_car_id.getText().toString());

                    detailActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(detailActivity);
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
