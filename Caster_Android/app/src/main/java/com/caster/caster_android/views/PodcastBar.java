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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.caster.caster_android.Podcast;
import com.caster.caster_android.PodcastPlayer;
import com.caster.caster_android.R;
import com.caster.caster_android.utils.PodcastDownloader;

/**
 * Created by Nick on 15-05-31.
 */
public class PodcastBar extends LinearLayout implements View.OnClickListener, PodcastDownloader.OnChangeListener{

    Podcast podcast;
    ImageView imgButton;
    TextView titleView,descView,timeView,authorView;
    ProgressBar progressBar;
    ImageButton downloadButton;

    public PodcastBar(Context context, AttributeSet attrs, Podcast podcast) {
        this(context, attrs);
        this.setOnClickListener(this);
        this.podcast = podcast;
        LayoutInflater inflater = LayoutInflater.from(this.getContext());
        inflater.inflate(R.layout.podcast_bar_layout, this);
        imgButton = (ImageView)findViewById(R.id.cover_image);
        imgButton.setBackground(new BitmapDrawable(getResources(), podcast.getCoverPhoto()));
        titleView = (TextView)findViewById(R.id.title);
        titleView.setText(podcast.getTitle());
        descView = (TextView)findViewById(R.id.description);
        descView.setText(podcast.getDescription());
        timeView = (TextView)findViewById(R.id.time);
        timeView.setText(podcast.getTime());
        authorView = (TextView)findViewById(R.id.author_name);
        authorView.setText(podcast.getCreator().getUsername());
        progressBar = (ProgressBar)findViewById(R.id.podcast_bar_progress_wheel);
        downloadButton = (ImageButton)findViewById(R.id.podcast_bar_download_button);
        if (PodcastDownloader.getDownloader(context).getDownloaded().contains(podcast.getId())) {
            downloadButton.setOnClickListener(this);
            downloadButton.setBackgroundResource(R.drawable.ic_circle_with_cross);
            downloadButton.setTag("delete_tag");
        }else{
            downloadButton.setOnClickListener(this);
            downloadButton.setTag("download_tag");
        }
    }

    public PodcastBar(Context context,AttributeSet attrs){
        super(context, attrs);
    }

    public PodcastBar(Context context){
        super(context);
    }

    public void onDownload(View view){
        PodcastDownloader downloader = PodcastDownloader.getDownloader(this.getContext());
        downloader.queueDownload(podcast.getId(), this);
        downloadButton.setTag("delete_tag");
        downloadButton.setBackgroundResource(R.drawable.ic_circle_with_cross);
    }

    public void onCancelDownload(View view){
        PodcastDownloader downloader = PodcastDownloader.getDownloader(this.getContext());
        if (downloader.getState(podcast.getId()) != PodcastDownloader.State.FINISHED) {
            downloader.cancelDownload(podcast.getId());
            downloadButton.setBackgroundResource(R.drawable.ic_download);
            downloadButton.setTag("download_tag");
        }
    }

    public void onDeleteDownload(View view){
        PodcastDownloader downloader = PodcastDownloader.getDownloader(this.getContext());
        if(downloader.deletePodcast(podcast.getId())){
            Toast.makeText(getContext(),"Deleted",Toast.LENGTH_SHORT).show();
            downloadButton.setBackgroundResource(R.drawable.ic_download);
            downloadButton.setTag("download_tag");
        }else{
            Toast.makeText(getContext(),"Deletion Failed",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        if(v != null && v.getTag().equals("download_tag")){
            progressBar.setVisibility(VISIBLE);
            onDownload(v);
            return;
        }
        else if(v != null && v.getTag().equals("delete_tag")){
            onDeleteDownload(v);
            return;
        }
        else if (v != null && v.getTag().equals("cancel_tag")) {
            onCancelDownload(v);
            return;
        }
        Intent intent = new Intent(getContext(),PodcastPlayer.class);
        PodcastPlayer.podcast = this.podcast;
        Bundle bundle = new Bundle();
        bundle.putByte(PodcastPlayer.KEY_COMMAND, PodcastPlayer.COMMAND_PLAY);
        intent.putExtras(bundle);
        ((Activity)getContext()).startActivity(intent);
    }

    @Override
    public void onDownloadStateChange(int progress, int podcast_id, PodcastDownloader.State state) {
        progressBar.setProgress(progress);
        if (progress == 100 || state == PodcastDownloader.State.CANCELLED){
            progressBar.setVisibility(GONE);
        }
        if(state == PodcastDownloader.State.FINISHED){
            downloadButton.setBackgroundResource(R.drawable.ic_circle_with_cross);
            downloadButton.setTag("delete_tag");
        }
    }
}
