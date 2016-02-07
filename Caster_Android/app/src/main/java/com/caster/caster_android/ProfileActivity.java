package com.caster.caster_android;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.caster.caster_android.views.PodcastBar;

import java.util.ArrayList;

/**
 * Created by Nick on 15-06-01.
 */
public class ProfileActivity extends AppCompatActivity{

    public static User user;

    LinearLayout podcastArea;

    ArrayList<Podcast> podcasts;
    GridLayout podcastBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_view);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (user == null){
            return;
        }
        podcastBar = (GridLayout)findViewById(R.id.podcast_bar);
        ((ImageView)findViewById(R.id.profilePicture)).setImageBitmap(user.getImage());
        ((TextView)findViewById(R.id.profile_username)).setText(user.getUsername());
        //((TextView)findViewById(R.id.profile_bio)).setText(user.getDescription());
        podcastArea = (LinearLayout)findViewById(R.id.podcast_area);
        podcasts = new ArrayList<>(user.getPodcasts());
        this.getSupportActionBar().setTitle("");
        this.getSupportActionBar().setDisplayShowTitleEnabled(true);
        for (Podcast podcast : podcasts){
            Log.v("caster_profile",podcast.getTitle());
            PodcastBar bar = new PodcastBar(this,null,podcast);
            podcastArea.addView(bar);
        }
        updatePlayerBar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_bio){
            Toast.makeText(this,"BIO",Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePlayerBar();
    }

    public void updatePlayerBar(){
        if (PodcastPlayer.podcast == null){
            podcastBar.setVisibility(View.INVISIBLE);
        }else{
            ((Button)podcastBar.findViewById(R.id.podcast_bar_img)).setBackground(
                    new BitmapDrawable(getResources(),PodcastPlayer.podcast.getCoverPhoto()));
            podcastBar.setVisibility(View.VISIBLE);
        }
    }

    public void openPlayer(View view){
        Intent intent = new Intent(this,PodcastPlayer.class);
        startActivity(intent);
    }
}
