package com.example.administrator.myapp;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

public class MyIntentService extends IntentService {

    public MyIntentService() {
        super("MyIntentService");
    }
    public MyIntentService(String name) {
        super(name);
    }

    @Override
    public void onCreate(){
        Log.v("Service", "OnCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Log.v("MyIntentService", "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent){
        Log.v("MyIntentService--", Thread.currentThread().getName() + "--" + intent.getStringExtra("info") );
        for(int i = 0; i < 100; i++){ //耗时操作
            Log.v("MyIntentService--",  i + "--" + Thread.currentThread().getName());
        }
    }

    @Override
    public void onDestroy(){
        Log.v("MyIntentService--", "onDestroy");
        super.onDestroy();
    }
}
