package com.jby.ridedriver.registration.home.confirmedRide.startRoute;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jby.ridedriver.R;
import com.jby.ridedriver.registration.home.HomeActivity;
import com.jby.ridedriver.registration.home.confirmedRide.dialog.CompletedRouteDialog;
import com.jby.ridedriver.registration.home.confirmedRide.startRoute.dialog.RouteScheduleDialog;
import com.jby.ridedriver.registration.home.confirmedRide.startRoute.object.MarkerObject;
import com.jby.ridedriver.registration.home.confirmedRide.startRoute.object.StartRouteRiderObject;
import com.jby.ridedriver.registration.others.CustomMarker;
import com.jby.ridedriver.registration.shareObject.AddressObject;
import com.jby.ridedriver.registration.shareObject.ApiDataObject;
import com.jby.ridedriver.registration.shareObject.ApiManager;
import com.jby.ridedriver.registration.shareObject.AsyncTaskManager;
import com.jby.ridedriver.registration.sharePreference.SharedPreferenceManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.jby.ridedriver.registration.home.myRoute.matchedRiderDetail.riderProfile.RiderProfileDialog.prefix;

public class StartRouteActivity extends AppCompatActivity implements View.OnClickListener,
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, CompletedRouteDialog.CompletedRouteDialogCallBack,
        RouteScheduleDialog.RouteScheduleDialogCallBack{

    private TextView startRouteActivityRiderName, startRouteActivityAddress, startRouteActivityCarType;
    private TextView startRouteActivityFare, startRouteActivityPaymendMethod, startRouteActivityNote;
    private ImageView startRouteActivityNagivation, startRouteActivityCloseButton, startRouteActivityChooseAddressButton;
    private CircleImageView startRouteActivityRiderProfilePicture;
    private RelativeLayout startRouteActivityCall, startRouteActivityMessage, startRouteActivityMore;
    private Button startRouteActivityButton;
    private MapFragment map;
    private LinearLayout  startRouteActivityParentLayout;
    //    asyncTask
    AsyncTaskManager asyncTaskManager;
    JSONObject jsonObjectLoginResponse;
    ArrayList<ApiDataObject> apiDataObjectArrayList;
    Handler handler;
    private String driverRideId;
    private ArrayList<StartRouteRiderObject> startRouteRiderObjectArrayList;
    //    map setting
    private GoogleMap googleMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
//    dialog purpose
    private DialogFragment dialogFragment;
    private FragmentManager fm;
//    variable
    private String phone, status, address;
    private int requestCode;
    // pickup purpose
    private int position = 0;
// for marker purpose
    private ArrayList<MarkerObject> markerObjectArrayList;
    private String lastAddress = "";

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

        startRouteActivityParentLayout = findViewById(R.id.activity_start_route_parent_layout);

        startRouteActivityButton = (Button) findViewById(R.id.activity_start_route_button);
        startRouteActivityChooseAddressButton = findViewById(R.id.activity_start_route_select_address);

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.activity_start_route_google_map));

        startRouteRiderObjectArrayList = new ArrayList<>();
        markerObjectArrayList = new ArrayList<>();
        handler = new Handler();
        fm =  getSupportFragmentManager();
    }

    private void objectSetting() {
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            SharedPreferenceManager.setMatchRideId(this, bundle.getString("driver_ride_id"));
        }
        driverRideId = SharedPreferenceManager.getMatchRideId(this);

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
        startRouteActivityAddress.setOnClickListener(this);
        startRouteActivityChooseAddressButton.setOnClickListener(this);

    }

    private void getMyRiderInformation(){
        startRouteRiderObjectArrayList.clear();

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
                        for(int i = 0; i < jsonArray.length(); i++){
                            startRouteRiderObjectArrayList.add(new StartRouteRiderObject(
                                    jsonArray.getJSONObject(i).getString("pick_up_location"),
                                    jsonArray.getJSONObject(i).getString("drop_off_location"),
                                    jsonArray.getJSONObject(i).getString("phone"),
                                    jsonArray.getJSONObject(i).getString("drop_off_address"),
                                    jsonArray.getJSONObject(i).getString("pick_up_address"),
                                    jsonArray.getJSONObject(i).getString("status"),
                                    jsonArray.getJSONObject(i).getString("id"),
                                    jsonArray.getJSONObject(i).getString("username"),
                                    jsonArray.getJSONObject(i).getString("profile_picture"),
                                    jsonArray.getJSONObject(i).getString("fare"),
                                    jsonArray.getJSONObject(i).getString("payment_method"),
                                    jsonArray.getJSONObject(i).getString("note"),
                                    jsonArray.getJSONObject(i).getDouble("pick_up_distance"),
                                    jsonArray.getJSONObject(i).getDouble("drop_off_distance")
                            ));
                        }
                        sortingArrayForPickUpPurpose();
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
        else
            showSnackBar("Please connect to a network");
    }

    private void setUpView(int position, boolean isPickUp) {
        String rider_name = startRouteRiderObjectArrayList.get(position).getRider_name();
        String profile_pic = startRouteRiderObjectArrayList.get(position).getProfile_pic();
        String fare = startRouteRiderObjectArrayList.get(position).getFare();
        String payment_method = startRouteRiderObjectArrayList.get(position).getPayment_method();
        String note = startRouteRiderObjectArrayList.get(position).getNote();
        fare = "RM " + fare;
        profile_pic = prefix + profile_pic;
        phone = startRouteRiderObjectArrayList.get(position).getPhone();
        status = startRouteRiderObjectArrayList.get(position).getStatus();

        //if pick up then set pick up address
        if(isPickUp)
            address = startRouteRiderObjectArrayList.get(position).getPickUpAddress();
        else
            address = startRouteRiderObjectArrayList.get(position).getDropOffAddress();

        if(payment_method.equals("1"))
            payment_method = "Cash";
        else
            payment_method = "RidePay";

        if(!note.equals("-")){
            startRouteActivityNote.setText(note);
            startRouteActivityNote.setVisibility(View.VISIBLE);
        }

        startRouteActivityRiderName.setText(rider_name);
        startRouteActivityAddress.setText(address);
        startRouteActivityFare.setText(fare);
        startRouteActivityPaymendMethod.setText(payment_method);

        Picasso.get()
                .load(profile_pic)
                .error(R.drawable.loading_gif)
                .into(startRouteActivityRiderProfilePicture);

        checkingButtonStatus();
        // if last address not same mean that the address is changed so refresh the map
        if(markerObjectArrayList.size() > 0 && !lastAddress.equals(address)) setUpMarker();
        lastAddress = address;
    }

    @Override
    public void onClick(View view) {
        clickEffect(view);
        if(startRouteRiderObjectArrayList.size() > 0){
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
                case R.id.activity_start_route_address:
                    checkingCurrentStatus();
                    break;
                case R.id.activity_start_route_select_address:
                    checkingCurrentStatus();
                    break;
            }
        }
        else{
            showSnackBar("Sorry, Something Went Wrong :(");
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
        markerObjectArrayList.add(new MarkerObject(latLng, "", "My Location"));

        Log.d("StartRouteActivity", "Location: " + location);

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            if(markerObjectArrayList.size() > 0 && status != null) setUpMarker();
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

    /*-----------------------------------------------------pick up purpose----------------------------------------------------------*/
    // sorting the pick up array by asc
    private void sortingArrayForPickUpPurpose() {
        // sort pick up array by ascending order
        Collections.sort(startRouteRiderObjectArrayList, new Comparator<StartRouteRiderObject>() {
            @Override
            public int compare(StartRouteRiderObject startRouteRiderObject, StartRouteRiderObject t1) {
                return Double.compare(startRouteRiderObject.getPickUpDistance(), t1.getPickUpDistance());
            }
        });
        //check pick up first
        checkingAnyDriverArrivedStatus();
    }

    private void checkingAnyDriverArrivedStatus(){
        //if found status '4 ' mean driver have arrived but haven't pick up yet
        for (int j = 0; j<startRouteRiderObjectArrayList.size(); j++){
            if(startRouteRiderObjectArrayList.get(j).getStatus().equals("4")){
                position = j;
                // setup view
                setUpView(position, true);
                // if found then terminate the loop
                break;
            }
            //not found then proceed to check who are waiting to pick up
            if (j == startRouteRiderObjectArrayList.size()-1)
                checkingRiderPickUpOrder();
        }
    }
    //checking who going to fetch rite now
    private void checkingRiderPickUpOrder(){
        for(int i = 0; i < startRouteRiderObjectArrayList.size(); i++){

            //if found status '3 ' mean the driver haven't arrived yet
             if(startRouteRiderObjectArrayList.get(i).getStatus().equals("3")){
                position = i;
                // setup view
                setUpView(position, true);
                // if found then terminate the loop
                break;
            }
            // this mean that all rider have been picked up
            if(i == startRouteRiderObjectArrayList.size() - 1){
                //start to check drop off order
                checkingRiderDropOffOrder();
            }
        }
    }

    private void checkingRiderDropOffOrder() {
/*        for(int i=0; i<startRouteRiderObjectArrayList.size(); i++)
            Log.d("HAHA","Testing: "+ startRouteRiderObjectArrayList.get(i).getStatus());*/

        for(int i = 0; i < startRouteRiderObjectArrayList.size(); i++){
            //if found status '5 ' mean the rider is not pick up yet
            if(startRouteRiderObjectArrayList.get(i).getStatus().equals("5")){
                position = i;
                // setup view, false mean that is drop off
                setUpView(position, false);
                // if found then terminate the loop
                break;
            }
            // mean every one already drop off
            if(i == startRouteRiderObjectArrayList.size()-1)
                popUpCompletedRouteDialog();
        }
    }

    private void updateStatusArrayList(String status){
        startRouteRiderObjectArrayList.set(position,new StartRouteRiderObject(
                startRouteRiderObjectArrayList.get(position).getPickUpLocation(),
                startRouteRiderObjectArrayList.get(position).getDropOffLocation(),
                startRouteRiderObjectArrayList.get(position).getPhone(),
                startRouteRiderObjectArrayList.get(position).getDropOffAddress(),
                startRouteRiderObjectArrayList.get(position).getPickUpAddress(),
                status,
                startRouteRiderObjectArrayList.get(position).getMatchRideID(),
                startRouteRiderObjectArrayList.get(position).getRider_name(),
                startRouteRiderObjectArrayList.get(position).getProfile_pic(),
                startRouteRiderObjectArrayList.get(position).getFare(),
                startRouteRiderObjectArrayList.get(position).getPayment_method(),
                startRouteRiderObjectArrayList.get(position).getNote(),
                startRouteRiderObjectArrayList.get(position).getPickUpDistance(),
                startRouteRiderObjectArrayList.get(position).getDropOffDistance()
                ));
    }
    /*------------------------------------------------------end of pick up purpose--------------------------------------------------*/

    /*------------------------------------------------------select address--------------------------------------------------------*/
    private void checkingCurrentStatus(){
        if(status.equals("4")){
            showSnackBar("Sorry, you must pick up this rider first");
        }else
            openRouteScheduleDialog();
    }

    private void openRouteScheduleDialog(){
        dialogFragment = new RouteScheduleDialog();
        Bundle bundle = new Bundle();

        bundle.putSerializable("array", startRouteRiderObjectArrayList);
        bundle.putString("status", status);
        dialogFragment.setArguments(bundle);
        dialogFragment.show(fm, "");
    }

    @Override
    public void updatePickUpAddress(int position) {
        this.position = position;
        setUpView(position, true);
    }

    @Override
    public void updateDropOffAddress(int position) {
        this.position = position;
        setUpView(position, false);
    }
    /*-----------------------------------------------------end of select address----------------------------------------------------*/

    /*------------------------------------------------------map marker setting------------------------------------------------------------*/
// update initial pickup point after received it
    private void setUpMarker(){
        googleMap.clear();
//        setting up initial marker
        MarkerOptions routeMarker = new MarkerOptions();
        //setting up marker information
        getMarkerInformationByStatus();

        //set up all the marker
        for(int i = 0; i<markerObjectArrayList.size(); i++){
            routeMarker
                    .position(markerObjectArrayList.get(i).getLocation())
                    .title(markerObjectArrayList.get(i).getLocationAddress());
            //custom marker icon
                    if(i == 0) routeMarker.icon(BitmapDescriptorFactory.fromBitmap(new CustomMarker(this).getMarkerBitmapFromView(R.drawable.activity_start_car_icon)));
                    else {
                        routeMarker.icon(BitmapDescriptorFactory.fromBitmap(customMarkerView(startRouteRiderObjectArrayList.get(position).getProfile_pic())));
                    }
            googleMap.addMarker(routeMarker);
            requestCameraFocus();
        }
    }
    private void requestCameraFocus(){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(int i = 0; i<markerObjectArrayList.size(); i++)
            builder.include(markerObjectArrayList.get(i).getLocation());
//
        LatLngBounds bounds = builder.build();
//
        int padding = 100; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        googleMap.animateCamera(cu);

    }

    //convert String pick up into LatLong
    private LatLng getLatLongFromString(String stringLatLong){

        stringLatLong = stringLatLong.substring(10, stringLatLong.length()-1);
        String[] LatLong =  stringLatLong.split(",");
        double latitude = Double.parseDouble(LatLong[0]);
        double longitude = Double.parseDouble(LatLong[1]);
        return new LatLng(latitude, longitude);
    }

    private void getMarkerInformationByStatus(){
        LatLng location;
        String address;
        if(status.equals("3") || status.equals("4")){
             location = getLatLongFromString(startRouteRiderObjectArrayList.get(position).getPickUpLocation());
             address = startRouteRiderObjectArrayList.get(position).getPickUpAddress();
        }
        else{
            location = getLatLongFromString(startRouteRiderObjectArrayList.get(position).getDropOffLocation());
            address = startRouteRiderObjectArrayList.get(position).getDropOffAddress();
        }

        //setup marker array
        if(markerObjectArrayList.size() >= 2)
            markerObjectArrayList.set(1, new MarkerObject(location, "", address));
        else
            markerObjectArrayList.add(1, new MarkerObject(location, "", address));

    }

    private Bitmap customMarkerView(String profile_pic) {

        //HERE YOU CAN ADD YOUR CUSTOM VIEW
        View customMarkerView = ((LayoutInflater) Objects.requireNonNull(getSystemService(Context.LAYOUT_INFLATER_SERVICE))).inflate(R.layout.activity_start_route_custom_marker_layout, null);
        CircleImageView userIcon = customMarkerView.findViewById(R.id.activity_start_route_custom_marker_layout_user_icon);
        Picasso.get()
                .load(prefix + profile_pic)
                .error(R.drawable.loading_gif)
                .into(userIcon);

        //IN THIS EXAMPLE WE ARE TAKING TEXTVIEW BUT YOU CAN ALSO TAKE ANY KIND OF VIEW LIKE IMAGEVIEW, BUTTON ETC.
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();

        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);

        Drawable drawable = customMarkerView.getBackground();

        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }

    /*------------------------------------------------------map end of marker------------------------------------------------------------*/
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
                String matchRideID = startRouteRiderObjectArrayList.get(position).getMatchRideID();
                updateRiderRideStatus(status, matchRideID);
                }
        },200);

    }

    private void updateRiderRideStatus(String status, String matchRideID){
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
                        updateStatusArrayList(status);
                        if(status.equals("6"))
                           //check whether still got other haven't drop off or not
                           checkingRiderDropOffOrder();
                        else
                            //checking any one is waiting to pick up or driver is arrived
                           checkingAnyDriverArrivedStatus();
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

    private void checkingButtonStatus(){
        switch (status){
            case "3":
                startRouteActivityButton.setText(getResources().getString(R.string.activity_start_route_button_arrived));
                break;
            case "4":
                startRouteActivityButton.setText(getResources().getString(R.string.activity_start_route_button_pick_up));
                break;
            case "5":
                startRouteActivityButton.setText(getResources().getString(R.string.activity_start_route_button_drop_off));
                break;
        }
    }

    private void popUpCompletedRouteDialog(){
        SharedPreferenceManager.setMatchRideId(this, "default");
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
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //    snackBar setting
    private void showSnackBar(String message){
        final Snackbar snackbar = Snackbar.make(startRouteActivityParentLayout, message, Snackbar.LENGTH_SHORT);
        snackbar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }
}
