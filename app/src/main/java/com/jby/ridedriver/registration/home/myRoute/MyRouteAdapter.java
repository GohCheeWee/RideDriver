package com.jby.ridedriver.registration.home.myRoute;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jby.ridedriver.R;
import com.jby.ridedriver.registration.home.myRoute.object.MatchRouteObject;
import com.jby.ridedriver.registration.others.SquareHeightLinearLayout;

import java.util.ArrayList;

public class MyRouteAdapter extends BaseAdapter{
    private Context context;
    private ArrayList<MatchRouteObject> matchRouteObjectArrayList;
    private MyRouteAdapterCallBack myRouteAdapterCallBack;

    public MyRouteAdapter(Context context, ArrayList<MatchRouteObject> matchRouteObjectArrayList, MyRouteAdapterCallBack myRouteAdapterCallBack){

        this.context = context;
        this.matchRouteObjectArrayList = matchRouteObjectArrayList;
        this.myRouteAdapterCallBack = myRouteAdapterCallBack;

    }
    @Override
    public int getCount() {
        return matchRouteObjectArrayList.size();
    }

    @Override
    public MatchRouteObject getItem(int i) {
        return matchRouteObjectArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (view == null){
            view = View.inflate(this.context, R.layout.fragment_my_route_list_view_layout, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        MatchRouteObject object = getItem(i);
        String date = object.getDate() + " " + object.getTime();
        String match_ride = object.getMatchNum() + " matches";

        viewHolder.date.setText(date);
        viewHolder.note.setText(object.getNote());
        viewHolder.title.setText(object.getRouteTitle());
        viewHolder.pickup.setText(object.getPickUpAddress());
        viewHolder.dropoff.setText(object.getDropOffAddress());
        viewHolder.match_number.setText(match_ride);

        viewHolder.viewRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickEffect(viewHolder.viewRoute);
                myRouteAdapterCallBack.viewRoute();
            }
        });
        viewHolder.deleteRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickEffect(viewHolder.deleteRoute);
                String routeID = matchRouteObjectArrayList.get(i).getRouteID();
                myRouteAdapterCallBack.deleteRoute(routeID, i);
            }
        });
        viewHolder.matchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickEffect(viewHolder.matchLayout);
                myRouteAdapterCallBack.openMatchRider(i);
            }
        });
        return view;
    }



    public interface MyRouteAdapterCallBack{
        void deleteRoute(String routeID, int position);
        void openMatchRider(int position);
        void viewRoute();
    }

    //click effect
    private void clickEffect(View view){
        Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
        animation1.setDuration(500);
        view.startAnimation(animation1);
    }

    private static class ViewHolder{
        private TextView title, pickup, dropoff, date, note, match_number;
        private SquareHeightLinearLayout viewRoute, deleteRoute;
        private RelativeLayout matchLayout;

        ViewHolder (View view){
            pickup = (TextView)view.findViewById(R.id.fragment_my_route_list_view_pick_up);
            dropoff = (TextView)view.findViewById(R.id.fragment_my_route_list_view_drop_off);
            date = (TextView)view.findViewById(R.id.fragment_my_route_list_view_date);
            note = (TextView)view.findViewById(R.id.fragment_my_route_list_view_note);
            match_number = (TextView)view.findViewById(R.id.fragment_my_route_list_view_match_number);
            title = (TextView)view.findViewById(R.id.fragment_my_route_list_view_title);

            viewRoute = (SquareHeightLinearLayout)view.findViewById(R.id.fragment_my_route_view_layout);
            deleteRoute = (SquareHeightLinearLayout)view.findViewById(R.id.fragment_my_route_delete_layout);
            matchLayout = (RelativeLayout)view.findViewById(R.id.fragment_my_route_list_view_match_layout);
        }
    }
}
