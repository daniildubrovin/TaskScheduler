package com.dubr0vin.taskscheduler.ui;

import android.content.Context;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dubr0vin.taskscheduler.App;
import com.dubr0vin.taskscheduler.R;
import com.dubr0vin.taskscheduler.db.Task;
import com.dubr0vin.taskscheduler.db.TasksDao;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskViewHolder> {
    private final ArrayList<Task> tasks;
    private int focusPosition;
    private final RecyclerView recyclerView;
    private final App app;
    private final TasksDao tasksDao;

    public TaskAdapter(RecyclerView recyclerView, App app) {
        this.recyclerView = recyclerView;
        this.app = app;
        tasksDao = app.db.tasksDao();
        tasks = new ArrayList<>();

        app.dbThreadPool.execute(()->{
            if(tasksDao.getAllTasks().isEmpty()) tasksDao.insertTask(new Task(false,""));
            List<Task> tasksFromDB = tasksDao.getAllTasks();
            app.uiThread.post(() -> {
                tasks.addAll(tasksFromDB);
                notifyItemRangeInserted(0,tasks.size());
            });
        });
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TaskViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Log.d(App.TAG,"onBindViewHolder: " + position + "| fp: " + focusPosition);

        if(position == focusPosition){
            holder.getEditText().requestFocus();
            InputMethodManager imm = (InputMethodManager) app.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
        else holder.getEditText().clearFocus();

        holder.getCheckBox().setChecked(tasks.get(position).isInCalendar());

        holder.getCheckBox().setOnCheckedChangeListener((compoundButton, b) -> {
            app.dbThreadPool.execute(() -> {
                Task task = tasks.get(holder.getAdapterPosition());
                task.setInCalendar(b);
                tasksDao.editTask(task);
                app.uiThread.post(() -> notifyItemChanged(holder.getAdapterPosition()));
            });
        });

        //before setText(), you need to remove TextWatcher, otherwise TextWatcher will loop
        holder.getEditText().removeTextChangedListener(holder.getTextWatcher());

        holder.getEditText().setText("");
        holder.getEditText().append(tasks.get(position).getValue());
        holder.getTextView().setText(String.valueOf(position + 1));

        holder.setTextWatcher(new taskTextWatcher(tasks,holder,app));
        holder.getEditText().addTextChangedListener(holder.getTextWatcher());

        setKeyEnterListener(holder);
        setKeyDeleteListener(holder);
    }

    private void setKeyEnterListener(TaskViewHolder holder){
        EditText editText = holder.getEditText();
        editText.setImeOptions(EditorInfo.IME_ACTION_GO);
        editText.setRawInputType(InputType.TYPE_CLASS_TEXT);
        editText.setOnEditorActionListener((textView, i, keyEvent) -> {
            if(i == EditorInfo.IME_ACTION_GO) {
                if(!editText.getText().toString().isEmpty() && holder.getAdapterPosition() == tasks.size() - 1){
                    Task newTask = new Task(false,"");
                    app.dbThreadPool.execute(() -> {
                        long newId = tasksDao.insertTask(newTask);
                        newTask.setId(newId);
                        List<Task> tasksFromDb = tasksDao.getAllTasks();
                        app.uiThread.post(() -> {
                            tasks.clear();
                            tasks.addAll(tasksFromDb);
                            focusPosition = holder.getAdapterPosition() + 1;
                            notifyItemInserted(tasks.size() - 1);
                        });
                    });
                }
                else if(editText.getSelectionStart() == holder.getEditText().getText().length()){
                    focusPosition = holder.getAdapterPosition() + 1;
                    notifyItemChanged(focusPosition);
                }
                recyclerView.scrollToPosition(focusPosition);
            }
            return false;
        });
    }

    private void setKeyDeleteListener(TaskViewHolder holder){
        holder.getEditText().setOnKeyListener((view, i, keyEvent) -> {
            if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyEvent.getKeyCode() == KeyEvent.KEYCODE_DEL){
                int position = holder.getAdapterPosition();
                if(tasks.size() > 1 && holder.getEditText().getText().toString().isEmpty()){
                    focusPosition = position > 0 ? position - 1 : 0;
                    Task delTask = tasks.get(position);
                    app.dbThreadPool.execute(() -> {
                        tasksDao.deleteTask(delTask);
                        List<Task> tasksFromDb = tasksDao.getAllTasks();
                        app.uiThread.post(() -> {
                            tasks.clear();
                            tasks.addAll(tasksFromDb);
                            notifyItemRemoved(position);
                            notifyItemChanged(focusPosition);
                        });
                    });
                }
                else if(holder.getAdapterPosition() != 0 && holder.getEditText().getSelectionStart() == 0){
                    focusPosition = position > 0 ? position - 1 : 0;
                    notifyItemChanged(focusPosition);
                }
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }
}
