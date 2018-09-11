package com.example.administrator.myapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


public class DisplayMsgActivity extends Activity implements Runnable{

    private  Thread  thread = null;
    private  int i;
    public   boolean  bExit;

    private Handler  handler = new Handler(){
        @Override
        public void handleMessage(@Nullable  Message msg) {
            switch (msg.what) {
                case 1:
                {
                    TextView ctrlText = findViewById(R.id.textView2);
                    ctrlText.setText(String.valueOf(msg.obj));
                }
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_msg);
        Intent intent = getIntent();
        String strMsg = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        TextView ctrlText = findViewById(R.id.textView2);
        ctrlText.setText(strMsg);
        bExit = false;
        thread = null;
        i = 0;
    }

    public void sendResult(View view) {
        setResult(2, (new Intent()).setAction("corky!"));
        finish();
    }

    public void startThrd(View view) {
        /*
        bExit = false;
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }*/
        //service
        Intent intent = new Intent(this, MyIntentService.class);
        intent.putExtra("info", "good good study");
        startService(intent);
    }

    public void stopThrd(View view) {
        /*
        bExit = true;
        if (thread != null) {
            thread.interrupt();
            thread = null;
            Log.v("Thread", "Thread interrupted");
        }*/
    }

    @Override
    public void run() {
        while (!bExit) {
            i++;
            Log.v("Thread", String.valueOf(i));
            try {
                Message msg = new Message();
                msg.obj = String.valueOf(i);
                msg.what = 1;
                handler.sendMessage(msg);
                Thread.sleep(2000);
            }catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void onDestroy() {
        bExit = true;
        if (thread != null) {
            thread.interrupt();
            thread = null;
            Log.v("onDestroy Thread", "Thread interrupted 0000");
        }
        super.onDestroy();
    }
}
