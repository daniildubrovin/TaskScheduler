package com.dubr0vin.taskscheduler.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Calendar {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String date;
    private int idTask;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getIdTask() {
        return idTask;
    }

    public void setIdTask(int idTask) {
        this.idTask = idTask;
    }
}
