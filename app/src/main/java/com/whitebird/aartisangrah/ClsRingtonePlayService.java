package com.whitebird.aartisangrah;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by girish on 24/1/17.
 */

public class ClsRingtonePlayService extends Service {
    MediaPlayer mediaPlayer;
    boolean isRunning;
    private SharedPreferences preferences;
    public static final String GALLERY = "gallery";
    public static final String SONG="song";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        preferences = this.getSharedPreferences(GALLERY, MODE_PRIVATE);

        String position = preferences.getString(SONG, "");
        int positions = Integer.parseInt(position);
        positions--;

        Uri ringtoneSong =ClsFullFragmentAndPager.songListDefine[positions];

        String getSwitch = intent.getExtras().getString("extra");
        Log.e("Ringtone State Extra is", getSwitch);
        //To Switch Alarm On and Off

        assert getSwitch != null;

        switch (getSwitch) {
            case "alarm on":
                startId = 1;
                break;
            case "alarm off":
                startId = 0;
                break;
            default:
                startId = 0;
                break;
        }
        if(!this.isRunning&&startId==1){
            mediaPlayer = MediaPlayer.create(this,ringtoneSong);
            mediaPlayer.start();
            this.isRunning = true;
            //notification doing

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            //Set intent for an pop up screen from notifiation

            Intent notification_manager_intent = new Intent(this.getApplicationContext(),ClsMainAlarmSetActivity.class);

            //set the pending Intent

            PendingIntent notification_pending_intent = PendingIntent.getActivity(this,0,
                    notification_manager_intent,0);



            //Make notification parameters

            Notification notification_popup = new Notification.Builder(this)
                    .setContentIntent(notification_pending_intent)
                    .setContentTitle("An Alarm is going on!")
                    .setContentText("Click Me!")
                    .setSmallIcon(R.drawable.notification)
                    .setAutoCancel(true)
                    .addAction(R.drawable.stopnotifi,"Stop",ClsMainAlarmSetActivity.pendingIntent2)
                    .build();

            notification_popup.flags |= Notification.FLAG_AUTO_CANCEL;
            notification_popup.flags |= Notification.FLAG_SHOW_LIGHTS;
            notification_popup.defaults |= Notification.DEFAULT_SOUND;
            notification_popup.defaults |= Notification.DEFAULT_VIBRATE;
            notification_popup.defaults|= Notification.DEFAULT_LIGHTS;

            //setup notification start command
            notificationManager.notify(0,notification_popup);
        }
        //if there is music playing and user place alarm off
        //music should shop playing

        else  if(this.isRunning&&startId==0){
            mediaPlayer.stop();
            this.isRunning=false;

        }

        //these are if the user placed random button
        //just to bug proof the app
        //if there is no music playing and usr pressed alarm off
        //do nothing

        else if(!this.isRunning&&startId==0){

        }

        //if music is playing and user placed alarm on
        //do nothing

        else if(this.isRunning&&startId==1){

        }


        return START_NOT_STICKY;

    }
    @Override
    public void onDestroy() {
        this.isRunning=false;

        //Toast.makeText(this,"On Destroy Call",Toast.LENGTH_SHORT).show();
    }
}
