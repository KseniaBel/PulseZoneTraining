package com.ksenia.pulsezonetraining;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.ksenia.pulsezonetraining.utils.PulseZoneUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageButton settingButton = findViewById(R.id.button_settings);
        Dialog_DataInput dialog = new Dialog_DataInput();
        settingButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                dialog.show(getSupportFragmentManager(), "Data input settings");
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dialog.isFormValid()) {
                    Dialog_ZoneInput dialog_zoneInput = new Dialog_ZoneInput();
                    dialog_zoneInput.show(getSupportFragmentManager(), "Pulse zone settings");
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.input_settings_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
