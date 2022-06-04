package com.dubr0vin.taskscheduler.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dubr0vin.taskscheduler.App;
import com.dubr0vin.taskscheduler.R;
import com.dubr0vin.taskscheduler.db.Day;
import com.dubr0vin.taskscheduler.db.Task;
import com.dubr0vin.taskscheduler.db.TasksDao;
import com.dubr0vin.taskscheduler.ui.holders.DayViewHolder;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarAdapter extends RecyclerView.Adapter<DayViewHolder> {
    private final App app;
    private final TasksDao tasksDao;
    private final ArrayList<Day> days;
    private final List<Task> allTasks;
    private final RecyclerView recyclerView;
    private final ProgressBar progressBar;

    public CalendarAdapter(App app, List<Task> allTasks, RecyclerView recyclerView, ProgressBar progressBar) {
        this.app = app;
        this.recyclerView = recyclerView;
        this.progressBar = progressBar;
        this.allTasks = allTasks;

        tasksDao = app.db.tasksDao();
        days = new ArrayList<>();
        setProgressBar(true);

        app.runInDBThread(() -> {
            List<Day> daysFromDB = tasksDao.getAllDays();

            app.runInUI(() -> {
                days.addAll(daysFromDB);
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
        Day day = days.get(position);
        List<Task> dayTasks = day.getTasks();

        holder.getTextView().setText(day.getDate());
        holder.getRecyclerView().setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.getRecyclerView().setAdapter(new DayAdapter(dayTasks));

        holder.getEditButton().setOnClickListener(v -> showDialogAlert(holder,position));
    }

    private void showDialogAlert(DayViewHolder holder, int position){
        //creating lists for dialog
        List<Task> inCalendarTasks = new ArrayList<>();
        for(Task task: allTasks) if(task.isInCalendar()) inCalendarTasks.add(task);

        String[] tasks = new String[inCalendarTasks.size()];
        boolean[] choice = new boolean[inCalendarTasks.size()];
        createListsForDialog(days.get(position).getTasks(), tasks, choice, inCalendarTasks);

        getAlertDialog(holder,tasks,choice,position).show();
    }

    private void createListsForDialog(List<Task> dayTasks, String[] tasks, boolean[] choice, List<Task> inCalendarTasks){
        for (int i = 0; i < inCalendarTasks.size(); i++) {
            tasks[i] = inCalendarTasks.get(i).getValue();
            choice[i] = containsTasksById(dayTasks,inCalendarTasks.get(i));
        }
    }

    private boolean containsTasksById(List<Task> tasks, Task task){
        for (Task task1 : tasks){
            if(task1.getId() == task.getId()) return true;
        }
        return false;
    }

    private MaterialAlertDialogBuilder getAlertDialog(DayViewHolder holder, String[] strTasks, boolean[] choice, int position){
        return new MaterialAlertDialogBuilder(holder.getEditButton().getContext())
                .setTitle(R.string.edit_day_tasks_title)
                .setNegativeButton(R.string.edit_day_tasks_negative_button, (dialog, which) -> { })
                .setPositiveButton(R.string.edit_day_tasks_positive_button, (dialog, which) -> editDayTasks(choice, position))
                .setMultiChoiceItems(strTasks, choice, (dialog, which, isChecked) -> { });
    }

    private void editDayTasks(boolean[] choice, int position){
        Day day = days.get(position);
        ArrayList<Task> newTasks = new ArrayList<>();

        //edit day's tasks
        for (int i = 0; i < choice.length; i++) {
            if(choice[i]) newTasks.add(allTasks.get(i));
        }
        day.setTasks(newTasks);

        app.runInDBThread(() -> {
            tasksDao.editDay(day);
            app.runInUI(() -> notifyItemChanged(position));
        });

        //adding tasks in calendar
        app.runInDBThread(() -> {
            for(Task newTask: newTasks){
                if(!newTask.isInCalendar()) {
                    newTask.setInCalendar(true);
                    tasksDao.editTask(newTask);
                }
            }
        });
    }

    public void setNewDays(){
        setProgressBar(true);
        days.clear();

        app.runInDBThread(() -> {
            tasksDao.deleteAllDays();
            for(Day newDay : getDateList()) tasksDao.insertDay(newDay);
            days.addAll(tasksDao.getAllDays());

            app.runInUI(() -> {
                setProgressBar(false);
                notifyItemRangeInserted(0, app.COUNT_GENERATE_DAY);
            });
        });
    }

    private List<Day> getDateList() {
        List<Day> lDays = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd  MMM", Locale.getDefault());
        for (int i = 0; i < app.COUNT_GENERATE_DAY; i++) {
            lDays.add(new Day(simpleDateFormat.format(calendar.getTime()), new ArrayList<>()));
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        return lDays;
    }

    private void setProgressBar(boolean isWorking){
        progressBar.setVisibility(isWorking ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isWorking ? View.GONE : View.VISIBLE);
    }

    public void onResume(){
        app.runInDBThread(() ->{
            List<Task> tasksFromDB = app.db.tasksDao().getAllTasks();
            app.runInUI(() -> {
                allTasks.clear();
                allTasks.addAll(tasksFromDB);
            });
        });
    }

    @Override
    public int getItemCount() {
        return days.size();
    }
}
