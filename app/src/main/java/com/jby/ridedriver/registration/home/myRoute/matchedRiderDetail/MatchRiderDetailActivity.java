package com.jby.ridedriver.registration.home.myRoute.matchedRiderDetail;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jby.ridedriver.R;
import com.jby.ridedriver.registration.home.myRoute.MyRouteFragment;
import com.jby.ridedriver.registration.home.myRoute.matchedRiderDetail.dialog.AcceptDialog;
import com.jby.ridedriver.registration.home.myRoute.matchedRiderDetail.riderProfile.RiderProfileDialog;
import com.jby.ridedriver.registration.home.myRoute.object.MatchedRiderDetailObject;
import com.jby.ridedriver.registration.others.CustomListView;
import com.jby.ridedriver.registration.others.SquareHeightLinearLayout;
import com.jby.ridedriver.registration.shareObject.ApiDataObject;
import com.jby.ridedriver.registration.shareObject.ApiManager;
import com.jby.ridedriver.registration.shareObject.AsyncTaskManager;
import com.jby.ridedriver.registration.sharePreference.SharedPreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.jby.ridedriver.registration.home.myRoute.MyRouteFragment.AFTER_ACCEPT_REQUEST;


public class MatchRiderDetailActivity extends AppCompatActivity implements MatchRiderDetailAdapter.MatchRiderDetailAdapterCallBack,
        SwipeRefreshLayout.OnRefreshListener, AcceptDialog.AcceptDialogCallBack, View.OnClickListener
{
    //    actionBar
    private SquareHeightLinearLayout actionBarMenuIcon, actionBarCloseIcon, actionBarLogout;
    private TextView actionBarTitle;

    private CustomListView matchRiderDetailCustomListView;
    private RelativeLayout matchRiderDetailNotFoundLayout;
    private ArrayList<MatchRiderDetailObject> matchRiderDetailObjectArrayList;
    private MatchRiderDetailAdapter matchRiderDetailAdapter;
    private ProgressBar matchRiderDetailProgressBar;
    private SwipeRefreshLayout matchRiderDetailSwipeRefreshLayout;

    private ArrayList<MatchedRiderDetailObject> matchedRiderRouteId = new ArrayList<>();
    private String driverRideId, rider_id, rider_ride_id;
    private int position;

    private ArrayList<Integer> acceptRideID = new ArrayList<>();
    private int requestCode;

    //    asyncTask
    AsyncTaskManager asyncTaskManager;
    JSONObject jsonObjectLoginResponse;
    ArrayList<ApiDataObject> apiDataObjectArrayList;
    private Handler handler;

    private FragmentManager fm;
    private DialogFragment dialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_rider_detail);
        objectInitialize();
        objectSetting();
    }

    private void objectInitialize() {
        actionBarMenuIcon = (SquareHeightLinearLayout)findViewById(R.id.actionbar_menu);
        actionBarCloseIcon = (SquareHeightLinearLayout)findViewById(R.id.actionbar_close);
        actionBarLogout = (SquareHeightLinearLayout)findViewById(R.id.actionbar_logout);
        actionBarTitle = (TextView)findViewById(R.id.actionBar_title);

        matchRiderDetailProgressBar = (ProgressBar)findViewById(R.id.activity_match_rider_detail_progress_bar);
        matchRiderDetailCustomListView = (CustomListView)findViewById(R.id.activity_match_rider_detail_list_view);
        matchRiderDetailNotFoundLayout = (RelativeLayout)findViewById(R.id.activity_match_rider_detail_not_found_layout);
        matchRiderDetailSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.activity_match_rider_detail_swipe_refresh_layout);
        matchRiderDetailObjectArrayList = new ArrayList<>();
        matchRiderDetailAdapter = new MatchRiderDetailAdapter(this, matchRiderDetailObjectArrayList, this);
        handler = new Handler();
        fm = getSupportFragmentManager();
    }

    private void objectSetting() {
        //        actionBar
        actionBarTitle.setText(R.string.match_rider_detail);
        actionBarMenuIcon.setVisibility(View.GONE);
        actionBarCloseIcon.setVisibility(View.VISIBLE);

        actionBarCloseIcon.setOnClickListener(this);

        matchRiderDetailSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.red));
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            matchedRiderRouteId = bundle.getParcelableArrayList("matched_rider_array_list");
            driverRideId = bundle.getString("driver_ride_id");
        }
        matchRiderDetailCustomListView.setAdapter(matchRiderDetailAdapter);
        matchRiderDetailSwipeRefreshLayout.setOnRefreshListener(this);

//        first checking if item then display not found
        if(matchedRiderRouteId.size() >  0){
            matchRiderDetailProgressBar.setVisibility(View.VISIBLE);
            setupListView();
        }
        else{
            matchRiderDetailNotFoundLayout.setVisibility(View.VISIBLE);
            matchRiderDetailProgressBar.setVisibility(View.GONE);
        }

    }

    @Override
    public void acceptRider(String rider_id, String rider_ride_id, int position) {
        this.rider_id = rider_id;
        this.rider_ride_id = rider_ride_id;
        this.position = position;

        dialogFragment = new AcceptDialog();
        dialogFragment.show(fm, "");
    }

    @Override
    public void viewRiderProfile(String riderID) {
        dialogFragment = new RiderProfileDialog();
        Bundle bundle = new Bundle();
        bundle.putString("rider_id", riderID);
        dialogFragment.setArguments(bundle);
        dialogFragment.show(fm, "");
    }
/*------------------------------------get matched ride purpose------------------------------------------------------------------*/
    private void setupListView(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for(int i = 0 ; i< matchedRiderRouteId.size(); i++){
                    getMatchedRoute(matchedRiderRouteId.get(i).getRideID(), i);
                }
            }
        },200);
    }

    private void getMatchedRoute(String routeID, int position){
        apiDataObjectArrayList = new ArrayList<>();
        apiDataObjectArrayList.add(new ApiDataObject("route_id", routeID));
        apiDataObjectArrayList.add(new ApiDataObject("getRiderRide", "1"));

        asyncTaskManager = new AsyncTaskManager(
                this,
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
                        JSONArray jsonArray = jsonObjectLoginResponse.getJSONArray("value").getJSONObject(0).getJSONArray("matched_ride");
                        matchRiderDetailObjectArrayList.add(new MatchRiderDetailObject(
                                jsonArray.getJSONObject(0).getString("username"),
                                jsonArray.getJSONObject(0).getString("profile_picture"),
                                jsonArray.getJSONObject(0).getString("gender"),
                                jsonArray.getJSONObject(0).getString("pick_up_address"),
                                jsonArray.getJSONObject(0).getString("drop_off_address"),
                                jsonArray.getJSONObject(0).getString("note"),
                                jsonArray.getJSONObject(0).getString("fare"),
                                jsonArray.getJSONObject(0).getString("payment_method"),
                                jsonArray.getJSONObject(0).getString("user_id"),
                                jsonArray.getJSONObject(0).getString("id")
                        ));
                    }
                }
                else {
                    Toast.makeText(this, "Network Error!", Toast.LENGTH_SHORT).show();
                }

            } catch (InterruptedException e) {
                Toast.makeText(this, "Interrupted Exception!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (ExecutionException e) {
                Toast.makeText(this,"Execution Exception!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (JSONException e) {
                Toast.makeText(this, "JSON Exception!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (TimeoutException e) {
                Toast.makeText(this, "Connection Time Out!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
//        if position == matchedRiderRouteId.size then close progress bar
        setUpView(position);
        matchRiderDetailAdapter.notifyDataSetChanged();
    }
    /*---------------------------------------aceept rider purpopse-----------------------------------------------------------------------*/

    public void acceptRiderAction(){
        apiDataObjectArrayList = new ArrayList<>();
        apiDataObjectArrayList.add(new ApiDataObject("driver_ride_id", driverRideId));
        apiDataObjectArrayList.add(new ApiDataObject("driver_id", SharedPreferenceManager.getUserID(this)));
        apiDataObjectArrayList.add(new ApiDataObject("rider_id", rider_id));
        apiDataObjectArrayList.add(new ApiDataObject("rider_ride_id", rider_ride_id));

        asyncTaskManager = new AsyncTaskManager(
                this,
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
                        showSnackBar("Request Send!");
                        updateViewAfterAccept(position);
                    }
                }
                else {
                    Toast.makeText(this, "Network Error!", Toast.LENGTH_SHORT).show();
                }

            } catch (InterruptedException e) {
                Toast.makeText(this, "Interrupted Exception!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (ExecutionException e) {
                Toast.makeText(this,"Execution Exception!", Toast.LENGTH_SHORT).show();
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
//    call when accept rider
    private void updateViewAfterAccept(int position) {
        matchRiderDetailObjectArrayList.remove(position);
        matchRiderDetailAdapter.notifyDataSetChanged();
        acceptRideID.add(position);
        requestCode = AFTER_ACCEPT_REQUEST;

        if(matchRiderDetailObjectArrayList.size() == 0){
            matchRiderDetailSwipeRefreshLayout.setVisibility(View.GONE);
            matchRiderDetailNotFoundLayout.setVisibility(View.VISIBLE);
        }
    }

//    call when the listview item is loaded
    private void setUpView(int position){
        if(position == matchedRiderRouteId.size()-1){
            matchRiderDetailProgressBar.setVisibility(View.GONE);
            if(matchRiderDetailObjectArrayList.size() > 0){
                matchRiderDetailNotFoundLayout.setVisibility(View.GONE);
                matchRiderDetailSwipeRefreshLayout.setVisibility(View.VISIBLE);

            }
            else
            {
                matchRiderDetailNotFoundLayout.setVisibility(View.VISIBLE);
                matchRiderDetailSwipeRefreshLayout.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onRefresh() {
        matchRiderDetailProgressBar.setVisibility(View.VISIBLE);
        matchRiderDetailSwipeRefreshLayout.setRefreshing(false);
        matchRiderDetailObjectArrayList.clear();
        matchRiderDetailAdapter.notifyDataSetChanged();
        setupListView();
    }

    //    snackBar setting
    private void showSnackBar(String message){
        final Snackbar snackbar = Snackbar.make(matchRiderDetailSwipeRefreshLayout, message, Snackbar.LENGTH_SHORT);
        snackbar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putIntegerArrayListExtra("accept_rider_id", acceptRideID);
        setResult(requestCode, intent);
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.actionbar_close:
                onBackPressed();
                break;
        }
    }
}
