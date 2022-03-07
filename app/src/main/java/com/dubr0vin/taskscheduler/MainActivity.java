package com.dubr0vin.taskscheduler;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addViewPager();

    }

    private void addViewPager(){
        TabLayout tabLayout = findViewById(R.id.main_tab_layout);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        ViewPager2 viewPager2 = findViewById(R.id.main_viewpager);
        viewPager2.setAdapter(viewPagerAdapter);

        String[] tabs = { getString(R.string.tab_item_tasks),getString(R.string.tab_item_calendar) };
        new TabLayoutMediator(tabLayout, viewPager2, true, (tab, position) -> tab.setText(tabs[position])).attach();
    }
}