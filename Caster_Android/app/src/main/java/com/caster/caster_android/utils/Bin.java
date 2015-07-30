package com.caster.caster_android.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.caster.caster_android.CasterRequest;
import com.caster.caster_android.MainActivity;
import com.caster.caster_android.Podcast;
import com.caster.caster_android.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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

    public static void init(){
        if (users == null){
            users = new HashMap<>();
            podcasts = new HashMap<>();
        }
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
                if (podcastToken.contains("Error")){
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
}
