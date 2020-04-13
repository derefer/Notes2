package com.example.notes2;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class NoteDatabaseInstrumentedTest {
    private NoteDao noteDao;
    private NoteDatabase noteDatabase;
    private int noteId1 = 42;
    private int noteId2 = 24;
    private String noteText1 = "Some random text to be saved 1...";
    private String noteText2 = "Some random text to be saved 2...";

    @Test
    public void useAppContext() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.notes2", appContext.getPackageName());
    }

    @Before
    public void createDatabase() {
        Context context = ApplicationProvider.getApplicationContext();
        noteDatabase = Room.inMemoryDatabaseBuilder(context, NoteDatabase.class).build();
        noteDao = noteDatabase.getNoteDao();
    }

    @After
    public void closeDatabase() throws IOException {
        noteDatabase.close();
    }

    @Test
    public void insertNote_getAllNotes() throws Exception {
        Note note = new Note(noteId1, noteText1);
        noteDao.insert(note);
        List<Note> notes = noteDao.getAll();
        assertEquals(1, notes.size());
        assertThat(notes.get(0), equalTo(note));
    }

    @Test
    public void insertNote_removeNote() throws Exception {
        Note note = new Note(noteId1, noteText1);
        noteDao.insert(note);
        noteDao.delete(note);
        List<Note> notes = noteDao.getAll();
        assertEquals(0, notes.size());
    }

    @Test
    public void insertNote_removeAllNotes() throws Exception {
        Note note = new Note(noteId1, noteText1);
        noteDao.insert(note);
        noteDao.removeAllNotes();
        List<Note> notes = noteDao.getAll();
        assertEquals(0, notes.size());
    }

    @Test
    public void insertNote_updateNote() throws Exception {
        Note note1 = new Note(noteId1, noteText1);
        Note note2 = new Note(noteId1, noteText2);
        noteDao.insert(note1);
        List<Note> notes = noteDao.getAll();
        assertThat(notes.get(0), equalTo(note1));
        noteDao.update(note2);
        notes = noteDao.getAll();
        assertThat(notes.get(0), equalTo(note2));
    }

    @Test
    public void insertAllNotes() throws Exception {
        final Note note1 = new Note(noteId1, noteText1);
        final Note note2 = new Note(noteId2, noteText2);
        List<Note> notes = new ArrayList<Note>() {{ add(note1); add(note2); }};
        noteDao.insertAll(notes);
        assertEquals(2, noteDao.getAll().size());
        assertThat(notes.get(0), equalTo(note1));
        assertThat(notes.get(1), equalTo(note2));
    }
}
