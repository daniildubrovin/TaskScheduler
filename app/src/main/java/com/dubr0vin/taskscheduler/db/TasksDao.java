package com.dubr0vin.taskscheduler.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public abstract class TasksDao {
    //TASK
    @Query("SELECT * FROM task")
    public abstract List<Task> getAllTasks();

    @Query("SELECT * FROM task WHERE task.inCalendar")
    public abstract List<Task> getInCalendarTasks();

    @Query("SELECT * FROM Task WHERE id = :idTask")
    public abstract Task getTask(long idTask);

    @Insert
    public abstract void insertTask(Task task);

    @Update
    public abstract  void editTask(Task task);

    @Delete
    public abstract void deleteTask(Task task);

    @Transaction
    public void deleteAllTasks(){
        for (Task task: getAllTasks()) deleteTask(task);
    }

    //DAY
    @Query("SELECT * FROM Day")
    public abstract List<Day> getAllDays();

    @Query("SELECT * FROM Day WHERE date = :date")
    public abstract Day getDay(String date);

    @Query("SELECT * FROM Day WHERE id = :idDay")
    public abstract Day getDay(int idDay);

    @Insert
    public abstract void insertDay(Day day);

    @Update
    public abstract void editDay(Day day);

    @Transaction
    public void updateDayTasks(Day day){
        for (int i = 0; i < day.getTasks().size(); i++) {
            Task oldTask = day.getTasks().get(i);
            Task newTask = getTask(oldTask.getId());
            if(newTask == null || !newTask.isInCalendar()){
                day.getTasks().remove(i);
                i--;
            }
            else oldTask.setValue(newTask.getValue());
        }
        editDay(day);
    }

    @Delete
    public abstract void deleteDay(Day day);

    @Transaction
    public void deleteAllDays(){
        for(Day day : getAllDays()) deleteDay(day);
    }
}