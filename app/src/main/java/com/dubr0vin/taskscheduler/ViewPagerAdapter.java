package com.dubr0vin.taskscheduler;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Log.d(App.TAG,"create fragment");
        return position == 0 ? new TaskFragment() : new CalendarFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
