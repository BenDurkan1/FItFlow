package com.example.FitFlow.repository.UI.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import com.example.FitFlow.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class CancelTrackingDialog extends DialogFragment {

    private Runnable yesListener = null;

    public void setYesListener(Runnable listener) {
        yesListener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setTitle("Cancel the Run?")
                .setMessage("Are you sure to cancel the current run and delete all its data?")
                .setIcon(R.drawable.icons8_x_50)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (yesListener != null) {
                            yesListener.run();
                        }
                        // Dismiss the dialog after executing the yesListener
                        dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.cancel();
                    }
                });

        return builder.create();
    }
}
