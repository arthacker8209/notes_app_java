package com.example.hellonotes.Adapters;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hellonotes.Acitivities.todo_activity;
import com.example.hellonotes.R;
import com.example.hellonotes.entities.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> tasks;
    private todo_activity todo_activity;

    public TaskAdapter(List<Task> tasks) {
        this.tasks = tasks;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TaskViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_tasks,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, final int position) {
        holder.setTask(tasks.get(position));

    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        return position ;
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView taskTitle ,taskSubtitle,taskDescription;
        LinearLayout taskLayout;
        CheckBox checkBox;
        todo_activity activity;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox=(itemView).findViewById(R.id.check);
            taskTitle=(itemView).findViewById(R.id.taskTitle);
            taskSubtitle=(itemView).findViewById(R.id.taskSubtitle);
            taskDescription=(itemView).findViewById(R.id.task_desciption);
            taskLayout=(itemView).findViewById(R.id.layout_task);
            checkBox.setOnClickListener(this);

        }
       public void setTask(Task task){

            taskTitle.setText(task.getTitle());
            if (task.getSubtitle().trim().isEmpty()){
                taskSubtitle.setVisibility(View.GONE);
            }
            else {
                taskSubtitle.setText(task.getSubtitle());
            }
            taskDescription.setText(task.getDescription());

            GradientDrawable gradientDrawable = (GradientDrawable) taskLayout.getBackground();
            if (task.getColor()!=null) {
                gradientDrawable.setColor(Color.parseColor(task.getColor()));
            }
            else {
                gradientDrawable.setColor(Color.parseColor("#333333"));
            }

        }

        @Override
        public void onClick(View view) {
            activity.prepareSelection(view , getAdapterPosition());
        }
    }


}
