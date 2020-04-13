package com.example.notes2;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class NoteUnitTest {
    private int noteId1 = 42;
    private int noteId2 = 24;
    private String noteText1 = "Some random text to be saved 1...";
    private String noteText2 = "Some random text to be saved 2...";

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void noteInitialization() {
        Note note = new Note(noteId1, noteText1);
        assertEquals(noteId1, note.getId());
        assertEquals(noteText1, note.getNote());
    }

    @Test
    public void noteEquality_sameObject() {
        Note note = new Note(noteId1, noteText1);
        assertEquals(note, note);
    }

    @Test
    public void noteEquality_differentClass() {
        Note note = new Note(noteId1, noteText1);
        Object object = new Object();
        assertNotEquals(note, object);
    }

    @Test
    public void noteEquality_sameClass_differentValues() {
        Note note1 = new Note(noteId1, noteText1);
        Note note2 = new Note(noteId2, noteText2);
        assertNotEquals(note1, note2);
    }

    @Test
    public void noteEquality_sameClass_sameValues() {
        Note note1 = new Note(noteId1, noteText1);
        Note note2 = new Note(noteId1, noteText1);
        assertEquals(note1, note2);
    }
}
