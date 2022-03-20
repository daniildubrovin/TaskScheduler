package com.dubr0vin.taskscheduler.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dubr0vin.taskscheduler.App;
import com.dubr0vin.taskscheduler.R;

public class CalendarFragment extends Fragment {

    public CalendarFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(App.TAG,"onCreateView in CalendarFragment");
        View view = inflater.inflate(R.layout.calendar_layout,container,false);

        App app = (App) requireActivity().getApplication();
        ProgressBar progressBar = view.findViewById(R.id.calendar_progress_bar);
        LinearLayout generateLayout = view.findViewById(R.id.calendar_generate_layout);

        view.findViewById(R.id.calendar_generate_button).setOnClickListener(v -> {

        });

        view.findViewById(R.id.calendar_generate_setting_button).setOnClickListener(v -> {

        });

        RecyclerView recyclerView = view.findViewById(R.id.calendar_recycler_view);
        CalendarAdapter adapter = new CalendarAdapter(app,recyclerView,progressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()) {
            @Override
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                try {super.onLayoutChildren(recycler, state);} catch (IndexOutOfBoundsException e) {e.printStackTrace();}
            }
        });

        recyclerView.setAdapter(adapter);


        return view;
    }


}