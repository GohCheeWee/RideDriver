package com.jby.ridedriver.registration.registration;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.jby.ridedriver.R;
import com.jby.ridedriver.registration.home.HomeActivity;
import com.jby.ridedriver.registration.profile.editProfile.EditProfileActivity;
import com.jby.ridedriver.registration.shareObject.ApiDataObject;
import com.jby.ridedriver.registration.shareObject.ApiManager;
import com.jby.ridedriver.registration.shareObject.AsyncTaskManager;
import com.jby.ridedriver.registration.registration.dialog.SomethingMissingDialog;
import com.jby.ridedriver.registration.sharePreference.SharedPreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText loginActivityEmail, loginActivityPassword;
    private TextView loginActivityForgotPassword;
    private ImageView loginActivityShowPassword, loginActivityCancelEmail;
    private LinearLayout loginActivityMainLayout;
    private ProgressBar loginActivityProgressBar;
    private boolean show = true;
    AsyncTaskManager asyncTaskManager;
    JSONObject jsonObjectLoginResponse;
    ArrayList<ApiDataObject> apiDataObjectArrayList;
    private Handler handler;
    //    dialog
    DialogFragment dialogFragment;
    FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        isLogin();
        objectInitialize();
        objectSetting();
    }

    private void objectInitialize() {
        loginActivityEmail = (EditText)findViewById(R.id.activity_login_email);
        loginActivityPassword = (EditText)findViewById(R.id.activity_login_password);

        loginActivityForgotPassword = (TextView)findViewById(R.id.activity_login_forgot_password);

        loginActivityShowPassword = (ImageView)findViewById(R.id.activity_login_show_password);
        loginActivityCancelEmail = (ImageView)findViewById(R.id.activity_login_cancel_email);

        loginActivityMainLayout = (LinearLayout)findViewById(R.id.activity_login_main_layout);
        loginActivityProgressBar = (ProgressBar)findViewById(R.id.login_activity_progress_bar);

        handler = new Handler();
        fm = getSupportFragmentManager();

    }

    private void objectSetting() {
        loginActivityShowPassword.setOnClickListener(this);
        loginActivityCancelEmail.setOnClickListener(this);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( this,  new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                SharedPreferenceManager.setDeviceToken(LoginActivity.this, newToken);
                Log.e("newToken",newToken);
            }
        });

    }

    public void signUp(View view){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    public void closeKeyBoard(){
        View view = getCurrentFocus();
        final InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null && view != null)
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.activity_login_show_password:
                showPasswordSetting();
                break;
            case R.id.activity_login_cancel_email:
                loginActivityEmail.setText("");
                break;
        }
    }

    //    show/ hide password setting
    private void showPasswordSetting(){
        if(show){
            loginActivityShowPassword.setImageDrawable(getResources().getDrawable(R.drawable.activity_login_hide_icon));
            loginActivityPassword.setTransformationMethod(null);
            show = false;
        }
        else{
            loginActivityShowPassword.setImageDrawable(getResources().getDrawable(R.drawable.activity_login_show_icon));
            loginActivityPassword.setTransformationMethod(new PasswordTransformationMethod());
            show = true;
        }
    }

    //    sign in setting
    public void checking(View v){
        loginActivityProgressBar.setVisibility(View.VISIBLE);
        final String email = loginActivityEmail.getText().toString().trim();
        final String password = loginActivityPassword.getText().toString().trim();
        closeKeyBoard();

        if(!email.equals("") && !password.equals(""))
        {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    signIn(email, password);
                }
            },200);
        }
        else
            alertDialog();

    }

    public void signIn(String email, String password){
        apiDataObjectArrayList = new ArrayList<>();
        apiDataObjectArrayList.add(new ApiDataObject("password", password));
        apiDataObjectArrayList.add(new ApiDataObject("email", email));

        asyncTaskManager = new AsyncTaskManager(
                this,
                new ApiManager().login,
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
                        String userID = jsonObjectLoginResponse.getString("driver_id");
                        String userName = jsonObjectLoginResponse.getString("username");
                        SharedPreferenceManager.setUserID(this, userID);
                        SharedPreferenceManager.setUserName(this, userName);
                        registerDriverToken();
                    }
                    else if(jsonObjectLoginResponse.getString("status").equals("2")){
                        loginActivityProgressBar.setVisibility(View.GONE);
                        showSnackBar();
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

    public void registerDriverToken(){
        apiDataObjectArrayList = new ArrayList<>();
        apiDataObjectArrayList.add(new ApiDataObject("driver_id", SharedPreferenceManager.getUserID(this)));
        apiDataObjectArrayList.add(new ApiDataObject("token", SharedPreferenceManager.getDeviceToken(this)));

        asyncTaskManager = new AsyncTaskManager(
                this,
                new ApiManager().login,
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
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        },200);
                    }
                    else if(jsonObjectLoginResponse.getString("status").equals("2"))
                        showSnackBar();

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
        loginActivityProgressBar.setVisibility(View.GONE);
    }

    //    snackBar setting
    private void showSnackBar(){
        final Snackbar snackbar = Snackbar.make(loginActivityMainLayout, "Invalid email or password", Snackbar.LENGTH_SHORT);
        snackbar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    //    dialogSetting
    private void alertDialog(){
        dialogFragment = new SomethingMissingDialog();
        dialogFragment.show(fm, "");
    }

    //    if login then redirect
    private void isLogin(){
        if(!SharedPreferenceManager.getUserID(this).equals("default")){
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }
}

