package com.jby.ridedriver.registration.home.myRoute;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jby.ridedriver.R;
import com.jby.ridedriver.registration.home.myRoute.dialog.CreateRouteDialog;
import com.jby.ridedriver.registration.home.myRoute.dialog.DeleteDialog;
import com.jby.ridedriver.registration.home.myRoute.matchedRiderDetail.MatchRiderDetailActivity;
import com.jby.ridedriver.registration.home.myRoute.object.MatchRouteObject;
import com.jby.ridedriver.registration.home.myRoute.object.MatchedRiderDetailObject;
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

import static com.jby.ridedriver.registration.home.HomeActivity.TAG;


public class MyRouteFragment extends Fragment implements View.OnClickListener, MyRouteAdapter.MyRouteAdapterCallBack,
        DeleteDialog.DeleteDialogCallBack, SwipeRefreshLayout.OnRefreshListener, CreateRouteDialog.CreateRouteDialogCallBack{
    View rootView;
    private Button myRouteFragmentCreateRouteButton;
    private RelativeLayout myRouteFragmentNotFoundLayout;
    private SwipeRefreshLayout myRouteFragmentSwipeRefreshLayout;
    private ImageView myRouteFragmentFloatingButton;

    private ProgressBar myRouteFragmentProgressBar;
    private CustomListView myRouteFragmentCustomListView;
    private MyRouteAdapter myRouteAdapter;
    private ArrayList<MatchRouteObject> matchRouteObjectArrayList;
    private ArrayList<MatchedRiderDetailObject> matchedRiderDetailObjectArrayList;

    private FragmentManager fm;
    private DialogFragment dialogFragment;

    //    asyncTask
    AsyncTaskManager asyncTaskManager;
    JSONObject jsonObjectLoginResponse;
    ArrayList<ApiDataObject> apiDataObjectArrayList;
    private Handler handler;

    //    result && request code
    public static int AFTER_ACCEPT_REQUEST = 36;
    private int position;
    private String id, pickUpAddress, dropOffAddress, date, time, note, matchedRide, title;

    public MyRouteFragment() {
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
        rootView =  inflater.inflate(R.layout.fragment_my_route, container, false);
        objectInitialize();
        objectSetting();
        return  rootView;
    }

    private void objectInitialize() {
        myRouteFragmentCreateRouteButton = (Button)rootView.findViewById(R.id.fragment_my_route_button);
        myRouteFragmentNotFoundLayout = (RelativeLayout) rootView.findViewById(R.id.fragment_my_route_not_found);
        myRouteFragmentSwipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.fragment_my_route_swipe_refresh_layout);
        myRouteFragmentFloatingButton = (ImageView) rootView.findViewById(R.id.fragment_my_route_floating_button);

        myRouteFragmentProgressBar = (ProgressBar)rootView.findViewById(R.id.fragment_my_route_progress_bar);

        myRouteFragmentCustomListView = (CustomListView)rootView.findViewById(R.id.fragment_my_route_list_view);
        matchRouteObjectArrayList = new ArrayList<>();

        myRouteAdapter = new MyRouteAdapter(getActivity(), matchRouteObjectArrayList, this);
        fm  = getActivity().getSupportFragmentManager();
        handler = new Handler();
    }

    private void objectSetting() {
        myRouteFragmentSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.red));
        myRouteFragmentSwipeRefreshLayout.setOnRefreshListener(this);
        myRouteFragmentCreateRouteButton.setOnClickListener(this);
        myRouteFragmentFloatingButton.setOnClickListener(this);
        myRouteFragmentCustomListView.setAdapter(myRouteAdapter);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getDriverRoute();
            }
        },200);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fragment_my_route_button:
                openCreateRouteDialog();
                break;
            case R.id.fragment_my_route_floating_button:
                openCreateRouteDialog();
                break;
        }
    }
    /*-----------------------------------------get Route purpose----------------------------------------------------------------*/
    private void getDriverRoute(){
        apiDataObjectArrayList = new ArrayList<>();
        apiDataObjectArrayList.add(new ApiDataObject("driver_id", SharedPreferenceManager.getUserID(getActivity())));
        apiDataObjectArrayList.add(new ApiDataObject("getAllRide", "1"));

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
                        JSONArray matchRiderIdArray;
                        JSONArray jsonArray = jsonObjectLoginResponse.getJSONArray("value").getJSONObject(0).getJSONArray("ride");
                        for(int i = 0 ; i< jsonArray.length(); i++){
                            matchRiderIdArray = jsonArray.getJSONObject(i).getJSONArray("matchRiderId_arr");
                            matchRouteObjectArrayList.add(new MatchRouteObject(
                                    jsonArray.getJSONObject(i).getString("id"),
                                    jsonArray.getJSONObject(i).getString("pick_up_address"),
                                    jsonArray.getJSONObject(i).getString("drop_off_address"),
                                    jsonArray.getJSONObject(i).getString("date"),
                                    jsonArray.getJSONObject(i).getString("time"),
                                    jsonArray.getJSONObject(i).getString("note"),
                                    jsonArray.getJSONObject(i).getString("matchedRide"),
                                    "My Route",
                                    setMatchRiderId(matchRiderIdArray)
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

    private ArrayList<MatchedRiderDetailObject> setMatchRiderId(JSONArray jsonArray){
        matchedRiderDetailObjectArrayList = new ArrayList<>();
        for(int i = 0 ; i < jsonArray.length(); i++){
            try {
                matchedRiderDetailObjectArrayList.add(new MatchedRiderDetailObject(
                        jsonArray.getJSONObject(i).getString("matched_id")
                ));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return matchedRiderDetailObjectArrayList;
    }
    /*-----------------------------------------adapter call back----------------------------------------------------------------*/
    @Override
    public void deleteRoute(String routeID, int position) {
        dialogFragment = new DeleteDialog();
        Bundle bundle = new Bundle();

        bundle.putString("route_id", routeID);
        bundle.putInt("position", position);
        dialogFragment.setArguments(bundle);
        dialogFragment.setTargetFragment(MyRouteFragment.this, 303);
        dialogFragment.show(fm, "");
    }

    @Override
    public void openMatchRider(int position) {
        ArrayList<MatchedRiderDetailObject> matchedRiderDetailObjectArrayList = matchRouteObjectArrayList.get(position).getArrayList();
        this.position = position;
        Intent intent = new Intent(getActivity(), MatchRiderDetailActivity.class);
        Bundle bundle = new Bundle();

        bundle.putSerializable("matched_rider_array_list", matchedRiderDetailObjectArrayList);
        bundle.putSerializable("driver_ride_id", matchRouteObjectArrayList.get(position).getRouteID());
        intent.putExtras(bundle);
        startActivityForResult(intent, AFTER_ACCEPT_REQUEST);
    }

    private void storePositionValue(){

    }

    @Override
    public void viewRoute() {

    }

    @Override
    public void deleteSelectedRoute(String routeID, int position) {
        myRouteFragmentProgressBar.setVisibility(View.VISIBLE);
        apiDataObjectArrayList = new ArrayList<>();
        apiDataObjectArrayList.add(new ApiDataObject("ride_id", routeID));
        apiDataObjectArrayList.add(new ApiDataObject("deleteRide", "1"));

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
                        matchRouteObjectArrayList.remove(position);
                        myRouteAdapter.notifyDataSetChanged();
                        showSnackBar("Delete Successful");
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

    /*-----------------------------------------end of adapter call back----------------------------------------------------------------*/

    @Override
    public void onRefresh() {
        refreshAction();
    }

    public void openCreateRouteDialog(){
        dialogFragment = new CreateRouteDialog();
        dialogFragment.setTargetFragment(MyRouteFragment.this, 305);
        dialogFragment.show(fm, "");
    }

    public void refreshAction(){
        myRouteFragmentProgressBar.setVisibility(View.VISIBLE);
        myRouteFragmentSwipeRefreshLayout.setRefreshing(false);
        matchRouteObjectArrayList.clear();
        matchRouteObjectArrayList.clear();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getDriverRoute();
            }
        },200);
    }

    private void setUpView() {
        myRouteFragmentProgressBar.setVisibility(View.GONE);
        if(matchRouteObjectArrayList.size() > 0)
        {
            myRouteFragmentNotFoundLayout.setVisibility(View.GONE);
            myRouteFragmentCustomListView.setVisibility(View.VISIBLE);
            myRouteFragmentSwipeRefreshLayout.setVisibility(View.VISIBLE);
            myRouteFragmentFloatingButton.setVisibility(View.VISIBLE);
        }
        else{
            myRouteFragmentNotFoundLayout.setVisibility(View.VISIBLE);
            myRouteFragmentCustomListView.setVisibility(View.GONE);
            myRouteFragmentSwipeRefreshLayout.setVisibility(View.GONE);
            myRouteFragmentFloatingButton.setVisibility(View.GONE);
        }
        myRouteAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == AFTER_ACCEPT_REQUEST){
            ArrayList<Integer> acceptRiderID = data.getIntegerArrayListExtra("accept_rider_id");
            updateViewAfterAcceptRider(acceptRiderID);
        }
    }

    private void updateViewAfterAcceptRider(ArrayList<Integer> data){
        id = matchRouteObjectArrayList.get(position).getRouteID();
        pickUpAddress = matchRouteObjectArrayList.get(position).getPickUpAddress();
        dropOffAddress = matchRouteObjectArrayList.get(position).getDropOffAddress();
        date = matchRouteObjectArrayList.get(position).getDate();
        time = matchRouteObjectArrayList.get(position).getTime();
        note = matchRouteObjectArrayList.get(position).getNote();
        matchedRide = matchRouteObjectArrayList.get(position).getMatchNum();
        title = matchRouteObjectArrayList.get(position).getRouteTitle();

        for(int i = 0; i < data.size(); i++){
            int removeID = data.get(i);
            matchRouteObjectArrayList.get(position).getArrayList().remove(removeID);
        }
        ArrayList<MatchedRiderDetailObject> matchedRiderDetailObjectArrayList = matchRouteObjectArrayList.get(position).getArrayList();
        String matchNumber = String.valueOf(Integer.valueOf(matchedRide) - data.size());

        matchRouteObjectArrayList.set(position, new MatchRouteObject(id, pickUpAddress, dropOffAddress, date, time, note, matchNumber, title, matchedRiderDetailObjectArrayList));
        myRouteAdapter.notifyDataSetChanged();
    }

    //    snackBar setting
    private void showSnackBar(String message){
        final Snackbar snackbar = Snackbar.make(myRouteFragmentSwipeRefreshLayout, message, Snackbar.LENGTH_SHORT);
        snackbar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }
}
