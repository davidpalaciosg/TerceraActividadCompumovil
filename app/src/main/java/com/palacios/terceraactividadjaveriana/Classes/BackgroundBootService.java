package com.palacios.terceraactividadjaveriana.Classes;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.palacios.terceraactividadjaveriana.MapsActivity;
import com.palacios.terceraactividadjaveriana.R;

public class BackgroundBootService extends Service {

    public static String CHANNEL_ID =
            "TerceraActividadJaveriana";
    int notificationId = 0;



    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public IBinder onBind(Intent intent) {return null;}

    public void onDestroy() {
        Log.i("BOOT", "BOOT Service has been stopped");
        Toast.makeText(this, "BOOT service stopped", Toast.LENGTH_LONG).show();

        /*
        if (parseQuery != null && parseLiveQueryClient != null)
            parseLiveQueryClient.unsubscribe(parseQuery);

         */
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
          /*
        startForeground(2, buildComplexNotification("Service Started", "Connected to NubePUJ", R.drawable.ic_baseline_adb_24, MapsActivity.class));
        Toast.makeText(this, "SmartPUJ Started", Toast.LENGTH_LONG).show();

        parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();
        parseQuery = ParseQuery.getQuery("SmartUser");
        subscriptionHandling = parseLiveQueryClient.subscribe(parseQuery);
        subscriptionHandling.handleEvents(new
                                                  SubscriptionHandling.HandleEventsCallback<ParseObject>() {
                                                      @Override
                                                      public void onEvents(ParseQuery<ParseObject> query,
                                                                           SubscriptionHandling.Event event, ParseObject object) {
                                                          dataChanged(query);
                                                      }
                                                  });

           */
        return START_STICKY;
    }

    private void createNotificationChannel() {
        // Create the No8fica8onChannel, but only on API 26+ because
        // the No8fica8onChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel";
            String description = "channel description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            //IMPORTANCE_MAX MUESTRA LA NOTIFICACIÓN ANIMADA
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other no8fica8on behaviors aTer this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }



}
