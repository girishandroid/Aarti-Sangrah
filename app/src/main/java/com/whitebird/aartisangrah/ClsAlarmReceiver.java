package com.whitebird.aartisangrah;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by girish on 24/1/17.
 */

public class ClsAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("We are in the Receiver","Yay");
        Toast.makeText(context, "Alarm!", Toast.LENGTH_LONG).show();
        String get_your_string = intent.getExtras().getString("extra");
        Log.e("What is your key",get_your_string);


        Intent service_intent = new Intent(context,ClsRingtonePlayService.class);
        service_intent.putExtra("extra",get_your_string);
        context.startService(service_intent);

    }

}
