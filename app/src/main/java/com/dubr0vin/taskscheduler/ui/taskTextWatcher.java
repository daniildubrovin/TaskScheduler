package com.dubr0vin.taskscheduler.ui;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import com.dubr0vin.taskscheduler.App;
import com.dubr0vin.taskscheduler.db.Task;
import com.dubr0vin.taskscheduler.db.TasksDao;

import java.util.ArrayList;

public class taskTextWatcher implements TextWatcher {
    ArrayList<Task> tasks;
    TaskViewHolder holder;
    App app;

    public taskTextWatcher(ArrayList<Task> tasks, TaskViewHolder holder, App app) {
        this.tasks = tasks;
        this.holder = holder;
        this.app = app;
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        Task task = tasks.get(holder.getAdapterPosition());
        task.setValue(charSequence.toString());
        app.dbThreadPool.execute(() -> app.db.tasksDao().editTask(task));
    }

    @Override
    public void afterTextChanged(Editable editable) { }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
}
