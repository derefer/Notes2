package com.example.notes2;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = Note.class, version = 1)
public abstract class NoteDatabase extends RoomDatabase {
    private static NoteDatabase databaseInstance;
    public abstract NoteDao getNoteDao();

    public static NoteDatabase getDatabase(Context context) {
        if (databaseInstance == null) {
            databaseInstance = Room.databaseBuilder(context, NoteDatabase.class, "note_database").allowMainThreadQueries().fallbackToDestructiveMigration().build();
        }
        return databaseInstance;
    }

    public static void destroyDatabase() {
        databaseInstance = null;
    }
}
