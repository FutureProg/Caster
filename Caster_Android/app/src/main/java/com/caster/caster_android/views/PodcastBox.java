package com.caster.caster_android.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caster.caster_android.Podcast;
import com.caster.caster_android.PodcastPlayer;
import com.caster.caster_android.R;

/**
 * Created by Nick on 15-05-26.
 */
public class PodcastBox extends LinearLayout implements View.OnClickListener{

    private Podcast podcast;
    private ImageButton imageButton;
    private TextView textView;


    public PodcastBox(Context context, AttributeSet attrs, Podcast podcast) {
        super(context,attrs);
        this.podcast = podcast;
        init();
    }

    public PodcastBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PodcastBox(Context context) {
        super(context);
    }

    public void init(){
        LayoutInflater inflater = LayoutInflater.from(this.getContext());
        inflater.inflate(R.layout.podcast_box_layout,this);
        imageButton = (ImageButton)findViewById(R.id.box_cover_image);
        imageButton.setBackground(new BitmapDrawable(getResources(), podcast.getCoverPhoto()));
        imageButton.setOnClickListener(this);
        textView = (TextView)findViewById(R.id.title);
        textView.setText(podcast.getTitle());
    }

    public Podcast getPodcast() {
        return podcast;
    }

    public void setPodcast(Podcast podcast) {
        this.podcast = podcast;
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
