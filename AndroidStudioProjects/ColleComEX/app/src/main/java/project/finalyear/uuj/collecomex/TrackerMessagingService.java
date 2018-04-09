package project.finalyear.uuj.collecomex;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class TrackerMessagingService extends Service {
    private Timer mTimer1;
    private TimerTask mTt1;
    private Handler mTimerHandler = new Handler();
    Context context = this;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID){
        Log.e("onStartCommand", "running");
        startTimer();
        return START_STICKY;
    }//end onStartCommand

    private void stopTimer(){
        if(mTimer1 != null){
            mTimer1.cancel();
            mTimer1.purge();
        }//end if
    }//end stopTimer

    private void startTimer(){
        mTimer1 = new Timer();
        mTt1 = new TimerTask() {
            @Override
            public void run() {
                mTimerHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final Contract.TrackerDbHelper mDbHelper = new Contract.TrackerDbHelper(context);
                            final SQLiteDatabase db = mDbHelper.getWritableDatabase();
                            final SQLiteDatabase dbRead = mDbHelper.getReadableDatabase();
                            Log.e("Timer", "Run Check");
                            if (Parser.compareItem(dbRead)) {
                                createNotification("Item updated!", "Open app for details");
                            }//end if
                        }catch(Exception e){
                            Log.v("Exception", "Timer exception caught");
                            Log.v("Exception", e.toString());
                        }//end catch
                    }//end run
                });//end post
            }//end run
        };
        mTimer1.schedule(mTt1, 1, 300000);
    }//end startTimer

    public void createNotification(String messageBody, String messageTitle){
        Log.e("createNotification", "Running");
        String CHANNEL_ID = "Tracker";

        Intent intent = new Intent(this, android.app.AlertDialog.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Uri notificationSoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(notificationSoundURI)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            CharSequence name = "Tracker";
            String description = "Tracks items";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, mBuilder.build());

    }//end createNotification

}//end service
