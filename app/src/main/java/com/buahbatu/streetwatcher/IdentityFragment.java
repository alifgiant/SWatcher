package com.buahbatu.streetwatcher;

import android.app.Activity;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.buahbatu.streetwatcher.network.MultiPartFormOutputStream;
import com.buahbatu.streetwatcher.network.NetConfig;
import com.buahbatu.streetwatcher.network.PostWebTask;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.jetbrains.annotations.Contract;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class IdentityFragment extends Fragment {

    private static final String TAG = "ID frag";
//    private OnFragmentInteractionListener mListener;

    public IdentityFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_identity, container, false);
        view.findViewById(R.id.test_alert).setOnClickListener(listener);

//        initializeMap(view);



        // in this example, a LineChart is initialized from xml
        return view;
    }

    private void initializeMap(View view) {
        MapView mapView = (MapView)view.findViewById(R.id.map_vew);
        try {
            GoogleMapOptions options = new GoogleMapOptions();
            options.liteMode(true);
            mapView.getMapAsync(mapReadyCallback);
        }catch (Exception e){
            Toast.makeText(getContext(),
                    "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                    .show();
        }

//        SupportMapFragment myMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_vew);
//        // check if map is created successfully or not
//        try{
//            myMapFragment.getMapAsync(mapReadyCallback);
//        }catch (Exception e){
//            Toast.makeText(getContext(),
//                    "Sorry! unable to create maps", Toast.LENGTH_SHORT)
//                    .show();
//        }
    }

    OnMapReadyCallback mapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {

        }
    };

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            NetConfig address = new NetConfig(getContext());
//            SyncHttpClient client = new SyncHttpClient();
//            RequestParams params = new RequestParams();
//
//            params.put(getString(R.string.api_coordinate), getString(R.string.default_coordinate));
//            params.put(getString(R.string.api_location), getString(R.string.default_location));
//            params.put(getString(R.string.api_idDev), getString(R.string.default_id));
//
//            try {
//                File image=new File(getContext().getExternalFilesDir(null),"unamed.jpg");
//                InputStream inputStream = getResources().openRawResource(+ R.drawable.unnamed);
//                OutputStream out=new FileOutputStream(image);
//                byte buf[]=new byte[4096];
//                int len;
//                while((len=inputStream.read(buf))>0)
//                    out.write(buf,0,len);
//                out.close();
//                inputStream.close();
//
//                params.put(getString(R.string.api_imgAlert), image);
//            }
//            catch (IOException e){
//                e.printStackTrace();
//                Log.e(TAG, "No file found");
//            }
////            catch (FileNotFoundException e){
////                e.printStackTrace();
////                Log.e(TAG, "No file found");
////            }
//
//            String adderess = address.getAlertURL();
//            Log.i(TAG, "onClick "+adderess);
////
//            client.post(adderess, params, new AsyncHttpResponseHandler() {
//                @Override
//                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                    Log.i(TAG, "Data Sended");
//                }
//
//                @Override
//                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                    Log.i(TAG, "Data Error");
//
//                    Log.i(TAG, "Status:"+Integer.toString(statusCode));
//
//                    error.printStackTrace();
//                }
//            });

            try {
                PostWebTask webTask = new PostWebTask(getContext(), new URL(address.getAlertURL()));
                MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
//                entityBuilder.setBoundary(MultiPartFormOutputStream.createBoundary());

                entityBuilder.addTextBody(getString(R.string.api_coordinate), getString(R.string.default_coordinate));
                entityBuilder.addTextBody(getString(R.string.api_location), getString(R.string.default_location));
                entityBuilder.addTextBody(getString(R.string.api_idDev), getString(R.string.default_id));

//                try {
                    Drawable d = getResources().getDrawable(R.drawable.unnamed);
                    Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] bitmapdata = stream.toByteArray();
                    entityBuilder.addBinaryBody(getString(R.string.api_imgAlert), bitmapdata);

//                    File image=new File(getContext().getExternalFilesDir(null),"unamed1.jpg");
//                    InputStream inputStream = getResources().openRawResource(+ R.drawable.unnamed);
//                    OutputStream out=new FileOutputStream(image);
//                    byte buf[]=new byte[4096];
//                    int len;
//                    while((len=inputStream.read(buf))>0)
//                        out.write(buf,0,len);
//                    out.close();
//                    inputStream.close();
//
//                    entityBuilder.addBinaryBody(getString(R.string.api_imgAlert), image);
//                }
//                catch (IOException e){
//                    e.printStackTrace();
//                    Log.e(TAG, "No file found");
//                }
//                ContentValues values = new ContentValues();

                webTask.execute(entityBuilder.build());
            }catch (MalformedURLException e){
                Log.i(TAG, "malformed");
                e.printStackTrace();
            }

            Toast.makeText(getContext(), "Alert Sent", Toast.LENGTH_LONG).show();
        }
    };


//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        try {
//            // un comment below later
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

}
