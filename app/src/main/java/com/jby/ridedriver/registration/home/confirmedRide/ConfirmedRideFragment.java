package com.jby.ridedriver.registration.home.confirmedRide;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jby.ridedriver.R;
import com.jby.ridedriver.registration.home.confirmedRide.dialog.ShowConfirmedRiderAdapter;
import com.jby.ridedriver.registration.home.confirmedRide.dialog.ShowConfirmedRiderDialog;
import com.jby.ridedriver.registration.home.confirmedRide.dialog.StartRouteDialog;
import com.jby.ridedriver.registration.home.confirmedRide.startRoute.StartRouteActivity;
import com.jby.ridedriver.registration.others.CustomListView;
import com.jby.ridedriver.registration.shareObject.ApiDataObject;
import com.jby.ridedriver.registration.shareObject.ApiManager;
import com.jby.ridedriver.registration.shareObject.AsyncTaskManager;
import com.jby.ridedriver.registration.sharePreference.SharedPreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ConfirmedRideFragment extends Fragment implements ConfirmedRideAdapter.ConfirmRideAdapterCallBack,
        SwipeRefreshLayout.OnRefreshListener, StartRouteDialog.StartRouteDialogCallBack{
    View rootView;
    private SwipeRefreshLayout confirmRideFragmentSwipeRefreshLayout;
    private RelativeLayout confirmRideFragmentNotFound;
    private ProgressBar confirmRideFragmentProgressBar;

    private CustomListView confirmRideFragmentListView;
    private ConfirmedRideAdapter confirmedRideAdapter;
    private ArrayList<ConfirmedRideObject> confirmedRideObjectArrayList;

    //    asyncTask
    AsyncTaskManager asyncTaskManager;
    JSONObject jsonObjectLoginResponse;
    ArrayList<ApiDataObject> apiDataObjectArrayList;
    private Handler handler;

    DialogFragment dialogFragment;
    FragmentManager fm;
    private int position = 0;

    public static int UPDATE_CONFIRMED_RIDE_REQUEST = 400;

    public ConfirmedRideFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_confirmed_ride, container, false);
        objectInitialize();
        objectSetting();
        return rootView;
    }

    private void objectInitialize() {
        confirmRideFragmentSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.fragment_confirmed_ride_swipe_refresh_layout);
        confirmRideFragmentProgressBar = (ProgressBar) rootView.findViewById(R.id.fragment_confirmed_ride_progress_bar);
        confirmRideFragmentNotFound = (RelativeLayout) rootView.findViewById(R.id.fragment_confirmed_ride_not_found);

        confirmRideFragmentListView = (CustomListView)rootView.findViewById(R.id.fragment_confirmed_ride_list_view);
        confirmedRideObjectArrayList = new ArrayList<>();
        confirmedRideAdapter = new ConfirmedRideAdapter(getActivity(), confirmedRideObjectArrayList,this);
        handler = new Handler();
        fm = getActivity().getSupportFragmentManager();
    }

    private void objectSetting() {
        confirmRideFragmentSwipeRefreshLayout.setOnRefreshListener(this);
        confirmRideFragmentSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.red));
        confirmRideFragmentListView.setAdapter(confirmedRideAdapter);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getConfirmRide();
            }
        },200);
    }

    private void getConfirmRide(){
        apiDataObjectArrayList = new ArrayList<>();
        apiDataObjectArrayList.add(new ApiDataObject("driver_id", SharedPreferenceManager.getUserID(getActivity())));

        asyncTaskManager = new AsyncTaskManager(
                getActivity(),
                new ApiManager().confirmRide,
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
                        JSONArray jsonArray = jsonObjectLoginResponse.getJSONArray("value").getJSONObject(0).getJSONArray("confirm_ride");
                        for(int i = 0 ; i< jsonArray.length(); i++){
                            confirmedRideObjectArrayList.add(new ConfirmedRideObject(
                                    jsonArray.getJSONObject(i).getString("route_title"),
                                    jsonArray.getJSONObject(i).getString("pick_up_address"),
                                    jsonArray.getJSONObject(i).getString("drop_off_address"),
                                    jsonArray.getJSONObject(i).getString("note"),
                                    jsonArray.getJSONObject(i).getString("confirm_num"),
                                    jsonArray.getJSONObject(i).getString("number_people"),
                                    jsonArray.getJSONObject(i).getString("estimate_fare"),
                                    jsonArray.getJSONObject(i).getString("date"),
                                    jsonArray.getJSONObject(i).getString("time"),
                                    jsonArray.getJSONObject(i).getString("driver_ride_id"),
                                    jsonArray.getJSONObject(i).getString("status")
                                    ));
                        }
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
        setUpView();
    }

    private void setUpView() {
        confirmRideFragmentProgressBar.setVisibility(View.GONE);
        if(confirmedRideObjectArrayList.size() > 0)
        {
            confirmRideFragmentNotFound.setVisibility(View.GONE);
            confirmRideFragmentListView.setVisibility(View.VISIBLE);
            confirmRideFragmentSwipeRefreshLayout.setVisibility(View.VISIBLE);
        }
        else{
            confirmRideFragmentNotFound.setVisibility(View.VISIBLE);
            confirmRideFragmentListView.setVisibility(View.GONE);
            confirmRideFragmentSwipeRefreshLayout.setVisibility(View.GONE);
        }
        confirmedRideAdapter.notifyDataSetChanged();
    }

    @Override
    public void startRoute(int position) {
        dialogFragment = new StartRouteDialog();
        this.position = position;

        Bundle bundle = new Bundle();
        bundle.putString("driver_ride_id", confirmedRideObjectArrayList.get(position).getDriver_ride_id());

        dialogFragment.setArguments(bundle);
        dialogFragment.setTargetFragment(ConfirmedRideFragment.this, 306);
        dialogFragment.show(fm, "");
    }

    @Override
    public void viewConfirmedRider(String driverRideID) {
        dialogFragment = new ShowConfirmedRiderDialog();
        Bundle bundle = new Bundle();
        bundle.putString("driver_ride_id", driverRideID);
        dialogFragment.setArguments(bundle);
        dialogFragment.show(fm, null);
    }

    @Override
    public void continueMyRoute(int position) {
        startRouteActivity(confirmedRideObjectArrayList.get(position).getDriver_ride_id());
    }

    @Override
    public void onRefresh() {
        confirmRideFragmentProgressBar.setVisibility(View.VISIBLE);
        confirmRideFragmentSwipeRefreshLayout.setRefreshing(false);
        confirmedRideObjectArrayList.clear();
        confirmedRideAdapter.notifyDataSetChanged();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getConfirmRide();
            }
        },200);
    }

    @Override
    public void startRouteActivity(String driverRideId) {
        Intent intent = new Intent(getActivity(), StartRouteActivity.class);
        Bundle bundle = new Bundle();

        bundle.putString("driver_ride_id", driverRideId);
        intent.putExtras(bundle);
        startActivityForResult(intent, UPDATE_CONFIRMED_RIDE_REQUEST);
        getActivity().finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == UPDATE_CONFIRMED_RIDE_REQUEST){
            onRefresh();
        }
    }
}
