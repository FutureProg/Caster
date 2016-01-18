package com.caster.caster_android.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.caster.caster_android.Comment;
import com.caster.caster_android.Podcast;
import com.caster.caster_android.PodcastPlayer;
import com.caster.caster_android.R;
import com.caster.caster_android.User;
import com.caster.caster_android.utils.Bin;
import com.caster.caster_android.utils.CommentListAdapter;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by Nick on 2016-01-02.
 */
public class PlayerFragment extends Fragment{

    ArrayList<Comment> comments;
    boolean subscribed;

    public void setComments(ArrayList<Comment> comments){
        this.comments = comments;
    }

    public void setSubscribed(boolean sub){
        this.subscribed = sub;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View re =  inflater.inflate(R.layout.fragment_podcast_player,container,false);
        initializeViews(re,inflater);
        return re;
    }

    private void initializeViews(View view, LayoutInflater inflater) {
        if(PodcastPlayer.podcast == null){
            return;
        }
        if(view == null)view = getView();
        if(view == null)return;
        final Podcast podcast = PodcastPlayer.podcast;
        View header = inflater.inflate(R.layout.activity_play_podcast_listview_header, null);
        ListView listView = (ListView)view.findViewById(R.id.play_podcast_comments_list);
        listView.addHeaderView(header);

        if (Bin.getSignedInUser() != null){
            Button subbutton = (Button)view.findViewById(R.id.subscribe);
            if (subscribed) {
                subbutton.setBackgroundResource(R.drawable.unsubscribe);
            }else{
                subbutton.setBackgroundResource(R.drawable.subscribe);
            }

        }


        PodcastPlayer.author = (TextView)view.findViewById(R.id.author_name);
        PodcastPlayer.author.setText(podcast.getCreator().getUsername());

        PodcastPlayer.descriptionBox = (TextView)view.findViewById(R.id.description);
        PodcastPlayer.descriptionBox.setText(podcast.getDescription());

        ((ImageView)view.findViewById(R.id.authorPicture)).setImageBitmap(podcast.getCreator().getImage());

        PodcastPlayer.coverImage = (ImageView)view.findViewById(R.id.cover_image);
        PodcastPlayer.coverImage.setImageBitmap(podcast.getCoverPhoto());

        PodcastPlayer.playButton = (Button)view.findViewById(R.id.playButton);

        //setTitle(podcast.getTitle()); setTitle isn't a function so idk how it was compiling..

        //Load Comments
        ListView lv = (ListView)view.findViewById(R.id.play_podcast_comments_list);
        CommentListAdapter adapter = new CommentListAdapter(inflater.getContext(), R.layout.comment_layout, comments.toArray(new Comment[comments.size()]));
        lv.setAdapter(adapter);

        //Listen for comments
        ((EditText)view.findViewById(R.id.comment_box)).setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (event != null) {
                            if (!event.isShiftPressed()) {
                                //Toast.makeText(getApplicationContext(),v.getText(), Toast.LENGTH_SHORT).show();
                                try {
                                    podcast.postComment(User.ID, v.getText().toString());
                                    ((PodcastPlayer)getActivity()).reloadComments();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                } catch (Exception e){
                                    e.printStackTrace();
                                    Toast.makeText(getActivity().getApplicationContext(), "Error posting comment", Toast.LENGTH_SHORT).show();
                                }

                                return true;
                            }
                        }
                        return false;
                    }
                }
        );
        //Media Player
        PodcastPlayer.endTime = podcast.getLength();
        PodcastPlayer.length = (TextView) view.findViewById(R.id.elapsed_time);
        PodcastPlayer.seekBar = (SeekBar) view.findViewById(R.id.seekbar);
        PodcastPlayer.seekBar.setMax((int) PodcastPlayer.endTime);
        PodcastPlayer.seekBar.setClickable(false);
        ((PodcastPlayer)getActivity()).getSupportActionBar().setTitle(podcast.getTitle());
        /*mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mp.setDataSource(podcast.getAudioURL());
            mp.prepareAsync();
            endTime = mp.getDuration();
            length = (TextView)findViewById(R.id.length);
            seekBar = (SeekBar)findViewById(R.id.seekbar);
            seekBar.setMax((int) endTime);
            seekBar.setClickable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }*/


    }
}
