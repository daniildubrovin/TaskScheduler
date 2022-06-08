package com.dubr0vin.taskscheduler.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dubr0vin.taskscheduler.App;
import com.dubr0vin.taskscheduler.R;
import com.dubr0vin.taskscheduler.db.Day;
import com.dubr0vin.taskscheduler.db.Task;
import com.dubr0vin.taskscheduler.db.TasksDao;
import com.dubr0vin.taskscheduler.ui.DayDiffUtilCallback;
import com.dubr0vin.taskscheduler.ui.holders.DayViewHolder;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DaysAdapter extends RecyclerView.Adapter<DayViewHolder> {
    private final App app;
    private final TasksDao tasksDao;
    private final ArrayList<Day> days;
    private final List<Task> tasks;
    private final RecyclerView recyclerView;
    private final ProgressBar progressBar;
    private final TextView statisticsTextView;

    public DaysAdapter(App app, RecyclerView recyclerView, ProgressBar progressBar, TextView textView) {
        this.app = app;
        this.recyclerView = recyclerView;
        this.progressBar = progressBar;
        this.tasks = new ArrayList<>();
        this.days = new ArrayList<>();
        this.statisticsTextView = textView;

        tasksDao = app.db.tasksDao();
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DayViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_day,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        Day day = days.get(position);

        holder.getTextView().setText(day.getDate());
        holder.getRecyclerView().setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.getRecyclerView().setAdapter(new DayTasksAdapter(day, app, statisticsTextView));

        holder.getEditButton().setOnClickListener(v -> showDialogAlert(holder));
    }

    private void showDialogAlert(DayViewHolder holder){
        int position = holder.getAdapterPosition();
        //creating lists for dialog
        List<Task> inCalendarTasks = new ArrayList<>();
        for(Task task: tasks) if(task.isInCalendar()) {
            Task newTask = new Task(false,task.getValue());
            newTask.setId(task.getId());
            inCalendarTasks.add(newTask);
        }

        //inCalendar replace isDone
        for(Task task: inCalendarTasks) task.setInCalendar(false);

        String[] strTasks = new String[inCalendarTasks.size()];
        boolean[] choice = new boolean[inCalendarTasks.size()];
        createListsForDialog(days.get(position).getTasks(), strTasks, choice, inCalendarTasks);

        getAlertDialog(holder,strTasks,choice,inCalendarTasks,position).show();
    }

    private void createListsForDialog(List<Task> dayTasks, String[] strTasks, boolean[] choice, List<Task> inCalendarTasks){
        for (int i = 0; i < inCalendarTasks.size(); i++) {
            strTasks[i] = inCalendarTasks.get(i).getValue();
            choice[i] = containsTasksById(dayTasks,inCalendarTasks.get(i));
        }
    }

    private boolean containsTasksById(List<Task> tasks, Task task){
        for (Task task1 : tasks){
            if(task1.getId() == task.getId()) {
                task.setInCalendar(task1.isInCalendar());
                return true;
            }
        }
        return false;
    }

    private MaterialAlertDialogBuilder getAlertDialog(DayViewHolder holder, String[] strTasks, boolean[] choice, List<Task> inCalendarTasks, int position){
        return new MaterialAlertDialogBuilder(holder.getEditButton().getContext())
                .setTitle(R.string.edit_day_tasks_title)
                .setNegativeButton(R.string.edit_day_tasks_negative_button, (dialog, which) -> { })
                .setPositiveButton(R.string.edit_day_tasks_positive_button, (dialog, which) -> editDayTasks(choice, inCalendarTasks, position))
                .setMultiChoiceItems(strTasks, choice, (dialog, which, isChecked) -> { });
    }

    private void editDayTasks(boolean[] choice, List<Task> inCalendarTasks, int position){
        Day day = days.get(position);
        ArrayList<Task> newTasks = new ArrayList<>();

        //edit day's tasks
        for (int i = 0; i < choice.length; i++) {
            if(choice[i]) newTasks.add(inCalendarTasks.get(i));
        }
        day.setTasks(newTasks);

        app.runInDBThread(() -> {
            tasksDao.editDay(day);
            app.runInUI(() -> {
                notifyItemChanged(position);
                setStatisticsText(days);
            });

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

    public void updateDaysAndTasks(){
        setProgressBar(days.isEmpty());

        app.runInDBThread(() -> {
            List<Task> tasksFromDB = app.db.tasksDao().getAllTasks();
            app.runInUI(() -> {
                tasks.clear();
                tasks.addAll(tasksFromDB);
            });
        });

        app.runInDBThread(() -> {
            for (Day day: tasksDao.getAllDays()) tasksDao.updateDayTasks(day);
            List<Day> daysFromDB = tasksDao.getAllDays();

            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DayDiffUtilCallback(days,daysFromDB));

            app.runInUI(() -> {
                days.clear();
                days.addAll(daysFromDB);
                diffResult.dispatchUpdatesTo(this);
                setProgressBar(false);
                setStatisticsText(daysFromDB);
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
        return days.size();
    }
}