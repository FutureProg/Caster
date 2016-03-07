package com.caster.caster_android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.caster.caster_android.utils.Bin;
import com.caster.caster_android.utils.PodcastDownloader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import static com.caster.caster_android.utils.Bin.podcasts;

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
                            LENGTH = 5, ID=6,CREATOR_ID=7, AUDIO_FILE = 8, URLID = 9,
                            DOWNLOADABLE = 10, CATEGORY = 11, EDIT_STAMP = 12;

    HashMap<Integer, Object> metadata; //image,title, descriptions, views, likes, length, etc.
    User creator;
    Bitmap coverPhoto;

    /***
     * Retrieve the podcast's metadata from the server
     * @param podcastid the podcast's id
     * @return an instance of the podcast
     */
    public static Podcast makeFromID(int podcastid){
        if (podcasts == null)
            podcasts = new HashMap<>();
        if (podcasts.containsKey(podcastid)){
            return podcasts.get(podcastid);
        }
        PodcastDownloader.State state = PodcastDownloader.getDownloader(null).getState(podcastid);
        if (state == PodcastDownloader.State.FINISHED || state == PodcastDownloader.State.METAONLY){
            return loadDownloadedPodcast(podcastid);
        }
        HashMap<Integer,Object> data = new HashMap<>();
        Podcast re = null;
        String urlStr = MainActivity.site + "/php/podcast.php";
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

    public static Podcast makeFromUrlid(String urlid){
        HashMap<Integer, Object> data = new HashMap<>();
        Podcast re = null;
        String urlStr = MainActivity.site + "/php/podcast.php";
        try{
            CasterRequest request = new CasterRequest(urlStr);
            String res = (String)request.addParam("q", "URLIDTOPID").addParam("id", urlid).execute().get();
            Log.d("Make From Urlid : ", res + "");
            return makeFromID(Integer.parseInt(res.trim()));
        }
        catch (Exception e){
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
            data.put(URLID, jsonObject.getString("urlid"));
            data.put(DOWNLOADABLE,jsonObject.getString("downloadable"));
            data.put(CATEGORY,jsonObject.getString("category"));
            String date_str = jsonObject.getString("edit_date");
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = df.parse(date_str);
            data.put(EDIT_STAMP,date);
            re = new Podcast(data);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return re;
    }

    private static Podcast loadDownloadedPodcast(int podcast_id){
        Podcast re = null;

        HashMap<Integer, Object> data = new HashMap<>();
        String basePath = MainActivity.instance.getFilesDir().getAbsolutePath() + "/podcast_" + podcast_id;
        String metaPath = basePath + "/metadata";
        String coverPath = basePath + "/cover_image";

        File file = new File(metaPath);
        if (!file.exists()) return re;
        try {
            Scanner input = new Scanner(file);
            //read metadata
            while (input.hasNextLine()){
                int key = Integer.parseInt(input.nextLine());
                String value = input.nextLine();
                switch (key){
                    case VIEWS:
                    case LIKES:
                    case LENGTH:
                    case ID:
                    case CREATOR_ID:
                        data.put(key,Integer.parseInt(value));
                        break;
                    default:
                        data.put(key,value);
                        break;
                }
            }
            Bitmap image = BitmapFactory.decodeFile(coverPath);
            re = new Podcast(data);
            re.coverPhoto = image;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return re;
    }

    private Podcast(User creator,HashMap<Integer, Object> metadata){
        if(Bin.podcasts == null)Bin.podcasts = new HashMap<>();
        this.creator = creator;
        this.metadata = metadata;
        Bin.podcasts.put(getId(), this);
    }

    private Podcast(HashMap<Integer, Object> metadata){
        this(null, metadata);
    }

    /**
     * Post a comment to this podcast
     * @param userid
     * @param message
     * @return a boolean based on whether it was successful or not
     */
    public boolean postComment(int userid,String message) throws InterruptedException,ExecutionException{
        String urlStr = MainActivity.site + "/php/podcast.php";
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

    String getAudioURL(){
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

    public String getUrlid() { return (String)metadata.get(URLID); }

    public Date getEditDate(){
        return (Date)metadata.get(EDIT_STAMP);
    }

    public boolean needsUpdate(){
        if(!Bin.checkConnection(MainActivity.instance)){
            return false;
        }
        Date lastUpdate = getEditDate();
        CasterRequest req = new CasterRequest(MainActivity.site + "/php/podcast.php");
        try {
            String res = (String)req.addParam("q","EDIT_DATE").addParam("id",getId()+"").execute().get();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date update = df.parse(res);
            metadata.put(EDIT_STAMP,update);
            return lastUpdate.before(update);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return false;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    public HashMap<Integer, Object> getMetadata(){
        return  metadata;
    }

}
