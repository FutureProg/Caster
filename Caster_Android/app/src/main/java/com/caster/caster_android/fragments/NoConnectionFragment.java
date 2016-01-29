package com.caster.caster_android.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.caster.caster_android.R;

/**
 * Created by Nick on 2016-01-21.
 */
public class NoConnectionFragment extends Fragment implements View.OnClickListener{

    Callback callback;

    public NoConnectionFragment(){
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View re = inflater.inflate(R.layout.fragment_noconnection,container,false);
        Button button = (Button)re.findViewById(R.id.fragment_noconnection_button);
        button.setOnClickListener(this);
        return re;
    }

    @Override
    public void onClick(View v) {
        if (callback != null){
            callback.onRetryConnection();
        }
    }

    public interface Callback{
        public void onRetryConnection();
    }

}
