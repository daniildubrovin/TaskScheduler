package com.dubr0vin.taskscheduler.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.dubr0vin.taskscheduler.App;
import com.dubr0vin.taskscheduler.R;
import com.dubr0vin.taskscheduler.Utilities;
import com.dubr0vin.taskscheduler.ui.CustomLinearLayoutManager;
import com.dubr0vin.taskscheduler.ui.adapters.DaysAdapter;

public class CalendarFragment extends Fragment {
    private DaysAdapter daysAdapter;

    public CalendarFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(App.TAG,"onCreateView in CalendarFragment");
        View view = inflater.inflate(R.layout.calendar_layout,container,false);

        App app = (App) requireActivity().getApplication();
        ProgressBar progressBar = view.findViewById(R.id.calendar_progress_bar);
        TextView statisticsTextView = view.findViewById(R.id.statistics_text_view);

        RecyclerView recyclerView = view.findViewById(R.id.calendar_recycler_view);
        recyclerView.setLayoutManager(new CustomLinearLayoutManager(view.getContext()));
        daysAdapter = new DaysAdapter(app,recyclerView,progressBar,statisticsTextView);

        view.findViewById(R.id.calendar_generate_button).setOnClickListener(v -> daysAdapter.setNewDays());
        view.findViewById(R.id.calendar_generate_setting_button).setOnClickListener(v -> {});

        recyclerView.setAdapter(daysAdapter);

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        if(getView() != null) Utilities.hideKeyBoard(getView());
        daysAdapter.updateDaysAndTasks();
    }
}