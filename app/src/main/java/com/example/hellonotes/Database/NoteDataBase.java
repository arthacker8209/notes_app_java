package com.example.hellonotes.Database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.hellonotes.Dao.NoteDao;
import com.example.hellonotes.entities.Note;

@Database(entities = Note.class , version = 1,exportSchema = false)
public abstract class NoteDataBase extends RoomDatabase {

    private static NoteDataBase noteDataBase=null;
    public static synchronized NoteDataBase getNoteDataBase(Context context){

        if (noteDataBase==null){
            noteDataBase = Room.databaseBuilder(context ,
                    NoteDataBase.class ,
                    "notes_db").build();
        }
        return noteDataBase;
    }

    public abstract NoteDao noteDao();


}
