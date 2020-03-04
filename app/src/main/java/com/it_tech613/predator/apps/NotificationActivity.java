package com.it_tech613.predator.apps;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


public class NotificationActivity extends Activity {
    @SuppressLint("WrongConstant")
    public static PendingIntent pending(int i, Context context) {
        String str = "CANCELLED";
        Intent intent = new Intent(context, NotificationActivity.class);
        intent.setFlags(268468224);
        intent.putExtra("NOTIFICATION_ID", i);
        return PendingIntent.getActivity(context, 0, intent, 268435456);
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancel(getIntent().getIntExtra("NOTIFICATION_ID", -1));
            finish();
            return;
        }
        throw new AssertionError();
    }
}
