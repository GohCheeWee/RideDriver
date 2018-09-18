package com.jby.ridedriver.registration.home.myRoute.dialog;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import android.os.Handler;
import android.os.ResultReceiver;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.jby.ridedriver.R;
import com.jby.ridedriver.registration.home.myRoute.object.MarkerObject;
import com.jby.ridedriver.registration.others.FetchAddressIntentService;
import com.jby.ridedriver.registration.others.LocationConstants;
import com.jby.ridedriver.registration.others.SquareHeightLinearLayout;
import com.jby.ridedriver.registration.shareObject.AddressObject;
import com.jby.ridedriver.registration.shareObject.AnimationUtility;
import com.jby.ridedriver.registration.shareObject.ApiDataObject;
import com.jby.ridedriver.registration.shareObject.ApiManager;
import com.jby.ridedriver.registration.shareObject.AsyncTaskManager;
import com.jby.ridedriver.registration.sharePreference.SharedPreferenceManager;
import com.xw.repo.BubbleSeekBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static android.app.Activity.RESULT_OK;

public class CreateRouteDialog extends DialogFragment implements View.OnClickListener, AdapterView.OnItemSelectedListener,
        InvalidTimeDialog.InvalidTimeDialogCallBack, NoteDialog.NoteDialogCallBack, View.OnTouchListener, CompoundButton.OnCheckedChangeListener{
    View rootView;
    //    actionBar
    private SquareHeightLinearLayout actionBarMenuIcon, actionBarCloseIcon, actionBarLogout;
    private TextView actionBarTitle;

    private ScrollView createRouteDialogScrollView;
    private LinearLayout createRouteDialogLabelAdvanceSettingLayout, createRouteDialogAdvanceSettingLayout;
    private LinearLayout createRouteDialogPickUpLayout, createRouteDialogDropOffLayout, createRouteDialogDateLayout;
    private LinearLayout createRouteDialogTimeLayout, createRouteDialogNoteLayout;

    private TextView createRouteDialogPickUp, createRouteDialogDropOff, createRouteDialogDate, createRouteDialogTime;
    private TextView createRouteDialogPickUpDistance, createRouteDialogDropOffDistance, createRouteDialogLabelSeat;
    private TextView createRouteDialogNote;

    private BubbleSeekBar createRouteDialogPickUpSeekBar, createRouteDialogDropOffSeekBar;
    private Spinner createRouteDialogSeat;
    private Button createRouteDialogButton;
    private CheckBox createRouteDialogGender;
    private ProgressBar createRouteDialogProgressBar;
    private ImageView createRouteDialogAdvanceIcon;
    //    seekbar purpose
    private float pickUpDistance = 1, dropOffDistance = 1;
    private static String km = " KM";
    private static String within = " < ";
    private String distance;
    //  spinner setting;
    List<String> spinnerItemList;
    ArrayAdapter<String> dataAdapter;
    private int availableSeat = 4;

    //    advance booking time & date setting
    Calendar mCurrentTime;
    TimePickerDialog timePicker;
    DatePickerDialog datePicker;
    Calendar calendar;
    private int hour, minute;
    private boolean selected = false;
    private String date = "-";
    private String time = "-";
    private String note = "None";

    //  advance setting layout
    private boolean show = false;

    //    dialog setting
    private DialogFragment dialogFragment;
    private FragmentManager fm;

    //    pick up drop off setting
    private static int PICK_UP_POINT_REQUEST = 1;
    private static int DROP_OFF_POINT_REQUEST = 2;
    private int requestCode;
    private boolean placePicker = false;
    private LatLng currentLocation;
    private boolean dropOffPoint = false;
    ArrayList<MarkerObject> routeArray = new ArrayList<>();
    FusedLocationProviderClient fusedLocationProviderClient;
    protected Location mLastLocation;
    private AddressResultReceiver mResultReceiver;

    //    asyncTask
    AsyncTaskManager asyncTaskManager;
    JSONObject jsonObjectLoginResponse;
    ArrayList<ApiDataObject> apiDataObjectArrayList;
    private Handler handler;

    private int gender = 0;

    private CreateRouteDialogCallBack createRouteDialogCallBack;

    public CreateRouteDialog() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_my_route_create_route_dialog, container);
        objectInitialize();
        objectSetting();
        return rootView;
    }

    private void objectInitialize() {
        actionBarMenuIcon = (SquareHeightLinearLayout) rootView.findViewById(R.id.actionbar_menu);
        actionBarCloseIcon = (SquareHeightLinearLayout) rootView.findViewById(R.id.actionbar_close);
        actionBarLogout = (SquareHeightLinearLayout) rootView.findViewById(R.id.actionbar_logout);
        actionBarTitle = (TextView) rootView.findViewById(R.id.actionBar_title);

        createRouteDialogScrollView = (ScrollView)rootView.findViewById(R.id.scrollView);
        createRouteDialogPickUpLayout = (LinearLayout) rootView.findViewById(R.id.create_route_dialog_pick_up_layout);
        createRouteDialogDropOffLayout = (LinearLayout) rootView.findViewById(R.id.create_route_dialog_drop_off_layout);
        createRouteDialogDateLayout = (LinearLayout) rootView.findViewById(R.id.create_route_dialog_date_layout);
        createRouteDialogTimeLayout = (LinearLayout) rootView.findViewById(R.id.create_route_dialog_time_layout);
        createRouteDialogNoteLayout = (LinearLayout) rootView.findViewById(R.id.create_route_dialog_note_layout);

        createRouteDialogPickUp = (TextView) rootView.findViewById(R.id.create_route_dialog_pick_up);
        createRouteDialogDropOff = (TextView) rootView.findViewById(R.id.create_route_dialog_drop_off);
        createRouteDialogDate = (TextView) rootView.findViewById(R.id.create_route_dialog_date);
        createRouteDialogTime = (TextView) rootView.findViewById(R.id.create_route_dialog_time);
        createRouteDialogNote = (TextView) rootView.findViewById(R.id.create_route_dialog_note);

        createRouteDialogPickUpDistance = (TextView) rootView.findViewById(R.id.create_route_dialog_pick_up_distance);
        createRouteDialogDropOffDistance = (TextView) rootView.findViewById(R.id.create_route_dialog_drop_off_distance);

        createRouteDialogPickUpSeekBar = (BubbleSeekBar) rootView.findViewById(R.id.create_route_dialog_pick_up_distance_seek_bar);
        createRouteDialogDropOffSeekBar = (BubbleSeekBar) rootView.findViewById(R.id.create_route_dialog_drop_off_distance_seek_bar);

        createRouteDialogButton = (Button) rootView.findViewById(R.id.create_route_dialog_button);

        createRouteDialogSeat = (Spinner) rootView.findViewById(R.id.create_route_dialog_seat);
        createRouteDialogLabelSeat = (TextView) rootView.findViewById(R.id.create_route_dialog_label_seat);

        createRouteDialogGender = (CheckBox)rootView.findViewById(R.id.create_route_dialog_gender);

        createRouteDialogLabelAdvanceSettingLayout = (LinearLayout) rootView.findViewById(R.id.create_route_dialog_label_advance_setting_layout);
        createRouteDialogAdvanceSettingLayout = (LinearLayout) rootView.findViewById(R.id.create_route_dialog_advance_setting_layout);
        createRouteDialogAdvanceIcon = (ImageView)rootView.findViewById(R.id.create_route_dialog_advance_seting_icon);

        createRouteDialogProgressBar = (ProgressBar)rootView.findViewById(R.id.create_route_dialog_progress_bar);

        spinnerItemList = new ArrayList<String>();
        dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, spinnerItemList);

        //        address setting
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mResultReceiver = new AddressResultReceiver(new Handler());

        fm = getActivity().getSupportFragmentManager();
        handler = new Handler();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void objectSetting() {
        //        actionBar
        actionBarTitle.setText(R.string.activitiy_home_my_route);
        actionBarMenuIcon.setVisibility(View.GONE);
        actionBarCloseIcon.setVisibility(View.VISIBLE);

        createRouteDialogCallBack = (CreateRouteDialogCallBack)getTargetFragment();

        createRouteDialogLabelAdvanceSettingLayout.setOnClickListener(this);

        createRouteDialogPickUpSeekBar.setOnTouchListener(this);
        createRouteDialogDropOffSeekBar.setOnTouchListener(this);

        createRouteDialogSeat.setOnItemSelectedListener(this);
        createRouteDialogDate.setOnClickListener(this);
        createRouteDialogTime.setOnClickListener(this);
        createRouteDialogPickUpLayout.setOnClickListener(this);
        createRouteDialogDropOffLayout.setOnClickListener(this);
        createRouteDialogNoteLayout.setOnClickListener(this);
        createRouteDialogButton.setOnClickListener(this);
        actionBarCloseIcon.setOnClickListener(this);
        createRouteDialogGender.setOnCheckedChangeListener(this);

//        for create route setting
        createRoutePurpose();
    }
    public void createRoutePurpose(){
        setSpinnerItem(availableSeat);
        initializeDateTime();
        setUpPickUpSeekBar();
        setUpDropOffSeekBar();
        fetchAddress();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            Objects.requireNonNull(d.getWindow()).setLayout(width, height);
            d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            d.getWindow().setWindowAnimations(R.style.dialog_up_down);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.actionbar_close:
                dismiss();
                break;
            case R.id.create_route_dialog_label_advance_setting_layout:
                clickEffect(createRouteDialogLabelAdvanceSettingLayout);
                setAdvanceSetting();
                break;
            case R.id.create_route_dialog_time:
                clickEffect(createRouteDialogTimeLayout);
                selectTimeDialog();
                break;
            case R.id.create_route_dialog_date:
                clickEffect(createRouteDialogDateLayout);
                selectDateDialog();
                break;
            case R.id.create_route_dialog_pick_up_layout:
                if (!placePicker) {
                    clickEffect(createRouteDialogPickUpLayout);
                    selectPoint(1);
                }
                break;
            case R.id.create_route_dialog_drop_off_layout:
                if (!placePicker) {
                    clickEffect(createRouteDialogDropOffLayout);
                    selectPoint(2);
                }
                break;
            case R.id.create_route_dialog_note_layout:
                clickEffect(createRouteDialogNoteLayout);
                openNote();
                break;
            case R.id.create_route_dialog_button:
                createRouteDialogProgressBar.setVisibility(View.VISIBLE);
                checkingBeforeUpload();
                break;
        }
    }

    private void setAdvanceSetting() {
        if (!show) {
            new AnimationUtility().slideUp(getActivity(), createRouteDialogAdvanceSettingLayout);
            show = true;
            createRouteDialogAdvanceIcon.setImageDrawable(getResources().getDrawable(R.drawable.create_route_dialog_arrow_down_icon));
        } else {
            new AnimationUtility().slideDown(getActivity(), createRouteDialogAdvanceSettingLayout);
            show = false;
            createRouteDialogAdvanceIcon.setImageDrawable(getResources().getDrawable(R.drawable.create_route_dialog_arrow_right_icon));
        }

    }

    //click effect
    public void clickEffect(View view) {
        Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
        animation1.setDuration(500);
        view.startAnimation(animation1);
    }

    private void setUpPickUpSeekBar() {
        distance = within + String.valueOf(pickUpDistance) + km;
        //        initial setting
        createRouteDialogPickUpSeekBar.setProgress(pickUpDistance);
        createRouteDialogPickUpDistance.setText(distance);

        createRouteDialogPickUpSeekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress, float progressFloat) {
                pickUpDistance = progressFloat;
                distance = within + String.valueOf(pickUpDistance) + km;
                createRouteDialogPickUpDistance.setText(distance);
            }

            @Override
            public void getProgressOnActionUp(int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(int progress, float progressFloat) {

            }
        });
    }

    private void setUpDropOffSeekBar() {
        distance = within + String.valueOf(dropOffDistance) + km;
//        initial setting
        createRouteDialogDropOffSeekBar.setProgress(dropOffDistance);
        createRouteDialogDropOffDistance.setText(distance);

        createRouteDialogDropOffSeekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress, float progressFloat) {
                dropOffDistance = progressFloat;
                distance = within + String.valueOf(dropOffDistance) + km;
                ;
                createRouteDialogDropOffDistance.setText(distance);
            }

            @Override
            public void getProgressOnActionUp(int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(int progress, float progressFloat) {

            }
        });
    }

    /*--------------------------------------spinner setting----------------------------------------------------*/
    private void setSpinnerItem(int size) {
        for (int i = 1; i <= size; i++) {
            spinnerItemList.add(String.valueOf(i));
        }
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        createRouteDialogSeat.setAdapter(dataAdapter);
        createRouteDialogSeat.setSelection(size-1);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.create_route_dialog_seat:
                availableSeat = Integer.valueOf(spinnerItemList.get(i));
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    /*--------------------------------------end of spinner setting----------------------------------------------------*/
    /*------------------------------------------date time setting--------------------------------------------------------*/
    private void selectDateDialog() {
        mCurrentTime = Calendar.getInstance();
        int dayOfMonth = mCurrentTime.get(Calendar.DAY_OF_MONTH);
        int month = mCurrentTime.get(Calendar.MONTH);
        int year = mCurrentTime.get(Calendar.YEAR);

        datePicker = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar = Calendar.getInstance();
                        calendar.set(year, monthOfYear, dayOfMonth);

                        SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.getDefault());
                        SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());

                        String strDate = monthFormat.format(calendar.getTime());
                        String day = dayFormat.format(calendar.getTime());
                        date = day + "/" + strDate;

                        createRouteDialogDate.setText(date);

                    }
                }, year, month, dayOfMonth);
        datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
//        open time when closing
        datePicker.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                selectTimeDialog();
            }
        });
        datePicker.show();
    }

    public void selectTimeDialog() {
        mCurrentTime = Calendar.getInstance();
        hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
        minute = mCurrentTime.get(Calendar.MINUTE);

        timePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override

            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                calendar.set(Calendar.MINUTE, selectedMinute);

                String format = "%02d:%02d";
                String hourStatus = setAmOrPm(selectedHour);
                int editedHour = setTimeInto12HourFormat(selectedHour);
                hour = selectedHour;

                time = String.format(Locale.getDefault(), format, editedHour, selectedMinute) + " " + hourStatus;
                createRouteDialogTime.setText(time);
                selected = true;

            }
        }, hour, minute, false);
        timePicker.setTitle("Select Time");

        timePicker.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

                SimpleDateFormat sdf = new SimpleDateFormat("HHmm", Locale.TAIWAN);
                int currentTime = Integer.valueOf(sdf.format(new Date()));
                int selectTime = Integer.valueOf(String.valueOf(hour) + String.valueOf(minute));

//                if (selectTime - currentTime < 10 && selected) {
//                    dialogFragment = new InvalidTimeDialog();
//                    dialogFragment.setTargetFragment(CreateRouteDialog.this, 300);
//                    dialogFragment.show(fm, "fragment_edit_name");
//                }
            }
        });

        timePicker.show();
    }

    public String setAmOrPm(int time) {
        String AM_PM = null;
        if (time < 12) {
            AM_PM = "AM";
        } else {
            AM_PM = "PM";
        }
        return AM_PM;
    }

    public int setTimeInto12HourFormat(int time) {
        if (time > 12)
            time = time - 12;
        else
            time = (time);
        return time;
    }

    private void initializeDateTime() {
        //            time setting
        mCurrentTime = Calendar.getInstance();
//            time
        hour = setTimeInto12HourFormat(mCurrentTime.get(Calendar.HOUR_OF_DAY));
        minute = mCurrentTime.get(Calendar.MINUTE);
//            date
        int dayOfMonth = mCurrentTime.get(Calendar.DAY_OF_MONTH);
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.getDefault());
        String month = monthFormat.format(mCurrentTime.getTime());
//            format
        String time_format = "%02d:%02d";
        String hourStatus = setAmOrPm(hour);

        time = String.format(Locale.getDefault(), time_format, hour, minute + 10) + " " + hourStatus;
        date = dayOfMonth + "/" + month;

        createRouteDialogDate.setText(date);
        createRouteDialogTime.setText(time);
    }

    /*------------------------------------------end of date time setting--------------------------------------------------------*/
    //    place picker && reset final fare = 4.00
    private void selectPoint(int point) {
        placePicker = true;
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        if (point == 1)
            requestCode = PICK_UP_POINT_REQUEST;
        else
            requestCode = DROP_OFF_POINT_REQUEST;
        try {
            startActivityForResult(builder.build(getActivity()), requestCode);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_UP_POINT_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, getActivity());
                updateLocation(place);
            }
        } else if (requestCode == DROP_OFF_POINT_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, getActivity());
                updateLocation(place);
                dropOffPoint = true;
            }
        }
        placePicker = false;
    }

    //    update location after getting result from place picker
    private void updateLocation(Place place) {
        String point = String.format("%s", place.getName());
        String address = String.format("%s", place.getAddress());
        LatLng latLng = place.getLatLng();

        if (requestCode == 1) {
            createRouteDialogPickUp.setText(point);
//            setup pick up point into route array
            if (routeArray.size() == 0)
                routeArray.add(0, new MarkerObject(latLng, point, address));
            else
                routeArray.set(0, new MarkerObject(latLng, point, address));
        } else {
            createRouteDialogDropOff.setText(point);
//            setup drop off marker
            if (routeArray.size() >= 2)
                routeArray.set(1, new MarkerObject(latLng, point, address));
            else
                routeArray.add(1, new MarkerObject(latLng, point, address));

        }
    }

    //for getting the initial address from result receiver
    private void fetchAddress() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        mLastLocation = location;

                        // In some rare cases the location returned can be null
                        if (mLastLocation == null) {
                            return;
                        }

                        if (!Geocoder.isPresent()) {
                            Toast.makeText(getActivity(),
                                    "Geo not found",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        // Start service and update UI to reflect new location
                        startIntentService();
                    }
                });
    }
    //start intent to get initilza
    protected void startIntentService() {
        Intent intent = new Intent(getContext(), FetchAddressIntentService.class);
        intent.putExtra(LocationConstants.RECEIVER, mResultReceiver);
        intent.putExtra(LocationConstants.LOCATION_DATA_EXTRA, mLastLocation);
        getActivity().startService(intent);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (view.getId()){
            case R.id.create_route_dialog_pick_up_distance_seek_bar:
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN || motionEvent.getAction() == MotionEvent.ACTION_MOVE)
                    createRouteDialogScrollView.requestDisallowInterceptTouchEvent(true);
                break;
            case R.id.create_route_dialog_drop_off_distance_seek_bar:
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN || motionEvent.getAction() == MotionEvent.ACTION_MOVE)
                    createRouteDialogScrollView.requestDisallowInterceptTouchEvent(true);
                break;
        }
        return false;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(b)
            gender = 1;
        else
            gender = 0;
    }

    //after received then call fetchAddress
    class AddressResultReceiver extends ResultReceiver {
        private AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (resultData == null) {
                return;
            }

            // Display the address string
            // or an error message sent from the intent service.

            ArrayList<AddressObject> addressList;
            addressList = resultData.getParcelableArrayList(LocationConstants.RESULT_DATA_KEY);
            if (addressList == null) {
                addressList = null;
            }
            // Show a toast message if an address was found.
            if (resultCode == LocationConstants.SUCCESS_RESULT){
                if(addressList!= null)
                    setInitialPickUpPoint(addressList);
            }

        }
    }
    // update initial pickup point after received it
    private void setInitialPickUpPoint(ArrayList<AddressObject> result){
        String streetName = result.get(0).getStreetName();
        String address = result.get(0).getFullAddress();

        currentLocation = new LatLng(result.get(0).getLatitude(), result.get(0).getLongitude());
        createRouteDialogPickUp.setText(streetName);

//        setting up initial marker
        routeArray.add(0, new MarkerObject(currentLocation, streetName, address));

    }
/*-----------------------------------------------nore setting---------------------------------------------*/
    public void setNote(String note){
    this.note = note;
    createRouteDialogNote.setText(note);
}

    public void openNote(){
        dialogFragment = new NoteDialog();
        Bundle bundle = new Bundle();
        String note = createRouteDialogNote.getText().toString();

        if(!note.equals("Write your note here..."))
            bundle.putString("note", note);
        dialogFragment.setTargetFragment(CreateRouteDialog.this, 301);
        dialogFragment.setArguments(bundle);
        dialogFragment.show(fm, "");
    }

    /*----------------------------create route-------------------------------------------------*/
    private void checkingBeforeUpload(){
        if(routeArray.size() == 2) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    createDriverRide();
                }
            },200);
        }
        else{
            openMissingDialog();
            createRouteDialogProgressBar.setVisibility(View.GONE);
        }
    }

    public void openMissingDialog(){

        dialogFragment = new CreateRouteSomethingMissingDialog();
        dialogFragment.setTargetFragment(CreateRouteDialog.this, 302);
        dialogFragment.show(fm, "");
    }

    private void createDriverRide(){
        String pickUpLocation = String.valueOf(routeArray.get(0).getLocation());
        String dropOffLocation = String.valueOf(routeArray.get(1).getLocation());
        String pickUpAddress = String.valueOf(routeArray.get(0).getLocationAddress());
        String dropOffAddress =  String.valueOf(routeArray.get(1).getLocationAddress());
        String date = createRouteDialogDate.getText().toString();
        String time = createRouteDialogTime.getText().toString();
        String num_seat = String.valueOf(availableSeat);
        String pick_up_distance = String.valueOf(pickUpDistance);
        String drop_off_distance = String.valueOf(dropOffDistance);
        String para_gender = String.valueOf(gender);

        apiDataObjectArrayList = new ArrayList<>();
        apiDataObjectArrayList.add(new ApiDataObject("driver_id", SharedPreferenceManager.getUserID(getActivity())));
        apiDataObjectArrayList.add(new ApiDataObject("pick_up_location", pickUpLocation));
        apiDataObjectArrayList.add(new ApiDataObject("drop_off_location",dropOffLocation));
        apiDataObjectArrayList.add(new ApiDataObject("pick_up_address", pickUpAddress));
        apiDataObjectArrayList.add(new ApiDataObject("drop_off_address",dropOffAddress));
        apiDataObjectArrayList.add(new ApiDataObject("dateRide", date));
        apiDataObjectArrayList.add(new ApiDataObject("time", time));
        apiDataObjectArrayList.add(new ApiDataObject("note", note));
        apiDataObjectArrayList.add(new ApiDataObject("num_seat", num_seat));
        apiDataObjectArrayList.add(new ApiDataObject("pick_up_distance", pick_up_distance));
        apiDataObjectArrayList.add(new ApiDataObject("drop_off_distance", drop_off_distance));
        apiDataObjectArrayList.add(new ApiDataObject("gender", para_gender));

        asyncTaskManager = new AsyncTaskManager(
                getActivity(),
                new ApiManager().matching,
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
                        createRouteDialogCallBack.refreshAction();
                        dismiss();
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
                Toast.makeText(getActivity(),"Execution Exception!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (JSONException e) {
                Toast.makeText(getActivity(), "JSON Exception!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (TimeoutException e) {
                Toast.makeText(getActivity(), "Connection Time Out!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
        createRouteDialogProgressBar.setVisibility(View.GONE);
    }

    //    snackBar setting
    private void showSnackBar(String message){
        final Snackbar snackbar = Snackbar.make(createRouteDialogDateLayout, message, Snackbar.LENGTH_SHORT);
        snackbar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    public interface CreateRouteDialogCallBack{
        void refreshAction();
    }
}