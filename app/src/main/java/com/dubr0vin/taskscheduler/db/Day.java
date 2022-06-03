package com.dubr0vin.taskscheduler.db;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Day {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String date;

    @TypeConverters({TasksConverter.class})
    private List<Task> tasks;

    public Day() { }

    @Ignore
    public Day(String date, List<Task> tasks) {
        this.date = date;
        this.tasks = tasks;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}
