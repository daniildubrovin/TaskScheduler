package com.dubr0vin.taskscheduler.ui;

import android.text.InputType;
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
            focusPosition = holder.getAdapterPosition() + 1;
            if(i == EditorInfo.IME_ACTION_GO) {
                if(!editText.getText().toString().isEmpty() && holder.getAdapterPosition() == tasks.size() - 1){
                    tasks.add("");
                    notifyItemInserted(tasks.size() - 1);
                }
                else notifyItemChanged(focusPosition);
                recyclerView.scrollToPosition(focusPosition);
                return true;
            }
            return false;
        });
    }

    private void setKeyDeleteListener(TaskViewHolder holder){
        holder.editText.setOnKeyListener((view, i, keyEvent) -> {
            if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyEvent.getKeyCode() == KeyEvent.KEYCODE_DEL){
                int position = holder.getAdapterPosition();
                focusPosition = position > 0 ? position - 1 : 0;
                if(tasks.size() > 1 && holder.editText.getText().toString().isEmpty()){
                    tasks.remove(position);
                    notifyItemRemoved(position);
                }
                notifyItemChanged(focusPosition);
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
