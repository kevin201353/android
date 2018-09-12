package com.example.administrator.myapp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class Welcome extends AppCompatActivity {

    //类似启动一个定时器
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_welcome);
        handler.postDelayed(DelayRunnable, 1000);
    }

    protected Runnable DelayRunnable = new Runnable() {
        @Override
        public void run() {
            Welcome.this.finish();
            startActivity(new Intent(Welcome.this, MainActivity.class));
        }
    };

    @Override
    protected void onStop(){
        handler.removeCallbacks(DelayRunnable);
        Log.v("Welcome", "close handler DelayRunnable");
        super.onStop();
    }
}
