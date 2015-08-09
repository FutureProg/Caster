package com.caster.caster_android.views;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.caster.caster_android.CasterRequest;
import com.caster.caster_android.MainActivity;
import com.caster.caster_android.Podcast;
import com.caster.caster_android.R;
import com.caster.caster_android.utils.PodcastListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Teo on 8/9/2015.
 */
public class SearchResults extends Activity {
    private ArrayList<Podcast> results;
    private String query;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_results_layout);
        Bundle search_query = getIntent().getExtras();
        if (search_query != null){
            query = search_query.getString("query");
            newSearch();
        }
        getActionBar().setDisplayShowTitleEnabled(true);

        //Open Podcast on item click
        ListView listview = ((ListView)findViewById(R.id.search_results_listview));
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Open profile view, or player view and play podcast

            }
        });

    }

    public void share(View v){
        Integer position = (Integer)v.findViewById(R.id.imgSearchResultsItemShare).getTag();
        Podcast toshare = results.toArray(new Podcast[results.size()])[position];
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        share.putExtra(Intent.EXTRA_SUBJECT, toshare.getTitle());
        //Change description to direct link to podcast when available
        share.putExtra(Intent.EXTRA_TEXT, toshare.getDescription());
        startActivity(Intent.createChooser(share, "Share link"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        getMenuInflater().inflate(R.menu.menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        if (null != searchView) {
            searchView.setSearchableInfo(searchManager
                    .getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(false);
        }

        final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {
                // this is your adapter that will be filtered
                return true;
            }

            public boolean onQueryTextSubmit(String toSearch) {
                //Create intent to start podcast list activity
                //From there you can see the results of your search and pick one to listen to
                //Toast.makeText(MainActivity.this, query,Toast.LENGTH_SHORT).show();
                query = toSearch;
                newSearch();
                return true;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);


        //return true;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void newSearch(){
        results = new ArrayList<>();
        CasterRequest req = new CasterRequest(MainActivity.site + "/php/search_podcasts.php");
        req.addParam("m", "MOBI").addParam("mq", query);
        try{
            String res = (String)req.execute().get();
            if(res == null){
                return;
            }
            Log.d("Search Results result: ", res);
            JSONArray jsonArray = new JSONArray(res);
            for(int i = 0; i< jsonArray.length(); i++){
                JSONObject obj = jsonArray.getJSONObject(i);
                results.add(Podcast.makeFromJson(obj));
            }
            //Pass array into listview adapter
            PodcastListAdapter adapter = new PodcastListAdapter(this, R.layout.search_results_listview_item_row, results.toArray(new Podcast[results.size()]));
            ListView lv = ((ListView)findViewById(R.id.search_results_listview));
            lv.setAdapter(adapter);
            getActionBar().setTitle("Results found for: " + query);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
