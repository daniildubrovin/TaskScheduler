package com.dubr0vin.taskscheduler.ui;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import com.dubr0vin.taskscheduler.App;

import java.util.ArrayList;

public class taskTextWatcher implements TextWatcher {
    ArrayList<String> tasks;
    TaskAdapter.TaskViewHolder holder;

    public taskTextWatcher(ArrayList<String> tasks, TaskAdapter.TaskViewHolder holder) {
        this.tasks = tasks;
        this.holder = holder;
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        tasks.set(holder.getAdapterPosition(),charSequence.toString());
    }

    @Override
    public void afterTextChanged(Editable editable) { }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
}
