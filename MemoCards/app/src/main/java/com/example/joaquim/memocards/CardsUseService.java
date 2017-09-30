package com.example.joaquim.memocards;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

public class CardsUseService extends Service {
    private static final String TAG = "CardsUseService";
    private NotificationManager notificationManager;

    @Override
    public void onCreate(){
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        BaseCards db = new BaseCards(this);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        int choice_delay = Integer.parseInt(settings.getString("notification_delay", "0"));
        long delay = 0;
        switch (choice_delay){
            case 1:
                delay = 20;
                break;
            case 2:
                delay = 86400;
                break;
            case 3:
                delay = 86400*2;
                break;
            case 4:
                delay = 86400*5;
                break;
            case 5:
                delay = 86400*10;
                break;
            default:
                break;
        }

        Log.v("DELAY", String.valueOf(delay));

        String cardsSet = "";
        ArrayList<String> liste_sets = db.getSetsNames();
        settings = getSharedPreferences("memocards_last_uses", 0);

        for(String set : liste_sets){
            long last_use = settings.getLong(set, 0);
            long interval = (new Date().getTime()) - last_use;
            long interval_sec = interval / 1000;

            Log.v("SET", set);
            Log.v("LAST USE", String.valueOf(last_use));
            Log.v("INTERVAL SEC", String.valueOf(interval_sec));

            if( (interval_sec) > delay)
                cardsSet += set+",";
        }

        displayNotificationMessage("Sets not used for a long time !", cardsSet);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        super.onStartCommand(intent, flags, startId);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy(){
        notificationManager.cancelAll();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void displayNotificationMessage(String titre, String message) {
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(titre)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_alarm_black_24dp)
                .setOngoing(true)
                .build();

        notificationManager.notify(0, notification);
    }
}
