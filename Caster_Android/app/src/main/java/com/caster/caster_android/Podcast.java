package com.caster.caster_android;

import android.graphics.Bitmap;

import java.util.HashMap;

/**
 * Created by Nick on 15-05-05.
 *
 * Podcast
 * Used to store the metadata and image for the Podcast
 * Can be created using Podcast.makeFromID(<id>) to retrieve one from the database.
 * holds information such as the coverphoto url, title, description, views, likes, length,
 * podcast id and creator id.
 */
public class Podcast {

    public final static int COVERPHOTO = 0, TITLE = 1, DESCRIPTION = 2, VIEWS = 3, LIKES = 4,
                            LENGTH = 5, ID=6,CREATOR_ID=7;

    HashMap<Integer, Object> metadata; //image,title, descriptions, views, likes, length, etc.
    User creator;

    /***
     * Retrieve the podcast's metadata from the server
     * @param podcastid the podcast's id
     * @return an instance of the podcast
     */
    public static Podcast makeFromID(int podcastid){
        HashMap<Integer,Object> data = new HashMap<>();
        Podcast re = null;
        String urlStr = "http://192.168.2.155:8000/php/podcast.php";
        try {
            CasterRequest request = new CasterRequest(urlStr);
            String res = (String)request.addParam("q","TTL").addParam("id",""+podcastid).execute().get();
            request = new CasterRequest(urlStr);
            data.put(TITLE, res);
            res = (String)request.addParam("q","LNGTH").addParam("id",""+podcastid).execute().get();
            request = new CasterRequest(urlStr);
            data.put(LENGTH, Integer.parseInt(res));
            res = (String)request.addParam("q","DESC").addParam("id",""+podcastid).execute().get();
            request = new CasterRequest(urlStr);
            data.put(DESCRIPTION, res);
            data.put(ID,podcastid);
            res = (String)request.addParam("q","USR").addParam("id",""+podcastid).execute().get();
            data.put(CREATOR_ID,Integer.parseInt(res));
            //String imgname = result.getString("image_file");
            //String imgURL = "users/"+(String)data.get(CREATOR_ID)+"/images/podcast/"+imgname;
            re = new Podcast(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return re;
    }

    public Podcast(User creator,HashMap<Integer, Object> metadata){
        this.creator = creator;
        this.metadata = metadata;
    }

    public Podcast(HashMap<Integer, Object> metadata){
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

    public int getId(){
        return (int)metadata.get(LENGTH);
    }

    public int getCreatorId(){
        return (int)metadata.get(CREATOR_ID);
    }


    public HashMap<Integer, Object> getMetadata(){
        return  metadata;
    }

}
