package com.dubr0vin.taskscheduler.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity
public class Task {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private boolean inCalendar;
    private String value;

    public Task() { }

    @Ignore
    public Task(boolean inCalendar, String value) {
        this.inCalendar = inCalendar;
        this.value = value;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isInCalendar() {
        return inCalendar;
    }

    public void setInCalendar(boolean inCalendar) {
        this.inCalendar = inCalendar;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @NonNull
    @Ignore @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", inCalendar=" + inCalendar +
                ", value='" + value + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && inCalendar == task.inCalendar && Objects.equals(value, task.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, inCalendar, value);
    }
}
