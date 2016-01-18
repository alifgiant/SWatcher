package com.buahbatu.streetwatcher.network;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpStatus;

/**
 * Created by maaakbar on 11/8/15.
 */

public class PostJSONTask extends AsyncTask<JSONObject, Void, String> {
    private Context context;
    private URL url;
    private HttpPostEvent httpPostEvent;
    private final String TAG = "POST web";

    public PostJSONTask(Context context, URL url) {
        this.context = context;
        this.url = url;
    }

    public PostJSONTask(Context context, URL url, HttpPostEvent event) {
        this.context = context;
        this.url = url;
        this.httpPostEvent = event;
    }


    @Override
    protected String doInBackground(JSONObject... params) {
        JSONObject object = params[0];

        StringBuilder response = new StringBuilder("");
        try {
//            url.toString()
            // optional default is GET
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setReadTimeout(10000);
            con.setConnectTimeout(15000);
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);

            con.setRequestProperty("User-Agent", "Android");
            con.setRequestProperty("Connection", "Keep-Alive");

            con.setRequestProperty( "Content-Type", "application/json" );
            con.setRequestProperty("Accept", "application/json");

//            Log.i(TAG, reqEntity.getContentType().getName()+" : "+ reqEntity.getContentType().getValue());
//            con.addRequestProperty("Content-Type", "multipart/form-data; boundary=" + MultiPartFormOutputStream.createBoundary());

            con.connect();

            //Send request
            DataOutputStream wr = new DataOutputStream (
                    con.getOutputStream ());

            wr.writeBytes(object.toString());
            wr.flush();
//            writOutValue(params[0], wr);

            int status = con.getResponseCode();
            Log.i(TAG, "error code: "+ Integer.toString(status));


            //Get Response
//            InputStream is = con.getInputStream();
            InputStream is;
            if(status >= HttpStatus.SC_BAD_REQUEST)
                is = con.getErrorStream();
            else
                is = con.getInputStream();

            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }

            rd.close();

//            if(con != null) {
            con.disconnect();
//            }
        }catch (MalformedURLException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
            Log.e(TAG, "doInBackground ");
        }
        Log.d("response", response.toString());
        return response.toString();
    }

//    private void writOutValue(ContentValues param, DataOutputStream out){
//        MultiPartFormOutputStream outputStream = new MultiPartFormOutputStream(out, MultiPartFormOutputStream.createBoundary());
//        for (Map.Entry<String, Object> entry : params.valueSet()){
//            String key = entry.getKey();
//            if (key.equals(context.getString(R.string.api_imgAlert)))
//                outputStream.wr
//            else
//                result.append("&");
//            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
//            result.append("=");
//            result.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
//        }
//    }

//    private String getQuery(ContentValues params) throws UnsupportedEncodingException
//    {
//        StringBuilder result = new StringBuilder();
//        boolean first = true;
//
//        for (Map.Entry<String, Object> entry : params.valueSet()){
//            if (first)
//                first = false;
//            else
//                result.append("&");
//            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
//            result.append("=");
//            result.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
//        }
//        Log.i(TAG, result.toString());
//        return result.toString();
//    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (httpPostEvent != null)
            httpPostEvent.preEvent();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (httpPostEvent != null)
            httpPostEvent.postEvent(s);
    }

    public interface HttpPostEvent{
        void preEvent();
        void postEvent(String... result);
    }
}