package com.caster.caster_android.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;

import com.caster.caster_android.CasterRequest;
import com.caster.caster_android.MainActivity;
import com.caster.caster_android.Podcast;
import com.caster.caster_android.R;
import com.caster.caster_android.utils.Bin;
import com.caster.caster_android.views.PodcastBox;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by Nick on 2015-12-26.
 */
public class MainFragment extends Fragment implements Runnable, NoConnectionFragment.Callback{

    private ArrayList<Podcast> recents;
    NoConnectionFragment noConnectionFragment;

    public MainFragment(){
        Log.v("CASTER_DEBUG","Starting Main...");
        boolean connection = Bin.isConnected();
        if(MainActivity.instance != null && Bin.checkConnection(MainActivity.instance.getApplicationContext()))
            connection = true;
        if (connection){
            new Thread(this).start();
        }else{
            noConnectionFragment = new NoConnectionFragment();
            noConnectionFragment.callback = this;
            MainActivity.instance.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, noConnectionFragment).commit();
            Log.v("CASTER_DEBUG", "not connected start");
        }
    }

    public void run(){
        Log.v("CASTER_DEBUG","Running MainFragment");
        recents = new ArrayList<>();
        CasterRequest req = new CasterRequest(MainActivity.site + "/php/search_podcasts.php");
        req.addParam("t", "RUJSON");
        try {
            String res = (String)req.execute().get();
            if(res == null){
                return;
            }
            JSONArray jsonArray = new JSONArray(res);
            for(int i = 0;i < jsonArray.length();i++){
                JSONObject obj = jsonArray.getJSONObject(i);
                recents.add(Podcast.makeFromJson(obj));
                /*GridLayout.LayoutParams param = new GridLayout.LayoutParams();
                param.height = GridLayout.LayoutParams.WRAP_CONTENT;
                param.width = GridLayout.LayoutParams.WRAP_CONTENT;
                param.rowSpec = GridLayout.spec(0);
                param.columnSpec = GridLayout.spec(0);
                podcastBox.setLayoutParams(param);*/
            }
            MainActivity.instance.getSupportFragmentManager()
                    .beginTransaction().replace(R.id.content_frame,this).commit();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View re =  inflater.inflate(R.layout.fragment_main,container,false);
        getRecents(re);
        return re;
    }

    private void getRecents(View view){
        GridLayout gl = (GridLayout) view.findViewById(R.id.recent_uploads);
        for(int i = 0;i < recents.size();i++) {
            PodcastBox podcastBox = new PodcastBox(view.getContext(), null, recents.get(i));
            gl.addView(podcastBox);
        }
    }


    @Override
    public void onRetryConnection() {
        if (Bin.checkConnection(MainActivity.instance.getApplicationContext())){
            new Thread(this).start();
        }else
            Log.v("CASTER_DEBUG", "not connected");
    }
}
