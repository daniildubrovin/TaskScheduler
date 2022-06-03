package com.dubr0vin.taskscheduler.db;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class TasksConverter {
    @TypeConverter
    public String toTasks(List<Task> tasks){
        return new Gson().toJson(tasks, new TypeToken<ArrayList>() {}.getType());
    }

    @TypeConverter
    public List<Task> fromTasks(String tasks){
        return tasks.isEmpty() ? new ArrayList<>() : new Gson().fromJson(tasks,List.class);
    }
}
