package com.caster.caster_android;

import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.caster.caster_android.utils.Bin;
import com.caster.caster_android.views.PodcastBox;
import com.caster.caster_android.views.SignUpActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class MainActivity extends Activity {
    public final static String site = "http://istrat.ddns.net";
    public static MainActivity instance;

    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private GridLayout podcastBar;

    private ArrayList<Podcast> recents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);
        instance = this;
        podcastBar = (GridLayout)findViewById(R.id.podcast_bar);
        Bin.init();
        updatePlayerBar();
        final ActionBar actionBar = getActionBar();
        //actionBar.setDisplayShowTitleEnabled(false);
        //actionBar.setDisplayShowCustomEnabled(true);
        //actionBar.setDisplayShowHomeEnabled(false);

        //actionBar.setCustomView(R.layout.actionbar_layout);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE );
        /*actionBar.setHomeButtonEnabled(true);

        NavigationView navview = (NavigationView)findViewById(R.id.navigation_view);
        navview.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                Snackbar.make(drawerLayout, menuItem.getTitle() + " pressed", Snackbar.LENGTH_LONG).show();
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                return true;
            }
        });*/
        setupNavDrawer();
        getRecents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePlayerBar();
    }

    public void updatePlayerBar(){
        if (PodcastPlayer.podcast == null){
            podcastBar.setVisibility(View.INVISIBLE);
        }else{
            ((Button)podcastBar.findViewById(R.id.podcast_bar_img)).setBackground(
                    new BitmapDrawable(getResources(),PodcastPlayer.podcast.getCoverPhoto()));
            podcastBar.setVisibility(View.VISIBLE);
        }
    }

    private void getRecents(){
        recents = new ArrayList<>();
        CasterRequest req = new CasterRequest(site + "/php/search_podcasts.php");
        req.addParam("t","RUJSON");
        try {
            String res = (String)req.execute().get();
            if(res == null){
                return;
            }
            JSONArray jsonArray = new JSONArray(res);
            GridLayout gl = (GridLayout) findViewById(R.id.recent_uploads);
            for(int i = 0;i < jsonArray.length();i++){
                JSONObject obj = jsonArray.getJSONObject(i);
                recents.add(Podcast.makeFromJson(obj));
                PodcastBox podcastBox = new PodcastBox(this,null,recents.get(i));
                /*GridLayout.LayoutParams param = new GridLayout.LayoutParams();
                param.height = GridLayout.LayoutParams.WRAP_CONTENT;
                param.width = GridLayout.LayoutParams.WRAP_CONTENT;
                param.rowSpec = GridLayout.spec(0);
                param.columnSpec = GridLayout.spec(0);
                podcastBox.setLayoutParams(param);*/
                gl.addView(podcastBox);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void setupNavDrawer(){
        final ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);
        if (Bin.getSignedInUser() != null){
            final User user = Bin.getSignedInUser();
            String[] navArray = {user.getUsername(),"Sign Out"};
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,navArray);
            drawerList.setAdapter(adapter);
            drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0){
                        ProfileActivity.user = user;
                        Intent intent = new Intent(MainActivity.instance,ProfileActivity.class);
                        startActivity(intent);
                        drawerLayout.closeDrawers();
                    }else{
                        Bin.signOut();
                        onCreate(null);
                    }
                }
            });
        }else{
            String[] navArray = { "Sign In","Sign Up"};
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, navArray);
            drawerList.setAdapter(adapter);
            drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0){
                        Intent intent = new Intent(MainActivity.instance,SignInActivity.class);
                        startActivity(intent);
                        drawerLayout.closeDrawers();
                    }else {
                        //Toast.makeText(MainActivity.this, (String) parent.getAdapter().getItem(position),
                        //        Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.instance, SignUpActivity.class);
                        startActivity(intent);
                        drawerLayout.closeDrawers();
                    }
                }
            });
        }


        drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.drawer_open,R.string.drawer_close){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // code here will execute once the drawer is opened( As I dont want anything happened whe drawer is
                // open I am not going to put anything here)
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Code here will execute once drawer is closed
            }



        };
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.setDrawerListener(drawerToggle);

        int upid = getResources().getIdentifier("android:id/up",null,null);
        if(upid != 0){
            View view = getWindow().getDecorView().findViewById(upid);
            if(view instanceof ImageView){
                ImageView imgView = (ImageView)view;
                imgView.setColorFilter(Color.rgb(255,255,255));
            }
        }
    }

    public void openPlayer(View view){
        Intent intent = new Intent(this,PodcastPlayer.class);
        startActivity(intent);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
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

        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {
                // this is your adapter that will be filtered
                return true;
            }

            public boolean onQueryTextSubmit(String query) {
                //Create intent to start podcast list activity
                //From there you can see the results of your search and pick one to listen to
                Toast.makeText(MainActivity.this, query,Toast.LENGTH_SHORT).show();
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

        if(drawerToggle.onOptionsItemSelected(item)){
            return true;
        }

        if (id == android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_search){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
