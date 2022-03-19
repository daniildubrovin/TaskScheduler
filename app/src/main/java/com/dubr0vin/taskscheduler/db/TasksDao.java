package com.dubr0vin.taskscheduler.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public abstract class TasksDao {
    //TASK
    @Query("SELECT * FROM task")
    public abstract List<Task> getAllTasks();

    @Query("SELECT * FROM Task WHERE id = :idTask")
    public abstract Task getTask(int idTask);

    @Insert
    public abstract long insertTask(Task task);

    @Update
    public abstract  void editTask(Task task);

    @Delete
    public abstract void deleteTask(Task task);

    //DAY

    @Query("SELECT * FROM Day")
    public abstract List<Day> getAllDays();

    @Query("SELECT * FROM Day WHERE date = :date")
    public abstract Day getDay(String date);

    @Query("SELECT * FROM Day WHERE id = :idDay")
    public abstract Day getDay(int idDay);

    @Transaction
    public List<Task> getTasksForDay(int idDay){
        List<Task> tasks = new ArrayList<>();
        for (ConnectionDayWithTask connectionDayWithTask : getConnectionDayWithTask(idDay)){
            tasks.add(getTask(connectionDayWithTask.getIdTask()));
        }
        return tasks;
    }

    @Transaction
    public void insertDay(Day day,List<Integer> idTasks){
        insertDayWithoutTasks(day);
        for(int idTask : idTasks){
            Day lDay = getDay(day.getDate());
            connectDayWithTask(new ConnectionDayWithTask(lDay.getId(),idTask));
        }
    }

    @Transaction
    public void deleteDay(int idDay){
        for (ConnectionDayWithTask connectionDayWithTask : getConnectionDayWithTask(idDay)) deleteTaskFromDay(connectionDayWithTask);
        notSafeDeleteDay(idDay);
    }

    @Transaction @Insert
    public abstract void insertDayWithoutTasks(Day... day);

    @Insert
    public abstract void connectDayWithTask(ConnectionDayWithTask connectionDayWithTask);

    @Update
    public abstract void editDay(Day... day);

    @Query("DELETE FROM Day WHERE id = :idDay")
    public abstract void notSafeDeleteDay(int idDay);

    @Delete
    public abstract void deleteTaskFromDay(ConnectionDayWithTask... connectionDayWithTasks);

    @Query("SELECT * FROM ConnectionDayWithTask WHERE idDay = :idDay")
    public abstract List<ConnectionDayWithTask> getConnectionDayWithTask(int idDay);
}
