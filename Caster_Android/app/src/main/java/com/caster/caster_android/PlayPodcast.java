package com.caster.caster_android;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.graphics.Color;
import android.widget.ImageButton;

import android.widget.SeekBar;
import android.media.MediaPlayer;
import android.os.Handler;

import java.sql.Time;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;


/*
TODO
-Set up podcast playlist
-set up previous and next buttons once the playlist is loaded in
    -adjust cover, author_name, etc. based on the current podcast in queue
-Add comments
*/


public class PlayPodcast extends Activity {

    //Grab Podcast Data
    //Podcast podcast = new Podcast();

    private static final String TAG = "Play Podcast";

    //Sample Data
    private String podcastName = "Podcast Name";
    private String authorName = "Author Name";
    private String description = "Lorem ipsum dolor sit amet, mel " +
            "ei nihil ignota verterem. Eam ea molestie consequat definiebas. " +
            "Ex persius gloriatur est, id sit integre ponderum postulant, vix " +
            "cu adhuc dicit. Usu duis velit senserit in, ut quo quas phaedrum lobortis, " +
            "habeo audiam ut mea.";
    private boolean subscribed;
    private int coverImageID = R.drawable.cover;
    private int profilePictureID;
    private int[] podcastList = {R.raw.sample, R.raw.sample_2};
    private int currentPodcast = 0;
    //private ArrayList<Podcast> podcastList = new ArrayList<Podcast>();


    //Views
    private TextView author,descriptionBox;
    private ImageView coverImage;
    private View view;

    //Player
    private MediaPlayer mp;
    private SeekBar seekBar;
    private Handler durationHandler = new Handler();
    private Handler playlistHandler = new Handler();
    private TextView length;
    private int track;
    private double timeElapsed, endTime;
    private ImageButton playButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_podcast);
        initializeViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_play_podcast, menu);
        return true;
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
        author = (TextView)findViewById(R.id.author_name);
        author.setText(authorName);

        descriptionBox = (TextView)findViewById(R.id.description);
        descriptionBox.setText(description);

        coverImage = (ImageView)findViewById(R.id.cover_image);
        coverImage.setBackgroundResource(coverImageID);

        playButton = (ImageButton)findViewById(R.id.playButton);

        setTitle(podcastName);

        //Media Player
        mp = MediaPlayer.create(this, R.raw.sample);
        endTime = mp.getDuration();
        length = (TextView)findViewById(R.id.length);


        seekBar = (SeekBar)findViewById(R.id.seekbar);
        seekBar.setMax((int) endTime);
        seekBar.setClickable(false);
    }

    //Plays or pauses the current podcast
    public void play(View view)
    {
        Log.i(TAG, "play");
        //Play
        if(!(mp.isPlaying()))
        {
            mp.start();
            playButton.setImageResource(R.drawable.pause);
        }
        //Pause
        else if(mp.isPlaying())
        {
            mp.pause();
            playButton.setImageResource(R.drawable.play);
        }

        //Handle Seekbar
        timeElapsed = mp.getCurrentPosition();
        seekBar.setProgress((int) timeElapsed);
        durationHandler.postDelayed(updateSeekBarTime, 100);

        //Checks if we've reached the end of the song
        playlistHandler.postDelayed(playNext,100);
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
                next(view);
            }
            playlistHandler.postDelayed(this,100);
        }
    };

    private boolean isSubscribed()
    {
        //Checks whether the user is subscribed, if subscribed, then display the correct button
        return false;
    }

}
