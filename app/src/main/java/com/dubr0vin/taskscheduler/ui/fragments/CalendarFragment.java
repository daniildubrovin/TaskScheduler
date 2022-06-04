package com.dubr0vin.taskscheduler.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.dubr0vin.taskscheduler.App;
import com.dubr0vin.taskscheduler.R;
import com.dubr0vin.taskscheduler.db.Task;
import com.dubr0vin.taskscheduler.ui.CustomLinearLayoutManager;
import com.dubr0vin.taskscheduler.ui.adapters.CalendarAdapter;

import java.util.ArrayList;
import java.util.List;

public class CalendarFragment extends Fragment {

    private final List<Task> allTasks;
    private App app;
    private CalendarAdapter calendarAdapter;

    public CalendarFragment() {
        allTasks = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(App.TAG,"onCreateView in CalendarFragment");
        View view = inflater.inflate(R.layout.calendar_layout,container,false);

        app = (App) requireActivity().getApplication();
        ProgressBar progressBar = view.findViewById(R.id.calendar_progress_bar);

        RecyclerView recyclerView = view.findViewById(R.id.calendar_recycler_view);
        recyclerView.setLayoutManager(new CustomLinearLayoutManager(view.getContext()));

        calendarAdapter = new CalendarAdapter(app,allTasks,recyclerView,progressBar);

        view.findViewById(R.id.calendar_generate_button).setOnClickListener(v -> calendarAdapter.setNewDays());

        view.findViewById(R.id.calendar_generate_setting_button).setOnClickListener(v -> {});

        recyclerView.setAdapter(calendarAdapter);

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        hideKeyBoard(requireActivity());
        calendarAdapter.onResume();
    }

    private void hideKeyBoard(Context context){
        System.out.println("hideKeyBoard");
        requireView().clearFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(requireView().getWindowToken(), 0);
    }
}