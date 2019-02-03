package com.ksenia.pulsezonetraining;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by ksenia on 31.01.19.
 */

public class ActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context,"recieved",Toast.LENGTH_SHORT).show();

        //String action = intent.getStringExtra("pause");
        //if(action.equals("pauseWorkout")|| action.equals("resumeWorkout")) {

        //fitness.PAUSE_RESUME_ACTION.onClick(null);

        //This is used to close the notification tray
        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(it);
    }

    public void performPauseResume() {

    }


}