package com.caster.caster_android;

import android.graphics.Bitmap;

import com.caster.caster_android.utils.Bin;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import static com.caster.caster_android.utils.Bin.*;

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

    public final static int COVER_PHOTO = 0, TITLE = 1, DESCRIPTION = 2, VIEWS = 3, LIKES = 4,
                            LENGTH = 5, ID=6,CREATOR_ID=7, AUDIO_FILE = 8;

    HashMap<Integer, Object> metadata; //image,title, descriptions, views, likes, length, etc.
    User creator;
    Bitmap coverPhoto;

    /***
     * Retrieve the podcast's metadata from the server
     * @param podcastid the podcast's id
     * @return an instance of the podcast
     */
    public static Podcast makeFromID(int podcastid){
        if (podcasts.containsKey(podcastid)){
            return podcasts.get(podcastid);
        }
        HashMap<Integer,Object> data = new HashMap<>();
        Podcast re = null;
        String urlStr = MainActivity.site+"/php/podcast.php";
        try {
            CasterRequest request = new CasterRequest(urlStr);
            String res = (String)request.addParam("q","PDCST_JSN").addParam("id",""+podcastid).execute().get();
            JSONObject jsonObject = new JSONObject(res);
            return makeFromJson(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return re;
    }

    public static Podcast makeFromJson(JSONObject jsonObject){
        HashMap<Integer,Object> data = new HashMap<>();
        Podcast re = null;
        try {
            data.put(TITLE,jsonObject.getString("title"));
            data.put(LENGTH,jsonObject.getInt("length"));
            data.put(DESCRIPTION,jsonObject.getString("description"));
            data.put(ID,jsonObject.getInt("podcast_id"));
            data.put(CREATOR_ID,jsonObject.getInt("user_id"));
            data.put(COVER_PHOTO,jsonObject.getString("image_file"));
            data.put(AUDIO_FILE,jsonObject.getString("audio_file"));
            re = new Podcast(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return re;
    }

    public Podcast(User creator,HashMap<Integer, Object> metadata){
        this.creator = creator;
        this.metadata = metadata;
        podcasts.put(getId(), this);
    }

    public Podcast(HashMap<Integer, Object> metadata){
        this(null,metadata);
    }

    /**
     *
     * @param userid
     * @param message
     * @return
     */
    public boolean postComment(int userid,String message) throws InterruptedException,ExecutionException{
        String urlStr = MainActivity.site+"/php/podcast.php";
        CasterRequest req = new CasterRequest(urlStr);
        String res = (String)req.addParam("q","CMNT").addParam("uid",userid+"").addParam("id",getId()+"")
                .addParam("msg", message).execute().get();
        if (!res.equals("OKAY")){
            return false;
        }
        return true;
    }

    public User getCreator(){
        if (creator == null){
            creator = User.makeFromID(getCreatorId());
        }
        return creator;
    }

    public Bitmap getCoverPhoto() {
        //TODO
        if (coverPhoto == null) {
            String urlStr = MainActivity.site + "/users/" + getCreatorId() + "/images/podcast/"
                    + metadata.get(COVER_PHOTO);

            try {
                coverPhoto = Bin.getBitmap(urlStr);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        return coverPhoto;
    }

    public String getAudioURL(){
        return MainActivity.site + "/users/" + getCreatorId() + "/audio/podcast/"+metadata.get(AUDIO_FILE);
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

    public String getTime(){
        String re = "";
        int length = getLength();
        int hrs = (int)Math.floor(length/60.0/60.0);
        length -= hrs * 60 * 60;
        int mins = (int)Math.floor(length/60.0);
        length -= mins * 60;
        int seconds = length;

        return re;
    }

    public int getId(){
        return (int)metadata.get(ID);
    }

    public int getCreatorId(){
        return (int)metadata.get(CREATOR_ID);
    }


    public HashMap<Integer, Object> getMetadata(){
        return  metadata;
    }

}
