package com.welcome.exoplayerwithservicenotification;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class PlayerActivity extends AppCompatActivity {

    PlayerView playView;
    SimpleExoPlayer  exoPlayer;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    String notificationBody;
    BroadCast broadCast;
    TextView notifytextView;
    class BroadCast extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            notificationBody=intent.getExtras().getString("message");
            Log.d("notify",""+notificationBody);
            notifytextView.setText(notificationBody);
            //Toast.makeText(this,notificationBody,Toast.LENGTH_LONG).show();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        playView=findViewById(R.id.play_view);

        getFirebaseToken();
        notifytextView=findViewById(R.id.notifyText);
        broadCast=new BroadCast();
    }

    private void getFirebaseToken() {
        String token=FirebaseInstanceId.getInstance().getToken();
        Log.d("token",""+token);
       
    }

    /**
     * TO GET THE VIDEO LINKS GO TO PLAYLISTS SECTION
     * https://github.com/google/ExoPlayer/blob/release-v2/demos/main/src/main/assets/media.exolist.json
     *
     * SAMPLE URLS
     * https://html5demos.com/assets/dizzy.mp4
     *         //http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4
     *         https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4
     *
     *  SAMPLE PROJECT
     *         https://github.com/yusufcakmak/ExoPlayerSample
     */
    private void initializePlayer() {

        exoPlayer=new SimpleExoPlayer.Builder(this).build();
        playView.setPlayer(exoPlayer);
        List<MediaSource> m=new ArrayList<>();
        DefaultDataSourceFactory  dataSourceFactory = new DefaultDataSourceFactory(this, "sample");
        MediaSource mediaSource= new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse("https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4"));
        for(int i=0; i<=5; i++)
        {
            m.add(i,mediaSource);
            Log.d("sizelist1",""+m.get(i));
        }
        //exoPlayer.setMediaSource(mediaSource);//to play single content
        exoPlayer.setMediaSources(m);
        Log.d("sizelist",""+m.size());
        exoPlayer.setPlayWhenReady(playWhenReady);
        exoPlayer.seekTo(currentWindow, playbackPosition);
        exoPlayer.prepare();
        exoPlayer.play();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(exoPlayer==null)
            initializePlayer();
    }

    private void initializePlayer1() {
        exoPlayer=new SimpleExoPlayer.Builder(this).build();
        playView.setPlayer(exoPlayer);

        MediaItem mediaItem=MediaItem.fromUri(Uri.parse("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"));
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.setPlayWhenReady(playWhenReady);
        exoPlayer.seekTo(currentWindow, playbackPosition);
        exoPlayer.prepare();
        exoPlayer.play();

    }


    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadCast, new IntentFilter("MyData"));
        if(exoPlayer==null)
            initializePlayer();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(exoPlayer==null)
            initializePlayer();

    }

    @Override
    protected void onPause() {
        super.onPause();
            releasePlayer();
    }

    private void releasePlayer() {
        if (exoPlayer != null) {
            playWhenReady = exoPlayer.getPlayWhenReady();
            playbackPosition = exoPlayer.getCurrentPosition();
            currentWindow = exoPlayer.getCurrentWindowIndex();
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadCast);
            releasePlayer();

    }
}