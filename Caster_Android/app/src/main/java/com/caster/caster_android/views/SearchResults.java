package com.caster.caster_android.views;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return false;
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
            JSONArray jsonArray = new JSONArray(res);
            for(int i = 0; i< jsonArray.length(); i++){
                JSONObject obj = jsonArray.getJSONObject(i);
                results.add(Podcast.makeFromJson(obj));
            }
            //Pass array into listview adapter
            PodcastListAdapter adapter = new PodcastListAdapter(this, R.layout.search_results_listview_item_row, (Podcast[])results.toArray());
            ListView lv = ((ListView)findViewById(R.id.search_results_listview));
            lv.setAdapter(adapter);
            ((TextView) findViewById(R.id.txtSearchResults)).setText("Results found for: " + query);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
