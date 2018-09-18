package com.jby.ridedriver.registration.profile;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jby.ridedriver.R;
import com.jby.ridedriver.registration.others.SquareHeightLinearLayout;
import com.jby.ridedriver.registration.profile.dialog.LogOutDialog;
import com.jby.ridedriver.registration.profile.editProfile.EditProfileActivity;
import com.jby.ridedriver.registration.shareObject.ApiDataObject;
import com.jby.ridedriver.registration.shareObject.ApiManager;
import com.jby.ridedriver.registration.shareObject.AsyncTaskManager;
import com.jby.ridedriver.registration.sharePreference.SharedPreferenceManager;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.jby.ridedriver.registration.home.HomeActivity.LOGOUT_REQUEST;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener,
        LogOutDialog.LogOutDialogCallBack{
    //    actionBar
    private SquareHeightLinearLayout actionBarMenuIcon, actionBarCloseIcon, actionBarLogout;
    private TextView actionBarTitle;

    private ImageView profileActivityDriverBackgroundImage, profileActivityGender;
    private CircleImageView profileActivityDriverProfilePicture;
    private EditText profileActivityUsername, profileActivityEmail, profileActivityPhone;
    private EditText profileActivityMobilePrefix;
    private TextView profileActivityLabelUsername, profileActivityRating, profileActivityCompletedRide;
    private Button profileActivityEditButton;

    //    path
    private static String profile_prefix = "http://188.166.186.198/~cheewee/ride/frontend/driver/profile/driver_profile_picture/";
    private static String car_prefix = "http://188.166.186.198/~cheewee/ride/frontend/driver/profile/driver_car/";

    //    async purpose
    AsyncTaskManager asyncTaskManager;
    JSONObject jsonObjectLoginResponse;
    ArrayList<ApiDataObject> apiDataObjectArrayList;
    private Handler handler;

    //    action hanlding purpose
    private int requestCode;

    private FragmentManager fm;
    private DialogFragment dialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        objectInitialize();
        objectSetting();
    }

    private void objectInitialize() {
        actionBarMenuIcon = (SquareHeightLinearLayout)findViewById(R.id.actionbar_menu);
        actionBarCloseIcon = (SquareHeightLinearLayout)findViewById(R.id.actionbar_close);
        actionBarLogout = (SquareHeightLinearLayout)findViewById(R.id.actionbar_logout);
        actionBarTitle = (TextView)findViewById(R.id.actionBar_title);

        profileActivityDriverBackgroundImage = (ImageView)findViewById(R.id.activity_profile_background_picture);
        profileActivityGender = (ImageView)findViewById(R.id.activity_profile_gender_icon);

        profileActivityDriverProfilePicture = (CircleImageView)findViewById(R.id.activity_profile_profile_picture);

        profileActivityUsername = (EditText) findViewById(R.id.activity_profile_edit_text_username);
        profileActivityEmail = (EditText)findViewById(R.id.activity_profile_edit_text_email);
        profileActivityPhone = (EditText)findViewById(R.id.activity_profile_edit_text_mobile);
        profileActivityMobilePrefix = (EditText)findViewById(R.id.activity_profile_edit_text_prefix);

        profileActivityLabelUsername = (TextView) findViewById(R.id.activity_profile_label_username);
        profileActivityCompletedRide = (TextView) findViewById(R.id.activity_profile_completed_ride);
        profileActivityRating = (TextView) findViewById(R.id.activity_profile_rating);

        profileActivityEditButton = (Button)findViewById(R.id.activity_profile_edit_button);
        handler = new Handler();
        fm = getSupportFragmentManager();
    }
    private void objectSetting(){
        //        actionBar
        actionBarTitle.setText(R.string.activity_profile);
        actionBarMenuIcon.setVisibility(View.GONE);
        actionBarCloseIcon.setVisibility(View.VISIBLE);
        actionBarLogout.setVisibility(View.VISIBLE);

        actionBarCloseIcon.setOnClickListener(this);
        actionBarLogout.setOnClickListener(this);
        profileActivityEditButton.setOnClickListener(this);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getDriverInformation();
            }
        },200);

    }

    public void getDriverInformation(){
        apiDataObjectArrayList = new ArrayList<>();
        apiDataObjectArrayList.add(new ApiDataObject("driver_id", SharedPreferenceManager.getUserID(this)));
        apiDataObjectArrayList.add(new ApiDataObject("driver_profile_detail", "1"));

        asyncTaskManager = new AsyncTaskManager(
                this,
                new ApiManager().editProfile,
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
                    if (jsonObjectLoginResponse.getString("status").equals("1")) {
//                        update successful
                        String username = jsonObjectLoginResponse.getJSONObject("value").getString("username");
                        String email = jsonObjectLoginResponse.getJSONObject("value").getString("email");
                        String gender = jsonObjectLoginResponse.getJSONObject("value").getString("gender");
                        String phone = jsonObjectLoginResponse.getJSONObject("value").getString("phone");
                        String profile_pic = jsonObjectLoginResponse.getJSONObject("value").getString("profile_pic");
                        String car_pic = jsonObjectLoginResponse.getJSONObject("value").getString("car_pic");

                        setDriverProfileInformation(username, email, gender, phone, profile_pic, car_pic);

                    }
                    else if(jsonObjectLoginResponse.getString("status").equals("2"))
                        showSnackBar("Something went wrong!");
                }
                else {
                    Toast.makeText(this, "Network Error!", Toast.LENGTH_SHORT).show();
                }
            } catch (InterruptedException e) {
                Toast.makeText(this, "Interrupted Exception!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (ExecutionException e) {
                Toast.makeText(this, "Execution Exception!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (JSONException e) {
                Toast.makeText(this, "JSON Exception!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (TimeoutException e) {
                Toast.makeText(this, "Connection Time Out!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    private void setDriverProfileInformation(String username, String email, String gender, String phone, String profile_pic, String car_pic) {
        profileActivityLabelUsername.setText(username);
        profileActivityUsername.setText(username);
        profileActivityEmail.setText(email);
        profileActivityPhone.setText(phone);

        if(gender.equals("1"))
            profileActivityGender.setImageDrawable(getResources().getDrawable(R.drawable.activity_profile_male_icon));
        else
            profileActivityGender.setImageDrawable(getResources().getDrawable(R.drawable.activity_profile_female_icon));

       if(!profile_pic.equals("")){
           profile_pic = profile_prefix + profile_pic;
           Picasso.get()
                   .load(profile_pic)
                   .error(R.drawable.loading_gif)
                   .into(profileActivityDriverProfilePicture);
       }
       else
           profileActivityDriverProfilePicture.setImageDrawable(getResources().getDrawable(R.drawable.activity_profile_picture_profile_icon));

       if (!car_pic.equals("")){
           car_pic = car_prefix + car_pic;
           Picasso.get()
                   .load(car_pic)
                   .error(R.drawable.loading_gif)
                   .fit()
                   .centerCrop()
                   .into(profileActivityDriverBackgroundImage);
       }
       else
           profileActivityDriverBackgroundImage.setImageDrawable(getResources().getDrawable(R.drawable.activity_car_picture_car_icon));

    }

    //    snackBar setting
    private void showSnackBar(String message){
        final Snackbar snackbar = Snackbar.make(profileActivityDriverBackgroundImage, message, Snackbar.LENGTH_SHORT);
        snackbar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.actionbar_close:
                onBackPressed();
                break;
            case R.id.actionbar_logout:
                openLogOutDialog();
                break;
            case R.id.activity_profile_edit_button:
                Intent intent = new Intent(this, EditProfileActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        setResult(requestCode);
        super.onBackPressed();
    }
    private void openLogOutDialog(){
        dialogFragment = new LogOutDialog();
        dialogFragment.show(fm, "");
    }

    @Override
    public void logOut() {
        requestCode = LOGOUT_REQUEST;
        onBackPressed();
    }
}
