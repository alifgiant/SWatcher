package com.buahbatu.streetwatcher.network;

import android.content.Context;

import com.buahbatu.streetwatcher.R;
import com.pusher.client.Pusher;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;

/**
 * Created by maakbar on 11/6/15.
 **/

public class PusherSubscriber{
    String YOUR_APP_KEY;
    String channelName;
    Pusher pusher = null;
    Channel channel;

    public PusherSubscriber(Context context) {
        this.YOUR_APP_KEY = context.getResources().getString(R.string.pusher_apis);
        this.channelName = context.getResources().getString(R.string.pusher_channel);
    }

    public void openConnection(){
        if (pusher == null) {
            pusher = new Pusher(YOUR_APP_KEY);
            pusher.connect(cel, ConnectionState.ALL);

            // Subscribe to a channel
            channel = pusher.subscribe(channelName);

            // Bind to listen for events called "my-event" sent to "my-channel"
            channel.bind("my_event", sel);
        }else {
            pusher.connect();
        }
    }

    public void closeConnection(){
        if (pusher != null){
            pusher.disconnect();
        }
    }

    SubscriptionEventListener sel = new SubscriptionEventListener() {
        @Override
        public void onEvent(String channel, String event, String data) {
            System.out.println("Received event with data: " + data);
        }
    };

    ConnectionEventListener cel = new ConnectionEventListener() {
        @Override
        public void onConnectionStateChange(ConnectionStateChange change) {
            System.out.println("State changed to " + change.getCurrentState() +
                    " from " + change.getPreviousState());
        }

        @Override
        public void onError(String message, String code, Exception e) {
            System.out.println("There was a problem connecting!");
        }
    };
}
