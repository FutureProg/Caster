package com.caster.caster_android.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;

import com.caster.caster_android.PodcastPlayer;
import com.caster.caster_android.R;

/**
 * Created by Nick on 15-06-06.
 */
public class PlayerBar extends GridLayout{

    public PlayerBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        update();
    }

    public PlayerBar(Context context){
        super(context);
    }

    public void init(){
        LayoutInflater inflater = LayoutInflater.from(this.getContext());
        inflater.inflate(R.layout.player_bar_layout,this);
    }

    public void update(){
        if (PodcastPlayer.podcast == null){
            this.setVisibility(View.INVISIBLE);
        }else{
            this.setVisibility(View.VISIBLE);
        }
    }
}
