package com.elhazent.picodiploma.notesapp.database;

import android.provider.BaseColumns;

public class DatabaseContract {
    static String TABLE_NAME = "note";
    static final class NoteColumns implements BaseColumns {
        static String TITLE = "title";
        static String DESCRIPTION = "description";
        static String DATE = "date";
    }
}
