package com.dubr0vin.taskscheduler.ui;

import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dubr0vin.taskscheduler.R;

public class TaskViewHolder extends RecyclerView.ViewHolder {
    private final CheckBox checkBox;
    private final EditText editText;
    private final TextView textView;
    private TextWatcher textWatcher;

    public TaskViewHolder(@NonNull View itemView) {
        super(itemView);
        checkBox = itemView.findViewById(R.id.task_check_box);
        editText = itemView.findViewById(R.id.task_edit_text);
        textView = itemView.findViewById(R.id.task_item_text_view);
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public EditText getEditText() {
        return editText;
    }

    public TextView getTextView() {
        return textView;
    }

    public void setTextWatcher(TextWatcher textWatcher) {
        this.textWatcher = textWatcher;
    }

    public TextWatcher getTextWatcher() {
        return textWatcher;
    }
}
