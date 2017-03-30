package com.whitebird.aartisangrah;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.media.TimedText;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static android.Manifest.permission.WRITE_SETTINGS;
import static com.whitebird.aartisangrah.ClsFullFragmentAndPager.mediaPlayer;

@SuppressWarnings("ResourceAsColor")
public class ClsFullFragmentAndPager extends AppCompatActivity implements MediaPlayer.OnCompletionListener,MediaPlayer.OnErrorListener,SeekBar.OnSeekBarChangeListener,AudioManager.OnAudioFocusChangeListener {
    ViewPager viewPager;
    MyPagerAdapter myPagerAdapter;
    private SharedPreferences preferences;
    public static final String GALLERY = "gallery";
    public static final String SONG="song";

    String selectedLyrics;
    ClsDevotionalGalleryMainGridView clsDevotionalGalleryMainGridView;

    //For All Media Player
    static MediaPlayer mediaPlayer;
    AudioManager manager;
    public ImageButton btnPlay;
    public ImageButton btnStop;
    public ImageButton btnShare;
    public SeekBar songProgressBar;
    private Handler handler;
    private Runnable runnable;
    private Utilities utils;
    private Handler mHandler = new Handler();
    static String Clicked;
    InterstitialAd musicScreenAd;
    ArrayList<ClsModelDataStoreRoom> gods;

    static Uri[] songListDefine= {
            Uri.parse("android.resource://com.whitebird.aartisangrah/" + R.raw.shree_hanuman_chalisa),
            Uri.parse("android.resource://com.whitebird.aartisangrah/" + R.raw.sukhakarta_dukhaharta_aarti),
            Uri.parse("android.resource://com.whitebird.aartisangrah/" + R.raw.gajanan_maharaj_aarti),
            Uri.parse("android.resource://com.whitebird.aartisangrah/" + R.raw.durga_maa_aarti),
            Uri.parse("android.resource://com.whitebird.aartisangrah/" + R.raw.mahalakshami_aarti),
            Uri.parse("android.resource://com.whitebird.aartisangrah/" + R.raw.maha_dev_omkar_song),
            Uri.parse("android.resource://com.whitebird.aartisangrah/" + R.raw.datta_song),
            Uri.parse("android.resource://com.whitebird.aartisangrah/" + R.raw.vithal_vithal)
    };

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cls_full_fragment_and_pager);
        checkCallingPermission(WRITE_SETTINGS);
        settingPermission();


        musicScreenAd = new InterstitialAd(this);
        musicScreenAd.setAdUnitId(getString(R.string.interstitial_full_screen));



        final TextView textViewHanumanStartTime = (TextView) findViewById(R.id.textViewStartTime);
        final TextView textViewHanumanEndTime = (TextView) findViewById(R.id.textViewEndTime);

        btnPlay = (ImageButton)findViewById(R.id.btnPlay);
        btnStop = (ImageButton)findViewById(R.id.btnStop);
        btnShare = (ImageButton)findViewById(R.id.btnShare);
        songProgressBar = (SeekBar)findViewById(R.id.songProgressBar);



        handler = new Handler();


        manager  = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);

        //Set Ringtone Permission



        //Songs Content

      /*  ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.BLACK)); // set your desired color
        actionBar.show();
        actionBar.setTitle("");*/



        //get position using extras
        Bundle bundle = getIntent().getExtras();
        final int position = bundle.getInt("position");

        //Shared For Song Detection
        preferences = this.getSharedPreferences(GALLERY,MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SONG, String.valueOf(position));
        editor.commit();

        selectedLyrics = ClsDevotionalGalleryMainGridView.lyricsOfGod[position];




        viewPager = (ViewPager)findViewById(R.id.viewPager);




        myPagerAdapter = new MyPagerAdapter(this, ClsDevotionalGalleryMainGridView.getGodsListComponent());
        viewPager.setAdapter(myPagerAdapter);
        //It Gives Position From Back Selected Page To a pager
        viewPager.setCurrentItem(position);
        selectedLyrics = ClsDevotionalGalleryMainGridView.lyricsOfGod[position];



        int positionfortitle = viewPager.getCurrentItem();
        final String nameTitle = clsDevotionalGalleryMainGridView.getGodsListComponent().get(positionfortitle).getListOfStringComponent();



        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
            // Do something for lollipop and above versions
            setTitle(Html.fromHtml("<font color='#ff8c00'>"+nameTitle+"</font>"));
        } else{
            ActionBar actionBar = getSupportActionBar();
            //actionBar.setTitle(nameTitle);
            setTitle(Html.fromHtml("<font color='#000000'>"+nameTitle+"</font>"));
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff8c00"))); // set your desired color
            actionBar.show();
            // do something for phones running an SDK before lollipop
        }





        new BackgroundAudioService(getApplicationContext(), songListDefine[position], manager);





        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                btnPlay.setImageResource(R.drawable.playios);
                selectedLyrics = ClsDevotionalGalleryMainGridView.lyricsOfGod[position];
                SharedPreferences.Editor editor=preferences.edit();
                editor.putString(SONG, String.valueOf(position));
                editor.commit();
                int positionfortitle = viewPager.getCurrentItem();
                final String nameTitle = clsDevotionalGalleryMainGridView.getGodsListComponent().get(positionfortitle).getListOfStringComponent();

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
                    // Do something for lollipop and above versions
                    setTitle(Html.fromHtml("<font color='#ff8c00'>"+nameTitle+"</font>"));
                } else{
                    setTitle(Html.fromHtml("<font color='#000000'>"+nameTitle+"</font>"));
                    // do something for phones running an SDK before lollipop
                }


                //setTitleColor(Color.parseColor("#000000"));

                new BackgroundAudioService(getApplicationContext(), songListDefine[position], manager);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        songProgressBar.setOnSeekBarChangeListener(this); // Important

        mediaPlayer.setOnCompletionListener(this); // Important

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);





        if (mediaPlayer.isPlaying()) {

            btnPlay.setImageResource(R.drawable.pauseios);


        } else {

            btnPlay.setImageResource(R.drawable.playios);
        }


        mediaPlayer.setOnTimedTextListener(new MediaPlayer.OnTimedTextListener() {
            @Override
            public void onTimedText(MediaPlayer mp, TimedText text) {

            }
        });



        //Taken the exact lyrics position
        final String lyrics = ClsDevotionalGalleryMainGridView.lyricsOfGod[position];


        //mediaPlayer.start();
        //btnPlay.setImageResource(R.drawable.pauseios);

        if (mediaPlayer!= null) {
            manager.requestAudioFocus(ClsFullFragmentAndPager.this, AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN);
        }

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onClick(View v) {
                Clicked = "on";
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    PlayCycle();
                    btnPlay.setImageResource(R.drawable.playios);
                } else {
                    if (mediaPlayer!=null) {
                        mediaPlayer.start();
                        if (mediaPlayer!= null&&mediaPlayer.isPlaying()) {
                            manager.requestAudioFocus(ClsFullFragmentAndPager.this, AudioManager.STREAM_MUSIC,
                                    AudioManager.AUDIOFOCUS_GAIN);
                        }
                        songProgressBar.setMax(mediaPlayer.getDuration());
                        final long totaltime = mediaPlayer.getDuration();
                        textViewHanumanEndTime.setText(String.format("%d:%02d",
                                TimeUnit.MILLISECONDS.toMinutes((long) totaltime),
                                TimeUnit.MILLISECONDS.toSeconds((long) totaltime) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                                totaltime)))
                        );
                        textViewHanumanEndTime.setTextColor(getResources().getColor(R.color.colorWhite));
                        PlayCycle();
                        btnPlay.setImageResource(R.drawable.pauseios);
                    }
                }

            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    mediaPlayer.seekTo(0);
                    PlayCycle();
                    btnPlay.setImageResource(R.drawable.playios);
                }else {
                    if (mediaPlayer!=null)
                    {
                        mediaPlayer.pause();
                        mediaPlayer.seekTo(0);
                    }

                    PlayCycle();
                    btnPlay.setImageResource(R.drawable.playios);

                }
            }
        });



        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //preferences = ClsFullFragmentAndPager.this.getSharedPreferences(GALLERY,MODE_PRIVATE);
                //String position = preferences.getString(SONG, "");
                btnPlay.setImageResource(R.drawable.playios);
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                String shareBody = selectedLyrics;
                String shareSub = "God Lyrics";
                sendIntent.putExtra(Intent.EXTRA_SUBJECT,shareSub);
                sendIntent.putExtra(Intent.EXTRA_TEXT,shareBody);
                startActivity(Intent.createChooser(sendIntent,"Share Using"));

            }

        });

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                //mediaPlayer = MediaPlayer.create(getActivity(), R.raw.shree_hanuman_chalisa);
                songProgressBar.setMax(mediaPlayer.getDuration());
                final long totaltime = mediaPlayer.getDuration();
                final long currenttime = mediaPlayer.getCurrentPosition();
                textViewHanumanEndTime.setText(String.format("%d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes((long) totaltime),
                        TimeUnit.MILLISECONDS.toSeconds((long) totaltime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                        totaltime)))
                );
                textViewHanumanEndTime.setTextColor(getResources().getColor(R.color.colorWhite));
                textViewHanumanStartTime.setText(String.format("%d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes((long) currenttime),
                        TimeUnit.MILLISECONDS.toSeconds((long) currenttime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                        currenttime)))
                );
                textViewHanumanStartTime.setTextColor(getResources().getColor(R.color.colorWhite));


                PlayCycle();
                //mediaPlayer.start();


            }
        });

        songProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    if (mediaPlayer!=null)
                    {
                        mediaPlayer.seekTo(progress);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //mHandler.removeCallbacks(mUpdateTimeTask);

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                /*
                mHandler.removeCallbacks(mUpdateTimeTask);
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);
                // forward or backward to certain seconds
                mediaPlayer.seekTo(currentPosition);
                // update timer progress again
                updateProgressBar();*/
            }
        });


    }
    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    public Runnable mUpdateTimeTask = new Runnable() {
        @SuppressLint("SetTextI18n")
        public void run() {
            long totalDuration = mediaPlayer.getDuration();

            long currentDuration = mediaPlayer.getCurrentPosition();


            // Displaying Total Duration time
            //textViewStartTime.setText("%d min,%d sec"+utils.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            //textViewEndTime.setText("%d min,%d sec"+utils.milliSecondsToTimer(currentDuration));


            // Updating progress bar
            int progress = (int) (utils.getProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            songProgressBar.setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
            //textViewStartTime.setText((int) currentDuration);
            //Log.d("Song State Extra is", String.valueOf(textViewStartTime));
        }
    };

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }


    public class Utilities {

        public String milliSecondsToTimer(long milliseconds) {
            String finalTimerString = "";
            String secondsString = "";

            // Convert total duration into time
            int hours = (int) (milliseconds / (1000 * 60 * 60));
            int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
            int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
            // Add hours if there
            if (hours > 0) {
                finalTimerString = hours + ":";
            }

            // Prepending 0 to seconds if it is one digit
            if (seconds < 10) {
                secondsString = "0" + seconds;
            } else {
                secondsString = "" + seconds;
            }

            finalTimerString = finalTimerString + minutes + ":" + secondsString;

            // return timer string
            return finalTimerString;
        }


        int getProgressPercentage(long currentDuration, long totalDuration) {
            Double percentage = (double) 0;

            long currentSeconds = (int) (currentDuration / 1000);
            long totalSeconds = (int) (totalDuration / 1000);

            // calculating percentage
            percentage = (((double) currentSeconds) / totalSeconds) * 100;

            // return percentage
            return percentage.intValue();
        }


        int progressToTimer(int progress, int totalDuration) {
            int currentDuration = 0;
            totalDuration = (int) (totalDuration / 1000);
            currentDuration = (int) ((((double) progress) / 100) * totalDuration);

            // return current duration in milliseconds
            return currentDuration * 1000;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (mediaPlayer!=null) {
            mHandler.removeCallbacks(mUpdateTimeTask);
            int totalDuration = mediaPlayer.getDuration();
            int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);


            // forward or backward to certain seconds
            mediaPlayer.seekTo(currentPosition);

            // update timer progress again
            updateProgressBar();
        }

    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer!=null) {
            if (mediaPlayer.isPlaying()) {


                if (mediaPlayer!= null&&mediaPlayer.isPlaying()) {
                    manager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                            AudioManager.AUDIOFOCUS_GAIN);
                }
                mediaPlayer.setLooping(true); // Set looping
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AdRequest adRequestfullmusic = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("1720EC3DF18CC1184849510529D6A998")
                .build();
        musicScreenAd.loadAd(adRequestfullmusic);
        musicScreenAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                showInterstitial();
            }
        });
    }

    private void showInterstitial() {
        if (musicScreenAd.isLoaded()) {
            musicScreenAd.show();
        }
    }
    @Override
    public void onCompletion(MediaPlayer mp) {
        btnPlay.setImageResource(R.drawable.playios);
        mediaPlayer.seekTo(0);
    }

    public void PlayCycle(){
        //mediaPlayer = MediaPlayer.create(getActivity(), R.raw.shree_hanuman_chalisa);
        if (mediaPlayer!=null){
            songProgressBar.setProgress(mediaPlayer.getCurrentPosition());
            if(mediaPlayer.isPlaying()) {
                runnable = new Runnable() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void run() {
                        PlayCycle();
                        //mediaPlayer = MediaPlayer.create(getActivity(), R.raw.shree_hanuman_chalisa);
                        long currenttime = mediaPlayer.getCurrentPosition();
                        final TextView textViewHanumanStartTime = (TextView) ClsFullFragmentAndPager.this.findViewById(R.id.textViewStartTime);
                        textViewHanumanStartTime.setText(String.format("%d:%02d",
                                TimeUnit.MILLISECONDS.toMinutes((long) currenttime),
                                TimeUnit.MILLISECONDS.toSeconds((long) currenttime) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                                currenttime)))
                        );
                        //textViewStartTime.setTextColor(getResources().getColor(R.color.colorWhite));

                    }
                };
                handler.postDelayed(runnable, 1000);

                //textViewStartTime.setText(mediaPlayer.getCurrentPosition());

            }
        }
    }




    public class MyPagerAdapter extends PagerAdapter{


        Context c;
        ArrayList<ClsModelDataStoreRoom> gods;
        MyPagerAdapter(Context ctx, ArrayList<ClsModelDataStoreRoom> gods){
            this.c=ctx;
            this.gods=gods;
        }

        @Override
        public int getCount() {
            return gods.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return object==view;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View viewAll = inflater.inflate(R.layout.full_view_of_image_and_lyrics,container,false);
            //Set Image To Pager
            ImageView imageViewOfPager = (ImageView)viewAll.findViewById(R.id.image_view_of_pager);
            imageViewOfPager.setImageResource(gods.get(position).getImageOfComponents());
            //Set Text names To Image
            //Toolbar toolbarNameOfImage = (Toolbar)viewAll.findViewById(R.id.toolbar_of_compress_view);
            //toolbarNameOfImage.setTitle(gods.get(position).getListOfStringComponent());

            //Add Lyrics To View
            TextView textViewForLyrics = (TextView)viewAll.findViewById(R.id.lyrics_of_god);
            textViewForLyrics.setText(gods.get(position).getLyrics());
            container.addView(viewAll);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(SONG, String.valueOf(position));
            editor.commit();

            return viewAll;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

            container.removeView((RelativeLayout) object);
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_alarm:
                Intent intent = new Intent(this,ClsMainAlarmSetActivity.class);
                startActivity(intent);
                break;
            case R.id.set_ringtone:
                settingPermission();
                preferences = this.getSharedPreferences(GALLERY,MODE_PRIVATE);
                String position = preferences.getString(SONG, "");
                int positions= Integer.parseInt(position);
                final int finalposition= --positions;

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Set Ringtone");
                builder.setMessage("Are you sure?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        RingtoneManager.setActualDefaultRingtoneUri(
                                getApplicationContext(), RingtoneManager.TYPE_RINGTONE,
                                songListDefine[finalposition]);
                        //Log.i("TESTT", "Ringtone Set to Resource: "+ songListDefine[finalposition].toString());
                        RingtoneManager.getRingtone(getApplicationContext(), songListDefine[finalposition]);
                        Toast.makeText(getApplicationContext(),"Ringtone",Toast.LENGTH_LONG).show();

                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Do nothing
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
                break;
            case R.id.contact_us:
                Intent intentContactUs = new Intent(this,ClsContactUs.class);
                startActivity(intentContactUs);
                break;
            default:
                break;

        }/*
        if (id == R.id.action_alarm) {

            Intent intent = new Intent(this,ClsMainAlarmSetActivity.class);
            startActivity(intent);

            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }
    public void settingPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(getApplicationContext())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 200);

            }
        }


    }
    @Override
    public void onAudioFocusChange(int focusChange) {
        if (mediaPlayer!=null&&mediaPlayer.isPlaying()) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    resumePlayer(); // Resume your media player here
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    pausePlayer();// Pause your media player here
                    break;
            }
        }
    }

    public void pausePlayer() {

        if (mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            btnPlay.setImageResource(R.drawable.playios);
        }
    }

    public void resumePlayer() {
        if (mediaPlayer!=null){
            mediaPlayer.start();
            if (mediaPlayer.isPlaying()) {
                btnPlay.setImageResource(R.drawable.pauseios);
            } else {
                btnPlay.setImageResource(R.drawable.playios);
            }
        }
    }

    class BackgroundAudioService {
        //MediaPlayer mediaPlayer;
        Context context;
        Uri uri;
        AudioManager manager;



        BackgroundAudioService(Context context, Uri uri, AudioManager manager) {

            this.context = context;
            this.uri = uri;
            this.manager = manager;


            if(mediaPlayer != null) {

                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();

                }
            }

            if (mediaPlayer!=null) {
                mediaPlayer.reset();
                mediaPlayer.release();
            }
            mediaPlayer = MediaPlayer.create(context, uri);
        }
    }
}

