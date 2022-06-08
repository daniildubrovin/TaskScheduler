package com.dubr0vin.taskscheduler.ui.fragments;

import android.os.Bundle;
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
import com.dubr0vin.taskscheduler.Utilities;
import com.dubr0vin.taskscheduler.ui.CustomLinearLayoutManager;
import com.dubr0vin.taskscheduler.ui.adapters.TasksAdapter;

public class TaskFragment extends Fragment {
    private TasksAdapter tasksAdapter;

    public TaskFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.task_layout,container,false);

        App app = (App) requireActivity().getApplication();
        ProgressBar progressBar = view.findViewById(R.id.task_progress_bar);

        RecyclerView recyclerView = view.findViewById(R.id.task_recycler_view);
        recyclerView.setLayoutManager(new CustomLinearLayoutManager(view.getContext()));
        tasksAdapter = new TasksAdapter(recyclerView,progressBar, app);
        recyclerView.setAdapter(tasksAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        tasksAdapter.updateTasks();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(getView() != null) Utilities.hideKeyBoard(getView());
    }
}
