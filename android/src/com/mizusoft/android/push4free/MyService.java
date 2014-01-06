package com.mizusoft.android.push4free;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.webkit.URLUtil;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author tim
 */
public class MyService extends Service {

    private final static int NOTIFICATION_ID = 666;
    private AsyncTask longPoll = null;
    private String url;
    private boolean enabled;

    private void startLongPoll() {
        if (longPoll == null || longPoll.getStatus() != AsyncTask.Status.RUNNING) {
            longPoll = new LongPoll().execute(url);
        }
    }

    private void stopLongPoll() {
        if (longPoll != null && longPoll.getStatus() == AsyncTask.Status.RUNNING) {
            longPoll.cancel(true);
        }
        PreferenceManager.getDefaultSharedPreferences(getApplication()).edit().putBoolean("P4FPrefEnabled", false).commit();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //
        this.enabled = PreferenceManager.getDefaultSharedPreferences(getApplication()).getBoolean("P4FPrefEnabled", false);
        this.url = PreferenceManager.getDefaultSharedPreferences(getApplication()).getString("P4FPrefURL", "");
        //
        if (this.enabled) {
            startLongPoll();
        } else {
            stopLongPoll();
            stopSelf(startId);
        }
        //
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class LongPoll extends AsyncTask<String, Integer, String> {

        private String initialUrl;

        @Override
        protected String doInBackground(String... params) {
            initialUrl = params[0]; // first param is the server ip
            String responseString = null;
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet get = new HttpGet(initialUrl);
                HttpResponse responseGet = httpClient.execute(get);
                responseString = EntityUtils.toString(responseGet.getEntity(), "UTF-8");
            } catch (IOException ex) {
                Log.e(MyService.class.getName(), ex.getMessage());
            } catch (ParseException ex) {
                Log.e(MyService.class.getName(), ex.getMessage());
            } catch (IllegalArgumentException ex) {
                Log.e(MyService.class.getName(), ex.getMessage());
                return "STOP";
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i(MyService.class.getName(), "Received a result: " + result);
            // only show a notification based on valid information
            if (result != null && !result.contains("PING") && !result.contains("STOP")) {
                notify(result);
            }
            // restart the listening
            if (!"STOP".equals(result)) {
                longPoll = new LongPoll().execute(initialUrl);
            } else {
                stopLongPoll();
            }
        }

        private void notify(String msg) {
            Message message = Message.fromString(msg);
            NotificationCompat.Builder builder
                    = new NotificationCompat.Builder(getApplicationContext())
                    .setContentTitle(message.getTitle())
                    .setSmallIcon(R.drawable.ic_stat_gcm)
                    .setContentText(message.getBody());
            NotificationManager notificationManager
                    = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // set some preferences configurable by user
            if (PreferenceManager.getDefaultSharedPreferences(getApplication()).getBoolean("P4FPrefTone", true)) {
                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                builder.setSound(alarmSound);
            }
            if (PreferenceManager.getDefaultSharedPreferences(getApplication()).getBoolean("P4FPrefVibrate", true)) {
                builder.setVibrate(new long[]{500l, 500, 500l});
            }
            if (PreferenceManager.getDefaultSharedPreferences(getApplication()).getBoolean("P4FPrefBlink", true)) {
                builder.setLights(Color.BLUE, 500, 500);
            }
            notificationManager.notify(message.getId(), builder.getNotification());
        }
    }

    @Override
    public void onDestroy() {
        Log.i(MyService.class.getName(), "Service destroyed.");
    }

    @Override
    public void onCreate() {
        Log.i(MyService.class.getName(), "Service created.");
    }

    /**
     * WORKAROUND FOR ANDROID 4.4 (API19) - START_STICKY broken
     * http://stackoverflow.com/questions/20636330/start-sticky-does-not-work-on-android-kitkat
     *
     * @param rootIntent
     */
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        if (Build.VERSION.SDK_INT >= 19) {
            Intent restartService = new Intent(getApplicationContext(),
                    this.getClass());
            restartService.setPackage(getPackageName());
            PendingIntent restartServicePI = PendingIntent.getService(
                    getApplicationContext(), 1, restartService,
                    PendingIntent.FLAG_ONE_SHOT);
            AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, restartServicePI);
        }
    }
}
