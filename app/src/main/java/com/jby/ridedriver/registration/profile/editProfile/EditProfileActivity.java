package com.jby.ridedriver.registration.profile.editProfile;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jby.ridedriver.R;
import com.jby.ridedriver.registration.others.SquareHeightLinearLayout;
import com.jby.ridedriver.registration.profile.editProfile.carModelDialog.CarModelDialog;
import com.jby.ridedriver.registration.profile.editProfile.carPicture.CarPictureActivity;
import com.jby.ridedriver.registration.profile.editProfile.drivingLicense.DrivingLicenseActivity;
import com.jby.ridedriver.registration.profile.editProfile.profilePicture.ProfilePictureActivity;
import com.jby.ridedriver.registration.profile.editProfile.vehicleNumber.VehicleNumberDialog;
import com.jby.ridedriver.registration.shareObject.ApiDataObject;
import com.jby.ridedriver.registration.shareObject.ApiManager;
import com.jby.ridedriver.registration.shareObject.AsyncTaskManager;
import com.jby.ridedriver.registration.sharePreference.SharedPreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener,
        CarModelDialog.CarModelDialogCallBack, VehicleNumberDialog.VehicleNumberDialogCallBack{
    //    actionBar
    private SquareHeightLinearLayout actionBarMenuIcon, actionBarCloseIcon, actionBarLogout;
    private TextView actionBarTitle;

    private TextView editProfileActivityName, editProfileActivityStatus;
    private TextView editProfileActivityEmail, editProfileActivityGender, editProfileActivityPhone;
    private TextView editProfileActivityCarModel, editProfileActivityCarNumber;
    private LinearLayout editProfileActivityProfilePictureLayout, editProfileActivityLicenseLayout;
    private LinearLayout editProfileActivityCarPictureLayout, editProfileActivityCarModelLayout;
    private LinearLayout editProfileActivityVehicleNumber, editProfileActivityMainLayout;

    private ImageView editProfileActivityProfilePictureStatus, editProfileActivityLicenseStatus;
    private ImageView editProfileActivityCarPictureStatus, editProfileActivityCarModelStatus;
    private ImageView editProfileActivityVehicleRegistrationNumber;

    private Intent intent;
    //    dialog
    DialogFragment dialogFragment;
    Bundle bundle;
    FragmentManager fm;
//    async purpose
    AsyncTaskManager asyncTaskManager;
    JSONObject jsonObjectLoginResponse;
    ArrayList<ApiDataObject> apiDataObjectArrayList;
    private Handler handler;
//    car model brand dialog purpose
    private String carBrand, carModel, carPlate;
//    request code for update driver profile
    public static int REQUEST_FOR_REFRESH = 15;

    //    action hanlding purpose
    private int requestCode;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == REQUEST_FOR_REFRESH)
            getDriverDetail();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        objectInitialize();
        objectSetting();
    }

    private void objectInitialize() {
        actionBarMenuIcon = (SquareHeightLinearLayout)findViewById(R.id.actionbar_menu);
        actionBarCloseIcon = (SquareHeightLinearLayout)findViewById(R.id.actionbar_close);
        actionBarLogout = (SquareHeightLinearLayout)findViewById(R.id.actionbar_logout);
        actionBarTitle = (TextView)findViewById(R.id.actionBar_title);

        editProfileActivityName = (TextView)findViewById(R.id.activity_edit_profile_name);
        editProfileActivityEmail = (TextView)findViewById(R.id.activity_edit_profile_email);
        editProfileActivityGender = (TextView)findViewById(R.id.activity_edit_profile_gender);
        editProfileActivityPhone = (TextView)findViewById(R.id.activity_edit_profile_phone);

        editProfileActivityStatus = (TextView)findViewById(R.id.activity_edit_profile_status);

        editProfileActivityCarModel = (TextView)findViewById(R.id.activity_edit_profile_car_model);
        editProfileActivityCarNumber = (TextView)findViewById(R.id.activity_edit_profile_car_number);

        editProfileActivityProfilePictureLayout = (LinearLayout)findViewById(R.id.activity_edit_profile_profile_picture_layout);
        editProfileActivityLicenseLayout = (LinearLayout)findViewById(R.id.activity_edit_profile_license_layout);
        editProfileActivityCarPictureLayout = (LinearLayout)findViewById(R.id.activity_edit_profile_car_picture_layout);
        editProfileActivityCarModelLayout = (LinearLayout)findViewById(R.id.activity_edit_profile_car_model_layout);
        editProfileActivityVehicleNumber = (LinearLayout)findViewById(R.id.activity_edit_profile_vehicle_number_layout);

        editProfileActivityMainLayout = (LinearLayout)findViewById(R.id.activity_edit_profile_main_layout);

        editProfileActivityProfilePictureStatus = (ImageView)findViewById(R.id.activity_edit_profile_profile_picture_status_icon);
        editProfileActivityLicenseStatus = (ImageView)findViewById(R.id.activity_edit_profile_license_status_icon);
        editProfileActivityCarPictureStatus = (ImageView)findViewById(R.id.activity_edit_profile_car_picture_status_icon);
        editProfileActivityCarModelStatus = (ImageView)findViewById(R.id.activity_edit_profile_car_model_status_icon);
        editProfileActivityVehicleRegistrationNumber = (ImageView)findViewById(R.id.activity_edit_profile_vehicle_number_status_icon);

        fm = getSupportFragmentManager();
    }

    private void objectSetting() {
        //        actionBar
        actionBarTitle.setText(R.string.activity_edit_profile);
        actionBarMenuIcon.setVisibility(View.GONE);
        actionBarCloseIcon.setVisibility(View.VISIBLE);

        editProfileActivityProfilePictureLayout.setOnClickListener(this);
        editProfileActivityLicenseLayout.setOnClickListener(this);
        editProfileActivityCarPictureLayout.setOnClickListener(this);
        editProfileActivityCarModelLayout.setOnClickListener(this);
        editProfileActivityVehicleNumber.setOnClickListener(this);
        actionBarCloseIcon.setOnClickListener(this);

        getDriverDetail();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.activity_edit_profile_profile_picture_layout:
                intent = new Intent(this, ProfilePictureActivity.class);
                startActivityForResult(intent, REQUEST_FOR_REFRESH);
                break;
            case R.id.activity_edit_profile_license_layout:
                intent = new Intent(this, DrivingLicenseActivity.class);
                startActivityForResult(intent, REQUEST_FOR_REFRESH);
                break;
            case R.id.activity_edit_profile_car_picture_layout:
                intent = new Intent(this, CarPictureActivity.class);
                startActivityForResult(intent, REQUEST_FOR_REFRESH);
                break;
            case R.id.activity_edit_profile_car_model_layout:
                openCarModelDialog();
                break;
            case R.id.activity_edit_profile_vehicle_number_layout:
                openVehicleNumberDialog();
                break;
            case R.id.actionbar_close:
                onBackPressed();
                break;

        }
    }

    public void openCarModelDialog(){
        dialogFragment = new CarModelDialog();
        bundle = new Bundle();
        if(!carModel.equals("") && !carBrand.equals("")){
            bundle.putString("car_model", carModel);
            bundle.putString("car_brand", carBrand);
            dialogFragment.setArguments(bundle);
        }
        dialogFragment.show(fm, "");
    }

    public void openVehicleNumberDialog(){
        dialogFragment = new VehicleNumberDialog();
        bundle = new Bundle();
        if(!carBrand.equals("")){
            bundle.putString("car_plate", carPlate);
            dialogFragment.setArguments(bundle);
        }
        dialogFragment.setArguments(bundle);
        dialogFragment.show(fm, "");
    }

    public void getDriverDetail(){
        apiDataObjectArrayList = new ArrayList<>();
        apiDataObjectArrayList.add(new ApiDataObject("driver_id", SharedPreferenceManager.getUserID(this)));
        apiDataObjectArrayList.add(new ApiDataObject("driver_detail", "1"));

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
//                        setup user detail
                        String profilePicStatus = jsonObjectLoginResponse.getJSONObject("value").getString("profile_pic_status");
                        String licenseStatus = jsonObjectLoginResponse.getJSONObject("value").getString("driving_license_status");
                        String carStatus = jsonObjectLoginResponse.getJSONObject("value").getString("car_status");
                        String carModelStatus = jsonObjectLoginResponse.getJSONObject("value").getString("car_model_status");
                        String carPlateStatus = jsonObjectLoginResponse.getJSONObject("value").getString("car_plate_status");
                        String carBrand = jsonObjectLoginResponse.getJSONObject("value").getString("car_brand");
                        String carModel = jsonObjectLoginResponse.getJSONObject("value").getString("car_model");
                        String carPlate = jsonObjectLoginResponse.getJSONObject("value").getString("car_plate");
                        String username = jsonObjectLoginResponse.getJSONObject("value").getString("username");
                        String email = jsonObjectLoginResponse.getJSONObject("value").getString("email");
                        String gender = jsonObjectLoginResponse.getJSONObject("value").getString("gender");
                        String phone = jsonObjectLoginResponse.getJSONObject("value").getString("phone");

                        setInitializeStatus(profilePicStatus, licenseStatus, carStatus, carModelStatus, carPlateStatus, carBrand, carModel,
                                carPlate, username, email, gender, phone);
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

    private void setInitializeStatus(String profilePicStatus, String licenseStatus, String carStatus, String carModelStatus, String carPlateStatus
    ,String carBrand, String carModel, String carPlate, String username, String email, String gender, String phone) {

        this.carBrand = carBrand;
        this.carModel = carModel;
        this.carPlate = carPlate;

        switch (profilePicStatus){
            case "1":
                editProfileActivityProfilePictureStatus.setImageDrawable(getDrawable(R.drawable.activity_edit_profile_pending_icon));
                break;
            case "2":
                editProfileActivityProfilePictureStatus.setImageDrawable(getDrawable(R.drawable.activity_edit_profile_approved_icon));
                break;
            case "3":
                editProfileActivityProfilePictureStatus.setImageDrawable(getDrawable(R.drawable.activity_edit_profile_rejected_icon));
                break;
        }

        switch (licenseStatus){
            case "1":
                editProfileActivityLicenseStatus.setImageDrawable(getDrawable(R.drawable.activity_edit_profile_pending_icon));
                break;
            case "2":
                editProfileActivityLicenseStatus.setImageDrawable(getDrawable(R.drawable.activity_edit_profile_approved_icon));
                break;
            case "3":
                editProfileActivityLicenseStatus.setImageDrawable(getDrawable(R.drawable.activity_edit_profile_rejected_icon));
                break;
        }

        switch (carStatus){
            case "1":
                editProfileActivityCarPictureStatus.setImageDrawable(getDrawable(R.drawable.activity_edit_profile_pending_icon));
                break;
            case "2":
                editProfileActivityCarPictureStatus.setImageDrawable(getDrawable(R.drawable.activity_edit_profile_approved_icon));
                break;
            case "3":
                editProfileActivityCarPictureStatus.setImageDrawable(getDrawable(R.drawable.activity_edit_profile_rejected_icon));
                break;
        }

        switch (carModelStatus){
            case "1":
                editProfileActivityCarModelStatus.setImageDrawable(getDrawable(R.drawable.activity_edit_profile_pending_icon));
                break;
            case "2":
                editProfileActivityCarModelStatus.setImageDrawable(getDrawable(R.drawable.activity_edit_profile_approved_icon));
                break;
            case "3":
                editProfileActivityCarModelStatus.setImageDrawable(getDrawable(R.drawable.activity_edit_profile_rejected_icon));
                break;
        }

        switch (carPlateStatus){
            case "1":
                editProfileActivityVehicleRegistrationNumber.setImageDrawable(getDrawable(R.drawable.activity_edit_profile_pending_icon));
                break;
            case "2":
                editProfileActivityVehicleRegistrationNumber.setImageDrawable(getDrawable(R.drawable.activity_edit_profile_approved_icon));
                break;
            case "3":
                editProfileActivityVehicleRegistrationNumber.setImageDrawable(getDrawable(R.drawable.activity_edit_profile_rejected_icon));
                break;
        }

        if(!carModel.equals("") && !carBrand.equals("")){
            carModel = carBrand +  " " + carModel;
            editProfileActivityCarModel.setText(carModel);
        }
        if(!carPlate.equals(""))
            editProfileActivityCarNumber.setText(carPlate);

        if(gender.equals("1"))
            gender = "Male";
        else
            gender = "Female";

        editProfileActivityName.setText(username);
        editProfileActivityEmail.setText(email);
        editProfileActivityGender.setText(gender);
        editProfileActivityPhone.setText(phone);
    }


    //    snackBar setting
    private void showSnackBar(String message){
        final Snackbar snackbar = Snackbar.make(editProfileActivityMainLayout, message, Snackbar.LENGTH_SHORT);
        snackbar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }
}
