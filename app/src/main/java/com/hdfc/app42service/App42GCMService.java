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

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.hdfc.caretaker.DashboardActivity;
import com.hdfc.caretaker.R;
import com.hdfc.libs.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;

/**
 * Created by Admin on 1/22/2016.
 */
public class App42GCMService extends IntentService {


    public static final String DisplayMessageAction = "com.example.app42sample.DisplayMessage";
    public static final String ExtraMessage = "message";
    private static final String App42GeoTag = "app42_geoBase";
    private static final String Alert = "alert";
    public static GoogleCloudMessaging gcm = null;
    // public static final String ExtraMessage = "message";
    static int msgCount = 0;
    private final String title = "NewZeal";
    private NotificationManager mNotificationManager = null;

    public App42GCMService() {
        super("GcmIntentService");
    }

    public static void unRegisterGcm() {
        try {
            if (gcm != null)
                gcm.unregister();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);
        if (!extras.isEmpty()) {
            if ("send_error".equals(messageType)) {
                //App42Log.debug("Send error: " + extras.toString());
                App42GCMReceiver.completeWakefulIntent(intent);
            } else if ("deleted_messages".equals(messageType)) {
                //App42Log.debug("Deleted messages on server: " + extras.toString());
                App42GCMReceiver.completeWakefulIntent(intent);
            } else if ("gcm".equals(messageType)) {
                String message = intent.getExtras().getString("message");
                //App42Log.debug("Received: " + extras.toString());
                //App42Log.debug("Message: " + message);
                this.validatePushIfRequired(message, intent);
            }
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
        this.broadCastMessage(message);
        this.sendNotification(message);
        App42GCMReceiver.completeWakefulIntent(intent);
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

        } catch (JSONException e) {
            e.printStackTrace();
            //Log.e("HDFC", "2");
            showNotification(message, intent);
        }
    }

    private void sendNotification(String msg) {
        //check wth vinay
        long when = System.currentTimeMillis();
        this.mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent;

        notificationIntent = new Intent(this, DashboardActivity.class);

        notificationIntent.putExtra("message_delivered", true);
        notificationIntent.putExtra("message", msg);
        notificationIntent.setFlags(603979776);//603979776 Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        //
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        mBuilder.setSmallIcon(R.mipmap.ic_launcher).setContentTitle(title).setContentText(msg).setWhen(when).setNumber(++msgCount).setAutoCancel(true)
                .setDefaults(1).setDefaults(2)
                .setLights(Notification.DEFAULT_LIGHTS, 5000, 5000)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

        mBuilder.setContentIntent(contentIntent);

        this.mNotificationManager.notify(1, mBuilder.build());
    }

    /**
     * @param message
     */
    public void broadCastMessage(String message) {
        Intent intent = new Intent(DisplayMessageAction);
        intent.putExtra(ExtraMessage, message);
        this.sendBroadcast(intent);
    }


}
