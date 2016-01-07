package com.caster.caster_android.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.caster.caster_android.R;

/**
 * Created by Nick on 2016-01-02.
 */
public class ProgressFragment extends Fragment{

    public final static String ARGUMENT_CALLBACK = "ARG_CALLBACK";
    //Queue<AsyncTask> tasks;
    //ProgressCallback callback;

    public ProgressFragment() {
        super();
        //tasks = new ArrayDeque<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View re = inflater.inflate(R.layout.fragment_progress,container,false);
        /*if(savedInstanceState.containsKey(ARGUMENT_CALLBACK)){
            callback = (ProgressCallback) savedInstanceState.getSerializable(ARGUMENT_CALLBACK);
        }*/
        return re;
    }

    /*public void addTask(AsyncTask task){
        tasks.add(task);
    }

    public void setCallback(ProgressCallback callback){
        this.callback = callback;
    }


    @Override
    public void run() {
        while(tasks.size() > 0){
            AsyncTask t = tasks.poll();
            t.execute().get();
        }
    }

    public interface ProgressCallback extends Serializable{
        void onTaskComplete(AsyncTask task);
    }*/
}
