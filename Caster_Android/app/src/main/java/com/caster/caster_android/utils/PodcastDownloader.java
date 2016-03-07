package com.caster.caster_android.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Looper;
import android.util.Log;

import com.caster.caster_android.MainActivity;
import com.caster.caster_android.Podcast;
import com.caster.caster_android.User;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Queue;

/**
 * Created by Nick on 2016-01-03.
 *
 * Used to manage the downloading of Podcasts (as can be seen in the name). Get the global instance
 * of the object by calling the getDownloader(Context) method.
 */
public class PodcastDownloader implements Runnable {

    static PodcastDownloader instance;
    public enum State{
        DNE,
        QUEUED,
        DOWNLOADING,
        CANCELLED,
        FINISHED,
        METAONLY
    };
    Queue<Item> items;
    HashSet<Integer> toCancel;
    HashSet<Integer> downloadedUsers;
    HashMap<Integer,State> states;
    final Object lock;
    final Thread thread;
    Context context;
    boolean threadAlive;


    /**
     * Returns the running instance of the Podcast Downloader class
     * @param context
     * @return the running instance
     */
    public static PodcastDownloader getDownloader(Context context){
        if(instance == null){
            instance = new PodcastDownloader(context);
            for (int key : instance.states.keySet()){
                State state = instance.getState(key);
                if (state  == State.METAONLY){
                    Podcast.makeFromID(key);
                }
            }
        }
        return instance;
    }

    private PodcastDownloader(Context _context){
        this.context = _context;
        items = new ArrayDeque<>();
        lock = new Object();
        toCancel = new HashSet<>();
        states = new HashMap<>();
        downloadedUsers = new HashSet<>();
        loadDownloadLog();
        thread = new Thread(this);
    }


    public boolean isPodcastDownloaded(int podcast_id){
        return getState(podcast_id) == State.FINISHED;
    }

    public boolean isMetadataDownloaded(int podcast_id){
        return getState(podcast_id) == State.METAONLY;
    }

    public boolean isUserDownloaded(int user_id){return  downloadedUsers.contains(user_id);}

    /**
     * Cancel the download of the specified ID
     * @param podcast_id the podcast to cancel
     */
    public void cancelDownload(int podcast_id){
        Log.v("CASTER_DOWNLOAD","Cancel Download");
        toCancel.add(podcast_id);
    }

    /**
     * Returns an enumerator depicting whether the Podcast was downloaded, is being downloaded or not
     * @param podcast_id
     * @return the state of the podcast download (complete, downloading, cancelled, dne)
     */
    public State getState(int podcast_id) {
        if (!states.containsKey(podcast_id)) {
            return State.DNE;
        }
        return states.get(podcast_id);
    }

    /**
     * Get a list of the downloaded podcasts by their ids
     * @return a list of the downloaded podcasts
     */
    public ArrayList<Integer> getDownloaded(){
        ArrayList<Integer> list = new ArrayList<>();
        for (int podcast_id:states.keySet()) {
            if (states.get(podcast_id) == State.FINISHED){
                list.add(podcast_id);
            }
        }
        return list;
    }


    /**
     * Downloads the specified user for offline use
     * @param user_id
     */
    public void downloadUser(int user_id) throws IOException {
        String basePath = context.getFilesDir().getAbsolutePath() + "/user_" + user_id;
        String path = basePath + "/metadata";
        File file = new File(path);
        if (!file.exists()) {
            waitForNetwork();
            file.getParentFile().mkdirs();
            file.createNewFile();
            User creator = User.makeFromID(user_id);
            //Save the metadata
            FileOutputStream outputStream = new FileOutputStream(file);
            for (int key : creator.getMetadata().keySet()) {
                outputStream.write((key + "\n").getBytes());
                outputStream.write((creator.getMetadata().get(key) + "\n").getBytes());
            }
            outputStream.flush();
            outputStream.close();
            //Save the profile photo
            waitForNetwork();
            path = basePath + "/profile_picture";
            file = new File(path);
            file.createNewFile();
            outputStream = new FileOutputStream(file);
            creator.getImage().compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            downloadedUsers.add(creator.getId());
            waitForNetwork();
            ArrayList<Podcast> userPodcasts = creator.getPodcasts();
            for (Podcast _podcast : userPodcasts){

                waitForNetwork();
                basePath = context.getFilesDir().getAbsolutePath() + "/podcast_" + _podcast.getId();
                String metaPath = basePath + "/metadata";
                file= new File(metaPath);
                if (file.exists()) continue;
                file.getParentFile().mkdirs();
                file.createNewFile();
                outputStream = new FileOutputStream(file);
                for (int key: _podcast.getMetadata().keySet()) {
                    outputStream.write((key + "\n").getBytes());
                    outputStream.write((_podcast.getMetadata().get(key) + "\n").getBytes());
                }
                outputStream.flush();
                outputStream.close();

                waitForNetwork();
                String coverPath = basePath + "/cover_image";
                file = new File(coverPath);
                file.createNewFile();
                outputStream = new FileOutputStream(file);
                Bitmap coverimage = _podcast.getCoverPhoto();
                coverimage.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
                states.put(_podcast.getId(),State.METAONLY);
            }
        }
        downloadedUsers.add(user_id);
    }

    /**
     * Add a podcast to the download queue
     * @param podcast_id the podcast to be added
     * @param listener the object to call when progress is made on the download
     */
    public void queueDownload(int podcast_id, OnChangeListener listener){
        Item item = new Item();
        item.listener = listener;
        item.podcast_id = podcast_id;

            items.add(item);
            if (listener != null){
                listener.onDownloadStateChange(0,podcast_id,State.QUEUED);
                states.put(podcast_id,State.QUEUED);
            }

        if(!threadAlive){
            
            threadAlive = true;
            thread.start();
        }
    }

    /**
     * Delete the Podcast from the file system if it's downloaded
     * @param podcast_id
     * @return
     */
    public boolean deletePodcast(int podcast_id){
        if(states.containsKey(podcast_id) && states.get(podcast_id) == State.FINISHED){
            Log.v("CASTER_DOWNLOAD","Delete Podcast " + podcast_id);
            String basePath = context.getFilesDir().getAbsolutePath() + "/podcast_"+podcast_id;
            String audioPath = basePath + "/audio_file";
            boolean re =  new File(audioPath).delete();
            states.put(podcast_id, State.METAONLY);
            return re;
        }
        return true;
    }

    /**
     * Save a list of all the downloaded podcasts.
     * This is simpler than going through the file system and checking that way
     */
    public void saveDownloadLog(){
        Log.v("CASTER_SAVE","SAVE");
        try {

            FileOutputStream outputStream = context.openFileOutput("download_list_podcasts",Context.MODE_PRIVATE);
            FileOutputStream metaStream = context.openFileOutput("metaonly_list_podcasts",Context.MODE_PRIVATE);
            for (int podcast_id : states.keySet()) {
                Log.v("CASTER_SAVE", "try " + podcast_id);
                if(states.get(podcast_id) == State.FINISHED){
                    String line = podcast_id + "\n";
                    Log.v("CASTER_SAVE",podcast_id+"");
                    outputStream.write(line.getBytes());
                }
                else if(states.get(podcast_id) == State.METAONLY){
                    String line = podcast_id + "\n";
                    metaStream.write(line.getBytes());
                }
            }
            outputStream.flush();
            outputStream.close();
            outputStream = context.openFileOutput("download_list_users",Context.MODE_PRIVATE);
            for (int user_id : downloadedUsers) {
                String line = user_id+ "\n";
                Log.v("CASTER_SAVE","User: " + user_id);
                outputStream.write(line.getBytes());
            }
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load the list of downloaded podcasts
     */
    public void loadDownloadLog(){
        Log.v("CASTER_LOAD","LOAD");
        try {
            if (context == null)
                context = MainActivity.instance;
            BufferedReader inputStream = new BufferedReader(
                    new FileReader(context.getFileStreamPath("download_list_podcasts")));
            String line;
            while ((line = inputStream.readLine()) != null){
                Log.v("CASTER_LOAD",line);
                states.put(Integer.parseInt(line),State.FINISHED);
            }
            inputStream.close();

            inputStream = new BufferedReader(
                    new FileReader(context.getFileStreamPath("metaonly_list_podcasts")));
            while ((line = inputStream.readLine()) != null){
                Log.v("CASTER_LOAD", line);
                int id = Integer.parseInt(line);
                states.put(id, State.METAONLY);
            }
            inputStream.close();

            inputStream = new BufferedReader(
                    new FileReader(context.getFileStreamPath("download_list_users")));
            while ((line = inputStream.readLine()) != null){
                Log.v("CASTER_LOAD","USER " + line);
                downloadedUsers.add(Integer.parseInt(line));
            }
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void waitForNetwork(){
        // If there's no internet connection just wait for it
        while (!Bin.checkConnection(context)){
            try {
                thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * The run loop used to download the podcasts in a seperate thread
     */
    @Override
    public void run() {
        Looper.prepare();
        while(true) {
            while (items.size() > 0) {
                final Item item = items.poll();
                if (toCancel.contains(item.podcast_id)) {
                    states.put(item.podcast_id, State.CANCELLED);
                    MainActivity.instance.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (item.listener != null)
                                item.listener.onDownloadStateChange(0, item.podcast_id, State.CANCELLED);
                        }
                    });
                    continue;
                }
                states.put(item.podcast_id, State.DOWNLOADING);
                if (item.listener != null) {
                    MainActivity.instance.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            item.listener.onDownloadStateChange(0, item.podcast_id, State.DOWNLOADING);
                        }
                    });
                }
                waitForNetwork();
                final Podcast podcast = Podcast.makeFromID(item.podcast_id);
                Log.v("CASTER_DOWNLOAD", "Downloading " + podcast.getTitle());
                String url = MainActivity.site + "/php/audio_file.php?q=" + podcast.getId() + "$" + Bin.getPodcastToken();
                String basePath = context.getFilesDir().getAbsolutePath() + "/podcast_" + podcast.getId();
                String audioPath = basePath + "/audio_file";
                //Download the audio file
                try {
                    File file = new File(audioPath);
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                    OutputStream outputStream = new FileOutputStream(file);
                    URLConnection con = new URL(url).openConnection();
                    final int size = Integer.parseInt(con.getHeaderField("Content-Length"));
                    BufferedInputStream bis = new BufferedInputStream(con.getInputStream(), 1024);
                    byte[] buffer = new byte[1024];

                    int current = 0;
                    int count = current;
                    states.put(item.podcast_id, State.DOWNLOADING);
                    while ((current = bis.read(buffer)) != -1) {
                        waitForNetwork();
                        count += current;
                        //Log.v("CASTER_CURRENT","Count: " + count + " Size: " + size + " Ratio: " + ((float)count/size));
                        outputStream.write(buffer, 0, current);
                        final int c = count;
                        if (item.listener != null) {
                            MainActivity.instance.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    item.listener.onDownloadStateChange((int) ((float) c * 100 / (size + 10)), item.podcast_id, State.DOWNLOADING);
                                }
                            });
                        }
                        if (toCancel.contains(item.podcast_id)) {
                            toCancel.remove(item.podcast_id);
                            states.put(item.podcast_id, State.CANCELLED);
                            outputStream.flush();
                            outputStream.close();
                            context.deleteFile(context.getFilesDir().getAbsolutePath() + "/podcast_" +
                                    item.podcast_id + "/audio_file");
                            bis.close();
                            MainActivity.instance.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (item.listener != null)
                                        item.listener.onDownloadStateChange((int) ((float) c * 100 / (size+10)), item.podcast_id, State.CANCELLED);
                                }
                            });
                            continue;
                        }
                    }
                    outputStream.flush();
                    outputStream.close();
                    bis.close();

                    //Save the podcast metadata
                    String metaPath = basePath + "/metadata";
                    file= new File(metaPath);
                    file.createNewFile();
                    outputStream = new FileOutputStream(file);
                    for (int key: podcast.getMetadata().keySet()) {
                        outputStream.write((key + "\n").getBytes());
                        if(key == Podcast.EDIT_STAMP){
                            outputStream.write((podcast.getMetadata().get(key) + "\n").getBytes());
                            continue;
                        }
                        outputStream.write((podcast.getMetadata().get(key) + "\n").getBytes());
                    }
                    outputStream.flush();
                    outputStream.close();

                    //save the cover image
                    waitForNetwork();
                    String coverPath = basePath + "/cover_image";
                    file = new File(coverPath);
                    file.createNewFile();
                    outputStream = new FileOutputStream(file);
                    Bitmap coverimage = podcast.getCoverPhoto();
                    coverimage.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();

                    //save the user
                    basePath = context.getFilesDir().getAbsolutePath() + "/user_" + podcast.getCreatorId();
                    String path = basePath + "/metadata";
                    file = new File(path);
                    if (!file.exists() || (User.makeFromID(podcast.getCreatorId())).needsUpdate()){
                        waitForNetwork();
                        file.getParentFile().mkdirs();
                        file.createNewFile();
                        User creator = podcast.getCreator();
                        //Save the metadata
                        outputStream = new FileOutputStream(file);
                        for (int key: creator.getMetadata().keySet()){
                            outputStream.write((key + "\n").getBytes());
                            outputStream.write((creator.getMetadata().get(key) + "\n").getBytes());
                        }
                        outputStream.flush();
                        outputStream.close();
                        if (item.listener != null) {
                            MainActivity.instance.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    item.listener.onDownloadStateChange(99, item.podcast_id, State.DOWNLOADING);
                                }
                            });
                        }
                        //Save the profile photo
                        waitForNetwork();
                        path = basePath + "/profile_picture";
                        file = new File(path);
                        file.createNewFile();
                        outputStream = new FileOutputStream(file);
                        creator.getImage().compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                        outputStream.flush();
                        outputStream.close();
                        downloadedUsers.add(creator.getId());
                        //Save the metadata of the users and the images
                        waitForNetwork();
                        ArrayList<Podcast> userPodcasts = creator.getPodcasts();
                        for (Podcast _podcast : userPodcasts){
                            waitForNetwork();
                            if(!_podcast.needsUpdate())continue;
                            basePath = context.getFilesDir().getAbsolutePath() + "/podcast_" + _podcast.getId();
                            metaPath = basePath + "/metadata";
                            file= new File(metaPath);
                            if (file.exists()) continue;
                            file.getParentFile().mkdirs();
                            file.createNewFile();
                            outputStream = new FileOutputStream(file);
                            for (int key: _podcast.getMetadata().keySet()) {
                                outputStream.write((key + "\n").getBytes());
                                outputStream.write((_podcast.getMetadata().get(key) + "\n").getBytes());
                            }
                            outputStream.flush();
                            outputStream.close();

                            waitForNetwork();
                            coverPath = basePath + "/cover_image";
                            file = new File(coverPath);
                            file.createNewFile();
                            outputStream = new FileOutputStream(file);
                            coverimage = _podcast.getCoverPhoto();
                            coverimage.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                            outputStream.flush();
                            outputStream.close();
                            states.put(_podcast.getId(),State.METAONLY);
                        }
                        if (item.listener != null) {
                            MainActivity.instance.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    item.listener.onDownloadStateChange(100, item.podcast_id, State.DOWNLOADING);
                                }
                            });
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //TODO: Test if metadata update is needed before saving
                states.put(item.podcast_id, State.FINISHED);
                if (item.listener != null) {
                    MainActivity.instance.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            item.listener.onDownloadStateChange(100, item.podcast_id, State.FINISHED);
                        }
                    });
                }
                saveDownloadLog();
                Log.v("CASTER_DOWNLOAD", "Finished downloading");
            }
        }
    }

    /**
     * The class that hols information such as the id of the podcast in the queue and the
     * listener object
     */
    class Item{
        public OnChangeListener listener;
        public int podcast_id;
    }

    /**
     * Delegate interface that is called when there is a change in the state of the download
     * of a desired podcast.
     */
    public interface OnChangeListener{
        public void onDownloadStateChange(int progress, int podcast_id, State state);
    }
}
