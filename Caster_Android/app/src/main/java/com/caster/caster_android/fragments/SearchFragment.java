package com.caster.caster_android.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.caster.caster_android.CasterRequest;
import com.caster.caster_android.MainActivity;
import com.caster.caster_android.Podcast;
import com.caster.caster_android.PodcastPlayer;
import com.caster.caster_android.R;
import com.caster.caster_android.utils.PodcastListAdapter;
import com.caster.caster_android.views.SearchResults;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by Nick on 2015-12-28.
 */
public class SearchFragment extends Fragment implements Runnable{

    ArrayList<Podcast> results;
    GridLayout podcastBar;
    android.support.v4.app.FragmentManager manager;
    String query;

    public void init(String query, android.support.v4.app.FragmentManager manager){
        this.query = query;
        this.manager = manager;
        new Thread(this).start();
    }

    public void run(){
        if(query == null)return;
        results = new ArrayList<>();
        CasterRequest req = new CasterRequest(MainActivity.site + "/php/search_podcasts.php");
        req.addParam("m", "MOBI").addParam("mq", query);
        try{
            String res = (String)req.execute().get();
            if(res == null){
                return;
            }
            Log.d("Search Results result: ", res);
            if (res.contains("No results found")){
                Toast.makeText(getActivity().getApplicationContext(), "No Results Found :(", Toast.LENGTH_SHORT);
            }else {
                JSONArray jsonArray = new JSONArray(res);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    results.add(Podcast.makeFromJson(obj));
                }
            }
            manager.beginTransaction().replace(R.id.content_frame, this).commit();
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
        View re = inflater.inflate(R.layout.fragment_search,container,false);
        newSearch(re);

        //TODO: Change the podcast bars in the activity to the actual PodcastBar class
        podcastBar = (GridLayout)getActivity().findViewById(R.id.podcast_bar);
        ListView listview = ((ListView)re.findViewById(R.id.search_results_listview));
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Open profile view, or player view and play podcast
                podcastBar.setVisibility(View.VISIBLE);
                PodcastPlayer.podcast = results.toArray(new Podcast[results.size()])[position];
                ((SearchResults)getActivity()).updatePlayerBar();
                ((SearchResults)getActivity()).openPlayer(view);
            }
        });
        return re;
    }

    public void newSearch(View view){
        if(query == null)return;
        if(view == null)view = getView();
        if (results.isEmpty()){
            Toast.makeText(getActivity().getApplicationContext(), "No Results Found :(", Toast.LENGTH_SHORT);
        }
        //Pass array into listview adapter
        PodcastListAdapter adapter = new PodcastListAdapter(getActivity().getApplicationContext(), R.layout.search_results_listview_item_row, results.toArray(new Podcast[results.size()]));
        ListView lv = ((ListView)view.findViewById(R.id.search_results_listview));
        lv.setAdapter(adapter);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Results found for: " + query);
    }
}
