package com.caster.caster_android.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.caster.caster_android.Comment;
import com.caster.caster_android.R;
import com.caster.caster_android.User;

/**
 * Created by SuperUser on 8/10/2015.
 */
public class CommentListAdapter extends ArrayAdapter<Comment>{
    Context context;
    int layoutResourceId;
    Comment[] data = null;

    public CommentListAdapter(Context context, int layoutResourceId, Comment[] data){
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View row = convertView;
        CommentHolder holder = null;

        if (row == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new CommentHolder();
            holder.image = (ImageView)row.findViewById(R.id.comment_list_user_image);
            holder.name = (TextView)row.findViewById(R.id.comment_list_user_name);
            holder.comment = (TextView)row.findViewById(R.id.comment_list_user_comment);

            row.setTag(holder);
        }
        else{
            holder = (CommentHolder)row.getTag();
        }

        Comment comment = data[position];
        User user = User.makeFromID(comment.getUserId());
        try{
            holder.image.setImageBitmap(user.getImage());
        } catch (Exception e){
            holder.image.setImageResource(R.drawable.default_profile);
        }

        try{
            holder.name.setText(user.getUsername());
        } catch (Exception e){
            holder.name.setText("Error getting username");
        }

        try{
            holder.comment.setText(comment.getMessage());
        } catch (Exception e){
            holder.comment.setText("Error getting message");
        }


        return row;
    }

    static class CommentHolder{
        ImageView image;
        TextView name;
        TextView comment;
    }

}
