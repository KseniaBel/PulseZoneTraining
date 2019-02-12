package com.ksenia.pulsezonetraining.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.ksenia.pulsezonetraining.R;
import com.ksenia.pulsezonetraining.preferences.InvalidPreferenceException;
import com.ksenia.pulsezonetraining.preferences.PulseZoneSettings;
import com.ksenia.pulsezonetraining.history.ExpandableListAdapter;

public class MainActivity extends AppCompatActivity {
    private ExpandableListView listView;
    private ExpandableListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ImageButton settingButton = findViewById(R.id.button_settings);

        settingButton.setOnClickListener(view -> {
                    Dialog_DataInput dialog = new Dialog_DataInput();
                    dialog.show(getSupportFragmentManager(), "Data input settings");
            }
        );

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            try {
                PulseZoneSettings pulseZoneSettings =  new PulseZoneSettings();
                pulseZoneSettings.validate(getApplicationContext());
                Dialog_ZoneInput dialog_zoneInput = new Dialog_ZoneInput();
                dialog_zoneInput.show(getSupportFragmentManager(), "Pulse zone settings");
            } catch (InvalidPreferenceException e) {
                Toast.makeText(getApplicationContext(), getString(R.string.input_settings_error), Toast.LENGTH_LONG).show();
                settingButton.callOnClick();
            }

        });

        populateWorkoutHistory();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        populateWorkoutHistory();
    }

    private void populateWorkoutHistory() {
        listView = findViewById(R.id.expandable_list_view);
        listAdapter = new ExpandableListAdapter(this);
        listView.setAdapter(listAdapter);
    }




}
