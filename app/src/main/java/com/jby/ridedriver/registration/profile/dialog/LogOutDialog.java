package com.jby.ridedriver.registration.profile.dialog;
import android.app.Dialog;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
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

public class LogOutDialog extends DialogFragment implements View.OnClickListener {
    View rootView;
    private Button logOutDialogConfirm;
    private TextView logOutDialogCancel;
    private LinearLayout logOutDialogMainLayout;
    LogOutDialogCallBack logOutDialogCallBack;

    public LogOutDialog() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.logout_dialog, container);
        objectInitialize();
        logOutDialogCallBack = (LogOutDialogCallBack) getActivity();
        return rootView;
    }

    private void objectInitialize() {
        logOutDialogMainLayout = (LinearLayout) rootView.findViewById(R.id.logout_dialog_main_layout);
        logOutDialogCancel = (TextView)rootView.findViewById(R.id.logout_dialog_cancel_button);
        logOutDialogConfirm = (Button) rootView.findViewById(R.id.logout_dialog_confirm_button);

        logOutDialogMainLayout.setOnClickListener(this);
        logOutDialogCancel.setOnClickListener(this);
        logOutDialogConfirm.setOnClickListener(this);
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
            case R.id.logout_dialog_cancel_button:
                dismiss();
                break;
            case R.id.logout_dialog_main_layout:
                dismiss();
                break;
            case R.id.logout_dialog_confirm_button:
                logOutDialogCallBack.logOut();
                break;
        }
    }
    public interface LogOutDialogCallBack {
        void logOut();
    }

}