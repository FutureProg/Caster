package com.caster.caster_android;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

import com.caster.caster_android.fragments.DownloadsFragment;
import com.caster.caster_android.fragments.MainFragment;
import com.caster.caster_android.fragments.NoConnectionFragment;
import com.caster.caster_android.fragments.ProgressFragment;
import com.caster.caster_android.utils.Bin;
import com.caster.caster_android.utils.PodcastDownloader;
import com.caster.caster_android.views.SearchResults;

import java.util.Stack;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, PodcastDownloader.OnChangeListener,
        NoConnectionFragment.Callback{
    public final static String site = "http://ec2-52-35-70-147.us-west-2.compute.amazonaws.com";
    public static MainActivity instance;

    private GridLayout podcastBar;
    Stack<FragmentItem> fragmentItemStack;
    Fragment currentFragment;
    int current_nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); //set the toolbar as the actionbar

        DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar, R.string.drawer_open,R.string.drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState(); //setup the navigation drawer and sync the icon with the drawer state

        NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this); //set the MainActivity to the Item Listener

        fragmentItemStack = new Stack<>();
        podcastBar = (GridLayout)findViewById(R.id.podcast_bar);
        Bin.init(this.getBaseContext()); //initialize the application
        if(findViewById(R.id.content_frame) != null){
            if(savedInstanceState == null){
                ProgressFragment progressFragment = new ProgressFragment();
                getSupportFragmentManager().beginTransaction().add(R.id.content_frame,progressFragment)
                        .commit(); //show the loading circle while the MainFragment does its thing
                MainFragment mainFragment = new MainFragment();
                mainFragment.setArguments(getIntent().getExtras());
                currentFragment = progressFragment;
                current_nav = R.id.nav_home;
            }
        }
        updatePlayerBar();

        //final ActionBar actionBar = getActionBar();
        //actionBar.setDisplayShowTitleEnabled(false);
        //actionBar.setDisplayShowCustomEnabled(true);
        //actionBar.setDisplayShowHomeEnabled(false);

        //actionBar.setCustomView(R.layout.actionbar_layout);
        //actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE );
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
        //setupNavDrawer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePlayerBar();
    }


    /**
     * Updates the playerbar, making it visible if a podcast is playing
     */
    public void updatePlayerBar(){
        if(currentFragment == null)return;
        if (PodcastPlayer.podcast == null){
            podcastBar.setVisibility(View.INVISIBLE);
            View view = currentFragment.getView();
            if(view != null) view.setPadding(view.getPaddingLeft(), view.getPaddingTop(),
                    view.getPaddingRight(), 0);
        }else{
            ((Button)podcastBar.findViewById(R.id.podcast_bar_img)).setBackground(
                    new BitmapDrawable(getResources(), PodcastPlayer.podcast.getCoverPhoto()));
            podcastBar.setVisibility(View.VISIBLE);
            int height = podcastBar.getHeight();
            View view = currentFragment.getView();
            if(view != null) view.setPadding(view.getPaddingLeft(),view.getPaddingTop(),
                    view.getPaddingRight(),height);
        }
    }
    /*
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
*/

    /**
     * Opens up the Podcast Player Activity
     * @param view
     */
    public void openPlayer(View view){
        Intent intent = new Intent(this,PodcastPlayer.class);
        startActivity(intent);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
    }

    /**
     * Manages the menu items which presently only includes search.
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        getMenuInflater().inflate(R.menu.menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        Log.v("Caster_DEBUG","create options menu: " + searchView);

        if (null != searchView) {
            searchView.setSearchableInfo(searchManager
                    .getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(false);
            SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
                public boolean onQueryTextChange(String newText) {
                    // this is your adapter that will be filtered
                    return true;
                }

                public boolean onQueryTextSubmit(String query) {
                    //Create intent to start podcast list activity
                    //From there you can see the results of your search and pick one to listen to
                    //Toast.makeText(MainActivity.this, query,Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getApplicationContext(), SearchResults.class);
                    i.putExtra("query", query);
                    startActivity(i);
                    return true;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
        }



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

        if (id == R.id.action_search){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Do stuff on navigation
     * @param menuItem
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        FragmentManager manager = getSupportFragmentManager();
        if (id == R.id.nav_downloads){  //If the navigation item is the download
            DownloadsFragment fragment = new DownloadsFragment();
            manager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();
            FragmentItem item = new FragmentItem();
            item.fragment = currentFragment;
            item.id = current_nav;
            fragmentItemStack.push(item);
            currentFragment = fragment;
            current_nav = R.id.nav_downloads;
        }
        else if(id == R.id.nav_home){ //if it's the home icon
            ProgressFragment progressFragment = new ProgressFragment();
            manager.beginTransaction().replace(R.id.content_frame, progressFragment).commit();
            MainFragment fragment = new MainFragment();
            FragmentItem item = new FragmentItem();
            item.fragment = currentFragment;
            item.id = current_nav;
            fragmentItemStack.push(item);
            currentFragment = progressFragment;
            current_nav = R.id.nav_home;
        }
        DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * No longer needed really...just for testing stuff
     * @param progress
     * @param podcast_id
     * @param state
     */
    @Override
    public void onDownloadStateChange(int progress, int podcast_id, PodcastDownloader.State state) {
        if (state == PodcastDownloader.State.QUEUED){
            Toast.makeText(getApplicationContext(), "Queued", Toast.LENGTH_SHORT).show();
        }
        else if(state == PodcastDownloader.State.DOWNLOADING){
            //Toast.makeText(getApplicationContext(),"Downloading " + progress + "%",Toast.LENGTH_SHORT).show();
            Log.v("CASTER_TEST","Downloading " + progress + "%");
            View view = findViewById(R.id.featured_view);
            view.setBackgroundColor(progress);
        }
        else if(state == PodcastDownloader.State.FINISHED){
            Toast.makeText(getApplicationContext(),"Finished",Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Override back button behaviour. Keeping track of the pervious fragments loaded
     */
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0){
            FragmentItem item = fragmentItemStack.pop();
            if (item.id == R.id.nav_home){
                super.onBackPressed();
                return;
            }
            current_nav = item.id;
            currentFragment = item.fragment;
            getSupportFragmentManager().popBackStack();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onRetryConnection() {

    }

    /**
     * Used to store a reference to the fragment in a fragment stack
     */
    class FragmentItem{
        public Fragment fragment;
        public int id;
    }

}
