package com.example.notes2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class NoteEditorActivity extends AppCompatActivity {
    private NoteDatabase database;
    private EditText editText;
    private int noteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);
        database = NoteDatabase.getDatabase(getApplicationContext());
        editText = findViewById(R.id.editText);

        Intent intent = getIntent();
        noteId = intent.getIntExtra("noteId", -1);

        loadNote();
        setNoteChangeListener();
    }

    private void loadNote() {
        if (noteId != -1) {
            editText.setText(MainActivity.notes.get(noteId).getNote());
        } else {
            editText.requestFocus();

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
        }
    }

    private void setNoteChangeListener() {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void handleAddNote(String newNoteText) {
        if (newNoteText.isEmpty()) {
            return;
        }

        Note newNote = new Note(0, newNoteText);
        MainActivity.notes.add(newNote);
        database.getNoteDao().insert(newNote);
        MainActivity.adapter.notifyDataSetChanged();
    }

    private void handleModifyNote(String newNoteText) {
        if (newNoteText.isEmpty()) {
            MainActivity.notes.remove(noteId);
        } else {
            Note selectedNote = MainActivity.notes.get(noteId);
            int selectedNoteId = selectedNote.getId();
            Note newNote = new Note(selectedNoteId, newNoteText);
            MainActivity.notes.set(noteId, newNote);
            database.getNoteDao().insert(newNote);
        }

        MainActivity.adapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        boolean isNewNote = -1 == noteId;
        String newNoteText = editText.getText().toString();

        if (isNewNote) {
            handleAddNote(newNoteText);
        } else {
            handleModifyNote(newNoteText);
        }
      }
}
