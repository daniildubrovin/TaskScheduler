package com.dubr0vin.taskscheduler.db;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class ConnectionDayWithTask {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int idDay;
    private int idTask;

    public ConnectionDayWithTask() {
    }

    @Ignore
    public ConnectionDayWithTask(int idDay, int idTask) {
        this.idDay = idDay;
        this.idTask = idTask;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getIdDay() {
        return idDay;
    }

    public void setIdDay(int idDay) {
        this.idDay = idDay;
    }

    public int getIdTask() {
        return idTask;
    }

    public void setIdTask(int idTask) {
        this.idTask = idTask;
    }
}
