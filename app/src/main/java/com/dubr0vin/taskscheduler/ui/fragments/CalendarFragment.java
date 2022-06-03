package com.dubr0vin.taskscheduler.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.dubr0vin.taskscheduler.App;
import com.dubr0vin.taskscheduler.R;
import com.dubr0vin.taskscheduler.ui.CustomLinearLayoutManager;
import com.dubr0vin.taskscheduler.ui.adapters.CalendarAdapter;

public class CalendarFragment extends Fragment {

    public CalendarFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(App.TAG,"onCreateView in CalendarFragment");
        View view = inflater.inflate(R.layout.calendar_layout,container,false);

        App app = (App) requireActivity().getApplication();
        ProgressBar progressBar = view.findViewById(R.id.calendar_progress_bar);

        RecyclerView recyclerView = view.findViewById(R.id.calendar_recycler_view);
        recyclerView.setLayoutManager(new CustomLinearLayoutManager(view.getContext()));

        CalendarAdapter calendarAdapter = new CalendarAdapter(app,recyclerView,progressBar);

        view.findViewById(R.id.calendar_generate_button).setOnClickListener(v -> calendarAdapter.setNewDays());

        view.findViewById(R.id.calendar_generate_setting_button).setOnClickListener(v -> {});

        recyclerView.setAdapter(calendarAdapter);

        return view;
    }
}