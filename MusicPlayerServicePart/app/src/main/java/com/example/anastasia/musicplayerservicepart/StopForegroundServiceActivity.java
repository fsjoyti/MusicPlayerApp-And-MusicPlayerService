package com.example.anastasia.musicplayerservicepart;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class StopForegroundServiceActivity extends AppCompatActivity {
    Button stopservice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_foreground_service);
        stopservice = (Button)findViewById(R.id.button);
        stopservice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"stop service button called",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(StopForegroundServiceActivity.this,MusicPlayerService.class);
                intent.setAction("StopMusicPlayerService");
                  startService(intent);
            }
        });
    }
}
