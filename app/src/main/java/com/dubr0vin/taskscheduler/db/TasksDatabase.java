package com.dubr0vin.taskscheduler.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Calendar.class,Task.class},version = 1)
public abstract class TasksDatabase extends RoomDatabase {

}
