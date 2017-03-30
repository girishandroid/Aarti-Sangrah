package com.whitebird.aartisangrah;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.whitebird.aartisangrah.MusicPlayer.GetMediaPlayer;

public class PopToPlayAllSongs extends AppCompatActivity implements AudioManager.OnAudioFocusChangeListener,MediaPlayer.OnCompletionListener {

    private ImageButton buttonPlay,buttonNext,buttonPrev;
    private AudioManager audioManager;
    GetMediaPlayer getMediaPlayer;
    public Integer currentTrack;
    Runnable runnable;
    Handler handler;

    private Uri[] playAllSongs= {
            Uri.parse("android.resource://com.whitebird.aartisangrah/" + R.raw.shree_hanuman_chalisa),
            Uri.parse("android.resource://com.whitebird.aartisangrah/" + R.raw.sukhakarta_dukhaharta_aarti),
            Uri.parse("android.resource://com.whitebird.aartisangrah/" + R.raw.gajanan_maharaj_aarti),
            Uri.parse("android.resource://com.whitebird.aartisangrah/" + R.raw.durga_maa_aarti),
            Uri.parse("android.resource://com.whitebird.aartisangrah/" + R.raw.mahalakshami_aarti),
            Uri.parse("android.resource://com.whitebird.aartisangrah/" + R.raw.maha_dev_omkar_song),
            Uri.parse("android.resource://com.whitebird.aartisangrah/" + R.raw.datta_song),
            Uri.parse("android.resource://com.whitebird.aartisangrah/" + R.raw.vithal_vithal)
    };
    private int[] images = {
            R.drawable.hanuman,
            R.drawable.ganpati_bappa,
            R.drawable.gajanan_maharaj,
            R.drawable.durga_devi,
            R.drawable.mahalakshami_mata,
            R.drawable.mahadev,
            R.drawable.datta,
            R.drawable.vitthal
    };

    private SharedPreferences sharedTrack;
    public static final String TRACK = "track";
    public static final String TRACKNO="trackno";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_to_play_all_songs);
        setTitle("");
        //Set the Diameter For Popup Window
        DisplayMetrics displayMetrics= new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        getWindow().setLayout((int)(width*.9),(int)(height*.6));

        //popupMedia = new MediaPlayer();
        sharedTrack = this.getSharedPreferences(TRACK,MODE_PRIVATE);
        currentTrack = sharedTrack.getInt(TRACKNO, 0);


        getWindow().setBackgroundDrawableResource(images[currentTrack]);






        //Set All Buttons On PopUp
        buttonPlay = (ImageButton)findViewById(R.id.popup_play_btn);
        buttonNext = (ImageButton)findViewById(R.id.popup_next_button);
        buttonPrev = (ImageButton)findViewById(R.id.popup_prev_button);
        //popupMedia = new MediaPlayer();
        audioManager  = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
        audioManager.requestAudioFocus(PopToPlayAllSongs.this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
        getMediaPlayer = new GetMediaPlayer(this);
        handler = new Handler();

        if (getMediaPlayer.getMediaPlayer()!=null){
            getMediaPlayer.getMediaPlayer().reset();
        }

        getMediaPlayer.GetTrackUri(playAllSongs[currentTrack]);



        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getMediaPlayer.getMediaPlayer()!=null&&getMediaPlayer.getMediaPlayer().isPlaying()){
                    StopTrack();
                    buttonPlay.setImageResource(R.drawable.playios);
                }else {
                    PlayTract(currentTrack);
                    buttonPlay.setImageResource(R.drawable.pauseios);
                    getWindow().setBackgroundDrawableResource(images[currentTrack]);
                }
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (currentTrack <7){
                   currentTrack +=1;
               }else {
                   currentTrack =0;
               }
                if (getMediaPlayer.getMediaPlayer()!=null){
                    getMediaPlayer.getMediaPlayer().reset();
                }

                getMediaPlayer.GetTrackUri(playAllSongs[currentTrack]);
                PlayTract(currentTrack);
                getWindow().setBackgroundDrawableResource(images[currentTrack]);
                SharedPreferences.Editor editor = sharedTrack.edit();
                editor.putInt(TRACKNO, currentTrack);
                editor.commit();
            }
        });
        buttonPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentTrack >0){
                    currentTrack -=1;
                }else {
                    currentTrack =7;
                }
                if (getMediaPlayer.getMediaPlayer()!=null){
                    getMediaPlayer.getMediaPlayer().reset();
                }

                getMediaPlayer.GetTrackUri(playAllSongs[currentTrack]);
                PlayTract(currentTrack);
                getWindow().setBackgroundDrawableResource(images[currentTrack]);
                SharedPreferences.Editor editor = sharedTrack.edit();
                editor.putInt(TRACKNO, currentTrack);
                editor.commit();
            }
        });


    }



    private void PlayTract(int currentTrack) {
        /*if (getMediaPlayer.getMediaPlayer()!=null){
            getMediaPlayer.getMediaPlayer().reset();
        }

        getMediaPlayer.GetTrackUri(playAllSongs[currentTrack]);*/

        getMediaPlayer.PlayTrack();
        PlayCycle();
        buttonPlay.setImageResource(R.drawable.pauseios);
        if (getMediaPlayer.getMediaPlayer()!= null&&getMediaPlayer.getMediaPlayer().isPlaying()) {
            audioManager.requestAudioFocus(PopToPlayAllSongs.this, AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN);
        }

    }

    public void PlayCycle(){
        getMediaPlayer.getMediaPlayer().setOnCompletionListener(this);
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        PlayCycle();

                    }
                };
                handler.postDelayed(runnable, 1000);
    }

    @Override
    public void onBackPressed() {
        if (getMediaPlayer.getMediaPlayer()!= null&&getMediaPlayer.getMediaPlayer().isPlaying()) {
            buttonPlay.setImageResource(R.drawable.pauseios);
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        if (getMediaPlayer.getMediaPlayer()!= null&&getMediaPlayer.getMediaPlayer().isPlaying()) {
            buttonPlay.setImageResource(R.drawable.pauseios);
        }
        super.onPause();
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        if (getMediaPlayer.getMediaPlayer()!=null&&getMediaPlayer.getMediaPlayer().isPlaying()) {
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

        if (getMediaPlayer.getMediaPlayer().isPlaying()){
            getMediaPlayer.PauseTrack();
            buttonPlay.setImageResource(R.drawable.playios);
        }
    }

    public void resumePlayer() {
        if (getMediaPlayer.getMediaPlayer()!=null){
            getMediaPlayer.PlayTrack();
            if (getMediaPlayer.getMediaPlayer().isPlaying()) {
                buttonPlay.setImageResource(R.drawable.pauseios);
            } else {
                buttonPlay.setImageResource(R.drawable.playios);
            }
        }
    }

    public void StopTrack() {
        if (getMediaPlayer.getMediaPlayer()!=null&&getMediaPlayer.getMediaPlayer().isPlaying()){
            getMediaPlayer.PauseTrack();
            buttonPlay.setImageResource(R.drawable.playios);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (currentTrack <7){
            currentTrack +=1;
        }else {
            currentTrack =0;
        }
        PlayTract(currentTrack);
        getWindow().setBackgroundDrawableResource(images[currentTrack]);
        SharedPreferences.Editor editor = sharedTrack.edit();
        editor.putInt(TRACKNO, currentTrack);
        editor.commit();
    }

}
