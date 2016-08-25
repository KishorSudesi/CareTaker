package com.hdfc.app42service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.hdfc.caretaker.DashboardActivity;
import com.hdfc.caretaker.MainActivity;
import com.hdfc.caretaker.R;
import com.hdfc.config.Config;
import com.hdfc.libs.SessionManager;
import com.hdfc.libs.Utils;
import com.hdfc.service.UpdateService;

import org.json.JSONObject;

import java.text.ParseException;
import java.util.Random;

import static android.support.v4.app.NotificationCompat.VISIBILITY_PUBLIC;

/**
 * Created by Admin on 1/22/2016.
 */
public class App42GCMService extends IntentService {


    public static final String DisplayMessageAction = "com.hdfc.careTaker.DashboardActivity";
    public static final String ExtraMessage = "message";
    private static final String App42GeoTag = "app42_geoBase";
    private static final String Alert = "alert";
    public static GoogleCloudMessaging gcm = null;
    private final static String GROUP_KEY = "activity";
    // public static final String ExtraMessage = "message";
    private static int msgCount = 0;
    private Utils utils;
    private SessionManager sessionManager;

    public App42GCMService() {
        super("GcmIntentService");
    }


    protected void onHandleIntent(Intent intent) {
        try {
            Bundle extras = intent.getExtras();
            gcm = GoogleCloudMessaging.getInstance(this);
            utils = new Utils(this);
            sessionManager = new SessionManager(this);
            String messageType = gcm.getMessageType(intent);
            if (!extras.isEmpty()) {
                if ("send_error".equals(messageType)) {
                    //App42Log.debug("Send error: " + extras.toString());
                    App42GCMReceiver.completeWakefulIntent(intent);
                } else if ("deleted_messages".equals(messageType)) {
                    //App42Log.debug("Deleted messages on server: " + extras.toString());
                    App42GCMReceiver.completeWakefulIntent(intent);
                } else if ("gcm".equals(messageType)) {
                    SessionManager sessionManager = new SessionManager(App42GCMService.this);
                    if (sessionManager.isLoggedIn()) {
                        String message = intent.getExtras().getString("message");
                        //App42Log.debug("Received: " + extras.toString());
                        //App42Log.debug("Message: " + message);
                        this.validatePushIfRequired(message, intent);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void showNotification(String message, Intent intent) {
       /* DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        Date date = Calendar.getInstance().getTime();
        date.getTime();
        SimpleDateFormat dateTime = new SimpleDateFormat("yyyy-MM-DD HH:mm:ss", Locale.US);
        String dt = dateTime.format(date);
        db.createNotification(title, message, dt);
        db.close();*/
        try {
            this.broadCastMessage(message);
            this.sendNotification(message);
            App42GCMReceiver.completeWakefulIntent(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //
    private void validatePushIfRequired(String message, final Intent intent) {
        try {
            final JSONObject jsonObject = new JSONObject(message);

            if (jsonObject.has("created_by")) {

                String strMessage = null;
                try {
                    strMessage = jsonObject.getString(App42GCMService.ExtraMessage)
                            + "\n Created On: " + Utils.writeFormat.format(Utils.readFormat.parse(jsonObject.getString("time")));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                try {
                    Intent in = new Intent(App42GCMService.this, UpdateService.class);
                    in.putExtra("message", message);
                    in.putExtra("updateAll", false);
                    startService(in);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                showNotification(strMessage, intent);
            } else {

                final String geoBaseType = jsonObject.optString(App42GeoTag, null);

                /*if (geoBaseType == null) {
                    showNotification(json.toString(),intent);
                }*/

                final String alertMessage = jsonObject.optString(Alert, null);

                if (alertMessage != null) {
                    showNotification(jsonObject.getString("alert"), intent);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            //Log.e("HDFC", "2");
            showNotification(message, intent);
        }
    }

    private void sendNotification(String msg) {

        try {
            long when = System.currentTimeMillis();

            NotificationManager mNotificationManager = (NotificationManager) this.
                    getSystemService(Context.NOTIFICATION_SERVICE);
            Class classToCall = null;
            if (Config.customerModel != null && Config.dependentModels.size() > 0) {
                classToCall = DashboardActivity.class;
            } else {
                classToCall = MainActivity.class;
            }

            Intent notificationIntent;

            notificationIntent = new Intent(this, classToCall);

            notificationIntent.putExtra("message_delivered", true);
            notificationIntent.putExtra(ExtraMessage, msg);
            notificationIntent.putExtra("LOAD", false);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            //
            // The stack builder object will contain an artificial back stack for the
            // started Activity.
            // This ensures that navigating backward from the Activity leads out of
            // your application to the Home screen.
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            // Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(classToCall);
            // Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(notificationIntent);
            //

            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

            mBuilder.setSmallIcon(R.mipmap.ic_launcher).setContentTitle(getString(R.string.text_notification_header)).
                    setContentText(msg).setWhen(when).setNumber(++msgCount)
                    .setDefaults(1).setDefaults(2)
                    .setLights(Notification.DEFAULT_LIGHTS, 5000, 5000)
                    .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                    .setAutoCancel(true)
                    .setGroup(GROUP_KEY) //GROUP_KEY strClientId
                    .setVisibility(VISIBILITY_PUBLIC)
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            //String[] events = msg.split(""); //new String[6]
            // Sets a title for the Inbox in expanded layout
            inboxStyle.setBigContentTitle(getString(R.string.text_notification_header));

            // Moves events into the expanded layout
       /* for (String event : events) {
            inboxStyle.addLine(event);
        }*/
            // Moves the expanded layout object into the notification object.
            inboxStyle.addLine(msg);
            mBuilder.setStyle(inboxStyle);

            mBuilder.setContentIntent(contentIntent);

            Random random = new Random();
            int m = random.nextInt(9999 - 1000) + 1000;
            Config.intSelectedMenu = Config.intDashboardScreen;
            mNotificationManager.notify(m, mBuilder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param message
     */
    public void broadCastMessage(String message) {
        try {
            Intent intent = new Intent(DisplayMessageAction);
            intent.putExtra(ExtraMessage, message);
            this.sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
