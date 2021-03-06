package com.dubr0vin.taskscheduler.ui.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.dubr0vin.taskscheduler.ui.fragments.CalendarFragment;
import com.dubr0vin.taskscheduler.ui.fragments.TaskFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return position == 0 ? new TaskFragment() : new CalendarFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
