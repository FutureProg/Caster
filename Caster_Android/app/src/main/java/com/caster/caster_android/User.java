package com.caster.caster_android;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.caster.caster_android.utils.Bin;
import com.caster.caster_android.utils.PodcastDownloader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
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

    public static final String KEY_APP_USER = "com.caste.caster_android.User.APP_USER";

    public static final int USERNAME = 0, DESCRIPTION = 1, SUBSCRIBER_COUNT = 2,ID=3, IMAGE = 4,
        SUBSCRIPTIONS = 5,EDIT_STAMP = 6;

    HashMap<Integer, Object> metadata; //username, description,subscriber count,id
    ArrayList<Podcast> podcasts;
    Bitmap image;

    /***
     *  Retrieve the user's metadata from the server
     * @param userid userid to process
     * @return an instance of the user
     */
    public static User makeFromID(int userid){
        if (userid < 0)return null;
        if (Bin.users.containsKey(userid)){
            return Bin.users.get(userid);
        }
        if (PodcastDownloader.getDownloader(null).isUserDownloaded(userid)){
            return loadDownloadedUser(userid);
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
            String date_str = jsonObject.getString("edit_date");
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = df.parse(date_str);
            data.put(EDIT_STAMP,date);
            re = new User(data);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return re;
    }

    private static User loadDownloadedUser(int user_id){
        User re = null;

        String basePath = MainActivity.instance.getFilesDir().getAbsolutePath() + "/user_" + user_id;
        String metapath = basePath + "/metadata";
        String photopath = basePath + "/profile_picture";
        HashMap<Integer,Object> data = new HashMap<>();
        File file = new File(metapath);
        try {
            Scanner input = new Scanner(file);
            while (input.hasNextLine()){
                int key = Integer.parseInt(input.nextLine());
                String value = input.nextLine();
                switch (key){
                    case SUBSCRIBER_COUNT:
                    case ID:
                        data.put(key,Integer.parseInt(value));
                        break;
                    default:
                        data.put(key,value);
                        break;
                }
            }
            Bitmap bitmap = BitmapFactory.decodeFile(photopath);
            re = new User(data);
            re.image = bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return re;
    }

    public User(HashMap<Integer, Object> metadata){
        this.metadata = metadata;
        Bin.users.put(getId(),this);
    }

    public ArrayList<Podcast> getPodcasts(){
        if (!Bin.checkConnection(MainActivity.instance)){
            ArrayList<Podcast> tempPodcasts = new ArrayList<>();
            tempPodcasts = Bin.getUsersOfflinePodcasts(getId());
            return tempPodcasts;
        }
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
        String subs = (String)metadata.get(SUBSCRIPTIONS);
        if (subs.isEmpty()) return new int[0];
        if(subs.indexOf(".") == subs.lastIndexOf(".")){
            return new int[]{Integer.parseInt(subs.substring(0,subs.indexOf(".")))};
        }
        String[] reS = subs.split(".");
        int[] re = new int[reS.length];
        for (int i = 0; i < reS.length;i++){
            re[i] = Integer.parseInt(reS[i]);
        }
        return re;
    }

    public boolean isSubscribed(int userid){
        return ((String)metadata.get(SUBSCRIPTIONS)).contains(userid + ".");
    }

    public Date getEditDate(){
        return (Date)metadata.get(EDIT_STAMP);
    }

    public boolean needsUpdate(Context context){
        if(!Bin.checkConnection(context)){
            return false;
        }
        Date lastUpdate = getEditDate();
        CasterRequest req = new CasterRequest(MainActivity.site + "/php/user_info.php");
        try {
            String res = (String)req.addParam("q","USR_JSON").addParam("id",getId()+"").execute().get();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Log.v("Caster_User",res);
            if(res == null || res.isEmpty())return false;
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

}
