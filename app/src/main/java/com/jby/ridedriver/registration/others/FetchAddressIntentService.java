package com.jby.ridedriver.registration.others;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;


import com.google.android.gms.maps.model.LatLng;
import com.jby.ridedriver.R;
import com.jby.ridedriver.registration.shareObject.AddressObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.jby.ridedriver.registration.home.HomeActivity.TAG;

public class FetchAddressIntentService extends IntentService {
    protected ResultReceiver mReceiver;
    ArrayList<AddressObject> addressFragments = new ArrayList<>();

    public FetchAddressIntentService() {
        super("FetchAddressIntentService");
    }

    // ...
    private void deliverResultToReceiver(int resultCode, ArrayList<AddressObject> addressFragments) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(LocationConstants.RESULT_DATA_KEY, addressFragments);
        mReceiver.send(resultCode, bundle);

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) {
            Log.i(TAG, "Intent null");
            return;
        }
        mReceiver = intent.getParcelableExtra(LocationConstants.RECEIVER);
        String errorMessage = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        // Get the location passed to this service through an extra.
        Location location = intent.getParcelableExtra(
                LocationConstants.LOCATION_DATA_EXTRA);

        // ...

        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    // In this sample, get just a single address.
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            errorMessage = getString(R.string.fetch_address_intent_service_service_not_found);
            Log.e(TAG, errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = getString(R.string.fetch_address_intent_service_invalid_position);
            Log.e(TAG, errorMessage + ". " +
                    "Latitude = " + location.getLatitude() +
                    ", Longitude = " +
                    location.getLongitude(), illegalArgumentException);
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size()  == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = getString(R.string.fetch_address_intent_service_not_found);
                Log.e(TAG, errorMessage);
            }
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        } else {
            Address address = addresses.get(0);

            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            for(int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(new AddressObject(
                        address.getAddressLine(i),
                        address.getFeatureName() +" " + address.getThoroughfare(),
                        address.getLatitude(),
                        address.getLongitude()));
            }
            Log.i(TAG, getString(R.string.fetch_address_intent_service_found));
            deliverResultToReceiver(LocationConstants.SUCCESS_RESULT,
                   addressFragments);
        }
    }
}