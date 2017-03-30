package com.whitebird.aartisangrah.MusicPlayer;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;


/**
 * Created by girish on 8/3/17.
 */

public class GetMediaPlayer {
    private MediaPlayer mediaPlayer;
    private Uri uri;
    Activity activity;

    public GetMediaPlayer(Activity activity){
        this.activity = activity;
        mediaPlayer = new MediaPlayer();
    }

    public void GetTrackUri(Uri uri){
        this.uri = uri;
        mediaPlayer = MediaPlayer.create(activity,uri);
    }

    public void PlayTrack(){
        if (mediaPlayer!=null) {
            mediaPlayer.start();
        }
    }

    public void PauseTrack(){
        if (mediaPlayer!=null) {
            mediaPlayer.pause();
        }
    }
    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }


}
