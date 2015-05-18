package com.caster.caster_android;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by Nick on 15-05-17.
 */
public class Comment {

    public final static int PODCAST_ID=0,USER_ID=1,ID=2,POST_DATE=4,MESSAGE=5;

    HashMap<Integer, Object> metadata; //podcast_id,user_id,id,post_date,message
    private Date date;

    /**
     *
     * @param podcastid the ID of the podcast where the comments are
     * @return a list of comments
     */
    public static ArrayList<Comment> makeFromID(int podcastid){
        HashMap<Integer,Object> data = new HashMap<>();
        ArrayList<Comment> re = new ArrayList<>();
        String urlStr = "http://192.168.2.155:8000/php/podcast.php";
        CasterRequest req = new CasterRequest(urlStr);
        req.addParam("q","CMNTS_JSON").addParam("id",""+podcastid);
        try {
            String resStr = (String)req.execute().get();
            JSONArray res = new JSONArray(resStr);
            for (int i = 0; i < res.length();i++){
                JSONObject obj = res.getJSONObject(i);
                data.put(PODCAST_ID,obj.getInt("podcast_id"));
                data.put(ID,obj.getInt("comment_id"));
                data.put(USER_ID,obj.getInt("user_id"));
                data.put(MESSAGE,obj.getInt("message"));
                data.put(POST_DATE,obj.getInt("post_date"));
                re.add(new Comment(data));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return re;
    }

    public Comment(HashMap<Integer,Object> data){
        this.metadata = data;
    }

    public int getPodcastId(){
        return (Integer)metadata.get(PODCAST_ID);
    }

    public int getUserId(){
        return (Integer)metadata.get(USER_ID);
    }

    public int getId(){
        return (Integer)metadata.get(ID);
    }

    public String getMessage(){
        return (String)metadata.get(MESSAGE);
    }

    public Date getPostDate(){
        if (date == null){
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                date = df.parse((String) metadata.get(POST_DATE));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return date;
    }



}
