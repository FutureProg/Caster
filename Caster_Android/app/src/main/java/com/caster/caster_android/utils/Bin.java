package com.caster.caster_android.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.caster.caster_android.CasterRequest;
import com.caster.caster_android.MainActivity;
import com.caster.caster_android.Podcast;
import com.caster.caster_android.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by Nick on 15-05-31.
 */
public class Bin {


    public static HashMap<Integer,User> users;
    public static HashMap<Integer,Podcast> podcasts;
    private static String podcastToken = null;
    private static User signedInUser = null;
    private static boolean networkConnection;

    public static void init(Context context){
        if (users == null){
            users = new HashMap<>();
            podcasts = new HashMap<>();
        }
        checkConnection(context);
        PodcastDownloader.getDownloader(context);
    }

    /**
     * Uses the Connectivity Manager to check whether the application is connected to the internet
     * @param context
     * @return true if connected
     */
    public static boolean checkConnection(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        networkConnection = info != null && info.isConnected();
        return networkConnection;
    }

    /**
     *
     * @return whether the device was connected to the internet during the last call to checkConnection
     */
    public static boolean isConnected(){
        return networkConnection;
    }

    public static Bitmap getBitmap(String url) throws ExecutionException, InterruptedException {
        return new ResourceGet().execute(url).get();
    }

    public static class ResourceGet extends AsyncTask<String,Void,Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            InputStream in = null;
            try {

                in = (InputStream) new URL(params[0]).openStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return BitmapFactory.decodeStream(in);
        }
    }

    public static String getPodcastToken(){
        if (podcastToken == null || podcastToken.isEmpty()){
            CasterRequest req = new CasterRequest(MainActivity.site + "/php/token.php");
            req.addParam("q","TKN").addParam("t","PDCST");
            try {
                podcastToken = (String) req.execute().get();
                if (podcastToken == null || podcastToken.contains("Error")){
                    podcastToken = null;
                    return null;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        String re = podcastToken;
        podcastToken = null;
        return re;
    }

    public static User getSignedInUser(){
        return signedInUser;
    }

    public static User signIn(String email, String password) throws ExecutionException, InterruptedException {
        signedInUser = null;
        CasterRequest req = new CasterRequest(MainActivity.site + "/php/signin.php");
        req.addParam("e",email).addParam("p",password).addParam("t","mobi");
        String str = (String) req.execute().get();
        Log.v("caster LOGIN",str);
        if (str != null && !str.isEmpty()){
            try {
                signedInUser = User.makeFromJson(new JSONObject(str));
            } catch (JSONException e) {
            }
        }
        return signedInUser;
    }

    public static void signOut(){
        signedInUser = null;
    }

    public static User signUp(String username, String email, String password) throws ExecutionException, InterruptedException {
        signedInUser = null;
        CasterRequest req = new CasterRequest(MainActivity.site + "/php/register.php");
        req.addParam("username", username).addParam("email", email).addParam("password", password).addParam("q", "SIGNUP");
        String str = (String) req.execute().get();
        Log.v("caster Created User", str);
        if (str != null && !str.isEmpty()){
            signedInUser = signIn(email, password);
        }
        else{
            Log.d("Bin", "There was an error creating the new user");
        }
        return signedInUser;
    }

    public static boolean subscribe(int subscribeID) throws  ExecutionException, InterruptedException {
        CasterRequest req = new CasterRequest(MainActivity.site + "/php/subscription.php");
        try {
            req.addParam("q", "MOBI").addParam("t", "SUB").addParam("u", Integer.toString(signedInUser.getId())).addParam("s", Integer.toString(subscribeID));
        }
        catch (Exception e){
            return false;
        }
        String str = (String) req.execute().get();
        Log.v("user subscribed", str);
        if (str != null && !str.isEmpty()){
            return true;
        }
        else{
            return false;
        }
    }

    public static boolean unsubscribe(int subscribeID) throws ExecutionException, InterruptedException {
        CasterRequest req = new CasterRequest(MainActivity.site + "/php/subscription.php");
        req.addParam("q", "MOBI").addParam("t", "SUB").addParam("u", Integer.toString(signedInUser.getId())).addParam("s", Integer.toString(subscribeID));
        String str = (String) req.execute().get();
        Log.v("user unsubscribed", str);
        if (str != null && !str.isEmpty()){
            return true;
        }
        else{
            return false;
        }
    }

    public static Podcast getMostRecentPodcast(Integer user_id){
        CasterRequest req = new CasterRequest(MainActivity.site + "/php/podcast.php");
        req.addParam("q", "BY_USR").addParam("id", "" +user_id);
        ArrayList<Podcast> recents = new ArrayList<>();
        try{
            String res = (String)req.execute().get();
            JSONArray jsonArray = new JSONArray(res);
            for(int i = 0; i< jsonArray.length(); i++){
                JSONObject obj = jsonArray.getJSONObject(i);
                recents.add(Podcast.makeFromJson(obj));
            }
            return recents.get(0);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<Podcast> getUsersOfflinePodcasts(int user_id){
        ArrayList<Podcast> re = new ArrayList<>();
        for (int key: podcasts.keySet()){
            Podcast p = podcasts.get(key);
            if (p.getCreatorId() == user_id){
                re.add(p);
            }
        }
        Collections.reverse(re);
        return re;
    }
}
