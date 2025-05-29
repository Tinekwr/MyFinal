package com.example.myfinal;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.core.app.NotificationCompat;

public class WaterReminderReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "WaterReminderChannel";

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = context.getSharedPreferences("WaterPrefs", Context.MODE_PRIVATE);
        String today = WaterRecordActivity.getToday();
        String savedDate = prefs.getString("record_date", "");
        int amount = savedDate.equals(today) ? prefs.getInt("record_amount", 0) : 0;
// 如果少于1000，则发通知提醒
        if (amount < 1000) {
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "喝水提醒", NotificationManager.IMPORTANCE_DEFAULT);
                manager.createNotificationChannel(channel);
            }

            // 点击通知跳转到 WaterRecordActivity
            // 以下内容参考https://github.com/SteveCampos/NotificationCompat.git

            Intent i = new Intent(context, WaterRecordActivity.class);
            PendingIntent pi = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("喝水提醒")
                    .setContentText("今天喝水有点少，睡前别忘了再喝点^^")
                    .setContentIntent(pi)
                    .setAutoCancel(true);

            manager.notify(1, builder.build());
        }
    }
}
