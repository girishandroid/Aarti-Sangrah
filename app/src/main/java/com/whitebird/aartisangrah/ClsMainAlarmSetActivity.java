package com.whitebird.aartisangrah;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class ClsMainAlarmSetActivity extends AppCompatActivity {

    DatePicker pickerDate;
    TimePicker pickerTime;
    Button buttonSetAlarm;
    private static Button buttonUnsetAlarm;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    static PendingIntent pendingIntent2;
    TextView info;
    TextView updateText;
    Context context;
    Intent My_intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cls_main_alarm_set);

        //Set the Diameter For Popup Window
        DisplayMetrics displayMetrics= new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((int)(width*.9),(int)(height*.9));



        alarmManager=(AlarmManager) getSystemService(ALARM_SERVICE);
        info = (TextView)findViewById(R.id.info);
        pickerDate = (DatePicker)findViewById(R.id.pickerdate);
        pickerTime = (TimePicker)findViewById(R.id.pickertime);
        this.context = this;
        final Intent My_intent2 = new Intent(this.context, ClsAlarmReceiver.class);

        My_intent2.putExtra("extra","alarm off");
        sendBroadcast(My_intent2);
        pendingIntent2 = PendingIntent.getBroadcast(ClsMainAlarmSetActivity.this,0,My_intent2,PendingIntent.FLAG_UPDATE_CURRENT);



        Calendar now = Calendar.getInstance();

        updateText = (TextView)findViewById(R.id.update_text_alarm);

        pickerDate.init(
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH),
                null);
        pickerTime.setCurrentHour(now.get(Calendar.HOUR_OF_DAY));
        pickerTime.setCurrentMinute(now.get(Calendar.MINUTE));


        buttonSetAlarm = (Button)findViewById(R.id.setalarm);
        buttonSetAlarm.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View arg0) {
                Calendar current = Calendar.getInstance();

                Calendar cal = Calendar.getInstance();
                cal.set(pickerDate.getYear(),
                        pickerDate.getMonth(),
                        pickerDate.getDayOfMonth(),
                        pickerTime.getCurrentHour(),
                        pickerTime.getCurrentMinute(),
                        00);

                if(cal.compareTo(current) <= 0){
                    //The set Date/Time already passed
                    Toast.makeText(getApplicationContext(),
                            "Invalid Date/Time",
                            Toast.LENGTH_LONG).show();
                }else{
                    setAlarm(cal);
                    Toast.makeText(getApplicationContext(),
                            "Set Date/Time",
                            Toast.LENGTH_LONG).show();
                }

            }});
        buttonUnsetAlarm = (Button)findViewById(R.id.unsetalarm);
        buttonUnsetAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarmManager.cancel(pendingIntent);

                Toast.makeText(getApplicationContext(),
                        "Alarm Canceled Date/Time",
                        Toast.LENGTH_LONG).show();

            }
        });

    }

    private void setAlarm(Calendar targetCal){

        info.setText("\n\n***\n"
                + "Alarm is set@ " + targetCal.getTime() + "\n"
                + "***\n");


        My_intent = new Intent(this.context, ClsAlarmReceiver.class);

        My_intent.putExtra("extra","alarm on");
        pendingIntent = PendingIntent.getBroadcast(ClsMainAlarmSetActivity.this,0,My_intent,PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);

    }
}

