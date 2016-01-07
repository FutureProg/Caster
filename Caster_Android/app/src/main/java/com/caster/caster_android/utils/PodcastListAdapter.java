package com.caster.caster_android.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.caster.caster_android.Podcast;
import com.caster.caster_android.R;


/**
 * Created by SuperUser on 8/9/2015.
 */
public class PodcastListAdapter extends ArrayAdapter<Podcast>{
    Context context;
    int layoutResourceId;
    Podcast[] data = null;

    public PodcastListAdapter(Context context, int layoutResourceId, Podcast[] data){
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View row = convertView;
        PodcastHolder holder = null;

        if (row == null){
            LayoutInflater inflater = ((Activity)parent.getContext()).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new PodcastHolder();
            holder.image = (ImageView)row.findViewById(R.id.imgSearchResultsItemImage);
            holder.share = (ImageView)row.findViewById(R.id.imgSearchResultsItemShare);
            holder.title = (TextView)row.findViewById(R.id.txtSearchResultsItemTitle);
            holder.description = (TextView)row.findViewById(R.id.txtSearchResultsItemDescription);

            row.setTag(holder);
        }
        else{
            holder = (PodcastHolder)row.getTag();
        }

        Podcast podcast = data[position];

        try{
            holder.image.setImageBitmap(podcast.getCoverPhoto());
        } catch (Exception e){
            holder.image.setImageResource(R.drawable.default_profile);
        }

        try{
            holder.title.setText(podcast.getTitle());
        } catch (Exception e){
            holder.title.setText("Error getting title");
        }

        try{
            holder.description.setText(podcast.getDescription());
        } catch (Exception e){
            holder.description.setText("Error getting description");
        }

        try{
            holder.share.setImageResource(R.drawable.ic_share_black_24dp);
            holder.share.setTag(position);
        }catch (Exception e){
            holder.share.setImageResource(R.drawable.ic_share_black_24dp);
            holder.share.setTag(position);
        }

        return row;
    }

    static class PodcastHolder{
        ImageView image;
        ImageView share;
        TextView title;
        TextView description;
    }

}
