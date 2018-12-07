package com.example.wangyicheng.spamtextdetector;

import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.text.Html;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class definition of the widget
 * Contains all execution logic of updating
 * Notice AppWidgetProvider is already the subclass of BroadcastReceiver
 */

public class DetectorWidgetProvider extends AppWidgetProvider {

    private final String SMS_RECIEVED = "android.provider.Telephony.SMS_RECEIVED";

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

        // Get the corresponding action of the broadcast
        String action = intent.getAction();
        // Get the bundle
        Bundle bundle = intent.getExtras();

        // Check if it's the correct broadcast
        if (action.equals(SMS_RECIEVED) && bundle != null) {

            // Get the message object
            Object[] smsObj = (Object[]) bundle.get("pdus");

            if (smsObj != null) {

                for (Object object : smsObj) {

                    // Get all message information
                    SmsMessage msg = SmsMessage.createFromPdu((byte[]) object);
                    Date date = new Date(msg.getTimestampMillis());
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    // Only need the time
                    String msgTime = format.format(date);
                    msgTime = msgTime.substring(11);

                    String msgAddress = msg.getOriginatingAddress();
                    String msgBody = msg.getDisplayMessageBody();

                    // TODO: do the prediction
                    boolean ifSpam = false;

                    // Get the remote view of the layout
                    RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                            R.layout.widget_layout);

                    // Seems we cannot place the whole message in the widget
                    /* String htmlSrc = "You received a message from <b>" + msgAddress + "</b> at "
                            + " <b>" + msgTime + "</b>, and it says that <i>" + msgBody + "</i>.";
                            */

                    String htmlSrc = "You received a message from <b>" + msgAddress + "</b> at "
                            + " <b>" + msgTime + "</b>.";

                    if (ifSpam == true) {
                        htmlSrc += " <font color=\"red\">It's a spam text!</font>";
                    } else {
                        htmlSrc += " <font color=\"green\">It's not a spam text.</font>";
                    }

                    remoteViews.setTextViewText(R.id.message, Html.fromHtml(htmlSrc));

                    // Get the widget manager
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                    // Get all widgets
                    ComponentName componentName = new ComponentName(context,
                            DetectorWidgetProvider.class);
                    // Update the all widgets
                    appWidgetManager.updateAppWidget(componentName, remoteViews);

                    // Use a toast to alert user
                    if (ifSpam == true) {
                        Toast.makeText(context, "Spam Text Received!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

        super.onReceive(context, intent);
    }
}
