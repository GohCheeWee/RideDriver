package com.jby.ridedriver.registration.home.myRoute.matchedRiderDetail;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jby.ridedriver.R;
import com.jby.ridedriver.registration.home.myRoute.object.MatchRouteObject;
import com.jby.ridedriver.registration.others.SquareHeightLinearLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MatchRiderDetailAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<MatchRiderDetailObject> matchRouteObjectArrayList;
    private MatchRiderDetailAdapterCallBack matchRiderDetailAdapterCallBack;
    private static String prefix = "http://188.166.186.198/~cheewee/ride/frontend/user/profile/profile_picture/";

    public MatchRiderDetailAdapter(Context context, ArrayList<MatchRiderDetailObject> matchRouteObjectArrayList, MatchRiderDetailAdapterCallBack matchRiderDetailAdapterCallBack){

        this.context = context;
        this.matchRouteObjectArrayList = matchRouteObjectArrayList;
        this.matchRiderDetailAdapterCallBack = matchRiderDetailAdapterCallBack;

    }
    @Override
    public int getCount() {
        return matchRouteObjectArrayList.size();
    }

    @Override
    public MatchRiderDetailObject getItem(int i) {
        return matchRouteObjectArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null){
            view = View.inflate(this.context, R.layout.match_rider_detail_list_view_layout, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        MatchRiderDetailObject object = getItem(i);
        String fare = "RM " + object.getFare();
        String profilePicture = prefix + object.getProfile_picture();
        String paymentMethod = object.getPayment_method();
        String note = object.getNote();
        String status = object.getStatus();

        Picasso.get()
                .load(profilePicture)
                .centerCrop()
                .fit()
                .error(R.drawable.loading_gif)
                .into(viewHolder.profilePicture);

        if(object.getGender().equals("1"))
            viewHolder.gender.setImageDrawable(context.getResources().getDrawable(R.drawable.activity_profile_male_icon));
        else
            viewHolder.gender.setImageDrawable(context.getResources().getDrawable(R.drawable.activity_profile_female_icon));

        if(paymentMethod.equals("1"))
            paymentMethod = "Cash";
        else
            paymentMethod = "RidePay";

        if(note.equals("-")){
            note = "None";
        }
        if(status.equals("1")){
            viewHolder.buttonAccept.setEnabled(true);
            viewHolder.buttonAccept.setText(context.getResources().getString(R.string.match_rider_detail_list_view_layout_button));
            viewHolder.buttonAccept.setTextColor(context.getResources().getColor(R.color.red));
        }
        else{
            viewHolder.buttonAccept.setEnabled(false);
            viewHolder.buttonAccept.setText(context.getResources().getString(R.string.match_rider_detail_list_view_layout_disable_button));
            viewHolder.buttonAccept.setTextColor(context.getResources().getColor(R.color.disable_color));
        }
        viewHolder.payment_method.setText(paymentMethod);
        viewHolder.note.setText(note);
        viewHolder.name.setText(object.getName());
        viewHolder.drop_off.setText(object.getDrop_off());
        viewHolder.pickup.setText(object.getPickup());
        viewHolder.fare.setText(fare);


        viewHolder.profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickEffect(view);
                matchRiderDetailAdapterCallBack.viewRiderProfile(matchRouteObjectArrayList.get(i).getUserId());
            }
        });

        viewHolder.buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickEffect(view);
                matchRiderDetailAdapterCallBack.acceptRider(matchRouteObjectArrayList.get(i).getUserId(),
                        matchRouteObjectArrayList.get(i).getRouteID()
                , i);
            }
        });
        return view;
    }

    public interface MatchRiderDetailAdapterCallBack{
        void acceptRider(String rider_id, String rider_ride_id, int position);
        void viewRiderProfile(String riderID);
    }

    //click effect
    private void clickEffect(View view){
        Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
        animation1.setDuration(500);
        view.startAnimation(animation1);
    }

    private static class ViewHolder{
        private TextView name, pickup, drop_off, note, payment_method, fare;
        private CircleImageView profilePicture;
        private TextView buttonAccept;
        private ImageView gender;

        ViewHolder (View view){
            name = (TextView)view.findViewById(R.id.match_rider_detail_list_view_rider_name);
            pickup = (TextView)view.findViewById(R.id.match_rider_detail_list_view_pick_up);
            drop_off = (TextView)view.findViewById(R.id.match_rider_detail_list_view_drop_off);
            note = (TextView)view.findViewById(R.id.match_rider_detail_list_view_note);
            payment_method = (TextView)view.findViewById(R.id.match_rider_detail_list_view_payment_method);
            fare = (TextView)view.findViewById(R.id.match_rider_detail_list_view_fare);
            buttonAccept = (TextView)view.findViewById(R.id.match_rider_detail_list_view_accept_button);

            profilePicture = (CircleImageView) view.findViewById(R.id.match_rider_detail_list_view_rider_profile_picture);
            gender = (ImageView) view.findViewById(R.id.match_rider_detail_list_view_rider_gender);
        }
    }
}
