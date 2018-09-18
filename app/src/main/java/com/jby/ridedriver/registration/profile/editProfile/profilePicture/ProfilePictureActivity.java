package com.jby.ridedriver.registration.profile.editProfile.profilePicture;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jby.ridedriver.BuildConfig;
import com.jby.ridedriver.R;
import com.jby.ridedriver.registration.others.SquareHeightLinearLayout;
import com.jby.ridedriver.registration.profile.UploadImageDialog;
import com.jby.ridedriver.registration.shareObject.ApiDataObject;
import com.jby.ridedriver.registration.shareObject.ApiManager;
import com.jby.ridedriver.registration.shareObject.AsyncTaskManager;
import com.jby.ridedriver.registration.sharePreference.SharedPreferenceManager;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.theartofdev.edmodo.cropper.CropImageView.CropShape.OVAL;
import static com.theartofdev.edmodo.cropper.CropImageView.Guidelines.ON_TOUCH;

public class ProfilePictureActivity extends AppCompatActivity implements View.OnClickListener,
        UploadImageDialog.UploadImageDialogListener{
    private SquareHeightLinearLayout actionBarMenuIcon, actionBarCloseIcon, actionBarLogout;
    private TextView actionBarTitle;

    private ImageView profilePictureActivityCameraIcon;
    private CircleImageView profilePictureActivityProfilePicture;
    private Button profilePictureActivityUploadButton;

    private int requestCode;

    //    dialog
    DialogFragment dialogFragment;
    Bundle bundle;
    FragmentManager fm;

    //    upload image purpose
    private static final int MY_READ_PERMISSION_REQUEST_CODE = 1;
    private static final int PICK_IMAGE_REQUEST = 2;
    private static final int CAMERA_REQUEST = 10;
    private static final int REQUEST_ACCESS_CAMERA = 3;
    private String newPhotoUrl;
    private Uri photoUri, croppedUrl;
    private String imageCode = null;

    //    async purpose
    AsyncTaskManager asyncTaskManager;
    JSONObject jsonObjectLoginResponse;
    ArrayList<ApiDataObject> apiDataObjectArrayList;
    private Handler handler;
    //    path
    private static String prefix = "http://188.166.186.198/~cheewee/ride/frontend/driver/profile/driver_profile_picture/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_picture);
        objectInitialize();
        objectSetting();
    }

    private void objectInitialize() {
        actionBarMenuIcon = (SquareHeightLinearLayout)findViewById(R.id.actionbar_menu);
        actionBarCloseIcon = (SquareHeightLinearLayout)findViewById(R.id.actionbar_close);
        actionBarLogout = (SquareHeightLinearLayout)findViewById(R.id.actionbar_logout);
        actionBarTitle = (TextView)findViewById(R.id.actionBar_title);

        profilePictureActivityProfilePicture = (CircleImageView) findViewById(R.id.activity_profile_picture_image_view);
        profilePictureActivityCameraIcon = (ImageView)findViewById(R.id.activity_profile_picture_camera_icon);
        profilePictureActivityUploadButton = (Button)findViewById(R.id.activity_profile_picture_upload_button);

        fm = getSupportFragmentManager();
        handler = new Handler();
    }

    private void objectSetting() {
        //        actionBar
        actionBarTitle.setText(R.string.activity_profile_title);
        actionBarMenuIcon.setVisibility(View.GONE);
        actionBarCloseIcon.setVisibility(View.VISIBLE);

        actionBarCloseIcon.setOnClickListener(this);
        profilePictureActivityCameraIcon.setOnClickListener(this);
        profilePictureActivityUploadButton.setOnClickListener(this);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getDriverProfilePicture();
            }
        },50);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.actionbar_close:
                onBackPressed();
                break;
            case R.id.activity_profile_picture_camera_icon:
                dialogFragment = new UploadImageDialog();
                dialogFragment.show(fm, "");
                break;
            case R.id.activity_profile_picture_upload_button:
                checkingBeforeUpload();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        setResult(requestCode);
        super.onBackPressed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST) {
            if (data != null && resultCode == RESULT_OK) {
//                getting photo from gallery
                photoUri = data.getData();
                cropImage(photoUri);
            }

        } else if (requestCode == CAMERA_REQUEST) {
            if (resultCode == RESULT_OK) {
//                getting image from camera
                photoUri = Uri.parse(newPhotoUrl);

                cropImage(photoUri);
            }
        }
        else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                croppedUrl = result.getUri();
                try
                {
                    Bitmap croppedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), croppedUrl);
                    profilePictureActivityProfilePicture.setImageBitmap(croppedImage);
                    imageCode = encodeToBase64(getResizedBitmap(croppedImage, 500));

                }
                catch (Exception e)
                {
                    //handle exception
                }
            }
        }
        else {
            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
    }

    public void checkGalleryPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_READ_PERMISSION_REQUEST_CODE);
            } else {
                openGallery();
            }
        }
    }

    public void checkCapturePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCameraApp();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE}
                        , REQUEST_ACCESS_CAMERA);
            } else {
                openCameraApp();
            }
        }
    }

    private void openGallery() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMinCropWindowSize(250,250)
                .setAspectRatio(1, 1)
                .setActivityTitle("Profile")
                .setCropShape(OVAL)
                .setAutoZoomEnabled(false)
                .setActivityMenuIconColor(R.color.red)
                .start(this);
    }

    private void openCameraApp() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile;
            try {
                photoFile = createImageFile();

                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,  BuildConfig.APPLICATION_ID + ".provider", createImageFile());
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //create file for storing
    private File createImageFile() throws IOException {
        String timeStamp = String.valueOf(android.text.format.DateFormat.format("yyyyMMMdd_HHmmss", new java.util.Date()));

        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        newPhotoUrl = "file:" + image.getAbsolutePath();
        return image;
    }
    //resize image
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
    //encode
    public static String encodeToBase64(Bitmap image) {
        ByteArrayOutputStream bAOS = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, bAOS);
        byte[] b = bAOS.toByteArray();
        return Base64.encodeToString(b,Base64.DEFAULT);
    }

    private void cropImage(Uri photo){
        CropImage.activity(photo)
                .setMinCropWindowSize(250,250)
                .setAspectRatio(1, 1)
                .setActivityTitle("Profile")
                .setCropShape(OVAL)
                .setGuidelines(ON_TOUCH)
                .setAutoZoomEnabled(false)
                .setActivityMenuIconColor(R.color.red)
                .start(this);
    }

    public void getDriverProfilePicture(){
        apiDataObjectArrayList = new ArrayList<>();
        apiDataObjectArrayList.add(new ApiDataObject("driver_id", SharedPreferenceManager.getUserID(this)));
        apiDataObjectArrayList.add(new ApiDataObject("profile_picture", "1"));

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
                        String profilePicture = jsonObjectLoginResponse.getJSONObject("value").getString("profile_pic");
                        setupProfilePicture(profilePicture);
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

    private void setupProfilePicture(String profilePicture){
        if(!profilePicture.equals(""))
        {
            profilePicture = prefix + profilePicture;

            Picasso.get()
                    .load(profilePicture)
                    .error(R.drawable.loading_gif)
                    .into(profilePictureActivityProfilePicture);
        }
        else{
            profilePictureActivityProfilePicture.setImageDrawable(getDrawable(R.drawable.activity_profile_picture_profile_icon));
        }
    }

    private void checkingBeforeUpload(){
        if(imageCode != null){
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    uploadProfilePicture();
                }
            },200);
        }
        else
            showSnackBar("Save Successful");
    }

    private void uploadProfilePicture(){
        String userID = SharedPreferenceManager.getUserID(this);
        String timeStamp = String.valueOf(android.text.format.DateFormat.format("yyyyMMMdd_HHmmss", new java.util.Date()));
        String image_name = userID + timeStamp + ".jpg";

        apiDataObjectArrayList = new ArrayList<>();
        apiDataObjectArrayList.add(new ApiDataObject("driver_id", userID));
        apiDataObjectArrayList.add(new ApiDataObject("image_name", image_name));
        apiDataObjectArrayList.add(new ApiDataObject("driver_image", imageCode));

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
                    if(jsonObjectLoginResponse.getString("status").equals("1")){
                        showSnackBar("Save Successful");
                        requestCode = 15;

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getDriverProfilePicture();
                            }
                        },200);
                    }
                    else if(jsonObjectLoginResponse.getString("status").equals("2")){
                        showSnackBar("Something went wrong!");
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


    //    snackBar setting
    private void showSnackBar(String message){
        final Snackbar snackbar = Snackbar.make(profilePictureActivityProfilePicture, message, Snackbar.LENGTH_SHORT);
        snackbar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }
}
