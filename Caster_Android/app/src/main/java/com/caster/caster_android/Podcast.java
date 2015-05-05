package com.caster.caster_android;

import android.graphics.Bitmap;

import java.util.Dictionary;

/**
 * Created by Nick on 15-05-05.
 */
public class Podcast {

    public final static int COVERPHOTO = 0, TITLE = 1, DESCRIPTION = 2, VIEWS = 3, LIKES = 4,
                            LENGTH = 5;

    Dictionary<Integer,Object> metadata; //image,title, descriptions, views, likes, length, etc.
    User creator;

    public Podcast(User creator,Dictionary<Integer,Object> metadata){
        this.creator = creator;
        this.metadata = metadata;
    }

    public Podcast(Dictionary<Integer,Object> metadata){
        this.metadata = metadata;
        this.creator = null;
    }

    public User getCreator(){
        return creator;
    }

    public Bitmap getCoverPhoto(){
        return (Bitmap)metadata.get(COVERPHOTO);
    }

    public String getTitle(){
        return (String)metadata.get(TITLE);
    }

    public String getDescription(){
        return (String)metadata.get(DESCRIPTION);
    }

    public int getViews(){
        return (int)metadata.get(VIEWS);
    }

    public int getLikes(){
        return (int)metadata.get(LIKES);
    }

    public int getLength(){
        return (int)metadata.get(LENGTH);
    }

    public Dictionary<Integer,Object> getMetadata(){
        return  metadata;
    }

}
