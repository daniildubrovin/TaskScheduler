package com.dubr0vin.taskscheduler.db;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;

@Entity
public class Day {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String date;

    public Day() { }

    @Ignore
    public Day(String date) {
        this.date = date;
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
}
