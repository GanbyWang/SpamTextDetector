package com.example.wangyicheng.spamtextdetector;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

/**
 * Class definition of the widget
 * Contains all execution logic of updating
 * Notice AppWidgetProvider is already the subclass of BroadcastReceiver
 */

public class DetectorWidgetProvider extends AppWidgetProvider {

    /* Currently the methods function as default
     * Might not include all functions
     * Always call super function first
     * TODO: override functions as needed */

    /**
     * Called when the first widget is created
     * @param context: the running context of the widget
     * */
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    /**
     * Called when the last widget is deleted
     * @param context: the running context of the widget
     * */
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    /**
     * Called when the widget is to update
     * @param context: the running context of the widget
     * @param appWidgetManager: widget manager
     * @param appWidgetIds: the id's of the widgets that to be updated
     * */
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    /**
     * Called when a widget is deleted
     * @param context: the running context of the widget
     * @param appWidgetIds: the id's of the widgets that have been deleted
     * */
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    /**
     * Called when the widget receives a broadcast
     * @param context: the running context of the widget
     * @param intent: the object used for broadcast
     * */
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }
}
