package com.caster.caster_android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.caster.caster_android.utils.Bin;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


/*
TODO
-Set up podcast playlist
-set up previous and next buttons once the playlist is loaded in
    -adjust cover, author_name, etc. based on the current podcast in queue
-Add comments
*/


public class PodcastPlayer extends Activity {

    public static final String KEY_COMMAND = "com.caster.caster_android.PodcastPlayer.COMMAND";
    public static final byte COMMAND_PLAY = 0x0;

    public static Podcast podcast;

    private static final String TAG = "Play Podcast";

    private int[] podcastList = {R.raw.sample, R.raw.sample_2};
    private int currentPodcast = 0;

    private TextView author,descriptionBox;
    private ImageView coverImage;

    public static MediaPlayer mp;
    private SeekBar seekBar;
    private Handler durationHandler = new Handler();
    private Handler playlistHandler = new Handler();
    private TextView length;
    private double timeElapsed, endTime;
    private Button playButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.slide_up,R.anim.abc_fade_out);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_podcast);
        initializeViews();
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(KEY_COMMAND)){
            if (getIntent().getExtras().getByte(KEY_COMMAND) == COMMAND_PLAY){
                mp = new MediaPlayer();
                mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                String url = MainActivity.site + "/php/audio_file.php?q="+podcast.getId()+"$"+ Bin.getPodcastToken();
                try {
                    mp.setDataSource(url);
                    mp.prepareAsync();
                    endTime = mp.getDuration();
                    length = (TextView)findViewById(R.id.elapsed_time);
                    seekBar = (SeekBar)findViewById(R.id.seekbar);
                    seekBar.setMax((int) endTime);
                    seekBar.setClickable(false);
                    play(null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.abc_fade_in, R.anim.slide_down);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_play_podcast, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initializeViews()
    {
        if(podcast == null){
            return;
        }

        author = (TextView)findViewById(R.id.author_name);
        author.setText(podcast.getCreator().getUsername());

        descriptionBox = (TextView)findViewById(R.id.description);
        descriptionBox.setText(podcast.getDescription());

        ((ImageView)findViewById(R.id.authorPicture)).setImageBitmap(podcast.getCreator().getImage());

        coverImage = (ImageView)findViewById(R.id.cover_image);
        coverImage.setImageBitmap(podcast.getCoverPhoto());

        playButton = (Button)findViewById(R.id.playButton);

        setTitle(podcast.getTitle());

        //Media Player
        mp = new MediaPlayer();
        /*mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mp.setDataSource(podcast.getAudioURL());
            mp.prepareAsync();
            endTime = mp.getDuration();
            length = (TextView)findViewById(R.id.length);
            seekBar = (SeekBar)findViewById(R.id.seekbar);
            seekBar.setMax((int) endTime);
            seekBar.setClickable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }*/


    }

    //Plays or pauses the current podcast
    public void play(View view)
    {
        Log.i(TAG, "play");
        //Play
        if(!(mp.isPlaying()))
        {
            mp.start();
            playButton.setBackground(getDrawable(R.drawable.pause));
        }
        //Pause
        else if(mp.isPlaying())
        {
            mp.pause();
            playButton.setBackground(getDrawable(R.drawable.play));
        }

        //Handle Seekbar
        timeElapsed = mp.getCurrentPosition();
        seekBar.setProgress((int) timeElapsed);
        durationHandler.postDelayed(updateSeekBarTime, 100);

        //Checks if we've reached the end of the song
        playlistHandler.postDelayed(playNext, 100);
    }

    //Play previous podcast
    public void previous(View view)
    {
        if(currentPodcast > 0)
        {
            Log.i(TAG, "previous");
            currentPodcast--;
            //change song
            mp = MediaPlayer.create(this, podcastList[currentPodcast]);
            //Change Author name, description, etc. here once the podcast changes.
        }
    }

    //Play next podcast
    public void next(View view)
    {
        if(currentPodcast < podcastList.length-1)
        {
            currentPodcast++;
            Log.i(TAG, "next");
            //change song
            mp = MediaPlayer.create(this, podcastList[currentPodcast]);
            //Change Author name, description, etc. here once the podcast changes.
        }
    }

    public void profilePage(View view){
        ProfileActivity.user = podcast.getCreator();
        Intent intent = new Intent(this,ProfileActivity.class);
        startActivity(intent);
        finish();
    }

    //Updates seekbar time
    private Runnable updateSeekBarTime = new Runnable() {
        @Override
        public void run() {
            timeElapsed = mp.getCurrentPosition();
            //seekbar progress
            seekBar.setProgress((int)timeElapsed);
            //set remaining time
            double remainingTime = endTime - timeElapsed;
            //Hours, minutes, seconds
            length.setText(String.format("%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours((long) remainingTime),
                    TimeUnit.MILLISECONDS.toMinutes((long) remainingTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours((long)remainingTime)),
                    TimeUnit.MILLISECONDS.toSeconds((long)remainingTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)remainingTime))));
            //Repeat in 100 milliseconds
            durationHandler.postDelayed(this,100);
        }
    };

    //Checks if song is over, then plays next song
    private Runnable playNext = new Runnable() {
        @Override
        public void run() {
            timeElapsed = mp.getCurrentPosition();
            if(timeElapsed == endTime)
            {
                next(null);
            }
            playlistHandler.postDelayed(this,100);
        }
    };

    private boolean isSubscribed()
    {
        //Checks whether the user is subscribed, if subscribed, then display the correct button
        return false;
    }

    public void subscribe(View v){
        Button subscribe = ((Button)findViewById(R.id.subscribe));
        //UnSubscribing
        if (subscribe.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.unsubscribe))) {
            try {
                if (Bin.unsubscribe(podcast.getId()) == false) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Error UnSubscribing, Are you signed in?").setTitle("Message");
                    builder.create().show();
                    return;
                } else {
                    subscribe.setBackground(getResources().getDrawable(R.drawable.subscribe));
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //Subscribing
        else{
            try{
                if(Bin.subscribe(podcast.getId()) == false){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Error Subscribing, Are you signed in?").setTitle("Message");
                    builder.create().show();
                    return;
                }
                else{
                    subscribe.setBackground(getResources().getDrawable(R.drawable.unsubscribe));
                }
            }
            catch (ExecutionException e){
                e.printStackTrace();
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }


}
