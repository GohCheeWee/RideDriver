package com.jby.ridedriver.registration.home.confirmedRide.startRoute;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.jby.ridedriver.R;
import com.jby.ridedriver.registration.home.confirmedRide.dialog.CompletedRouteDialog;
import com.jby.ridedriver.registration.shareObject.ApiDataObject;
import com.jby.ridedriver.registration.shareObject.ApiManager;
import com.jby.ridedriver.registration.shareObject.AsyncTaskManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.jby.ridedriver.registration.home.myRoute.matchedRiderDetail.riderProfile.RiderProfileDialog.prefix;

public class StartRouteActivity extends AppCompatActivity implements View.OnClickListener,
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, CompletedRouteDialog.CompletedRouteDialogCallBack {

    private TextView startRouteActivityRiderName, startRouteActivityAddress, startRouteActivityCarType;
    private TextView startRouteActivityFare, startRouteActivityPaymendMethod, startRouteActivityNote;
    private ImageView startRouteActivityNagivation, startRouteActivityCloseButton;
    private CircleImageView startRouteActivityRiderProfilePicture;
    private RelativeLayout startRouteActivityCall, startRouteActivityMessage, startRouteActivityMore;
    private Button startRouteActivityButton;
    private MapFragment map;
    //    asyncTask
    AsyncTaskManager asyncTaskManager;
    JSONObject jsonObjectLoginResponse;
    ArrayList<ApiDataObject> apiDataObjectArrayList;
    Handler handler;
    private String driverRideId;

    //    map setting
    private GoogleMap googleMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
//    dialog purpose
    private DialogFragment dialogFragment;
    private FragmentManager fm;
//    variable
    private String pickUpLocation, dropOffLocation, phone, dropOffAddress;
    private String pickUpAddress, matchRideID, status, address;
    private int requestCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_route);
        objectInitialize();
        objectSetting();
    }

    private void objectInitialize() {
        startRouteActivityRiderName = (TextView)findViewById(R.id.activity_start_route_rider_name);
        startRouteActivityAddress = (TextView)findViewById(R.id.activity_start_route_address);
        startRouteActivityCarType = (TextView)findViewById(R.id.activity_start_route_car_type);
        startRouteActivityFare = (TextView)findViewById(R.id.activity_start_route_fare);
        startRouteActivityPaymendMethod = (TextView)findViewById(R.id.activity_start_route_payment_method);
        startRouteActivityNote = (TextView)findViewById(R.id.activity_start_route_note);

        startRouteActivityNagivation = (ImageView)findViewById(R.id.activity_start_route_navigation);
        startRouteActivityCloseButton = (ImageView)findViewById(R.id.activity_start_route_close_button);
        startRouteActivityRiderProfilePicture = (CircleImageView)findViewById(R.id.activity_start_route_rider_profile_picture);

        startRouteActivityCall = (RelativeLayout)findViewById(R.id.activity_start_route_call_layout);
        startRouteActivityMessage = (RelativeLayout)findViewById(R.id.activity_start_route_message_layout);
        startRouteActivityMore = (RelativeLayout)findViewById(R.id.activity_start_route_more_layout);

        startRouteActivityButton = (Button) findViewById(R.id.activity_start_route_button);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.activity_start_route_google_map));
        handler = new Handler();
        fm =  getSupportFragmentManager();
    }

    private void objectSetting() {
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            driverRideId = bundle.getString("driver_ride_id");
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getMyRiderInformation();
            }
        },200);
        initializeMap();

        startRouteActivityCall.setOnClickListener(this);
        startRouteActivityCloseButton.setOnClickListener(this);
        startRouteActivityMessage.setOnClickListener(this);
        startRouteActivityMore.setOnClickListener(this);
        startRouteActivityNagivation.setOnClickListener(this);
        startRouteActivityButton.setOnClickListener(this);
    }

    private void getMyRiderInformation(){
        apiDataObjectArrayList = new ArrayList<>();
        apiDataObjectArrayList.add(new ApiDataObject("driver_ride_id", driverRideId));
        apiDataObjectArrayList.add(new ApiDataObject("getRiderDetail", "1"));
        asyncTaskManager = new AsyncTaskManager(
                this,
                new ApiManager().startRide,
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
                        JSONArray jsonArray = jsonObjectLoginResponse.getJSONArray("value").getJSONObject(0).getJSONArray("start_ride");
                        pickUpLocation = jsonArray.getJSONObject(0).getString("pick_up_location");
                        dropOffLocation = jsonArray.getJSONObject(0).getString("drop_off_location");
                        phone = jsonArray.getJSONObject(0).getString("phone");
                        dropOffAddress = jsonArray.getJSONObject(0).getString("drop_off_address");
                        pickUpAddress = jsonArray.getJSONObject(0).getString("pick_up_address");
                        status = jsonArray.getJSONObject(0).getString("status");
                        matchRideID = jsonArray.getJSONObject(0).getString("id");

                        String rider_name = jsonArray.getJSONObject(0).getString("username");
                        String profile_pic = jsonArray.getJSONObject(0).getString("profile_picture");
                        String fare = jsonArray.getJSONObject(0).getString("fare");
                        String payment_method = jsonArray.getJSONObject(0).getString("payment_method");
                        String note = jsonArray.getJSONObject(0).getString("note");

                        setUpView(rider_name, profile_pic, pickUpAddress, fare, payment_method, note);
                        checkingStatus();
                    }
                    else if(jsonObjectLoginResponse.getString("status").equals("2")){
                        Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
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

    private void setUpView(String rider_name, String profile_pic, String pick_up_address, String fare,
                           String payment_method, String note) {
        fare = "RM " + fare;
        profile_pic = prefix + profile_pic;

        if(payment_method.equals("1"))
            payment_method = "Cash";
        else
            payment_method = "RidePay";

        if(!note.equals("-")){
            startRouteActivityNote.setText(note);
            startRouteActivityNote.setVisibility(View.VISIBLE);
        }

        startRouteActivityRiderName.setText(rider_name);
        startRouteActivityAddress.setText(pick_up_address);
        startRouteActivityFare.setText(fare);
        startRouteActivityPaymendMethod.setText(payment_method);
        Picasso.get()
                .load(profile_pic)
                .error(R.drawable.loading_gif)
                .into(startRouteActivityRiderProfilePicture);


    }

    @Override
    public void onClick(View view) {
        clickEffect(view);
        switch(view.getId()){
            case R.id.activity_start_route_call_layout:
                phoneCallPermission();
                break;
            case R.id.activity_start_route_close_button:
                onBackPressed();
                break;
            case R.id.activity_start_route_navigation:
                openNavigationApp();
                break;
            case R.id.activity_start_route_button:
                    updateStatusSetting();
                break;
        }
    }

    //<-----------------------------------Map and  GPS setting-------------------------------------------------------->
    public void initializeMap() {
        map.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
//                if permission is granted then get my location
                buildGoogleApiClient();
                googleMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
        }

//        setButtonPosition();
        googleMap.setMyLocationEnabled(true);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    //
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(50);
        mLocationRequest.setFastestInterval(50);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(16));
        Log.d("StartRouteActivity", "Location: " + location);

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    //<-----------------------------------End of Map and  GPS setting-------------------------------------------------------->
    /*----------------------------------------------------call purpose-------------------------------------------------------*/
    public static final int MY_PERMISSIONS_REQUEST_PHONE_CALL = 10;

    public boolean checkPhoneCallPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CALL_PHONE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                ActivityCompat.requestPermissions(StartRouteActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_PHONE_CALL);

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        MY_PERMISSIONS_REQUEST_PHONE_CALL);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_PHONE_CALL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.CALL_PHONE)
                            == PackageManager.PERMISSION_GRANTED) {
                        //Request location updates:
                        onCall();
                    }
                } else {
                    Toast.makeText(this, "Unable to make a phone call with permission!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void phoneCallPermission() {
        if (checkPhoneCallPermission()) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {
                onCall();
            }
        }
    }

    public void onCall() {
        String phoneNo = phone;
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:"+phoneNo));    //this is the phone number calling

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            //request permission from user if the app hasn't got the required permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE},   //request specific permission from user
                    MY_PERMISSIONS_REQUEST_PHONE_CALL);
        }else {     //have got permission
            try{
                startActivity(callIntent);  //call activity and make phone call
            }
            catch (android.content.ActivityNotFoundException ex){
                Toast.makeText(this,"Invalid Number",Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*----------------------------------------------------end of call purpose-------------------------------------------------------*/

    private void openNavigationApp(){
        try
        {
            // Launch Waze to look for Hawaii:
            String url = "https://waze.com/ul?q=" + address;
            Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( url ) );
            startActivity( intent );
        }
        catch ( ActivityNotFoundException ex  )
        {
            // If Waze is not installed, open it in Google Play:
            Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( "market://details?id=com.waze" ) );
            startActivity(intent);
        }
    }

    private void updateStatusSetting(){
        switch(status){
            case "3":
                status = "4";
                break;
            case "4":
                status = "5";
                break;
            case "5":
                status = "6";
                break;
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateRiderRideStatus(status);
                }
        },200);

    }

    private void updateRiderRideStatus(String status){
        apiDataObjectArrayList = new ArrayList<>();
        apiDataObjectArrayList.add(new ApiDataObject("matched_ride_id", matchRideID));
        apiDataObjectArrayList.add(new ApiDataObject("status", status));
        asyncTaskManager = new AsyncTaskManager(
                this,
                new ApiManager().startRide,
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
                        checkingStatus();
                        if(status.equals("6"))
                            popUpCompletedRouteDialog();
                    }
                    else if(jsonObjectLoginResponse.getString("status").equals("2")){
                        Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
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

    private void checkingStatus(){
        switch (status){
            case "3":
                startRouteActivityButton.setText(getResources().getString(R.string.activity_start_route_button_arrived));
                address = pickUpAddress;
                startRouteActivityAddress.setText(pickUpAddress);
                break;
            case "4":
                startRouteActivityButton.setText(getResources().getString(R.string.activity_start_route_button_pick_up));
                address = dropOffAddress;
                startRouteActivityAddress.setText(dropOffAddress);
                break;
            case "5":
                startRouteActivityButton.setText(getResources().getString(R.string.activity_start_route_button_drop_off));
                break;
        }
    }

    private void popUpCompletedRouteDialog(){
        dialogFragment = new CompletedRouteDialog();
        dialogFragment.show(fm, "");
    }
    //click effect
    public void clickEffect(View view){
        Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
        animation1.setDuration(500);
        view.startAnimation(animation1);
    }

    @Override
    public void updateConfirmRideListView(int requestCode) {
        this.requestCode = requestCode;
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        setResult(requestCode);
        super.onBackPressed();
    }
}
