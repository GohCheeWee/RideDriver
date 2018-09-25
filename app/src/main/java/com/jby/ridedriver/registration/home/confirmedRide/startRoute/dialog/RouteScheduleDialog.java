package com.jby.ridedriver.registration.home.confirmedRide.startRoute.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jby.ridedriver.R;
import com.jby.ridedriver.registration.home.confirmedRide.startRoute.object.StartRouteRiderObject;
import com.jby.ridedriver.registration.shareObject.ApiDataObject;
import com.jby.ridedriver.registration.shareObject.ApiManager;
import com.jby.ridedriver.registration.shareObject.AsyncTaskManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RouteScheduleDialog extends DialogFragment implements RouteScheduleDialogAdapter.RouteScheduleDialogAdapterCallBack,
        View.OnClickListener{
    View rootView;
    private ListView routeScheduleDialogListView;
    private TextView routeScheduleDialogTitle;
    private TextView routeScheduleDialogCancelButton, routeScheduleDialogConfirmButton;
    private ArrayList<StartRouteRiderObject> startRouteRiderObjectArrayList;
    private RouteScheduleDialogAdapter routeScheduleDialogAdapter;
    private String status;
    private int position;

    RouteScheduleDialogCallBack routeScheduleDialogCallBack;

    public RouteScheduleDialog() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.start_route_activity_route_schedule_dialog, container);
        objectInitialize();
        objectSetting();

        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    private void objectInitialize() {
        routeScheduleDialogCallBack = (RouteScheduleDialogCallBack) getActivity();

        routeScheduleDialogListView = rootView.findViewById(R.id.route_schedule_dialog_list_view);
        routeScheduleDialogTitle = rootView.findViewById(R.id.route_schedule_dialog_title);
        routeScheduleDialogCancelButton = rootView.findViewById(R.id.route_schedule_dialog_cancel_button);
        routeScheduleDialogConfirmButton = rootView.findViewById(R.id.route_schedule_dialog_confirm_button);
        startRouteRiderObjectArrayList = new ArrayList<>();
    }

    private void objectSetting() {
        Bundle bundle = getArguments();
        if(bundle != null){
            ArrayList<StartRouteRiderObject> arrayList = bundle.getParcelableArrayList("array");
            //determine whether is pick up or drop off status
            status = bundle.getString("status");
            if (status != null) {
                switch(status){
                    case "3":
                        routeScheduleDialogTitle.setText(R.string.route_schedule_dialog_pick_up_title);
                        break;
                    case "5":
                        routeScheduleDialogTitle.setText(R.string.route_schedule_dialog_drop_off_title);
                        break;
                }
            }
            assert arrayList != null;
            setUpArrayList(arrayList);
        }
        //setup array list
        routeScheduleDialogAdapter = new RouteScheduleDialogAdapter(getActivity(), startRouteRiderObjectArrayList, this);
        routeScheduleDialogListView.setAdapter(routeScheduleDialogAdapter);
        routeScheduleDialogCancelButton.setOnClickListener(this);
        routeScheduleDialogConfirmButton.setOnClickListener(this);
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

    private void setUpArrayList(ArrayList<StartRouteRiderObject> arrayList){
        for(int i = 0; i< arrayList.size(); i++){
            // pick up address
            if(status.equals("3") && arrayList.get(i).getStatus().equals("3"))
                startRouteRiderObjectArrayList.add(arrayList.get(i));
            //drop off address
            else if(status.equals("5") && arrayList.get(i).getStatus().equals("5"))
                startRouteRiderObjectArrayList.add(arrayList.get(i));
        }
    }
    @Override
    public void selectPosition(int position) {
        this.position = position;
        routeScheduleDialogConfirmButton.setEnabled(true);
        routeScheduleDialogConfirmButton.setTextColor(getActivity().getResources().getColor(R.color.red));
    }

    @Override
    public void onClick(View view) {
        clickEffect(view);
        switch (view.getId()){
            case R.id.route_schedule_dialog_confirm_button:
                if(status.equals("5")) routeScheduleDialogCallBack.updateDropOffAddress(position);
                else routeScheduleDialogCallBack.updatePickUpAddress(position);
                dismiss();
                break;
            case R.id.route_schedule_dialog_cancel_button:
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

    public interface RouteScheduleDialogCallBack {
        void updatePickUpAddress(int position);
        void updateDropOffAddress(int position);
    }
}