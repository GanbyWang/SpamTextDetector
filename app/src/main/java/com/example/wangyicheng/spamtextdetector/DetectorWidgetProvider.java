package com.example.wangyicheng.spamtextdetector;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler;
import android.telephony.SmsMessage;
import android.text.Html;
import android.util.Log;
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

    // Static strings for HTTP requests
    private final String SMS_RECIEVED = "android.provider.Telephony.SMS_RECEIVED";
    private final String PREDICT_URL = "http://18.222.232.141/predict/";

    // Private variables to connect the handler and the main thread
    // Not a good way to do though
    private Context privateContext = null;
    private String msgTime = "";
    private String msgAddress = "";
    private String msgBody = "";

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
        // Set the context variable for the handler
        privateContext = context;

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
                    msgTime = format.format(date);
                    msgTime = msgTime.substring(11);

                    msgAddress = msg.getOriginatingAddress();
                    msgBody = msg.getDisplayMessageBody();

                    String msgToSend = "{\"text\":\"" + msgBody + "\"}";

                    // Do the prediction
                    // Move everything to the handler
                    new HttpPost(msgToSend.getBytes(), PREDICT_URL,
                            predictHandler, HttpPost.TYPE_CHECK);
                }
            }
        }

        super.onReceive(context, intent);
    }

    // Private handler of HTTP POST request
    Handler predictHandler = new Handler() {

        /**
         * The overridden handleMessage function
         * @param: the message containing the POST response
         * */
        @Override
        public void handleMessage(Message msg) {

            // Get the result from the message
            String result = msg.obj.toString();

            // Switch the logic based on the types
            switch (msg.what) {

                // If the POST is successful
                case HttpPost.POST_SUCC:

                    if (privateContext != null) {

                        // Get the remote view of the layout
                        RemoteViews remoteViews = new RemoteViews(privateContext.getPackageName(),
                                R.layout.widget_layout);

                        // Generate the text view to present
                        String htmlSrc = "You received a message from <b>" + msgAddress + "</b> at "
                                + " <b>" + msgTime + "</b>.";

                        // Check if it's a spam text
                        if (result.equals("true")) {
                            htmlSrc += " <font color=\"red\">It's a spam text!</font>";
                        } else {
                            htmlSrc += " <font color=\"green\">It's not a spam text.</font>";
                        }

                        // Set the view
                        remoteViews.setTextViewText(R.id.message, Html.fromHtml(htmlSrc));

                        // Get the widget manager
                        AppWidgetManager appWidgetManager =
                                AppWidgetManager.getInstance(privateContext);
                        // Get all widgets
                        ComponentName componentName = new ComponentName(privateContext,
                                DetectorWidgetProvider.class);
                        // Update the all widgets
                        appWidgetManager.updateAppWidget(componentName, remoteViews);

                        // Use a toast to alert user
                        if (result.equals("true")) {
                            Toast.makeText(privateContext, "Spam Text Received!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    break;

                // If the POST failed
                case HttpPost.POST_FAIL:
                    Toast.makeText(privateContext, "POST failed!", Toast.LENGTH_SHORT).show();
                    break;

                // Unrecognized response type
                default:
                    Toast.makeText(privateContext, "Unrecognized response type!",
                            Toast.LENGTH_SHORT).show();
            }

        }
    };

}
