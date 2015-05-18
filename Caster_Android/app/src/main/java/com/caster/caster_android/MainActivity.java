package com.caster.caster_android;

import android.app.ActionBar;
import android.app.Activity;
<<<<<<< HEAD
import android.content.Intent;
=======
import android.content.res.Configuration;
import android.graphics.Color;
>>>>>>> f396947de4351efd890bd11c68e14cfa156038e1
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;


public class MainActivity extends Activity {
    public static String host = "jdbc:mysql://192.168.2.155:8000/caster_db?autoReconnect=true";
    public static MainActivity instance;

    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private ListView drawerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);
        instance = this;

        final ActionBar actionBar = getActionBar();
        //actionBar.setDisplayShowTitleEnabled(false);
        //actionBar.setDisplayShowCustomEnabled(true);
        //actionBar.setDisplayShowHomeEnabled(false);

        //actionBar.setCustomView(R.layout.actionbar_layout);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP|ActionBar.DISPLAY_SHOW_TITLE|
                                    ActionBar.DISPLAY_SHOW_HOME|ActionBar.DISPLAY_USE_LOGO);


        setupNavDrawer();

        /*
        // Testing backend stuff
        User user = User.makeFromID(24);
        Podcast podcast = Podcast.makeFromID(3);
        String str = user.getUsername() + ":" + user.getDescription() + "\n"+
                podcast.getTitle() +":"+ podcast.getDescription();
        ((TextView) findViewById(R.id.test_text_view)).setText(str);*/
    }

    private void setupNavDrawer(){
        final ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);
        String[] navArray = { "Sign in","Sign Up"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, navArray);
        drawerList.setAdapter(adapter);
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, (String) parent.getAdapter().getItem(position),
                        Toast.LENGTH_SHORT).show();
            }
        });

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
        return true;
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
