package com.dubr0vin.taskscheduler.ui;

import android.text.InputType;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dubr0vin.taskscheduler.App;
import com.dubr0vin.taskscheduler.R;

import java.util.ArrayList;
import java.util.Random;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private final ArrayList<String> tasks;
    private int focusPosition;
    private final RecyclerView recyclerView;

    public TaskAdapter(ArrayList<String> tasks, RecyclerView recyclerView) {
        this.tasks = tasks;
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TaskViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        if(position == focusPosition) holder.editText.requestFocus();
        else holder.editText.clearFocus();

        //before setText(), you need to remove TextWatcher, otherwise TextWatcher will loop
        holder.editText.removeTextChangedListener(holder.textWatcher);

        holder.editText.setText("");
        holder.editText.append(tasks.get(position));
        holder.textView.setText(String.valueOf(position + 1));

        holder.textWatcher = new taskTextWatcher(tasks,holder);
        holder.editText.addTextChangedListener(holder.textWatcher);

        setKeyEnterListener(holder);
        setKeyDeleteListener(holder);
    }

    private void setKeyEnterListener(TaskViewHolder holder){
        EditText editText = holder.editText;
        editText.setImeOptions(EditorInfo.IME_ACTION_GO);
        editText.setRawInputType(InputType.TYPE_CLASS_TEXT);
        editText.setOnEditorActionListener((textView, i, keyEvent) -> {
            if(i == EditorInfo.IME_ACTION_GO) {
                if(!editText.getText().toString().isEmpty() && holder.getAdapterPosition() == tasks.size() - 1){
                    tasks.add("");
                    notifyItemInserted(tasks.size() - 1);
                    focusPosition = holder.getAdapterPosition() + 1;
                }
                else if(editText.getSelectionStart() == holder.editText.getText().length()){
                    focusPosition = holder.getAdapterPosition() + 1;
                    notifyItemChanged(focusPosition);
                }
                recyclerView.scrollToPosition(focusPosition);
            }
            return false;
        });
    }

    private void setKeyDeleteListener(TaskViewHolder holder){
        holder.editText.setOnKeyListener((view, i, keyEvent) -> {
            if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyEvent.getKeyCode() == KeyEvent.KEYCODE_DEL){
                int position = holder.getAdapterPosition();
                if(holder.getAdapterPosition() != 0 && holder.editText.getSelectionStart() == 0){
                    focusPosition = position > 0 ? position - 1 : 0;
                    notifyItemChanged(focusPosition);
                }
                else if(tasks.size() > 1 && holder.editText.getText().toString().isEmpty()){
                    focusPosition = position > 0 ? position - 1 : 0;
                    tasks.remove(position);
                    notifyItemRemoved(position);
                    notifyItemChanged(focusPosition);
                }
            }
            return false;
        });
    }

    @Override
    public int getItemCount() { return tasks.size(); }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
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
    }
}
