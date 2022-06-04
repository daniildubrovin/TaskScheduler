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
import com.dubr0vin.taskscheduler.db.Task;
import com.dubr0vin.taskscheduler.db.TasksDao;
import com.dubr0vin.taskscheduler.ui.CustomLinearLayoutManager;
import com.dubr0vin.taskscheduler.ui.adapters.TaskAdapter;

import java.util.ArrayList;
import java.util.List;

public class TaskFragment extends Fragment {
    private List<Task> allTasks;
    private App app;
    private TaskAdapter taskAdapter;

    public TaskFragment() {
        allTasks = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.task_layout,container,false);

        app = (App) requireActivity().getApplication();
        ProgressBar progressBar = view.findViewById(R.id.task_progress_bar);

        RecyclerView recyclerView = view.findViewById(R.id.task_recycler_view);
        recyclerView.setLayoutManager(new CustomLinearLayoutManager(view.getContext()));
        taskAdapter = new TaskAdapter(recyclerView,progressBar,app,allTasks);
        recyclerView.setAdapter(taskAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        taskAdapter.showTasks();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
