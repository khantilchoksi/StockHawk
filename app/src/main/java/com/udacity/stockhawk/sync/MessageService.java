package com.udacity.stockhawk.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import timber.log.Timber;

/**
 * Created by Khantil on 18-01-2017.
 */

public class MessageService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sendMessage(intent.getStringExtra("symbol"));
        return super.onStartCommand(intent, flags, startId);
    }

    private void sendMessage(String symbol){
        Timber.d("sender _ Broadcasting message");
        Intent intent = new Intent("custom-event-name");
        // You can also include some extra data.
        intent.putExtra("message", "Stock Symbol : "+symbol+" doesn't exist!");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
