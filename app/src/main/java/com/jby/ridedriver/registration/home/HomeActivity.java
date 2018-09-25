package com.jby.ridedriver.registration.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jby.ridedriver.R;
import com.jby.ridedriver.registration.home.confirmedRide.ConfirmedRideFragment;
import com.jby.ridedriver.registration.home.confirmedRide.startRoute.StartRouteActivity;
import com.jby.ridedriver.registration.home.myRoute.MyRouteFragment;
import com.jby.ridedriver.registration.home.quickRide.QuickRideFragment;
import com.jby.ridedriver.registration.others.SquareHeightLinearLayout;
import com.jby.ridedriver.registration.others.ViewPagerAdapter;
import com.jby.ridedriver.registration.others.ViewPagerObject;
import com.jby.ridedriver.registration.profile.ProfileActivity;
import com.jby.ridedriver.registration.profile.editProfile.EditProfileActivity;
import com.jby.ridedriver.registration.registration.LoginActivity;
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

public class HomeActivity extends AppCompatActivity implements View.OnClickListener,
 NavigationView.OnNavigationItemSelectedListener, ViewPager.OnPageChangeListener{

    public static String TAG = "HomeActivity";
    private DrawerLayout homeActivityDrawerLayout;
    private ActionBarDrawerToggle homeActivityActionBarDrawerToggle;
    private NavigationView homeActivityNavigationView;
    private SquareHeightLinearLayout actionBarMenu;

    //    path
    private static String prefix = "http://188.166.186.198/~cheewee/ride/frontend/driver/profile/driver_profile_picture/";
    private CircleImageView homeActivityProfilePicture;
    private TextView homeActivityUsername, homeActivityRating;

    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager homeActivityViewPager;
    private ArrayList<ViewPagerObject> viewPagerObjectsArrayList;

    private TabLayout homeActivityTabLayout;
    private ConfirmedRideFragment confirmedRideFragment;
    private MyRouteFragment myRouteFragment;
    private QuickRideFragment quickRideFragment;
    private FragmentManager fm;
    private FragmentTransaction ft;
//    update profile purpose
    private LinearLayout homeActivityUpdateProfileLayout;
    private Button homeActivityUpdateProfileButton;

    //    async purpose
    AsyncTaskManager asyncTaskManager;
    JSONObject jsonObjectLoginResponse;
    ArrayList<ApiDataObject> apiDataObjectArrayList;
    private Handler handler;

//    result && request code
    public static int LOGOUT_REQUEST = 34;
    public static int UPDATE_REQUEST = 35;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //if route is on going then redirect to start route activity
        checkingRouteStatus();
        objectInitialize();
        objectSetting();
    }

    private void objectInitialize() {
        homeActivityDrawerLayout = (DrawerLayout) findViewById(R.id.activity_main);
        homeActivityActionBarDrawerToggle = new ActionBarDrawerToggle(this, homeActivityDrawerLayout, R.string.activity_main_open, R.string.activity_main_close);
        homeActivityNavigationView = (NavigationView) findViewById(R.id.activity_home_navigation_view);
        View headerView = homeActivityNavigationView.getHeaderView(0);
        homeActivityUsername = (TextView)headerView.findViewById(R.id.activity_home_username);
        homeActivityRating = (TextView)headerView.findViewById(R.id.activity_home_rating);
        homeActivityProfilePicture = (CircleImageView)headerView.findViewById(R.id.activity_home_profile_picture);

        actionBarMenu = (SquareHeightLinearLayout) findViewById(R.id.actionbar_menu);
        homeActivityTabLayout= (TabLayout)findViewById(R.id.activity_home_tab_layout);
        homeActivityViewPager = (ViewPager) findViewById(R.id.activity_home_view_pager);

        homeActivityUpdateProfileLayout = (LinearLayout)findViewById(R.id.activity_home_update_profile_layout);
        homeActivityUpdateProfileButton = (Button) findViewById(R.id.activity_home_update_profile_button);
        viewPagerObjectsArrayList = new ArrayList<>();


        confirmedRideFragment= new ConfirmedRideFragment();
        myRouteFragment= new MyRouteFragment();
        quickRideFragment= new QuickRideFragment();

        fm = getSupportFragmentManager();
        handler = new Handler();
    }

    private void objectSetting() {
//        navigation
        homeActivityDrawerLayout.addDrawerListener(homeActivityActionBarDrawerToggle);
        homeActivityActionBarDrawerToggle.syncState();
        homeActivityNavigationView.setNavigationItemSelectedListener(this);
        homeActivityProfilePicture.setOnClickListener(this);
//        actionbar
        actionBarMenu.setOnClickListener(this);
//        view pager and tan
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), viewPagerObjectsArrayList);
        homeActivityViewPager.setAdapter(viewPagerAdapter);
        homeActivityTabLayout.setupWithViewPager(homeActivityViewPager);
//        update Profile
        homeActivityUpdateProfileButton.setOnClickListener(this);
//

        setPager();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getDriverStatus();
                getDriverInformation();
            }
        },200);
    }

    private void checkingRouteStatus() {
        if(!SharedPreferenceManager.getMatchRideId(this).equals("default")) {
            startActivity(new Intent(this, StartRouteActivity.class));
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.actionbar_menu:
                homeActivityDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.activity_home_profile_picture:
                intent = new Intent(this, ProfileActivity.class);
                startActivityForResult(intent, LOGOUT_REQUEST);
                break;
            case R.id.activity_home_update_profile_button:
                intent = new Intent(this, EditProfileActivity.class);
                startActivityForResult(intent, LOGOUT_REQUEST);
                break;
        }
    }
/*----------------------------navigation drawer setup------------------------------------------------*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return homeActivityActionBarDrawerToggle.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //calling the method displayselectedscreen and passing the id of selected menu
        displaySelectedScreen(item.getItemId());
        //make this method blank
        return true;
    }

    private void displaySelectedScreen(int itemId) {
        //creating fragment object
        Fragment fragment = null;

        //initializing the fragment object which is selected
        switch (itemId) {
            case R.id.history:
                break;
            case R.id.wallet:
                break;
            case R.id.emergency:
                break;
            case R.id.helpCenter:
                break;
        }
        homeActivityDrawerLayout.closeDrawer(GravityCompat.START);
    }

    public void getDriverInformation(){
        apiDataObjectArrayList = new ArrayList<>();
        apiDataObjectArrayList.add(new ApiDataObject("driver_id", SharedPreferenceManager.getUserID(this)));
        apiDataObjectArrayList.add(new ApiDataObject("driver_home_detail", "1"));

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
                        String profilePicture = jsonObjectLoginResponse.getJSONObject("value").getString("profile_pic");

                        setDriverProfileInformation(username, profilePicture);

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

    public void getDriverStatus(){
        apiDataObjectArrayList = new ArrayList<>();
        apiDataObjectArrayList.add(new ApiDataObject("driver_id", SharedPreferenceManager.getUserID(this)));
        apiDataObjectArrayList.add(new ApiDataObject("driver_status", "1"));

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
                        String status = jsonObjectLoginResponse.getJSONObject("value").getString("status");
                        if(status.equals("2"))
                            homeActivityUpdateProfileLayout.setVisibility(View.GONE);
                        else
                            homeActivityUpdateProfileLayout.setVisibility(View.VISIBLE);
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

    private void setDriverProfileInformation(String username, String profilePicture) {
        if(!profilePicture.equals("")){
            profilePicture = prefix + profilePicture;
            Picasso.get()
                    .load(profilePicture)
                    .error(R.drawable.loading_gif)
                    .into(homeActivityProfilePicture);
        }
        else{
            homeActivityProfilePicture.setImageDrawable(getDrawable(R.drawable.activity_profile_picture_profile_icon));
        }

        homeActivityUsername.setText(username);
    }

    /*----------------------------end navigation drawer setup------------------------------------------------*/

    /*-------------------------------Tab layout and view pager setup----------------------------------------------------------------*/

    public void setPager() {
        viewPagerObjectsArrayList.add(new ViewPagerObject(confirmedRideFragment, "Confirmed"));
        viewPagerObjectsArrayList.add(new ViewPagerObject(quickRideFragment, "Quick Ride"));
        viewPagerObjectsArrayList.add(new ViewPagerObject(myRouteFragment, "My Route"));

        viewPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /*-------------------------------end Tab layout and view pager setup----------------------------------------------------------------*/

    //    snackBar setting
    private void showSnackBar(String message){
        final Snackbar snackbar = Snackbar.make(homeActivityNavigationView, message, Snackbar.LENGTH_SHORT);
        snackbar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == LOGOUT_REQUEST){
            logOut();
        }
    }

    private void logOut(){
        SharedPreferenceManager.setUserID(this, "default");
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
