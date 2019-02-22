package com.elhazent.picodiploma.notesapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.elhazent.picodiploma.notesapp.entity.Note;

import java.util.ArrayList;

import static android.provider.BaseColumns._ID;
import static com.elhazent.picodiploma.notesapp.database.DatabaseContract.NoteColumns.DATE;
import static com.elhazent.picodiploma.notesapp.database.DatabaseContract.NoteColumns.DESCRIPTION;
import static com.elhazent.picodiploma.notesapp.database.DatabaseContract.NoteColumns.TITLE;
import static com.elhazent.picodiploma.notesapp.database.DatabaseContract.TABLE_NAME;


// Kelas di atas menggunakan sebuah pattern yang bernama Singleton Pattern.
// Dengan Singleton sebuah obyek  yang hanya bisa memiliki sebuah instance.
// Ciri khas dari sebuah kelas Singleton terletak pada Constructor yang memiliki modifier private.

public class NoteHelp {
    private static final String DATABASE_TABLE = TABLE_NAME;
    private static DatabaseHelp dataBaseHelp;
    private static NoteHelp INSTANCE;
    private static SQLiteDatabase database;

    private NoteHelp(Context context) {
        dataBaseHelp = new DatabaseHelp(context);
    }

//    Metode untuk menginisiasi database

//    Pembuatan instance dari sebuah kelas Singleton bisa ditambahkan pada sebuah static method.
    public static NoteHelp getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (SQLiteOpenHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new NoteHelp(context);
                }
            }
        }
        return INSTANCE;
    }

//    Metode open dan close connection to database

    public void open() throws SQLException {
        database = dataBaseHelp.getWritableDatabase();
    }
    public void close() {
        dataBaseHelp.close();
        if (database.isOpen())
            database.close();
    }

//    Metode CRUD pengambilan data
    public ArrayList<Note> getAllNotes() {
        ArrayList<Note> arrayList = new ArrayList<>();
        Cursor cursor = database.query(DATABASE_TABLE, null,
                null,
                null,
                null,
                null,
                _ID + " ASC",
                null);
        cursor.moveToFirst();
        Note note;
        if (cursor.getCount() > 0) {
            do {
                note = new Note();
                note.setId(cursor.getInt(cursor.getColumnIndexOrThrow(_ID)));
                note.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(TITLE)));
                note.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION)));
                note.setDate(cursor.getString(cursor.getColumnIndexOrThrow(DATE)));
                arrayList.add(note);
                cursor.moveToNext();
            } while (!cursor.isAfterLast());
        }
        cursor.close();
        return arrayList;
    }


//    Metode Penyimpanan Data
    public long insertNote(Note note) {
        ContentValues args = new ContentValues();
        args.put(TITLE, note.getTitle());
        args.put(DESCRIPTION, note.getDescription());
        args.put(DATE, note.getDate());
        return database.insert(DATABASE_TABLE, null, args);
    }

//    Metode untuk mengupdate data
    public int updateNote(Note note) {
        ContentValues args = new ContentValues();
        args.put(TITLE, note.getTitle());
        args.put(DESCRIPTION, note.getDescription());
        args.put(DATE, note.getDate());
        return database.update(DATABASE_TABLE, args, _ID + "= '" + note.getId() + "'", null);
    }

//    Metode mendelete data
    public int deleteNote(int id) {
        return database.delete(TABLE_NAME, _ID + " = '" + id + "'", null);
    }
}
