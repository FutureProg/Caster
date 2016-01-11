package com.caster.caster_android.utils;

import android.content.Context;
import android.os.Looper;
import android.util.Log;

import com.caster.caster_android.MainActivity;
import com.caster.caster_android.Podcast;

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
        FINISHED
    };
    Queue<Item> items;
    HashSet<Integer> toCancel;
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
        }
        return instance;
    }

    private PodcastDownloader(Context context){
        this.context = context;
        items = new ArrayDeque<>();
        lock = new Object();
        toCancel = new HashSet<>();
        states = new HashMap<>();
        loadDownloadLog();
        thread = new Thread(this);
    }


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
            states.put(podcast_id,State.DNE);
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
            FileOutputStream outputStream = context.openFileOutput("download_list",Context.MODE_PRIVATE);
            for (int podcast_id : states.keySet()) {
                if(states.get(podcast_id) == State.FINISHED){
                    String line = podcast_id + "\n";
                    Log.v("CASTER_SAVE",podcast_id+"");
                    outputStream.write(line.getBytes());
                }
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
            BufferedReader inputStream = new BufferedReader(
                    new FileReader(context.getFileStreamPath("download_list")));
            String line;
            while ((line = inputStream.readLine()) != null){
                Log.v("CASTER_LOAD",line);
                states.put(Integer.parseInt(line),State.FINISHED);
            }
            inputStream.close();
        } catch (FileNotFoundException e) {
            return;
        } catch (IOException e) {
            e.printStackTrace();
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
                    while ((current = bis.read(buffer)) != -1) {
                        count += current;
                        //Log.v("CASTER_CURRENT","Count: " + count + " Size: " + size + " Ratio: " + ((float)count/size));
                        outputStream.write(buffer, 0, current);
                        final int c = count;
                        if (item.listener != null) {
                            MainActivity.instance.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    item.listener.onDownloadStateChange((int) ((float) c * 100 / size), item.podcast_id, State.DOWNLOADING);
                                    states.put(item.podcast_id, State.DOWNLOADING);
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
                                        item.listener.onDownloadStateChange((int) ((float) c * 100 / size), item.podcast_id, State.CANCELLED);
                                }
                            });
                            continue;
                        }
                    }
                    outputStream.flush();
                    outputStream.close();
                    bis.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //TODO: Save the metadata, coverimage, user metadata and profile pic
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
