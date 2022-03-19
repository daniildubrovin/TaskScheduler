package com.dubr0vin.taskscheduler.ui;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dubr0vin.taskscheduler.R;
import com.dubr0vin.taskscheduler.db.Task;

import java.util.ArrayList;
import java.util.List;

public class DayAdapter extends RecyclerView.Adapter<TaskViewHolder> {

    private final ArrayList<Task> tasks;

    public DayAdapter(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TaskViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.getCheckBox().setChecked(task.isInCalendar());
        holder.getTextView().setText(String.valueOf(position));
        holder.getEditText().setText("");
        holder.getEditText().append(task.getValue());
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }
}
