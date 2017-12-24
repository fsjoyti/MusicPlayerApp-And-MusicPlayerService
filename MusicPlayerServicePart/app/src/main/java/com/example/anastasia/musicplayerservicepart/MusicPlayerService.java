package com.example.anastasia.musicplayerservicepart;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Button;
import android.widget.Toast;

import com.intentfilter.androidpermissions.PermissionManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class MusicPlayerService extends Service {
    private MediaPlayer mp;
    static final int PLAY = 1;
    static final int PAUSE = 2;
    static final int STOP = 3;
    private static ArrayList<SongObject> songsList = new ArrayList<SongObject>();
    private int currentSongIndex = 0;
    private String mSelectionClause = MediaStore.Audio.Media.IS_MUSIC + " = 1";
    private static final String CHANNEL_ID = "media_playback_channel";
    private boolean isPaused;
    private boolean isStopped = false;
    private boolean isStarted = false;
    final Messenger myMessenger = new Messenger(new IncomingHandler());
    private Thread backgroundThread;
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case PLAY:
                    if (isPaused){
                        mp.start();
                        isPaused = false;
                    }
                    else {
                        if ( isStarted == false &&backgroundThread !=null ){
                        backgroundThread.start();
                        isStarted = true;

                        }
                        else{

                        }

                    }


                    break;
                case PAUSE:
                    if (mp.isPlaying()) {
                        mp.pause();
                        isPaused = true;
                    }
                    break;
                case STOP:
                    mp.stop();
                   mp.release();
                   backgroundThread = null;

                    break;
                default:
                    super.handleMessage(msg);



            }


        }
    }


    public MusicPlayerService() {
    }
    @Override
    public void onCreate(){
        super.onCreate();
        mp = new MediaPlayer();
        populateSongsList();
        backgroundThread = new Thread(new Runnable() {
            @Override
            public void run() {
                playSong(currentSongIndex);
            }
        });
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                    currentSongIndex = (currentSongIndex + 1) % songsList.size();
                    playSong(currentSongIndex);
            }
        });

    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
     if (intent.getAction().equals("StopMusicPlayerService")){
         try{
             if (mp.isPlaying()){
                 mp.release();

             }
             Thread dummy = backgroundThread;
             backgroundThread = null;
             dummy.interrupt();

         }
         catch (Exception e){
             e.printStackTrace();

         }
         stopForeground(true);
         stopSelf();
     }

        return  START_STICKY;
    }

    @Override
    public void onDestroy() {

        if (mp!= null ){
            mp.release();
            mp = null;
        }
        if (backgroundThread != null){
            Thread dummy = backgroundThread;
            backgroundThread = null;
            dummy.interrupt();
        }
        super.onDestroy();


    }

    public void playSong(int songIndex) {
        // Play song if index is within the songsList
        if (songIndex < songsList.size() && songIndex >= 0) {
            try {
                mp.stop();
                mp.reset();
                mp.setDataSource(songsList.get(songIndex).getFilePath());
                mp.prepare();
                mp.start();
                // Displaying Song title


                // Changing Button Image to pause image

                // Update song index
                currentSongIndex = songIndex;
                SongObject song = songsList.get(currentSongIndex);
                notifyUser(song);




            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (songsList.size() > 0) {
            playSong(currentSongIndex);
        }
    }

    private void notifyUser(SongObject song) {
        Intent intent = new Intent(this,StopForegroundServiceActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,CHANNEL_ID);
        mBuilder.setContentTitle("Music started playing");
        mBuilder.setContentText(song.getTitle());
        mBuilder.setSmallIcon(R.drawable.ic_audiotrack_black_24dp);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setAutoCancel(true);
        Notification notification = mBuilder.build();
        NotificationManager notificationManager  = (NotificationManager)getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        //notificationManager.notify(0,notification);
        if (currentSongIndex == 0){
            startForeground(0,notification);
            notificationManager.notify(0,notification);}
        else {
            notificationManager.notify(0,notification);
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
       return  myMessenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);

    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        playSong(currentSongIndex);
    }

    public void populateSongsList() {
        //TODO add all songs from audio content URI to this.songsList
        // Get a Cursor object from the content URI
        Cursor mCursor = getContentResolver().query(MediaStore.Audio.Media.
                        EXTERNAL_CONTENT_URI, new String[]{MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA},
                mSelectionClause, null, MediaStore.Audio.Media.TITLE);

        // Use the cursor to loop through the results and add them to
        //		the songsList as SongObjects
        while (mCursor.moveToNext()) {
            String title = mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            // String album = mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
            songsList.add(new SongObject(title, path));
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationManager
                mNotificationManager =
                (NotificationManager) getApplicationContext()
                        .getSystemService(Context.NOTIFICATION_SERVICE);
        String id = CHANNEL_ID;
        CharSequence name = "Media playback";
        String description = "Media playback controls";
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel mChannel = new NotificationChannel(id, name, importance);
        mChannel.setDescription(description);
        mChannel.enableLights(true);
        mChannel.setLightColor(Color.RED);
        mChannel.enableVibration(true);
        mChannel.setShowBadge(false);
        mChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        mNotificationManager.createNotificationChannel(mChannel);
    }
}
