package com.example.hellonotes.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.hellonotes.Dao.TaskDao;
import com.example.hellonotes.entities.Task;

@Database(entities = Task.class,version = 1,exportSchema = false)
public abstract class TaskDataBase extends RoomDatabase {

    private static TaskDataBase taskDataBase= null;
    public static synchronized TaskDataBase getTaskDataBase(Context context){
        if (taskDataBase==null){
            taskDataBase= Room.databaseBuilder(context,TaskDataBase.class,"tasks_db").build();
        }
        return taskDataBase;
    }
    public abstract TaskDao taskDao();
}
