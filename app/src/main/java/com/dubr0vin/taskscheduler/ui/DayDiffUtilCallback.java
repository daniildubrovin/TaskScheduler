package com.dubr0vin.taskscheduler.ui;

import androidx.recyclerview.widget.DiffUtil;

import com.dubr0vin.taskscheduler.db.Day;

import java.util.List;

public class DayDiffUtilCallback extends DiffUtil.Callback {
    private final List<Day> oldList;
    private final List<Day> newList;

    public DayDiffUtilCallback(List<Day> oldList, List<Day> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }
}
