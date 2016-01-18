package com.buahbatu.streetwatcher.services;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

/**
 * Created by maaakbar on 10/31/15.
 */
public class SoundLevelDetection {
    private final static String TAG = "SoundLevelDetection";
    public AudioRecord ar = null;
    private int minSize;
    private short[] data;

    private static int[] mSampleRates = new int[] { 8000, 16000, 11025, 22050, 44100 };
    public AudioRecord findAudioRecord() {
        for (int rate : mSampleRates) {
            for (short audioFormat : new short[] { AudioFormat.ENCODING_PCM_8BIT, AudioFormat.ENCODING_PCM_16BIT }) {
                for (short channelConfig : new short[] { AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO }) {
                    try {
                        Log.d(TAG, "Attempting rate " + rate + "Hz, bits: " + audioFormat + ", channel: "
                                + channelConfig);
                        int bufferSize = AudioRecord.getMinBufferSize(rate, channelConfig, audioFormat);

                        if (bufferSize != AudioRecord.ERROR_BAD_VALUE) {
                            // check if we can instantiate and have a success
                            AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, rate, channelConfig, audioFormat, bufferSize);

                            if (recorder.getState() == AudioRecord.STATE_INITIALIZED){
                                minSize = bufferSize;
                                return recorder;
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, rate + "Exception, keep trying.",e);
                    }
                }
            }
        }
        return null;
    }

    public void start() {
        ar = findAudioRecord();
        try {
            ar.startRecording();
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    public void stop() {
        if (ar != null) {
            ar.stop();
            ar.release();
        }
    }

    public double getAmplitude() {
        short[] buffer = new short[minSize];
        ar.read(buffer, 0, minSize);
        int max = 0;
        for (short s : buffer)
        {
            if (Math.abs(s) > max)
            {
                max = Math.abs(s);
            }
        }
        data = buffer;
        return max;
    }
}
