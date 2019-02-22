package com.elhazent.picodiploma.notesapp;

import com.elhazent.picodiploma.notesapp.entity.Note;

import java.util.ArrayList;

public interface NoteCallback {
    void preExecute();
    void postExecute(ArrayList<Note> notes);
}
