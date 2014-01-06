package com.mizusoft.android.push4free;

import android.content.BroadcastReceiver;
import static android.content.ContentValues.TAG;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 *
 * @author tim
 */
public class MyBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context cntxt, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            MainActivity.startService(cntxt);
            Log.i(TAG, "MyBootReceiver BOOT_COMPLETED started MyService.");
        }
    }

}
