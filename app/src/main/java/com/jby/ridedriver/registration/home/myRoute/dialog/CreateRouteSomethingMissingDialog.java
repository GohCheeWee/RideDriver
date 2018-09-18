package com.jby.ridedriver.registration.home.myRoute.dialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.jby.ridedriver.R;

public class CreateRouteSomethingMissingDialog extends DialogFragment {
    View rootView;
    public Button somethingMissingbutton;

    public CreateRouteSomethingMissingDialog() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.create_route_dialog_something_missing_dialog, container);
        objectInitialize();
        return rootView;
    }

    private void objectInitialize() {

        somethingMissingbutton = (Button)rootView.findViewById(R.id.create_route_dialog_something_missing_button);

        somethingMissingbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);
            d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

}