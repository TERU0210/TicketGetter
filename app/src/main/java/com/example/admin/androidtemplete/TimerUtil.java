package com.example.admin.androidtemplete;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import hugo.weaving.DebugLog;

public class TimerUtil {
    Context context = null;

    @DebugLog
    public TimerUtil(Context context) {
        this.context = context;
    }


    @SuppressLint("NewApi")
    @DebugLog
    void setTimer(int time) {

        Intent intent = new Intent(context, TimerReceiver_.class);
        PendingIntent pending = PendingIntent.getBroadcast(context, 0, intent, 0);

        // アラームをセットする
        AlarmManager am = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + time, pending);
    }
}
