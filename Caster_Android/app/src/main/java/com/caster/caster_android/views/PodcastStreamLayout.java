package com.caster.caster_android.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caster.caster_android.Podcast;
import com.caster.caster_android.ProfileActivity;
import com.caster.caster_android.R;
import com.caster.caster_android.User;

/**
 * Created by Nick on 16-02-15.
 */
public class PodcastStreamLayout extends LinearLayout implements View.OnClickListener{

    User user;
    Podcast[] podcasts;
    int headerHeight;

    public PodcastStreamLayout(Context context, AttributeSet attrs, Podcast[] podcasts){
        this(context,attrs);
        this.podcasts = podcasts;
        LayoutInflater inflater = LayoutInflater.from(this.getContext());
        inflater.inflate(R.layout.podcast_stream_layout,this);
        TextView textView = (TextView) findViewById(R.id.podcast_stream_layout_title);
        textView.setText("");
        ImageView imageView = (ImageView) findViewById(R.id.podcast_stream_layout_image);
        imageView.setVisibility(INVISIBLE);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.podcast_stream_layout_content);
        for(Podcast podcast : podcasts){
            linearLayout.addView(new PodcastBox(context,null,podcast));
        }
        LinearLayout view = (LinearLayout)findViewById(R.id.podcast_Stream_layout_header);
        view.setOnClickListener(this);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        headerHeight = layoutParams.height;
        layoutParams.height = 0;
    }

    public PodcastStreamLayout(Context context) {
        super(context);
    }

    public PodcastStreamLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setTitle(String text){
        TextView textView = (TextView) findViewById(R.id.podcast_stream_layout_title);
        textView.setText(text);
        findViewById(R.id.podcast_Stream_layout_header).getLayoutParams().height = headerHeight;
    }

    public void setImage(Bitmap image){
        ImageView imageView = (ImageView) findViewById(R.id.podcast_stream_layout_image);
        imageView.setImageBitmap(image);
        imageView.setVisibility(VISIBLE);
        findViewById(R.id.podcast_Stream_layout_header).getLayoutParams().height = headerHeight;
    }

    public void setUser(User user){
        this.user = user;
        setTitle(user.getUsername());
        setImage(user.getImage());
    }

    public User getUser(){
        return user;
    }

    @Override
    public void onClick(View view){
        if (user != null){
            ProfileActivity.user = user;
            Intent intent = new Intent(getContext(),ProfileActivity.class);
            getContext().startActivity(intent);
        }
    }

}
