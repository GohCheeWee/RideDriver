package com.jby.ridedriver.registration.home.myRoute.matchedRiderDetail.dialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jby.ridedriver.R;

public class AcceptDialog extends DialogFragment {
    View rootView;
    public Button acceptDialogConfirmButton;
    private TextView acceptDialogCancelButton;
    AcceptDialogCallBack acceptDialogCallBack;

    public AcceptDialog() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_match_rider_detail_accept_dialog, container);
        objectInitialize();
        acceptDialogCallBack = (AcceptDialogCallBack) getActivity();
        return rootView;
    }

    private void objectInitialize() {

        acceptDialogCancelButton = (TextView) rootView.findViewById(R.id.activity_match_rider_detail_accept_dialog_cancel_button);
        acceptDialogConfirmButton = (Button)rootView.findViewById(R.id.activity_match_rider_detail_accept_dialog_comfirm_button);

        acceptDialogConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acceptDialogCallBack.acceptRiderAction();
                dismiss();
            }
        });

        acceptDialogCancelButton.setOnClickListener(new View.OnClickListener() {
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
            d.getWindow().setWindowAnimations(
                    R.style.dialog_fade_in_out);
        }
    }

    public interface AcceptDialogCallBack {
        void acceptRiderAction();
    }

}