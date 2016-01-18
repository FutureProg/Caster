package com.caster.caster_android;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.caster.caster_android.utils.Bin;
import com.caster.caster_android.utils.PodcastDownloader;

import java.io.IOException;

/**
 * Created by Nick on 2016-01-17.
 */
public class MediaPlayerService extends Service implements MediaPlayer.OnPreparedListener,
    MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener{

    public final static int ID = 1;

    public final static String PODCAST_ID = "PODCAST_ID";

    public final static String ACTION_PLAY = "ACTION_PLAY", ACTION_PAUSE = "ACTION_PAUSE",
        ACTION_REWIND = "ACTION_REWIND", ACTION_FAST_FORWARD = "ACTION_FAST_FORWARD",
        ACTION_NEXT = "ACTION_NEXT", ACTION_PREVIOUS = "ACTION_PREVIOUS",
        ACTION_STOP = "ACTION_STOP", ACTION_START = "ACTION_START";

    public static MediaPlayer mediaPlayer;
    //private MediaSessionManager manager;
    //private MediaSession mediaSession;
    //private MediaController mediaController;
    private Podcast podcast;
    WifiManager.WifiLock wifiLock;

    @Override
    public void onCreate() {
        super.onCreate();
        initMediaSessions();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    void handleIntent(Intent intent){
        if (intent == null || intent.getAction() == null){
            return;
        }
        String action = intent.getAction();
        if (action.equals(ACTION_START)){
            if(!intent.hasExtra(PODCAST_ID)){
                return;
            }
            int id = intent.getExtras().getInt(PODCAST_ID);
            if (podcast != null && podcast.getId() == id)return;
            podcast = Podcast.makeFromID(id);
            if (mediaPlayer != null){
                mediaPlayer.stop();
                mediaPlayer.reset();
            }
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnPreparedListener(this);
            String url = "";
            if (PodcastDownloader.getDownloader(getApplicationContext()).isDownloaded(podcast.getId())) {
                url = getFilesDir().getAbsolutePath() + "/podcast_" + podcast.getId() + "/audio_file";
            }else{
                url = MainActivity.site + "/php/audio_file.php?q=" + podcast.getId() + "$" + Bin.getPodcastToken();
            }
            try {
                mediaPlayer.setDataSource(url);
                mediaPlayer.prepare();
            }catch (IllegalStateException exc){
                exc.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (action.equals(ACTION_PLAY)) {
            mediaPlayer.start();
            buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE));
        }
        else if(action.equals(ACTION_PAUSE)) {
            mediaPlayer.pause();
            buildNotification(generateAction(android.R.drawable.ic_media_play,"Play",ACTION_PLAY));
        }
        else if(action.equals(ACTION_STOP)){
            mediaPlayer.stop();
            mediaPlayer.reset();
            Log.i("CASTER_MEDIA", "Stop");
            wifiLock.release();
            NotificationManager notificationManager = (NotificationManager)getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(ID);
            intent = new Intent(getApplicationContext(),MediaPlayerService.class);
            stopService(intent);
        }
    }

    void buildNotification(Notification.Action action){
        Notification.MediaStyle style = new Notification.MediaStyle();
        //style.setMediaSession(mediaSession.getSessionToken());
        Intent intent = new Intent(getApplicationContext(),MediaPlayerService.class);
        Intent cIntent = new Intent(getApplicationContext(),PodcastPlayer.class);
        intent.setAction(ACTION_STOP);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(),1,intent,0);
        PendingIntent cpendingIntent = PendingIntent.getActivity(getApplicationContext(),0,cIntent,0);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.drawable.ic_caster_logo)
                .setContentTitle(podcast.getTitle())
                .setContentText(podcast.getCreator().getUsername())
                .setDeleteIntent(pendingIntent)
                .setContentIntent(cpendingIntent)
                .setStyle(style)
                .setLargeIcon(podcast.getCoverPhoto());
        builder.addAction(generateAction(android.R.drawable.ic_media_previous, "Previous", ACTION_PREVIOUS));
        builder.addAction(generateAction(android.R.drawable.ic_media_rew, "Rewind", ACTION_REWIND));
        builder.addAction(action);
        builder.addAction(generateAction(android.R.drawable.ic_media_ff, "Fast Forward", ACTION_FAST_FORWARD));
        builder.addAction(generateAction(android.R.drawable.ic_media_next, "Next", ACTION_NEXT));

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(ID,builder.build());
    }

    private Notification.Action generateAction(int icon, String title, String intentAction){
        Intent intent = new Intent(getApplicationContext(),MediaPlayerService.class);
        intent.setAction(intentAction);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(),1,intent,0);
        return new Notification.Action.Builder(icon,title,pendingIntent).build();
    }

    void initMediaSessions(){
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setOnErrorListener(this);
        wifiLock = ((WifiManager)getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL,"caster_wifi_lock");
        wifiLock.acquire();
        /*mediaSession = new MediaSession(getApplicationContext(),"podcast player session");
        mediaController = new MediaController(getApplicationContext(),mediaSession.getSessionToken());

        mediaSession.setCallback(new MediaSession.Callback() {
            @Override
            public void onPause() {
                //super.onPause();
                Log.i("CASTER_MEDIA", "Pause");
                mediaPlayer.pause();
                buildNotification(generateAction(android.R.drawable.ic_media_play, "Play", ACTION_PLAY));
            }

            @Override
            public void onPlay() {
                //super.onPlay();
                Log.i("CASTER_MEDIA", "Play");
                mediaPlayer.start();
                buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE));
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
                Log.i("CASTER_MEDIA", "Skip");
                buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE));
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
                Log.i("CASTER_MEDIA", "Back");
                buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE));
            }

            @Override
            public void onFastForward() {
                super.onFastForward();
                Log.i("CASTER_MEDIA", "FF");
            }

            @Override
            public void onRewind() {
                super.onRewind();
                Log.i("CASTER_MEDIA", "RW");
            }

            @Override
            public void onSeekTo(long pos) {
                super.onSeekTo(pos);
                Log.i("CASTER_MEDIA", "Seek");
            }

            @Override
            public void onStop() {
                super.onStop();
                Log.i("CASTER_MEDIA", "Stop");
                wifiLock.release();
                NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
                notificationManager.cancel(ID);
                Intent intent = new Intent(getApplicationContext(), MediaPlayerService.class);
                stopService(intent);
            }

            @Override
            public void onSetRating(Rating rating) {
                Log.i("CASTER_MEDIA", "rating");
                super.onSetRating(rating);
            }
        });*/
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleIntent(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mediaPlayer.release();
        return super.onUnbind(intent);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mediaPlayer.start();
        buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE));
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if (wifiLock != null && wifiLock.isHeld()) wifiLock.release();
        if (mediaPlayer != null)mediaPlayer.release();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return true;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        buildNotification(generateAction(android.R.drawable.ic_media_pause, "Play", ACTION_PLAY));
    }
}
