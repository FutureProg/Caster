package com.caster.caster_android.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caster.caster_android.Podcast;
import com.caster.caster_android.PodcastPlayer;
import com.caster.caster_android.R;

/**
 * Created by Nick on 15-05-31.
 */
public class PodcastBar extends LinearLayout implements View.OnClickListener{

    Podcast podcast;
    ImageView imgButton;
    TextView titleView,descView,timeView,authorView;

    public PodcastBar(Context context, AttributeSet attrs, Podcast podcast) {
        this(context, attrs);
        this.setOnClickListener(this);
        this.podcast = podcast;
        LayoutInflater inflater = LayoutInflater.from(this.getContext());
        inflater.inflate(R.layout.podcast_bar_layout,this);
        imgButton = (ImageView)findViewById(R.id.cover_image);
        imgButton.setBackground(new BitmapDrawable(getResources(),podcast.getCoverPhoto()));
        titleView = (TextView)findViewById(R.id.title);
        titleView.setText(podcast.getTitle());
        descView = (TextView)findViewById(R.id.description);
        descView.setText(podcast.getDescription());
        timeView = (TextView)findViewById(R.id.time);
        timeView.setText(podcast.getTime());
        authorView = (TextView)findViewById(R.id.author_name);
        authorView.setText(podcast.getCreator().getUsername());
    }

    public PodcastBar(Context context,AttributeSet attrs){
        super(context, attrs);
    }

    public PodcastBar(Context context){
        super(context);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getContext(),PodcastPlayer.class);
        PodcastPlayer.podcast = this.podcast;
        Bundle bundle = new Bundle();
        bundle.putByte(PodcastPlayer.KEY_COMMAND, PodcastPlayer.COMMAND_PLAY);
        intent.putExtras(bundle);
        ((Activity)getContext()).startActivity(intent);
    }
}
