package com.tcl.myapplication.view;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.tcl.myapplication.MainActivity;
import com.tcl.myapplication.R;
import com.tcl.myapplication.util.SerialManager;

/**
 * Created by lishiwei on 2017/3/27.
 */

public class BasicWidget extends AppWidgetProvider {
    private static final String TAG = BasicWidget.class.getSimpleName();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int i = 0; i < appWidgetIds.length; i++) {
            int appId = appWidgetIds[i];
            Log.d(TAG, "onUpdate: "+appId);
            Intent intent = new Intent(SerialManager.CLOSEACTION);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0,intent,0);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
            remoteViews.setOnClickPendingIntent(R.id.textview,pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetIds,remoteViews);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        super.onRestored(context, oldWidgetIds, newWidgetIds);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }
}
