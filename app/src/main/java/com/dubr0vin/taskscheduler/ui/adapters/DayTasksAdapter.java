package com.dubr0vin.taskscheduler.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dubr0vin.taskscheduler.App;
import com.dubr0vin.taskscheduler.R;
import com.dubr0vin.taskscheduler.db.Day;
import com.dubr0vin.taskscheduler.db.Task;
import com.dubr0vin.taskscheduler.db.TasksDao;
import com.dubr0vin.taskscheduler.ui.holders.TaskViewHolder;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class DayTasksAdapter extends RecyclerView.Adapter<TaskViewHolder> {

    private final List<Task> tasks;
    private final Day day;
    private final App app;
    TextView statisticsTextView;
    private final TasksDao tasksDao;

    public DayTasksAdapter(Day day, App app, TextView statisticsTextView) {
        this.day = day;
        this.app = app;
        this.statisticsTextView = statisticsTextView;
        this.tasksDao = app.db.tasksDao();
        this.tasks = day.getTasks();
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

        holder.getCheckBox().setChecked(task.isInCalendar());
        holder.getCheckBox().setOnCheckedChangeListener((buttonView, isChecked) -> {
            tasks.get(holder.getAdapterPosition()).setInCalendar(isChecked);
            app.runInDBThread(() -> {
                tasksDao.editDay(day);
                List<Day> days = tasksDao.getAllDays();
                app.runInUI(() -> setStatisticsText(days));
            });
        });
    }

    private void setStatisticsText(List<Day> days){
        int allCount = 0, doneCount = 0;
        for(Day day: days) {
            for(Task task: day.getTasks()){
                if(task.isInCalendar()) doneCount++;
                allCount++;
            }
        }
        double percents = (double) doneCount / allCount * 100;

        statisticsTextView.setTextColor(percents >= 80 ? app.getResources().getColor(R.color.green) : percents > 50 ? app.getResources().getColor(R.color.orange) : app.getResources().getColor(R.color.red));
        statisticsTextView.setText(R.string.statistics_text);
        statisticsTextView.append(" " + NumberFormat.getNumberInstance().format(percents) + "%");
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }
}
