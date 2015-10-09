package com.example.jagr.alexandria.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AlexandriaReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = AlexandriaReceiver.class.getSimpleName();
    public static final String MESSAGE_EVENT            = "com.example.jagr.alexandria.ACTION_MESSAGE_EVENT";
    public static final String MESSAGE_KEY              = "MESSAGE_EXTRA";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action.equals(MESSAGE_EVENT)) {
            Toast.makeText(context, intent.getStringExtra(MESSAGE_KEY), Toast.LENGTH_LONG).show();
        }
    }
}
