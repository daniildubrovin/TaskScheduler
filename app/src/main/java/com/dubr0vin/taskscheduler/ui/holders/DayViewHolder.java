package com.dubr0vin.taskscheduler.ui.holders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dubr0vin.taskscheduler.R;
import com.google.android.material.button.MaterialButton;

public class DayViewHolder extends RecyclerView.ViewHolder{
    private final TextView textView;
    private final MaterialButton editButton;
    private final RecyclerView recyclerView;

    public DayViewHolder(@NonNull View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.item_day_date_text_view);
        editButton = itemView.findViewById(R.id.item_day_change_button);
        recyclerView = itemView.findViewById(R.id.item_day_recycler_view);
    }

    public TextView getTextView() {
        return textView;
    }

    public MaterialButton getEditButton() {
        return editButton;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }
}
