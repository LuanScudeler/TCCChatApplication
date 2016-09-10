package br.chatup.tcc.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.view.View;

import org.jxmpp.util.XmppStringUtils;

import java.nio.charset.Charset;

import br.chatup.tcc.myapplication.R;

/**
 * Created by jadson on 3/27/16.
 */
public class Util {

    public static String getTagForClass(Class clazz) {
        return Constants.LOG_TAG + clazz.getSimpleName();
    }

    public static String getStringResource(View v, int id) {
        return v.getResources().getString(id);
    }

    public static String getStringResource(Activity activity, int id) {
        return activity.getResources().getString(id);
    }

    public static String parseByteArrayToStr(byte[] bytes) {
        String str = new String(bytes, Charset.defaultCharset());
        return str;
    }

    public static String toCapital(String str) {
        String cap = "";

        for (int cont = 0; cont < str.length(); cont++) {
            if (cont == 0)
                cap += Character.toUpperCase(str.charAt(cont));
            else
                cap += str.charAt(cont);
        }
        return cap;
    }

    public static String parseContactName(String contactJID) {
        String[] split = contactJID.split("@");
        String pJID = split[0];
        pJID = pJID.toUpperCase();

        return pJID;
    }

    public static boolean anyNull(Object[] fields) {
        for (Object o : fields)
            if (o == null) return true;
        return false;
    }

    public static void showNotification(Context ct, Class<?> clazz, String contactJID ,String msgBody){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ct)
                .setSmallIcon(R.drawable.notification_icon_mdpi)
                .setLargeIcon(BitmapFactory.decodeResource(ct.getResources(), R.drawable.notification_icon_xhdpi))
                .setTicker(contactJID)
                .setContentTitle(XmppStringUtils.parseLocalpart(contactJID))
                .setContentText(msgBody)
                .setAutoCancel(true);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(ct, clazz);
        resultIntent.putExtra("contactJID", contactJID);
        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(ct);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(clazz);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        // Get Android notification service
        NotificationManager mNotificationManager = (NotificationManager) ct.getSystemService(Context.NOTIFICATION_SERVICE);

        // Configure
        Notification n = mBuilder.build();
        n.vibrate = new long[]{150, 300, 150, 600};

        // First parameter refers to notification id, notification can be modified later
        mNotificationManager.notify(R.drawable.notification_icon_mdpi, n);
    }
}
