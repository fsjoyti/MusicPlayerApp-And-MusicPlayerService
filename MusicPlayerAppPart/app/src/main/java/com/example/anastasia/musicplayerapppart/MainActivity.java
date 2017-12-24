package com.example.anastasia.musicplayerapppart;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
  Button playButton;
  Button pauseButton;
  Button stopButton;
    Messenger myService = null;
    Intent serviceIntent;
    boolean isBound;

    @Override
    protected void onDestroy() {
        unbindService(myConnection);
        isBound = false;
        myService = null;
        super.onDestroy();

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        playButton = (Button)findViewById(R.id.button8);
        pauseButton = (Button)findViewById(R.id.button7);
        stopButton    =(Button)findViewById(R.id.button9);
        serviceIntent = new Intent();
        ComponentName componentName = new ComponentName("com.example.anastasia.musicplayerservicepart","com.example.anastasia.musicplayerservicepart.MusicPlayerService");
        serviceIntent.setComponent(componentName);
        bindService(serviceIntent,myConnection, Context.BIND_AUTO_CREATE);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isBound) {
                    Message message = Message.obtain(null, 1, 0, 0);

                    try {
                        myService.send(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                else{
                    Toast.makeText(getApplicationContext(),"No Service Available",Toast.LENGTH_LONG);
                }



            }
        });
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBound) {
                    Message message = Message.obtain(null, 2, 0, 0);
                    try {
                        myService.send(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"No Service Available",Toast.LENGTH_LONG);
                }

            }
        });
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBound) {
                    Message message = Message.obtain(null, 3, 0, 0);
                    try {
                        myService.send(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"No Service Available",Toast.LENGTH_LONG);
                }


            }
        });
    }

@Override
protected  void onResume(){
    super.onResume();
    serviceIntent = new Intent();
    ComponentName componentName = new ComponentName("com.example.anastasia.musicplayerservicepart","com.example.anastasia.musicplayerservicepart.MusicPlayerService");
    serviceIntent.setComponent(componentName);

    bindService(serviceIntent,myConnection, Context.BIND_AUTO_CREATE);

}
    private ServiceConnection myConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            myService = new Messenger(service);
            isBound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            myService = null;
            isBound = false;
        }
    };

}
