package com.dubr0vin.taskscheduler;

import android.app.Application;
import android.util.Log;

public class App extends Application {

    static public String TAG = "TaskScheduler";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"start application");
    }
}
