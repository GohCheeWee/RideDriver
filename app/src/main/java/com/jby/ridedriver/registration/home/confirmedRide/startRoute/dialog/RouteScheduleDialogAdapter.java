package com.jby.ridedriver.registration.home.confirmedRide.startRoute.dialog;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jby.ridedriver.R;
import com.jby.ridedriver.registration.home.confirmedRide.dialog.ShowConfirmedRiderObject;
import com.jby.ridedriver.registration.home.confirmedRide.startRoute.object.StartRouteRiderObject;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class RouteScheduleDialogAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<StartRouteRiderObject> routeScheduleDialogObjectArrayList;
    private RouteScheduleDialogAdapterCallBack routeScheduleDialogAdapterCallBack;
    private static String prefix = "http://188.166.186.198/~cheewee/ride/frontend/user/profile/profile_picture/";
    private int clickPosition = -1;

    public RouteScheduleDialogAdapter(
            Context context,
            ArrayList<StartRouteRiderObject> routeScheduleDialogObjectArrayList,
            RouteScheduleDialogAdapterCallBack routeScheduleDialogAdapterCallBack){

        this.context = context;
        this.routeScheduleDialogObjectArrayList = routeScheduleDialogObjectArrayList;
        this.routeScheduleDialogAdapterCallBack = routeScheduleDialogAdapterCallBack;
        
    }
    @Override
    public int getCount() {
        return routeScheduleDialogObjectArrayList.size();
    }

    @Override
    public StartRouteRiderObject getItem(int i) {
        return routeScheduleDialogObjectArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null){
            view = View.inflate(this.context, R.layout.start_route_activity_route_schedule_dialog_list_view_item, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        StartRouteRiderObject object = getItem(i);
        String profilePictureUrl = prefix + object.getProfile_pic();
        String status = object.getStatus();
        String address;
        double distance;
        DecimalFormat distanceFormat = new DecimalFormat("0.00");

        if(status.equals("5")){
            address = object.getDropOffAddress();
            distance = object.getDropOffDistance();

            if(clickPosition != -1 && clickPosition == i )
                viewHolder.parentLayout.setBackgroundColor(context.getResources().getColor(R.color.background_white));
            else
                viewHolder.parentLayout.setBackgroundColor(context.getResources().getColor(R.color.white));
                viewHolder.parentLayout.setEnabled(true);

        }
        else{
            address = object.getPickUpAddress();
            distance = object.getPickUpDistance();

            if(clickPosition != -1 && clickPosition == i )
                viewHolder.parentLayout.setBackgroundColor(context.getResources().getColor(R.color.background_white));
            else
                viewHolder.parentLayout.setBackgroundColor(context.getResources().getColor(R.color.white));

                viewHolder.parentLayout.setEnabled(true);
        }

        String stringDistance = distanceFormat.format(distance) + " KM";
        viewHolder.name.setText(object.getRider_name());
        viewHolder.address.setText(address);
        viewHolder.distance.setText(stringDistance);
        Picasso.get()
                .load(profilePictureUrl)
                .centerCrop()
                .fit()
                .error(R.drawable.loading_gif)
                .into(viewHolder.profilePicture);

        viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickEffect(view);
                routeScheduleDialogAdapterCallBack.selectPosition(i);
                // for click background purpose
                clickPosition = i;
                notifyDataSetChanged();
            }
        });
        return view;
    }

    //click effect
    private void clickEffect(View view){
        Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
        animation1.setDuration(500);
        view.startAnimation(animation1);
    }

    public interface RouteScheduleDialogAdapterCallBack{
        void selectPosition(int position);
    }

    private static class ViewHolder{
        private TextView name, address, distance, status;
        private CircleImageView profilePicture;
        private RelativeLayout parentLayout;


        ViewHolder (View view){
            name = (TextView)view.findViewById(R.id.route_schedule_dialog_list_view_user_name);
            address = (TextView)view.findViewById(R.id.route_schedule_dialog_list_view_user_address);
            distance = (TextView)view.findViewById(R.id.route_schedule_dialog_list_view_user_distance);
            parentLayout = (RelativeLayout)view.findViewById(R.id.route_schedule_dialog_list_view_parent_layout);
            profilePicture = (CircleImageView) view.findViewById(R.id.route_schedule_dialog_list_view_user_icon);
        }
    }
}
