package com.dubr0vin.taskscheduler.db;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Day day = (Day) o;
        return id == day.id && Objects.equals(date, day.date) && Objects.equals(tasks, day.tasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, tasks);
    }
}
