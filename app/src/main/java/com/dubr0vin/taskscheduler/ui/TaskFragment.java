package com.dubr0vin.taskscheduler.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dubr0vin.taskscheduler.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class TaskFragment extends Fragment {
    public TaskFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.task_layout,container,false);
        ArrayList<String> tasks = new ArrayList<>(Arrays.asList("learn english","java core","networks","android","libgdx","books","algorithm","patterns"));

        RecyclerView recyclerView = view.findViewById(R.id.task_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(new TaskAdapter(tasks,recyclerView));

        return view;
    }
}
