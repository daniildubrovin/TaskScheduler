package com.dubr0vin.taskscheduler.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dubr0vin.taskscheduler.R;
import com.google.android.material.button.MaterialButton;

public class CalendarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    enum ViewTypes { GENERATE, DAY }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == ViewTypes.GENERATE.ordinal()) return new GenerateViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_generate,parent,false));
        else return new DayViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_day,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder.getItemViewType() == ViewTypes.GENERATE.ordinal()){

        }
        else {

        }
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? ViewTypes.GENERATE.ordinal() : ViewTypes.DAY.ordinal();
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    static class GenerateViewHolder extends RecyclerView.ViewHolder {
        private final MaterialButton generateButton;
        private final MaterialButton settingButton;
        private final TextView textView;
        public GenerateViewHolder(@NonNull View itemView) {
            super(itemView);
            generateButton = itemView.findViewById(R.id.item_generate_button);
            settingButton = itemView.findViewById(R.id.item_generate_setting_button);
            textView = itemView.findViewById(R.id.item_generate_text_view);
        }
    }

    static class DayViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final MaterialButton button;
        private final RecyclerView recyclerView;

        public DayViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item_day_date_text_view);
            button = itemView.findViewById(R.id.item_day_change_button);
            recyclerView = itemView.findViewById(R.id.item_day_recycler_view);
        }
    }
}
