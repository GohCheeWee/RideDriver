package com.jby.ridedriver.registration.profile.editProfile.vehicleNumber;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jby.ridedriver.R;
import com.jby.ridedriver.registration.shareObject.ApiDataObject;
import com.jby.ridedriver.registration.shareObject.ApiManager;
import com.jby.ridedriver.registration.shareObject.AsyncTaskManager;
import com.jby.ridedriver.registration.sharePreference.SharedPreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class VehicleNumberDialog extends DialogFragment implements View.OnClickListener {
    View rootView;
    private Button vehicleNumberUpload;
    private EditText vehicleNumberCarPlate;
    private RelativeLayout vehicleNumberMainLayout;
    VehicleNumberDialogCallBack vehicleNumberDialogCallBack;

    //    async purpose
    AsyncTaskManager asyncTaskManager;
    JSONObject jsonObjectLoginResponse;
    ArrayList<ApiDataObject> apiDataObjectArrayList;
    private Handler handler;

    public VehicleNumberDialog() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_edit_profile_upload_car_number_dialog, container);
        objectInitialize();
        vehicleNumberDialogCallBack = (VehicleNumberDialogCallBack) getActivity();
        return rootView;
    }

    private void objectInitialize() {
        vehicleNumberMainLayout = (RelativeLayout) rootView.findViewById(R.id.car_number_dialog_main_layout);
        vehicleNumberUpload = (Button)rootView.findViewById(R.id.activity_car_number_upload);
        vehicleNumberCarPlate = (EditText) rootView.findViewById(R.id.activity_car_dialog_car_plate);

        vehicleNumberUpload.setOnClickListener(this);
        vehicleNumberMainLayout.setOnClickListener(this);
        handler = new Handler();

        Bundle mArgs = getArguments();
        if (mArgs != null) {
            String carPlate =  mArgs.getString("car_plate");
            vehicleNumberCarPlate.setText(carPlate);
        }
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
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.car_number_dialog_main_layout:
                dismiss();
                break;
            case R.id.activity_car_number_upload:
                checkingBeforeUpload();
                break;
        }
    }

    private void checkingBeforeUpload(){
        String carPlate = vehicleNumberCarPlate.getText().toString();

        if(!carPlate.equals(""))
            updateCarPlate();
        else
            showSnackBar("All required is required");
    }

    public void updateCarPlate(){
        apiDataObjectArrayList = new ArrayList<>();
        apiDataObjectArrayList.add(new ApiDataObject("driver_id", SharedPreferenceManager.getUserID(getActivity())));
        apiDataObjectArrayList.add(new ApiDataObject("car_plate", vehicleNumberCarPlate.getText().toString()));

        asyncTaskManager = new AsyncTaskManager(
                getActivity(),
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
                        vehicleNumberDialogCallBack.getDriverDetail();
                        dismiss();
                    }
                    else if(jsonObjectLoginResponse.getString("status").equals("2"))
                        showSnackBar("Something went wrong!");
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
    }

    public interface VehicleNumberDialogCallBack {
        void getDriverDetail();
    }

    //    snackBar setting
    private void showSnackBar(String message){
        final Snackbar snackbar = Snackbar.make(vehicleNumberMainLayout, message, Snackbar.LENGTH_SHORT);
        snackbar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }
}