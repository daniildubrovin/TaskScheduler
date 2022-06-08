package com.dubr0vin.taskscheduler.ui.adapters;

import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.dubr0vin.taskscheduler.App;
import com.dubr0vin.taskscheduler.R;
import com.dubr0vin.taskscheduler.Utilities;
import com.dubr0vin.taskscheduler.db.Task;
import com.dubr0vin.taskscheduler.db.TasksDao;
import com.dubr0vin.taskscheduler.ui.TaskDiffUtilCallback;
import com.dubr0vin.taskscheduler.ui.TaskTextWatcher;
import com.dubr0vin.taskscheduler.ui.holders.TaskViewHolder;

import java.util.ArrayList;
import java.util.List;

public class TasksAdapter extends RecyclerView.Adapter<TaskViewHolder> {
    private final List<Task> tasks;
    private int focusPosition;
    private final RecyclerView recyclerView;
    private final App app;
    private final TasksDao tasksDao;
    private final ProgressBar progressBar;

    public TasksAdapter(RecyclerView recyclerView, ProgressBar progressBar, App app) {
        this.recyclerView = recyclerView;
        this.progressBar = progressBar;
        this.app = app;
        this.tasks = new ArrayList<>();

        tasksDao = app.db.tasksDao();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TaskViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        if(position == focusPosition){
            holder.getEditText().requestFocus();
            holder.getEditText().postDelayed(() -> Utilities.showKeyBoard(holder.getEditText()), 100);
        }

        holder.getCheckBox().setChecked(tasks.get(position).isInCalendar());

        holder.getCheckBox().setOnCheckedChangeListener((compoundButton, b) -> app.runInDBThread(() -> {
            Task task = tasks.get(holder.getAdapterPosition());
            task.setInCalendar(b);
            tasksDao.editTask(task);
            app.runInUI(() -> notifyItemChanged(holder.getAdapterPosition()));
        }));

        holder.getTextView().setText(String.valueOf(position + 1));

        //before setText(), you need to remove TextWatcher, otherwise TextWatcher will loop
        holder.getEditText().removeTextChangedListener(holder.getTextWatcher());

        holder.getEditText().setText("");
        holder.getEditText().append(tasks.get(position).getValue());
        holder.getEditText().setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) focusPosition = holder.getAdapterPosition();
        });

        holder.setTextWatcher(new TaskTextWatcher(tasks,holder,app));
        holder.getEditText().addTextChangedListener(holder.getTextWatcher());

        setKeyEnterListener(holder);
        setKeyDeleteListener(holder);
    }

    private void setKeyEnterListener(TaskViewHolder holder){
        EditText editText = holder.getEditText();
        editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        editText.setRawInputType(InputType.TYPE_CLASS_TEXT);

        editText.setOnEditorActionListener((view, i, keyEvent) -> {
            if(i == EditorInfo.IME_ACTION_NEXT) {
                focusPosition = holder.getAdapterPosition() + 1;

                if(holder.getAdapterPosition() == tasks.size() - 1) addNewTask(holder);
                else {
                    notifyItemChanged(focusPosition);
                    recyclerView.scrollToPosition(focusPosition+1);
                }
                return true;
            }
            return false;
        });
    }

    private void addNewTask(TaskViewHolder holder){
        if(holder.getEditText().getText().length() == 0) {
            Utilities.hideKeyBoard(holder.getEditText());
            Toast.makeText(holder.itemView.getContext(), R.string.empty_new_task,Toast.LENGTH_SHORT).show();
        }
        else app.runInDBThread(() -> {
            tasksDao.insertTask(new Task(false,""));
            List<Task> tasksFromDb = tasksDao.getAllTasks();

            app.runInUI(() -> {
                tasks.clear();
                tasks.addAll(tasksFromDb);
                notifyItemInserted(tasks.size()-1);
                notifyItemChanged(focusPosition);
                recyclerView.scrollToPosition(focusPosition);
            });
        });
    }

    private void setKeyDeleteListener(TaskViewHolder holder){
        holder.getEditText().setOnKeyListener((view, i, keyEvent) -> {
            if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyEvent.getKeyCode() == KeyEvent.KEYCODE_DEL){
                int position = holder.getAdapterPosition();
                focusPosition = position > 0 ? position - 1 : 0;

                if(tasks.size() > 1 && holder.getEditText().getText().length() == 0) deleteTask(holder);
                else if(holder.getAdapterPosition() != 0 && holder.getEditText().getSelectionStart() == 0) {
                    notifyItemChanged(focusPosition);
                    recyclerView.scrollToPosition(focusPosition);
                }
            }
            return false;
        });
    }

    private void deleteTask(TaskViewHolder holder){
        app.runInDBThread(() -> {
            tasksDao.deleteTask(tasks.get(holder.getAdapterPosition()));
            List<Task> tasksFromDb = tasksDao.getAllTasks();

            app.runInUI(() -> {
                tasks.clear();
                tasks.addAll(tasksFromDb);
                recyclerView.scrollToPosition(focusPosition);
                notifyItemRemoved(holder.getAdapterPosition());
                notifyItemRangeChanged(focusPosition,tasks.size());
            });
        });
    }

    public void updateTasks(){
        setProgressBar(tasks.isEmpty());

        app.runInDBThread(() -> {
            TasksDao tasksDao = app.db.tasksDao();
            List<Task> tasksFromDB = tasksDao.getAllTasks();

            if(tasksDao.getAllTasks().isEmpty()) {
                tasksDao.insertTask(new Task(false,""));
                tasksFromDB.clear();
                tasksFromDB.addAll(tasksDao.getAllTasks());
            }

            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new TaskDiffUtilCallback(tasks,tasksFromDB));

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
