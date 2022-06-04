package com.dubr0vin.taskscheduler.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dubr0vin.taskscheduler.R;
import com.dubr0vin.taskscheduler.db.Task;
import com.dubr0vin.taskscheduler.ui.holders.TaskViewHolder;

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
        Task task = (Task)tasks.get(position);
        holder.getTextView().setText(String.valueOf(position));
        holder.getEditText().setEnabled(false);
        holder.getEditText().setText(task.getValue());
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }
}
