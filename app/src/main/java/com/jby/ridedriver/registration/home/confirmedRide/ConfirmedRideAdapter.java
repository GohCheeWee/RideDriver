package com.jby.ridedriver.registration.home.confirmedRide;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jby.ridedriver.R;
import com.jby.ridedriver.registration.home.myRoute.object.MatchRouteObject;
import com.jby.ridedriver.registration.others.SquareHeightLinearLayout;

import java.util.ArrayList;

public class ConfirmedRideAdapter extends BaseAdapter{
    private Context context;
    private ArrayList<ConfirmedRideObject> confirmedRideObjectArrayList;
    private ConfirmRideAdapterCallBack confirmRideAdaapterCallBack;
    String status;

    public ConfirmedRideAdapter(Context context, ArrayList<ConfirmedRideObject> confirmedRideObjectArrayList, ConfirmRideAdapterCallBack confirmRideAdaapterCallBack){

        this.context = context;
        this.confirmedRideObjectArrayList = confirmedRideObjectArrayList;
        this.confirmRideAdaapterCallBack = confirmRideAdaapterCallBack;

    }
    @Override
    public int getCount() {
        return confirmedRideObjectArrayList.size();
    }

    @Override
    public ConfirmedRideObject getItem(int i) {
        return confirmedRideObjectArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (view == null){
            view = View.inflate(this.context, R.layout.fragment_confirm_ride_list_view_layout, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        final ConfirmedRideObject object = getItem(i);
        String date = object.getDate() + " " + object.getTime();
        String estiamte_earn = "RM " + object.getEstimate_earn();
        String confirmRiderNumber = object.getConfirm_rider();
        String numberOfPeople = object.getNumber_people();
        if(confirmRiderNumber.equals(numberOfPeople))
            viewHolder.confirm_rider_num.setTextColor(context.getResources().getColor(R.color.red));
        else
            viewHolder.confirm_rider_num.setTextColor(context.getResources().getColor(R.color.green));
        String confirmRiderNum = confirmRiderNumber + "/" + numberOfPeople;

        status = object.getStatus();

        if(!status.equals("2"))
            status = "Ongoing";
        else
            status = "Start my journey";


        viewHolder.date.setText(date);
        viewHolder.note.setText(object.getNote());
        viewHolder.title.setText(object.getTitle());
        viewHolder.pickup.setText(object.getPick_up());
        viewHolder.dropoff.setText(object.getDrop_off());
        viewHolder.estimate_earn.setText(estiamte_earn);
        viewHolder.confirm_rider_num.setText(confirmRiderNum);
        viewHolder.startMyJourney.setText(status);
        viewHolder.viewDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickEffect(view);
            }
        });

        viewHolder.startMyJourney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickEffect(view);
                if(confirmedRideObjectArrayList.get(i).getStatus().equals("2"))
                    confirmRideAdaapterCallBack.startRoute(i);
                else
                    confirmRideAdaapterCallBack.continueMyRoute(i);
            }
        });

        viewHolder.viewDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmRideAdaapterCallBack.viewConfirmedRider(object.getDriver_ride_id());
            }
        });

        return view;
    }



    public interface ConfirmRideAdapterCallBack{
        void startRoute(int position);
        void viewConfirmedRider(String driverRideID);
        void continueMyRoute(int position);
    }

    //click effect
    private void clickEffect(View view){
        Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
        animation1.setDuration(500);
        view.startAnimation(animation1);
    }

    private static class ViewHolder{
        private TextView title, pickup, dropoff, date, note, confirm_rider_num, estimate_earn, viewDetail;
        private TextView startMyJourney;
        ViewHolder (View view){
            pickup = (TextView)view.findViewById(R.id.fragment_confirm_ride_list_view_pick_up);
            dropoff = (TextView)view.findViewById(R.id.fragment_confirm_ride_list_view_drop_off);
            date = (TextView)view.findViewById(R.id.fragment_confirm_ride_list_view_date);
            note = (TextView)view.findViewById(R.id.fragment_confirm_ride_list_view_note);
            confirm_rider_num = (TextView)view.findViewById(R.id.fragment_confirm_ride_list_view_confirm_rider_num);
            title = (TextView)view.findViewById(R.id.fragment_confirm_ride_list_view_title);
            viewDetail = (TextView)view.findViewById(R.id.fragment_confirm_ride_list_view_confirm_rider_num_detail);
            estimate_earn = (TextView)view.findViewById(R.id.fragment_confirm_ride_list_view_estimate_earn);

            startMyJourney = (TextView)view.findViewById(R.id.fragment_confirm_ride_list_view_start_button);
        }
    }
}
