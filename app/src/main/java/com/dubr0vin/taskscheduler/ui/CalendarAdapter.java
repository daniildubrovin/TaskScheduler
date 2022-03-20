package com.dubr0vin.taskscheduler.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dubr0vin.taskscheduler.App;
import com.dubr0vin.taskscheduler.R;
import com.dubr0vin.taskscheduler.db.Day;
import com.dubr0vin.taskscheduler.db.Task;
import com.dubr0vin.taskscheduler.db.TasksDao;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.DayViewHolder> {
    private final App app;
    private final TasksDao tasksDao;
    private final ArrayList<Day> days;
    private final List<List<Task>> tasksForDay;
    private final RecyclerView recyclerView;
    private final ProgressBar progressBar;

    public CalendarAdapter(App app, RecyclerView recyclerView, ProgressBar progressBar) {
        this.app = app;
        this.recyclerView = recyclerView;
        this.progressBar = progressBar;
        tasksDao = app.db.tasksDao();
        days = new ArrayList<>();
        tasksForDay = new ArrayList<>();
        setProgressBar(true);

        app.dbThreadPool.execute(() -> {
            List<Day> daysFromDB = tasksDao.getAllDays();
            days.addAll(daysFromDB);
            tasksForDay.addAll(tasksDao.getTasksForDays(daysFromDB));
            try{Thread.sleep(500);} catch (InterruptedException e) { }
            app.uiThread.post(() -> {
                notifyItemRangeInserted(0,days.size());
                setProgressBar(false);
            });
        });
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DayViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_day,parent,false));
    }


    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        holder.textView.setText(days.get(position).getDate());
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.recyclerView.setAdapter(new DayAdapter(tasksForDay.get(position)));
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    private void setNewDays(){
        setProgressBar(true);
        days.clear(); tasksForDay.clear();
        notifyItemRangeRemoved(0,app.COUNT_GENERATE_DAY);

        app.dbThreadPool.execute(() -> {
            tasksDao.deleteAllDays();

            List<Task> tasks = tasksDao.getInCalendarTasks();
            tasksDao.insertDays(getDateList(app.COUNT_GENERATE_DAY), tasks);

            List<Day> lDays = tasksDao.getAllDays();

            tasksForDay.addAll(tasksDao.getTasksForDays(lDays));
            days.addAll(lDays);

            app.uiThread.post(() -> {
                setProgressBar(false);
                notifyItemRangeInserted(0, app.COUNT_GENERATE_DAY);
            });
        });
    }

    private List<Day> getDateList(int count) {
        List<Day> lDays = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd  MMM", Locale.getDefault());
        for (int i = 0; i < count; i++) {
            lDays.add(new Day(simpleDateFormat.format(calendar.getTime())));
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        return lDays;
    }

    private void setProgressBar(boolean isWorking){
        progressBar.setVisibility(isWorking ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isWorking ? View.GONE : View.VISIBLE);
    }

    static class DayViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final MaterialButton button;
        private final RecyclerView recyclerView;

        public DayViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item_day_date_text_view);
            button = itemView.findViewById(R.id.item_day_change_button);
            recyclerView = itemView.findViewById(R.id.item_day_recycler_view);
        }
    }
}
