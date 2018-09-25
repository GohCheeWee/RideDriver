package com.jby.ridedriver.registration.home.myRoute.matchedRiderDetail.riderProfile;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jby.ridedriver.R;
import com.jby.ridedriver.registration.shareObject.ApiDataObject;
import com.jby.ridedriver.registration.shareObject.ApiManager;
import com.jby.ridedriver.registration.shareObject.AsyncTaskManager;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import de.hdodenhof.circleimageview.CircleImageView;

public class RiderProfileDialog extends DialogFragment implements View.OnClickListener {
    View rootView;
    private ImageView riderProfileDialogCloseButton, riderProfileDialogGenderIcon;
    private TextView riderProfileDialogUsername, riderProfileDialogRating, riderProfileDialogCompletedRide;
    private CircleImageView riderProfileDialogUserProfile;
    private RelativeLayout riderProfileDialogMainLayout;
    private ProgressBar riderProfileDialogProgressBar;

    //    asyncTask
    AsyncTaskManager asyncTaskManager;
    JSONObject jsonObjectLoginResponse;
    ArrayList<ApiDataObject> apiDataObjectArrayList;
    Handler handler;

    private String userID;
    //    path
    public static String prefix = "http://188.166.186.198/~cheewee/ride/frontend/user/profile/profile_picture/";

    public RiderProfileDialog() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.user_profile_dialog, container);
        objectInitialize();
        objectSetting();
        return rootView;
    }

    private void objectInitialize() {

        riderProfileDialogCloseButton = (ImageView)rootView.findViewById(R.id.user_profile_dialog_cancel_icon);
        riderProfileDialogGenderIcon = (ImageView)rootView.findViewById(R.id.user_profile_dialog_gender_icon);
        riderProfileDialogMainLayout = (RelativeLayout)rootView.findViewById(R.id.user_profile_dialog_main_layout);
        riderProfileDialogProgressBar = (ProgressBar) rootView.findViewById(R.id.user_profile_dialog_progress_bar);

        riderProfileDialogUsername = (TextView)rootView.findViewById(R.id.user_profile_dialog_username);
        riderProfileDialogRating = (TextView)rootView.findViewById(R.id.user_profile_dialog_user_rating);
        riderProfileDialogCompletedRide = (TextView)rootView.findViewById(R.id.user_profile_dialog_comepleted_trip);

        riderProfileDialogUserProfile = (CircleImageView)rootView.findViewById(R.id.user_profile_dialog_user_icon);
        handler = new Handler();
    }

    private void objectSetting() {
        riderProfileDialogCloseButton.setOnClickListener(this);
        Bundle mArgs = getArguments();
        if (mArgs != null) {
            userID = mArgs.getString("rider_id");
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getUserDetail();
                }
            },200);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);
            d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            d.getWindow().setWindowAnimations(R.style.dialog_fade_in_out);
        }
    }

    @Override
    public void onClick(View view) {
        clickEffect(view);
        switch (view.getId()){
            case R.id.user_profile_dialog_cancel_icon:
                dismiss();
                break;
        }
    }

    //click effect
    private void clickEffect(View view){
        Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
        animation1.setDuration(500);
        view.startAnimation(animation1);
    }

    private void getUserDetail(){

        apiDataObjectArrayList = new ArrayList<>();
        apiDataObjectArrayList.add(new ApiDataObject("user_id",userID));
        asyncTaskManager = new AsyncTaskManager(
                getActivity(),
                new ApiManager().userProfile,
                new ApiManager().getResultParameter(
                        "",
                        new ApiManager().setData(apiDataObjectArrayList),
                        ""
                )
        );
        asyncTaskManager.execute();

        if (!asyncTaskManager.isCancelled()) {
            try {
                jsonObjectLoginResponse = asyncTaskManager.get(30000, TimeUnit.MILLISECONDS);

                if (jsonObjectLoginResponse != null) {
                    if(jsonObjectLoginResponse.getString("status").equals("1")){
                        String username = jsonObjectLoginResponse.getJSONObject("value").getString("username");
                        String gender = jsonObjectLoginResponse.getJSONObject("value").getString("gender");
                        String profile_picture = jsonObjectLoginResponse.getJSONObject("value").getString("profile_picture");
                        setUpUserDetail(username, gender, profile_picture);

                    }
                    else if(jsonObjectLoginResponse.getString("status").equals("2")){
                        Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getActivity(), "Network Error!", Toast.LENGTH_SHORT).show();
                }

            } catch (InterruptedException e) {
                Toast.makeText(getActivity(), "Interrupted Exception!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (ExecutionException e) {
                Toast.makeText(getActivity(), "Execution Exception!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (JSONException e) {
                Toast.makeText(getActivity(), "JSON Exception!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (TimeoutException e) {
                Toast.makeText(getActivity(), "Connection Time Out!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
        setUpView();
    }

    private void setUpView(){
        riderProfileDialogProgressBar.setVisibility(View.GONE);
        riderProfileDialogMainLayout.setVisibility(View.VISIBLE);
    }

    private void setUpUserDetail(String username, String gender, String profile_picture) {
        riderProfileDialogUsername.setText(username);

        if(profile_picture != null){
            profile_picture = prefix + profile_picture;

            Picasso.get()
                    .load(profile_picture)
                    .error(R.drawable.loading_gif)
                    .into(riderProfileDialogUserProfile);
        }

        if(gender.equals("Male"))
            riderProfileDialogGenderIcon.setImageDrawable(getResources().getDrawable(R.drawable.activity_profile_male_icon));
        else
            riderProfileDialogGenderIcon.setImageDrawable(getResources().getDrawable(R.drawable.activity_profile_female_icon));
    }
}