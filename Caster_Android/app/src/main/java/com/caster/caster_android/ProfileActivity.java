package com.caster.caster_android;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caster.caster_android.views.PodcastBar;

import java.util.ArrayList;

/**
 * Created by Nick on 15-06-01.
 */
public class ProfileActivity extends Activity{

    public static User user;

    LinearLayout podcastArea;

    ArrayList<Podcast> podcasts;
    GridLayout podcastBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_view);
        if (user == null){
            return;
        }
        podcastBar = (GridLayout)findViewById(R.id.podcast_bar);
        ((ImageView)findViewById(R.id.profilePicture)).setImageBitmap(user.getImage());
        ((TextView)findViewById(R.id.profile_username)).setText(user.getUsername());
        ((TextView)findViewById(R.id.profile_bio)).setText(user.getDescription());
        podcastArea = (LinearLayout)findViewById(R.id.podcast_area);
        podcasts = new ArrayList<>(user.getPodcasts());
        for (Podcast podcast : podcasts){
            Log.v("caster_profile",podcast.getTitle());
            PodcastBar bar = new PodcastBar(this,null,podcast);
            podcastArea.addView(bar);
        }
        updatePlayerBar();
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
