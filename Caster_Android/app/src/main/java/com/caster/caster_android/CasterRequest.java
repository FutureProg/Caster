package com.caster.caster_android;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nick on 15-05-05.
 * Used to send requests to the Caster website.
 */
public class CasterRequest extends AsyncTask<Void,Void,Object>{

    private static final char PARAM_DELIM = '&', PARAM_EQUALS = '=';

    String reqUrl;
    HashMap<String,String> params;

    /***
     *
     * @param reqUrl - The url that we are trying to access.
     */
    public CasterRequest(String reqUrl){
        super();
        this.reqUrl = reqUrl;
        this.params = new HashMap<String,String>();

    }

    /***
     * used to add parameters to the Http Request
     * @param key - the key for the parameter
     * @param value - the value of the parameter
     * @return the modified CasterRequest object
     */
    public CasterRequest addParam(String key, String value){
        params.put(key,value);
        return this;
    }

    /***
     *
     * @param params - set the request parameters using a Map
     * @return the modified CasterRequest object
     */
    public CasterRequest setParams(Map<String,String> params){
        this.params = (HashMap<String, String>) params;
        return this;
    }

    /***
     *
     * @param url - the request url
     * @return the modified CasterRequest object
     */
    public CasterRequest setUrl(String url){
        this.reqUrl = url;
        return this;
    }

    /***
     * From the parameters in @param parameters, create a query string to send to the
     * server
     * @param parameters - the parameters to query
     * @return the query string
     */
    public static String createQueryString(Map<String,String> parameters){
        StringBuilder re = new StringBuilder();
        if(parameters != null){
            boolean firstParam = true;
            for (String paramName : parameters.keySet()){
                if(!firstParam){
                    re.append(PARAM_DELIM);
                }
                re.append(paramName).append(PARAM_EQUALS).append(parameters.get(paramName));
                firstParam = false;
            }
        }
        return re.toString();
    }

    @Override
    protected Object doInBackground(Void... param_to_not_use) {
        try {
            URL urlToReq = new URL(reqUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) urlToReq.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            String query = createQueryString(this.params);
            urlConnection.setFixedLengthStreamingMode(query.getBytes().length);
            urlConnection.setConnectTimeout(2000);
            DataOutputStream out = new DataOutputStream(urlConnection.getOutputStream());
            out.writeBytes(query);
            out.flush();
            int responseCode = urlConnection.getResponseCode();
            Log.i("Caster_Android_Info","\nSending 'POST' request to URL : " + urlToReq);
            Log.i("Caster_Android_Info", "Post parameters : " + query);
            Log.i("Caster_Android_Info", "Response Code : " + responseCode);
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while((inputLine = in.readLine()) != null){
                response.append(inputLine);
            }
            in.close();
            out.close();
            params = new HashMap<>();
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
