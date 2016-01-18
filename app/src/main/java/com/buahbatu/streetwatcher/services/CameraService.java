package com.buahbatu.streetwatcher.services;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;

import org.jetbrains.annotations.Contract;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by maaakbar on 11/10/15.
 */
public class CameraService implements Service {
    private static final String TAG = "Camera Service";
    private Camera mCamera;
    private TextureView mTextureView;
    private SurfaceTexture surfaceTexture;
    private Context mContext;

    public CameraService(TextureView mTextureView, Context context) {
        this.mTextureView = mTextureView;
        this.mContext = context;
        mTextureView.setSurfaceTextureListener(surfaceTextureListener);
        mTextureView.setOnTouchListener(mOnTouchListener);
    }

    private void openCamera() {
        if (mCamera == null)
            mCamera = getCameraInstance();
        try {
            //Log.i(TAG, Boolean.toString(mCamera == null));
            mCamera.setPreviewTexture(surfaceTexture);
            mCamera.startPreview();
        } catch (IOException e) {
            // Something bad happened
        } catch (Exception e){

        }
    }

    private void closeCamera() {
        if(mCamera!=null) {
            mCamera.stopPreview();
            mCamera.release();
        }
    }

    public void takePicture(OnPictureReadyListener onPictureReadyListener) {
        // enable to play other sound
        // mCamera.takePicture(shutterCallback, null, pictureCallback);
        this.onPictureReadyListener = onPictureReadyListener;
        mCamera.takePicture(null, null, pictureCallback);
    }

    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // onPictureReadyListener.onPictureAvailable(data);
            onPictureReadyListener.onFileReady(data);
//            savePicture(data);
            openCamera();
        }
    };

    private static  final int FOCUS_AREA_SIZE= 300;
    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                focusOnTouch(event);
            }
            return false;
        }
    };
    private void focusOnTouch(MotionEvent event) {
        if (mCamera != null ) {
            Camera.Parameters parameters = mCamera.getParameters();
            Rect rect = calculateFocusArea(event.getX(), event.getY());
            if (parameters.getMaxNumMeteringAreas() > 0){
                Log.i(TAG, "fancy !");

                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                List<Camera.Area> meteringAreas = new ArrayList<>();
                meteringAreas.add(new Camera.Area(rect, 800));
                parameters.setFocusAreas(meteringAreas);

                mCamera.setParameters(parameters);
                mCamera.autoFocus(mAutoFocusTakePictureCallback);
            }else {
                mCamera.autoFocus(mAutoFocusTakePictureCallback);
            }
        }
    }

    private Rect calculateFocusArea(float x, float y) {
        int left = clamp(Float.valueOf((x / mTextureView.getWidth()) * 2000 - 1000).intValue(), FOCUS_AREA_SIZE);
        int top = clamp(Float.valueOf((y / mTextureView.getHeight()) * 2000 - 1000).intValue(), FOCUS_AREA_SIZE);

        return new Rect(left, top, left + FOCUS_AREA_SIZE, top + FOCUS_AREA_SIZE);
    }

    @Contract(pure = true)
    private int clamp(int touchCoordinateInCameraReper, int focusAreaSize) {
        int result;
        if (Math.abs(touchCoordinateInCameraReper)+focusAreaSize/2>1000){
            if (touchCoordinateInCameraReper>0){
                result = 1000 - focusAreaSize/2;
            } else {
                result = -1000 + focusAreaSize/2;
            }
        } else{
            result = touchCoordinateInCameraReper - focusAreaSize/2;
        }
        return result;
    }

    private Camera.AutoFocusCallback mAutoFocusTakePictureCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            if (success) {
                // do something...
                Log.i("tap_to_focus","success!");
            } else {
                // do something...
                Log.i("tap_to_focus", "fail!");
            }
        }
    };

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
            Log.e(TAG, "no camera available");
        }
        return c; // returns null if camera is unavailable
    }

    TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            // TODO Auto-generated method stub
            Log.i(TAG, "onSurfaceTextureAvailable()");
            surfaceTexture = surface;
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            closeCamera();
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    interface OnPictureReadyListener{
        void onFileReady(byte[] data_img);
    }

    OnPictureReadyListener onPictureReadyListener;
    public void savePicture(byte[] data){
        File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
        if (pictureFile == null){
            Log.d(TAG, "Error creating media file, check storage permissions: " );
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();
            Log.d(TAG, "Picture Saved");
//            onPictureReadyListener.onFileReady(pictureFile);
            notifyMediaStoreScanner(pictureFile);
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
    }

    private void notifyMediaStoreScanner(final File file) {
        try {
            MediaStore.Images.Media.insertImage(mContext.getContentResolver(),
                    file.getAbsolutePath(), file.getName(), null);
            mContext.sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    /** Create a file Uri for saving an image or video */
    @NonNull
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    @Nullable
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "BloodEy");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d(TAG, "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    public void start() {
        openCamera();
    }

    @Override
    public void stop() {
        closeCamera();
    }
}
