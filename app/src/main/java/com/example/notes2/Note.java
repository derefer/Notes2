package com.example.notes2;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "note_table")
public class Note {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "note")
    @NonNull
    private String note;

    //@Ignore
    //private boolean someRandomBooleanToIgnore;

    public Note(int id, @NonNull String note) {
        this.id = id;
        this.note = note;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Note)) {
            return false;
        }

        Note n = (Note) o;
        return id == n.id && note.equals(n.note);
    }

    public int getId() { return id; }
    public String getNote() { return note; }
}
