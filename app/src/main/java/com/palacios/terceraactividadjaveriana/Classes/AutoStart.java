package com.palacios.terceraactividadjaveriana.Classes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class AutoStart extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent arg) {
        if(arg.getAction() == Intent.ACTION_BOOT_COMPLETED) {
            Intent intent = new Intent(context, BackgroundBootService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent);
            } else {
                context.startService(intent);
            }
            Log.i("Autostart", "started");
        }
    }
}
