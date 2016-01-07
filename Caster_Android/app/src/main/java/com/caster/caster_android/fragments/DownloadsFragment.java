package com.caster.caster_android.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.caster.caster_android.MainActivity;
import com.caster.caster_android.Podcast;
import com.caster.caster_android.R;
import com.caster.caster_android.utils.PodcastDownloader;
import com.caster.caster_android.views.PodcastBar;

import java.util.ArrayList;

/**
 * Created by Nick on 2016-01-06.
 */
public class DownloadsFragment extends Fragment{

    ArrayList<Podcast> podcasts;

    public DownloadsFragment() {
        PodcastDownloader downloader = PodcastDownloader.getDownloader(MainActivity.instance.getApplicationContext());
        podcasts = new ArrayList<>();
        ArrayList<Integer> podcast_ids = downloader.getDownloaded();
        for (int podcast_id:podcast_ids){
            podcasts.add(Podcast.makeFromID(podcast_id));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View re = inflater.inflate(R.layout.fragment_downloads,container,false);
        for (Podcast podcast : podcasts){
            PodcastBar bar = new PodcastBar(re.getContext(),null,podcast);
            ((LinearLayout)re).addView(bar);
        }
        return re;
    }
}
