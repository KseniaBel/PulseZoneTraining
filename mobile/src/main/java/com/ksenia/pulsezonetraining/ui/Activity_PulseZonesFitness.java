package com.ksenia.pulsezonetraining.ui;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dsi.ant.plugins.antplus.pccbase.MultiDeviceSearch;
import com.ksenia.pulsezonetraining.connectivity.ConnectivityManager;
import com.ksenia.pulsezonetraining.connectivity.ConnectivityManagerFactory;
import com.ksenia.pulsezonetraining.connectivity.Event;
import com.ksenia.pulsezonetraining.ui.custom.CustomChronometer;
import com.ksenia.pulsezonetraining.R;
import com.ksenia.pulsezonetraining.database.FitnessSQLiteDBHelper;
import com.ksenia.pulsezonetraining.utils.PulseLimits;
import com.ksenia.pulsezonetraining.preferences.PulseZoneSettings;
import com.ksenia.pulsezonetraining.utils.PulseZoneUtils;
import com.ksenia.pulsezonetraining.database.HRRecordsRepository;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static android.app.Notification.VISIBILITY_PUBLIC;
import static com.ksenia.pulsezonetraining.ui.Activity_MultiDeviceSearchSampler.EXTRA_KEY_MULTIDEVICE_SEARCH_RESULT;

/**
 * Created by ksenia on 30.12.18.
 */

public class Activity_PulseZonesFitness extends AppCompatActivity implements Observer {
    public static final int REQUEST_CODE_BLUETOOTH_DEVICES = 2;
    public static final int REQUEST_CODE_ANT_DEVICES = 3;
    public static final int REQUEST_ENABLE_BT = 1;
    public static final String START_TIMING = "startTiming";
    public static final String WORKOUT_TIME = "workoutTime";
    public static final String WEIGHT = "weight";
    public static final String SCAN_RESULTS = "devicesArray";
    private static final String CHANNEL_ID = "beating_heart";
    private static final int NOTIFICATION_ID = 0;
    public static final String ZONE_BUTTON_ID = "zoneButtonId";


    private Logger logger = Logger.getLogger(this.getClass().getName());
    private TextView tv_status;
    private TextView tv_heartRate;
    private FloatingActionButton btn_pauseResume;
    private FloatingActionButton btn_stop;
    private List<Integer> readingsBuffer;

    private ScheduledExecutorService service;
    private LineChart graph;
    private HRRecordsRepository repository;
    private boolean isVibrationDisable;

    private PulseZoneSettings pulseSettings;
    private CustomChronometer chronometer;
    private PulseLimits pulseLimits;
    private ConnectivityManager connectivityManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_pulse_zone_fitness);
            pulseSettings = new PulseZoneSettings();
            pulseSettings.read(getApplicationContext());
            logger.info("Restore setting: " + pulseSettings.toString());
            pulseLimits = PulseZoneUtils.calculateZonePulse(pulseSettings);
            logger.info("low: " + pulseLimits.getLowPulseLimit() + " high: " + pulseLimits.getHighPulseLimit());
            readingsBuffer = Collections.synchronizedList(new ArrayList<>());

            Toolbar toolbar = findViewById(R.id.toolbar_workout);
            setSupportActionBar(toolbar);

            tv_status = findViewById(R.id.textView_ZoneStatus);
            tv_heartRate = findViewById(R.id.textView_HeartRatePulseZone);
            chronometer = findViewById(R.id.chronometer);
            tv_status.setText(R.string.connection_string);
            btn_pauseResume = findViewById(R.id.button_pause);
            btn_stop = findViewById(R.id.button_stop);
            btn_stop.setVisibility(View.GONE);
            graph = findViewById(R.id.graph);
            setupChart();
            setLegend();
            setupAxes();
            setupData();

            createNotificationChannel();

            FitnessSQLiteDBHelper helper = new FitnessSQLiteDBHelper(this);
            repository = new HRRecordsRepository(helper);
            //handleReset();

            btn_pauseResume.setOnClickListener(v -> {
                if(connectivityManager.isConnected() && chronometer.isRunning()) {
                    pause();
                } else if(connectivityManager.isConnected() && !chronometer.isRunning()) {
                    resume();
                }
            });

            btn_stop.setOnClickListener(view -> {
                Intent intent = new Intent(this, Activity_WorkoutStatistics.class);
                intent.putExtra(START_TIMING, chronometer.getStartTime());
                intent.putExtra(WORKOUT_TIME, chronometer.getText());
                intent.putExtra(WEIGHT, pulseSettings.getWeight());
                intent.putExtra(ZONE_BUTTON_ID, pulseSettings.getZoneRadioButtonId());
                startActivity(intent);
                finish();
            });

        connectivityManager = ConnectivityManagerFactory.getInstance(this, getApplicationContext(), pulseSettings.getConnectionRadioButtonId());
        connectivityManager.addObserver(this);
        connectivityManager.scanForDevices();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_training, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //noinspection SimplifiableIfStatement
        if (item.getTitle().equals("Turn OFF vibration")) {
            isVibrationDisable = true;
            item.setTitle("Turn ON vibration");
        } else {
            isVibrationDisable = false;
            item.setTitle("Turn OFF vibration");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_CANCELED) {
                //Bluetooth not enabled.
               finish();
                return;
            } else if(resultCode == Activity.RESULT_OK) {
                connectivityManager.scanForDevices();
            }
            //result from scanner activity
        } else if (requestCode == REQUEST_CODE_BLUETOOTH_DEVICES) {
            if(resultCode == RESULT_OK) {
                String deviceToConnect = data.getStringExtra(Activity_BluetoothDevices.DEVICE_TO_CONNECT_NR);
                connectivityManager.connect(deviceToConnect);
            } else {
                finish();
                return;
            }
        } else if(requestCode == REQUEST_CODE_ANT_DEVICES) {
            if(resultCode == RESULT_OK) {
                MultiDeviceSearch.MultiDeviceSearchResult result = data.getParcelableExtra(EXTRA_KEY_MULTIDEVICE_SEARCH_RESULT);
                connectivityManager.connect(String.valueOf(result.getAntDeviceNumber()));
            } else {
                finish();
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.name_channel);
            String description = getString(R.string.description_channel);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    //Creates and displays graph
    private void setupChart() {
        // disable description text
        graph.getDescription().setEnabled(false);
        // enable touch gestures
        graph.setTouchEnabled(true);
        // if disabled, scaling can be done on x- and y-axis separately
        graph.setPinchZoom(true);
        // enable scaling
        graph.setScaleEnabled(true);
        graph.setDrawGridBackground(false);
        // set an alternative background color
        graph.setBackgroundColor(Color.DKGRAY);
    }

    private void setupAxes() {
        XAxis xl = graph.getXAxis();
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(false);

        YAxis leftAxis = graph.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMaximum(PulseZoneUtils.calculateUpperRangeLimit(pulseLimits.getHighPulseLimit()));
        leftAxis.setAxisMinimum(PulseZoneUtils.calculateLowerRangeLimit(pulseLimits.getLowPulseLimit()));
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = graph.getAxisRight();
        rightAxis.setEnabled(false);

        // Add a limit line
        LimitLine upperLimitLine = getLimitLine(pulseLimits.getHighPulseLimit(), "Upper limit");
        LimitLine lowerLimitLine = getLimitLine(pulseLimits.getLowPulseLimit(), "Lower limit");
        // reset all limit lines to avoid overlapping lines
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(upperLimitLine);
        leftAxis.addLimitLine(lowerLimitLine);
        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);
    }

    @NonNull
    private LimitLine getLimitLine(int limit, String label) {
        LimitLine ll = new LimitLine(limit, label);
        ll.setLineWidth(2f);
        ll.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll.setTextSize(10f);
        ll.setTextColor(Color.WHITE);
        return ll;
    }

    private void setupData() {
        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);
        // add empty data
        graph.setData(data);
    }

    private void setLegend() {
        // get the legend (only possible after setting data)
        Legend l = graph.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.CIRCLE);
        l.setTextColor(Color.WHITE);
    }

    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "Heart rate data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColors(ColorTemplate.VORDIPLOM_COLORS[0]);
        set.setCircleColor(Color.WHITE);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        return set;
    }

    /**
     * Adds an entry of heart rate to the graph
     * @param time - the time of the reading
     * @param hrValue - the heart rate value
     */
    private void addEntry(int time, int hrValue) {
        logger.info("Add entry: time: " + time + " heart Reat: " + hrValue);
        LineData data = graph.getData();

        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(0);

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }
            YAxis leftAxis = graph.getAxisLeft();

            //increase axis maximum heart rate by 5 in case heart rate value is bigger than current maximum or
            //Decrease axis minimum heart rate by 5 in case heart rate value is smaller than current minimum
            if(hrValue > leftAxis.getAxisMaximum()) {
                leftAxis.setAxisMaximum(hrValue + 5);
            } else if(hrValue < leftAxis.getAxisMinimum()) {
                leftAxis.setAxisMinimum(hrValue - 5);
            }
            leftAxis.setDrawGridLines(true);

            data.addEntry(new Entry(time, hrValue), 0);

            // let the chart know it's data has changed
            data.notifyDataChanged();
            graph.notifyDataSetChanged();

            // limit the number of visible entries
            graph.setVisibleXRangeMaximum(20);

            // move to the latest entry
            graph.moveViewToX(data.getEntryCount());
        }
    }

    /**
     * Computes the average of heart rate and runs the UI thread, that displays info and adds new record in DB
     */
    private void displayReading() {
        logger.entering(this.getClass().toString(), "displayReading");
        int computedHeartRate;
        int sum = 0;
        //calculate the average of the readings from shared readingBuffer in synchronized way
        synchronized (readingsBuffer) {
            if (readingsBuffer.isEmpty()) {
                return;
            }

            for(Integer i : readingsBuffer) {
                sum += i;
            }
            computedHeartRate = Math.round(sum/readingsBuffer.size());
            readingsBuffer.clear();
            readingsBuffer.notifyAll();
        }

        addEntry(chronometer.getElapsedTime()/1000, computedHeartRate);
        repository.addNewRecord(computedHeartRate);
        tv_heartRate.setText(String.valueOf(computedHeartRate));

        //Create notification
        CharSequence timer = chronometer.getText();
        presentNotification(computedHeartRate, timer);

        if(!isVibrationDisable) {
            //Set vibration
            if(computedHeartRate > pulseLimits.getHighPulseLimit() || computedHeartRate < pulseLimits.getLowPulseLimit()) {
                // Get instance of Vibrator from current Context
                Vibrator mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                // Vibrate for 75 milliseconds
                mVibrator.vibrate(75);
            }
        }
    }

    private void presentNotification(int heartRate, CharSequence timer) {
        Intent intent = new Intent(this, Activity_PulseZonesFitness.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark_normal_background)
                .setContentTitle("WorkoutHistoryItem")
                .setContentText(String.format("Heart rate: %d\nTime: %s", heartRate, timer))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVisibility(VISIBILITY_PUBLIC)
                .setCategory(Notification.CATEGORY_MESSAGE);
                //.setOngoing(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void update(Observable o, Object arg) {
        Event event = (Event) arg;
        if(event.getEventType().equals(Event.Type.STATUS)) {
            runOnUiThread(() ->
            {
                if (event.getMessage().equals("connected") && !chronometer.isRunning()) {
                    resume();
                } else if (event.getMessage().equals("disconnected") && chronometer.isRunning()) {
                    pause();
                }
            });
        } else if(event.getEventType().equals(Event.Type.READING)) {
            readingsBuffer.add((Integer) event.getMessage());
            readingsBuffer.notifyAll();
        }
    }

    public void pause() {
            List<Runnable> list = service.shutdownNow();
            chronometer.stop();
            btn_pauseResume.setImageResource(R.drawable.baseline_play_arrow_24);
            btn_stop.setVisibility(View.VISIBLE);
        }
    public void resume(){

            chronometer.start();
            service = Executors.newSingleThreadScheduledExecutor();
            service.scheduleAtFixedRate(() -> runOnUiThread(
                    () -> displayReading()
            ), 1, 1, TimeUnit.SECONDS);
            btn_pauseResume.setImageResource(R.drawable.baseline_pause_24);
            btn_stop.setVisibility(View.GONE);
    }


    /**
     * Actions on Destroying the activity
     */
    @Override
    protected void onDestroy() {
        if(service != null) {
            List<Runnable> list = service.shutdownNow();
            logger.fine("Scheduled events are skipped: " + list.size());
        }
        chronometer.stop();
        repository.closeDb();
        connectivityManager.disconnect();
        super.onDestroy();
    }
}

