package com.caster.caster_android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.caster.caster_android.utils.Bin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by Nick on 15-05-05.
 *
 * User
 * Used to store the metadata and image for a User (not the app user)
 * Can be created using User.makeFromID(<id>) to retrieve one from the database
 * Holds information such as the username, descripion, and user id.
 */
public class User {

    public static final int USERNAME = 0, DESCRIPTION = 1, SUBSCRIBER_COUNT = 2,ID=3, IMAGE = 4,
        SUBSCRIPTIONS = 5;

    HashMap<Integer, Object> metadata; //username, description,subscriber count,id
    ArrayList<Podcast> podcasts;
    Bitmap image;

    /***
     *  Retrieve the user's metadata from the server
     * @param userid userid to process
     * @return an instance of the user
     */
    public static User makeFromID(int userid){
        if (Bin.users.containsKey(userid)){
            return Bin.users.get(userid);
        }
        HashMap<Integer,Object> data = new HashMap<>();
        User re = null;
        String urlStr = MainActivity.site + "/php/user_info.php";
        CasterRequest req = new CasterRequest(urlStr);
        req.addParam("q","USR_JSN").addParam("id",""+userid);
        try {
            String res = (String)req.execute().get();
            JSONObject obj = new JSONObject(res);
            re = makeFromJson(obj);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return re;
    }

    public static User makeFromUsername(String username){
        HashMap<Integer, Object> data = new HashMap<>();
        User re = null;
        String urlStr = MainActivity.site + "/php/user_info.php";
        CasterRequest req = new CasterRequest(urlStr);
        req.addParam("q", "UNAMETOUID").addParam("id", username);
        try{
            String res = (String)req.execute().get();
            if (res != null){
                return makeFromID(Integer.parseInt(res.trim()));
            }
            else{
                return re;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return  re;
    }

    public static User makeFromJson(JSONObject jsonObject){
        HashMap<Integer,Object> data = new HashMap<>();
        User re = null;
        try {
            data.put(IMAGE,jsonObject.getString("picture"));
            data.put(DESCRIPTION,jsonObject.getString("description"));
            data.put(ID,jsonObject.getInt("user_id"));
            data.put(USERNAME,jsonObject.getString("username"));
            data.put(SUBSCRIPTIONS,jsonObject.getString("subscriptions"));
            re = new User(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return re;
    }

    public User(HashMap<Integer, Object> metadata){
        this.metadata = metadata;
        Bin.users.put(getId(),this);
    }

    public ArrayList<Podcast> getPodcasts(){
        if (podcasts == null){
            podcasts = new ArrayList<>();
            CasterRequest req = new CasterRequest(MainActivity.site + "/php/podcast.php");
            req.addParam("q","BY_USR").addParam("id",this.getId()+"");
            try {
                String result = (String)req.execute().get();
                JSONArray array = new JSONArray(result);
                for (int i = 0;i < array.length();i++){
                    JSONObject obj = array.getJSONObject(i);
                    podcasts.add(Podcast.makeFromJson(obj));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return podcasts;
    }

    public String getUsername(){
        return (String)this.metadata.get(USERNAME);
    }

    public Bitmap getImage(){
        if (image == null){
            String imgName = (String) this.metadata.get(IMAGE);
            if (imgName == null || imgName.trim().isEmpty()){
                image = BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                        R.drawable.default_profile);
            }else{
                String url = MainActivity.site + "/users/"+getId()+"/images/"+imgName;
                try {
                    image = Bin.getBitmap(url);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return image;
    }

    public String getDescription(){
        return (String)this.metadata.get(DESCRIPTION);
    }

    public int getSubscriberCount(){
        return (int)this.metadata.get(SUBSCRIBER_COUNT);
    }

    public int getId(){
        return (int)this.metadata.get(ID);
    }

    public HashMap<Integer, Object> getMetadata(){
        return metadata;
    }

    public int[] getSubscriptions(){
        String[] reS = ((String)metadata.get(SUBSCRIPTIONS)).split(".");
        int[] re = new int[reS.length];
        for (int i = 0; i < reS.length;i++){
            re[i] = Integer.parseInt(reS[i]);
        }
        return re;
    }

    public boolean isSubscribed(int userid){
        return ((String)metadata.get(SUBSCRIPTIONS)).contains(userid + ".");
    }

}
