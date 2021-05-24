package com.example.hellonotes.Acitivities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hellonotes.Adapters.TaskAdapter;
import com.example.hellonotes.Database.TaskDataBase;
import com.example.hellonotes.R;
import com.example.hellonotes.entities.Task;

import java.util.ArrayList;
import java.util.List;

public class todo_activity extends AppCompatActivity {

    private TextView taskCounter;
    private ImageView imageAddTask;
    private RecyclerView task_recyclerView;
    private List<Task> task_list;
    private TaskAdapter taskAdapter;
    private static final int REQUEST_CODE_ADD_TASK=1;
    private LinearLayout resetTask;
    private int pos;
    private int count;
    private ArrayList<Task> selectTask;
    private int counter=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_activity);
        imageAddTask= findViewById(R.id.imageAddTask);
        taskCounter=findViewById(R.id.taskCounter);
        imageAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivityForResult(new Intent(getApplicationContext(),
                        Create_task.class),
                        REQUEST_CODE_ADD_TASK);

            }
        });

        task_recyclerView= findViewById(R.id.todoRecyclerView);
        task_recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        task_list= new ArrayList<>();
      taskAdapter= new TaskAdapter(task_list);
        task_recyclerView.setAdapter(taskAdapter);

        resetTask= findViewById(R.id.reset_task);
        selectTask= new ArrayList<>();

        resetTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAllTasks();
            }
        });

        getAllTasks();
       }

    private void deleteAllTasks(){
        @SuppressLint("StaticFieldLeak")
        class ResetTask extends AsyncTask<Void ,Void,Void>{
            @Override
            protected Void doInBackground(Void... voids) {

                TaskDataBase.getTaskDataBase(getApplicationContext()).taskDao().deleteAll();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(todo_activity.this, "All Tasks Deleted!", Toast.LENGTH_SHORT).show();
                if (task_list.size()>0){
                    task_list.clear();
                    taskCounter.setText(0+"");
                }
                taskAdapter.notifyDataSetChanged();
            }
        }
        new ResetTask().execute();

    }

    private void getAllTasks() {

        @SuppressLint("StaticFieldLeak")
        class getTasks extends AsyncTask<Void,Void,List<Task>>{

            @Override
            protected List<Task> doInBackground(Void... voids) {
               return TaskDataBase.getTaskDataBase(getApplicationContext()).taskDao().getAllTasks();
            }

            protected void onPostExecute(List<Task> tasks) {
                super.onPostExecute(tasks);
                pos=tasks.size() - 1;
                if (task_list.size()==0){
                    task_list.addAll(tasks);
                    taskAdapter.notifyDataSetChanged();
               }
               else {
                   task_list.add(pos,tasks.get(pos));
                   taskAdapter.notifyItemInserted(pos);

               }
               task_recyclerView.smoothScrollToPosition(0);
               count= tasks.size();
               taskCounter.setText(count+"");
            }
        }
        new getTasks().execute();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(todo_activity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_CODE_ADD_TASK && resultCode == RESULT_OK){
            getAllTasks();
        }
    }

    public void prepareSelection(View view, int adapterPosition) {
        if (((CheckBox)view).isChecked()){
            selectTask.add(task_list.get(adapterPosition));
            counter+=1;
            updateCounter(counter);

        }
        else{
            selectTask.remove(task_list.get(adapterPosition));
            counter-=1;
            updateCounter(counter);
        }
    }
    private void updateCounter(int counter) {
        if (counter==0){
            taskCounter.setText(count+"");
        }
        else{
            taskCounter.setText(count+"");
        }
    }
}

