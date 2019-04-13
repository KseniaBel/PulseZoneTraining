package com.ksenia.pulsezonetraining.ui;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ScaleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.ksenia.pulsezonetraining.R;
import com.ksenia.pulsezonetraining.connectivity.bluetooth.BluetoothCommunicationManager;

import java.util.ArrayList;

/**
 * Created by ksenia on 15.02.19.
 */

public class Activity_BluetoothDevices extends AppCompatActivity {
    public final static String DEVICE_TO_CONNECT_NR = "deviceToConnect";
    private ListView listView;
    private String deviceToConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_devices);
        Toolbar toolbar = findViewById(R.id.toolbar_bluetooth);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String[] results = intent.getStringArrayExtra(Activity_PulseZonesFitness.SCAN_RESULTS);

        listView = findViewById(R.id.list_of_devices);
        listView.setDivider(getDrawable(R.drawable.divider));
        listView.setDividerHeight(2);
        ArrayAdapter<String> listAdapter = new ArrayAdapter<>(this, R.layout.devices_list_item, R.id.device_name, results);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            deviceToConnect = results[position];
            Intent backIntent = new Intent();
            backIntent.putExtra(DEVICE_TO_CONNECT_NR, deviceToConnect);
            setResult(RESULT_OK, backIntent);
            finish();
        });
    }

}
