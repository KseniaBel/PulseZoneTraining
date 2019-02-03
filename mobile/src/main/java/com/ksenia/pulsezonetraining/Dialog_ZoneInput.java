package com.ksenia.pulsezonetraining;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;

import com.ksenia.pulsezonetraining.utils.PulseZoneSettings;

/**
 * Created by ksenia on 24.01.19.
 */

public class Dialog_ZoneInput extends DialogFragment {
    private RadioGroup radioGroupZone;
    private PulseZoneSettings pulseSettings;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setPositiveButton("OK", (DialogInterface dialog, int which) -> {
                    pulseSettings = new PulseZoneSettings(getContext());
                    pulseSettings.setZoneRadioButtonId(radioGroupZone.getCheckedRadioButtonId());
                    pulseSettings.save();
                    Intent intent = new Intent(Dialog_ZoneInput.this.getActivity(), Activity_PulseZonesFitness.class);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    //Let dialog dismiss
                });

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View detailsView = inflater.inflate(R.layout.dialog_zone_settings, null);
        builder.setView(detailsView);

        radioGroupZone = detailsView.findViewById(R.id.radioGroupZones);

        //Get shared preferences
        pulseSettings = new PulseZoneSettings(detailsView.getContext());
        radioGroupZone.check(pulseSettings.getZoneRadioButtonId());

        return builder.create();
    }



}
