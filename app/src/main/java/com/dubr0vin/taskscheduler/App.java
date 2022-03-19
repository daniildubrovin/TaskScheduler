package com.dubr0vin.taskscheduler;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.room.Room;

import com.dubr0vin.taskscheduler.db.TasksDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App extends Application {
    static public String TAG = "TaskScheduler";
    public TasksDatabase db;
    public ExecutorService dbThreadPool;
    public Handler uiThread;
    public int COUNT_GENERATE_DAY = 10;

    @Override
    public void onCreate() {
        super.onCreate();
        dbThreadPool = Executors.newFixedThreadPool(4);
        uiThread = new Handler(Looper.getMainLooper());
        db = Room.databaseBuilder(getApplicationContext(),TasksDatabase.class,"tasks").build();
    }
}
