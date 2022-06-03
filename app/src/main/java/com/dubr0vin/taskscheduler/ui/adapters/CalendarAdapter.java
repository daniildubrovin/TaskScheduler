package com.dubr0vin.taskscheduler.ui.adapters;

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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.DayViewHolder> {
    private final App app;
    private final TasksDao tasksDao;
    private final ArrayList<Day> days;
    private final List<Task> allTasks;
    private final RecyclerView recyclerView;
    private final ProgressBar progressBar;

    public CalendarAdapter(App app, RecyclerView recyclerView, ProgressBar progressBar) {
        this.app = app;
        this.recyclerView = recyclerView;
        this.progressBar = progressBar;
        tasksDao = app.db.tasksDao();
        days = new ArrayList<>();
        allTasks = new ArrayList<>();
        setProgressBar(true);

        app.runInDBThread(() -> {
            List<Day> daysFromDB = tasksDao.getAllDays();

            app.runInUI(() -> {
                days.addAll(daysFromDB);
                notifyItemRangeInserted(0,days.size());
                setProgressBar(false);
            });
        });

        app.runInDBThread(() -> {
            List<Task> tasksFromDB = tasksDao.getAllTasks();
            app.runInUI(() -> allTasks.addAll(tasksFromDB));
        });
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DayViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_day,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        Day day = days.get(position);
        List<Task> dayTasks = day.getTasks();

        holder.textView.setText(day.getDate());
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.recyclerView.setAdapter(new DayAdapter(dayTasks));

        holder.editButton.setOnClickListener(v -> {
            app.runInDBThread(() -> {
                List<Task> tasksFromDB = tasksDao.getAllTasks();

                app.runInUI(() -> {
                    allTasks.clear();
                    allTasks.addAll(tasksFromDB);

                    String[] strTasks = new String[allTasks.size()];
                    boolean[] choice = new boolean[allTasks.size()];

                    for (int i = 0; i < allTasks.size(); i++) {
                        strTasks[i] = allTasks.get(i).getValue();
                        choice[i] = dayTasks.contains(allTasks.get(i));
                    }

                    showDialogAlert(holder,strTasks,choice,day,position);
                });
            });
        });
    }

    public void setNewDays(){
        setProgressBar(true);
        days.clear();

        app.runInDBThread(() -> {
            tasksDao.deleteAllDays();

            for(Day newDay : getDateList(app.COUNT_GENERATE_DAY)) tasksDao.insertDay(newDay);

            days.addAll(tasksDao.getAllDays());

            app.runInUI(() -> {
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
            lDays.add(new Day(simpleDateFormat.format(calendar.getTime()), new ArrayList<>()));
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        return lDays;
    }

    private void setProgressBar(boolean isWorking){
        progressBar.setVisibility(isWorking ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isWorking ? View.GONE : View.VISIBLE);
    }

    private void showDialogAlert(DayViewHolder holder, String[] strTasks, boolean[] choice, Day day, int position){
        new MaterialAlertDialogBuilder(holder.editButton.getContext())
                .setTitle(R.string.edit_day_tasks_title)
                .setNegativeButton(R.string.edit_day_tasks_negative_button, (dialog, which) -> { })
                .setPositiveButton(R.string.edit_day_tasks_positive_button, (dialog, which) -> {
                    ArrayList<Task> newTasks = new ArrayList<>();
                    for (int i = 0; i < choice.length; i++) if(choice[i]) newTasks.add(allTasks.get(i));
                    day.setTasks(newTasks);

                    app.runInDBThread(() -> {
                        tasksDao.editDay(day);
                        app.runInUI(() -> notifyItemChanged(position));
                    });
                    app.runInDBThread(() -> {
                        for(Task newTask: newTasks){
                            if(!newTask.isInCalendar()) {
                                newTask.setInCalendar(true);
                                tasksDao.editTask(newTask);
                            }
                        }
                    });
                })
                .setMultiChoiceItems(strTasks, choice, (dialog, which, isChecked) -> { })
                .show();
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    static class DayViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final MaterialButton editButton;
        private final RecyclerView recyclerView;

        public DayViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item_day_date_text_view);
            editButton = itemView.findViewById(R.id.item_day_change_button);
            recyclerView = itemView.findViewById(R.id.item_day_recycler_view);
        }
    }
}
