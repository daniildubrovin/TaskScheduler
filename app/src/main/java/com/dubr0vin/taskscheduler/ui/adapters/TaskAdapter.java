package com.dubr0vin.taskscheduler.ui.adapters;

import android.content.Context;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.dubr0vin.taskscheduler.App;
import com.dubr0vin.taskscheduler.R;
import com.dubr0vin.taskscheduler.db.Task;
import com.dubr0vin.taskscheduler.db.TasksDao;
import com.dubr0vin.taskscheduler.ui.TaskDiffUtilCallback;
import com.dubr0vin.taskscheduler.ui.holders.TaskViewHolder;
import com.dubr0vin.taskscheduler.ui.TaskTextWatcher;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskViewHolder> {
    private final List<Task> tasks;
    private int focusPosition;
    private final RecyclerView recyclerView;
    private final App app;
    private final TasksDao tasksDao;
    private final ProgressBar progressBar;

    public TaskAdapter(RecyclerView recyclerView, ProgressBar progressBar, App app, List<Task> tasks) {
        this.recyclerView = recyclerView;
        this.progressBar = progressBar;
        this.app = app;
        tasksDao = app.db.tasksDao();
        this.tasks = tasks;
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
            app.runInDBThread(() -> {
                Task task = tasks.get(holder.getAdapterPosition());
                task.setInCalendar(b);
                tasksDao.editTask(task);
                app.runInUI(() -> notifyItemChanged(holder.getAdapterPosition()));
            });
        });

        //before setText(), you need to remove TextWatcher, otherwise TextWatcher will loop
        holder.getEditText().removeTextChangedListener(holder.getTextWatcher());

        holder.getEditText().setText("");
        holder.getEditText().append(tasks.get(position).getValue());
        holder.getTextView().setText(String.valueOf(position + 1));

        holder.setTextWatcher(new TaskTextWatcher(tasks,holder,app));
        holder.getEditText().addTextChangedListener(holder.getTextWatcher());

        setKeyEnterListener(holder);
        setKeyDeleteListener(holder);
    }

    private void addNewTask(TaskViewHolder holder){
        app.runInDBThread(() -> {
            Task newTask = new Task(false,"");
            newTask.setId(tasksDao.insertTask(newTask));

            List<Task> tasksFromDb = tasksDao.getAllTasks();

            app.runInUI(() -> {
                tasks.clear();
                tasks.addAll(tasksFromDb);
                focusPosition = holder.getAdapterPosition() + 1;
                notifyItemInserted(tasks.size() - 1);
            });
        });
    }

    private void setKeyEnterListener(TaskViewHolder holder){
        EditText editText = holder.getEditText();
        editText.setImeOptions(EditorInfo.IME_ACTION_GO);
        editText.setRawInputType(InputType.TYPE_CLASS_TEXT);

        editText.setOnEditorActionListener((textView, i, keyEvent) -> {
            if(i == EditorInfo.IME_ACTION_GO) {
                if(!editText.getText().toString().isEmpty() && holder.getAdapterPosition() == tasks.size() - 1) addNewTask(holder);
                else if(editText.getSelectionStart() == holder.getEditText().getText().length()){
                    focusPosition = holder.getAdapterPosition() + 1;
                    notifyItemChanged(focusPosition);
                }
                recyclerView.scrollToPosition(focusPosition);
            }
            return false;
        });
    }

    private void deleteTask(int position){
        Task delTask = tasks.get(position);

        app.runInDBThread(() -> {
            tasksDao.deleteTask(delTask);

            List<Task> tasksFromDb = tasksDao.getAllTasks();

            app.runInUI(() -> {
                tasks.clear();
                tasks.addAll(tasksFromDb);
                notifyItemRemoved(position);
                notifyItemChanged(focusPosition);
            });
        });
    }

    private void setKeyDeleteListener(TaskViewHolder holder){
        holder.getEditText().setOnKeyListener((view, i, keyEvent) -> {
            if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyEvent.getKeyCode() == KeyEvent.KEYCODE_DEL){
                int position = holder.getAdapterPosition();
                if(tasks.size() > 1 && holder.getEditText().getText().toString().isEmpty()){
                    focusPosition = position > 0 ? position - 1 : 0;
                    deleteTask(position);
                }
                else if(holder.getAdapterPosition() != 0 && holder.getEditText().getSelectionStart() == 0){
                    focusPosition = position > 0 ? position - 1 : 0;
                    notifyItemChanged(focusPosition);
                }
            }
            return false;
        });
    }

    public void showTasks(){
        setProgressBar(tasks.isEmpty());

        app.runInDBThread(() -> {
            TasksDao tasksDao = app.db.tasksDao();
            List<Task> tasksFromDB = tasksDao.getAllTasks();

            if(tasksDao.getAllTasks().isEmpty()) {
                tasksDao.insertTask(new Task(false,""));
                tasksFromDB.clear();
                tasksFromDB.addAll(tasksDao.getAllTasks());
            }

            List<Task> oldTasks = new ArrayList<>(tasks);

            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new TaskDiffUtilCallback(oldTasks,tasksFromDB));

            app.runInUI(() -> {
                tasks.clear();
                tasks.addAll(tasksFromDB);
                diffResult.dispatchUpdatesTo(this);
                setProgressBar(false);
            });
        });
    }

    private void setProgressBar(boolean isWorking){
        progressBar.setVisibility(isWorking ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isWorking ? View.GONE : View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }
}
