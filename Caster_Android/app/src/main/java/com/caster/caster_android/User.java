package com.caster.caster_android;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by Nick on 15-05-05.
 *
 * User
 * Used to store the metadata and image for a User (not the app user)
 * Can be created using User.makeFromID(<id>) to retrieve one from the database
 * Holds information such as the username, descripion, and user id.
 */
public class User {

    public static final int USERNAME = 0, DESCRIPTION = 1, SUBSCRIBER_COUNT = 2,ID=3;

    HashMap<Integer, Object> metadata; //username, description,subscriber count,id

    /***
     *  Retrieve the user's metadata from the server
     * @param userid - userid to process
     * @return - an instance of the user
     */
    public static User makeFromID(int userid){
        HashMap<Integer,Object> data = new HashMap<>();
        User re = null;
        String urlStr = "http://192.168.2.155:8000/php/user_info.php";
        CasterRequest req = new CasterRequest(urlStr);
        req.addParam("q","UN").addParam("id",""+userid);
        try {
            data.put(ID,userid);
            data.put(USERNAME, (String) req.execute().get());
            req = new CasterRequest(urlStr);
            String res = (String)req.addParam("q","DESC").addParam("id",""+userid).execute().get();
            data.put(DESCRIPTION,res);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        re = new User(data);
        return re;
    }

    public User(HashMap<Integer, Object> metadata){
        this.metadata = metadata;
    }

    public String getUsername(){
        return (String)this.metadata.get(USERNAME);
    }

    public String getDescription(){
        return (String)this.metadata.get(DESCRIPTION);
    }

    public int getSubscriberCount(){
        return (int)this.metadata.get(SUBSCRIBER_COUNT);
    }

    public int getId(){
        return (int)this.metadata.get(ID);
    }

    public HashMap<Integer, Object> getMetadata(){
        return metadata;
    }

}
