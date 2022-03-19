package com.dubr0vin.taskscheduler.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dubr0vin.taskscheduler.App;
import com.dubr0vin.taskscheduler.R;
import com.dubr0vin.taskscheduler.db.Day;
import com.dubr0vin.taskscheduler.db.TasksDao;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    enum ViewTypes { GENERATE, DAY }
    private final App app;
    private final TasksDao tasksDao;
    private final ArrayList<Day> days;

    public CalendarAdapter(App app) {
        this.app = app;
        tasksDao = app.db.tasksDao();
        days = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == ViewTypes.GENERATE.ordinal()) return new GenerateViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_generate,parent,false));
        else return new DayViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_day,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder.getItemViewType() == ViewTypes.GENERATE.ordinal()){
            GenerateViewHolder generateHolder = (GenerateViewHolder) holder;
            generateHolder.generateButton.setOnClickListener(view -> app.dbThreadPool.execute(() -> { }));
        }
        else {
            DayViewHolder dayHolder = (DayViewHolder) holder;
            app.dbThreadPool.execute(() -> {

            });
            dayHolder.recyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
            dayHolder.recyclerView.setAdapter(new DayAdapter(new ArrayList<>()));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? ViewTypes.GENERATE.ordinal() : ViewTypes.DAY.ordinal();
    }

    @Override
    public int getItemCount() {
        return days.size() + 1;
    }

    private List<String> getDateList(int count) {
        List<String> lDates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd  MMM", Locale.getDefault());
        for (int i = 0; i < count; i++) {
            lDates.add(simpleDateFormat.format(calendar.getTime()));
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        return lDates;
    }

    static class GenerateViewHolder extends RecyclerView.ViewHolder {
        private final MaterialButton generateButton;
        private final MaterialButton settingButton;
        private final TextView textView;
        public GenerateViewHolder(@NonNull View itemView) {
            super(itemView);
            generateButton = itemView.findViewById(R.id.item_generate_button);
            settingButton = itemView.findViewById(R.id.item_generate_setting_button);
            textView = itemView.findViewById(R.id.item_generate_text_view);
        }
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
