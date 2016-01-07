package com.caster.caster_android.views;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;

import com.caster.caster_android.MainActivity;
import com.caster.caster_android.Podcast;
import com.caster.caster_android.PodcastPlayer;
import com.caster.caster_android.R;
import com.caster.caster_android.fragments.ProgressFragment;
import com.caster.caster_android.fragments.SearchFragment;

import java.util.ArrayList;

/**
 * Created by Teo on 8/9/2015.
 */
public class SearchResults extends AppCompatActivity {
    private ArrayList<Podcast> results;
    private String query;
    private GridLayout podcastBar;
    Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_results_layout);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Bundle search_query = getIntent().getExtras();

        if (search_query != null){
            query = search_query.getString("query");
            newSearch();
        }
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        podcastBar = (GridLayout)findViewById(R.id.podcast_bar);

    }

    public void updatePlayerBar(){
        if (PodcastPlayer.podcast == null){
            podcastBar.setVisibility(View.INVISIBLE);

            View view = currentFragment.getView();
            if(view != null)
                view.setPadding(view.getPaddingLeft(), view.getPaddingTop(),
                        view.getPaddingRight(), 0);
        }else{
            Log.v("Caster_DEBUG","Showing search podcast bar");
            ((Button)podcastBar.findViewById(R.id.podcast_bar_img)).setBackground(
                    new BitmapDrawable(getResources(), PodcastPlayer.podcast.getCoverPhoto()));
            podcastBar.setVisibility(View.VISIBLE);
            int height = podcastBar.getHeight();
            View view = currentFragment.getView();
            if(view != null)
                view.setPadding(view.getPaddingLeft(), view.getPaddingTop(),
                        view.getPaddingRight(), height);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePlayerBar();
    }

    public void share(View v){
        Integer position = (Integer)v.findViewById(R.id.imgSearchResultsItemShare).getTag();
        Podcast toshare = results.toArray(new Podcast[results.size()])[position];
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        share.putExtra(Intent.EXTRA_SUBJECT, toshare.getTitle());
        //Change description to direct link to podcast when available
        String text = "Check out this podcast!\n";
        text += MainActivity.site + "/" + toshare.getCreator().getUsername() + "/" + toshare.getUrlid();
        share.putExtra(Intent.EXTRA_TEXT, text);
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

    public void openPlayer(View view){
        Intent intent = new Intent(this,PodcastPlayer.class);
        startActivity(intent);
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
        currentFragment = new ProgressFragment();
        getSupportFragmentManager().beginTransaction().
                replace(R.id.content_frame,currentFragment).commit();
        SearchFragment fragment = new SearchFragment();
        fragment.init(query,getSupportFragmentManager());
        /*results = new ArrayList<>();
        CasterRequest req = new CasterRequest(MainActivity.site + "/php/search_podcasts.php");
        req.addParam("m", "MOBI").addParam("mq", query);
        try{
            String res = (String)req.execute().get();
            if(res == null){
                return;
            }
            Log.d("Search Results result: ", res);
            if (res.contains("No results found")){
                Toast.makeText(getApplicationContext(), "No Results Found :(", Toast.LENGTH_SHORT);
            }else {
                JSONArray jsonArray = new JSONArray(res);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    results.add(Podcast.makeFromJson(obj));
                }
            }
            //Pass array into listview adapter
            PodcastListAdapter adapter = new PodcastListAdapter(this, R.layout.search_results_listview_item_row, results.toArray(new Podcast[results.size()]));
            ListView lv = ((ListView)findViewById(R.id.search_results_listview));
            lv.setAdapter(adapter);
            getSupportActionBar().setTitle("Results found for: " + query);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }*/
    }
}
