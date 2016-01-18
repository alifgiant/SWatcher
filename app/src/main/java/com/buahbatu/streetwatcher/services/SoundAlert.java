package com.buahbatu.streetwatcher.services;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.TextureView;
import android.widget.Toast;

import com.buahbatu.streetwatcher.R;
import com.buahbatu.streetwatcher.network.NetConfig;
import com.buahbatu.streetwatcher.network.PostWebTask;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;


/**
 * Created by maaakbar on 11/10/15.
 */
public class SoundAlert implements Service{
    /* constants */
    private static final String TAG = "SoundAlert";
    private static final int POLL_INTERVAL = 50;  // default 300 ms
    private static final int NO_NUM_DIALOG_ID=1;

    /** running state **/
    private boolean mRunning = false;
    private int mHitCount =0;

    /** config state **/
    private int mThreshold = 8000; //default value
    private Context context;

    /** Other Service **/
    CameraService cameraService = null;
    TextureView mTextureView = null;

    public SoundAlert(Context context) {
        this.context = context;
        mSensor = new SoundLevelDetection();
    }

    public SoundAlert(Context context, TextureView textureView) {
        this.context = context;
        this.mTextureView = textureView;
        mSensor = new SoundLevelDetection();
    }

    private Handler mHandler = new Handler();

    void enableCamera(){
        cameraService = new CameraService(mTextureView, context);
    }

    /* data source */
    SoundLevelDetection mSensor;

    private Runnable mPollTask = new Runnable() {
        public void run() {
            double amp = mSensor.getAmplitude();
            mThreshold = getCurrentThreshold();
            if ((amp > mThreshold)) {
                Log.i(TAG, "Above" + Double.toString(mSensor.getAmplitude()));
                mHitCount++;
                if (mHitCount > 1){
//                    Log.i(TAG, "callForHelp called");
                    callForHelp();
                    mHitCount = 0;
                }
            }else{
                mHitCount=0;
                Log.i(TAG, "below"+Double.toString(mSensor.getAmplitude()));
            }
            insertToLastRead(amp);
            if (mRunning)
                mHandler.postDelayed(mPollTask, POLL_INTERVAL);
        }
    };

    private ArrayList<Double> lastRead = new ArrayList<>();
    private void insertToLastRead(double currentRead){
        lastRead.add(currentRead);
        if (lastRead.size() > 5)
            lastRead.remove(0);
    }
    private int getCurrentThreshold(){
        if (lastRead.size()<5)return mThreshold;
        int tempThres = 0;
        for (double read:lastRead) {
            tempThres += read;
        }
        tempThres /= 5;
        return tempThres * 2; // * n for multiplication
    }


    @Override
    public void start(){
        if (mSensor!=null) {
            mHitCount = 0;
            mRunning = true;
            mSensor.start();
            if (mSensor.ar != null) {
                Log.i(TAG, "tread started");
                mHandler.postDelayed(mPollTask, POLL_INTERVAL);
            }
        }
        if (cameraService==null){
            enableCamera();
            cameraService.start();
        }
        else cameraService.start();
    }

    @Override
    public void stop(){
        if (mSensor!=null) {
            mRunning = false;
//            mHandler.removeCallbacks(mPollTask);
            mSensor.stop();
        }
        if (cameraService!=null)cameraService.stop();
    }

    public void callForHelp(){
        final NetConfig address = new NetConfig(context);
        Log.i(TAG, "callForHelp called");
//        cameraService.takePicture(new CameraService.OnPictureReadyListener() {
//            @Override
//            public void onFileReady(byte[] data_img) {
                try {
                    PostWebTask webTask = new PostWebTask(context, new URL(address.getAlertURL()));
                    MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
//                entityBuilder.setBoundary(MultiPartFormOutputStream.createBoundary());

                    entityBuilder.addTextBody(getString(R.string.api_coordinate), getString(R.string.default_coordinate));
                    entityBuilder.addTextBody(getString(R.string.api_location), getString(R.string.default_location));
                    entityBuilder.addTextBody(getString(R.string.api_idDev), getString(R.string.default_id));

//                    try {
//                        Bitmap bitmap = BitmapFactory.decodeByteArray(data_img, 0, data_img.length);
//                        bitmap = Bitmap.createScaledBitmap(bitmap, 800, 600, false);
//                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//                        byte[] bitmapdata = stream.toByteArray();
//
//    //                    Drawable d = getResources().getDrawable(R.drawable.unnamed);
//    //                    Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
//    //                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//    //                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//    //                    byte[] bitmapdata = stream.toByteArray();
//
//    //                    entityBuilder.addBinaryBody(getString(R.string.api_imgAlert), data_img);
//
//    //                    File image=new File(getContext().getExternalFilesDir(null),"unamed1.jpg");
//    //                    InputStream inputStream = getResources().openRawResource(+ R.drawable.unnamed);
//    //                    OutputStream out=new FileOutputStream(image);
//    //                    byte buf[]=new byte[4096];
//    //                    int len;
//    //                    while((len=inputStream.read(buf))>0)
//    //                        out.write(buf,0,len);
//    //                    out.close();
//    //                    inputStream.close();
//    //
//                        entityBuilder.addBinaryBody(getString(R.string.api_imgAlert), bitmapdata);
//    //                    entityBuilder.addBinaryBody(getString(R.string.api_imgAlert), image);
//                    } catch (Exception e){
//                        e.printStackTrace();
//                        Log.e(TAG, "No file found");
//                    }
//                ContentValues values = new ContentValues();

                    webTask.execute(entityBuilder.build());

                }catch (MalformedURLException e){
                    Log.i(TAG, "malformed");
                    e.printStackTrace();
                }
                Toast.makeText(context, "Alert Sent", Toast.LENGTH_LONG).show();
//            }
//        });
    }

    private String getString(int id){
        Resources res = context.getResources();
        return res.getString(id);
    }
}
