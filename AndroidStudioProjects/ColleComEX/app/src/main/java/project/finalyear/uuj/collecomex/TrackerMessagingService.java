package project.finalyear.uuj.collecomex;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class TrackerMessagingService extends Service {




    /*public TrackerService() {
    }*/

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID){
        //Parser.compareItem();
        Log.e("onStartCommand", "running");
        //createNotification("Hello this is the message body", "Title");

        //return super.onStartCommand(intent, flags, startID);
        return START_STICKY;
    }
}
