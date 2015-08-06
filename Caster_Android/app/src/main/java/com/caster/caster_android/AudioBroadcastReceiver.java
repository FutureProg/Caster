package com.caster.caster_android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;

/**
 * Created by Nick on 15-08-03.
 */
public class AudioBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.v("caster ACTION",action);
        MediaPlayer player = PodcastPlayer.mp;
        if (action.equalsIgnoreCase("com.caster.caster_android.ACTION_PLAY")){
            if (player.isPlaying()){
                player.pause();
                if (intent.hasExtra("VIEW")){
                    ((View)intent.getExtras().get("VIEW")).findViewById(R.id.playButton)
                            .setBackgroundResource(R.drawable.pause);
                }
            }else{
                player.start();
                if (intent.hasExtra("VIEW")){
                    ((View)intent.getExtras().get("VIEW")).findViewById(R.id.playButton)
                            .setBackgroundResource(R.drawable.play_button);
                }
            }
        }
    }
}
