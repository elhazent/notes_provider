package com.elhazent.picodiploma.notesapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.elhazent.picodiploma.notesapp.adapter.NoteAdapter;
import com.elhazent.picodiploma.notesapp.database.DatabaseHelp;
import com.elhazent.picodiploma.notesapp.database.NoteHelp;
import com.elhazent.picodiploma.notesapp.entity.Note;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static com.elhazent.picodiploma.notesapp.NoteUpdate.REQUEST_UPDATE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NoteCallback{
    private RecyclerView rvNotes;
    private ProgressBar progressBar;
    private FloatingActionButton fabAdd;
    private static final String EXTRA_STATE = "EXTRA_STATE";
    private NoteAdapter adapter;
    private NoteHelp noteHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("Notes");
        rvNotes = findViewById(R.id.rv_notes);
        rvNotes.setLayoutManager(new LinearLayoutManager(this));
        rvNotes.setHasFixedSize(true);
        noteHelp = NoteHelp.getInstance(getApplicationContext());
        noteHelp.open();
        progressBar = findViewById(R.id.progressbar);
        fabAdd = findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(this);
        adapter = new NoteAdapter(this);
        rvNotes.setAdapter(adapter);
        if (savedInstanceState == null) {
            new LoadNotesAsync(noteHelp, this).execute();
        } else {
            ArrayList<Note> list = savedInstanceState.getParcelableArrayList(EXTRA_STATE);
            if (list != null) {
                adapter.setListNotes(list);
            }
        }
    }

//    Pada metode ini Anda akan menyimpan arraylist, jadi pada saat rotasi, layar berubah dan aplikasi tidak memanggil ulang proses Asynctask-nya.
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(EXTRA_STATE, adapter.getListNotes());
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab_add) {
            Intent intent = new Intent(MainActivity.this, NoteUpdate.class);
            startActivityForResult(intent, NoteUpdate.REQUEST_ADD);
        }

    }

    @Override
    public void preExecute() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void postExecute(ArrayList<Note> notes) {
        progressBar.setVisibility(View.INVISIBLE);
        adapter.setListNotes(notes);
    }
    private static class LoadNotesAsync extends AsyncTask<Void, Void, ArrayList<Note>> {
        private final WeakReference<NoteHelp> weakNoteHelper;
        private final WeakReference<NoteCallback> weakCallback;

        private LoadNotesAsync(NoteHelp noteHelper, NoteCallback callback) {
            weakNoteHelper = new WeakReference<>(noteHelper);
            weakCallback = new WeakReference<>(callback);
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            weakCallback.get().preExecute();
        }
        @Override
        protected ArrayList<Note> doInBackground(Void... voids) {
            return weakNoteHelper.get().getAllNotes();
        }
        @Override
        protected void onPostExecute(ArrayList<Note> notes) {
            super.onPostExecute(notes);
            weakCallback.get().postExecute(notes);
        }
    }

//    metode onActivityResult untuk menerima nilai balik yang dikirimkan dari NoteAddUpdateActivity.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            if (requestCode == NoteUpdate.REQUEST_ADD) {
                if (resultCode == NoteUpdate.RESULT_ADD) {
                    Note note = data.getParcelableExtra(NoteUpdate.EXTRA_NOTE);
                    adapter.addItem(note);
                    rvNotes.smoothScrollToPosition(adapter.getItemCount() - 1);
                    showSnackbarMessage("One item success added");
                }
            }  else if (requestCode == REQUEST_UPDATE) {
                if (resultCode == NoteUpdate.RESULT_UPDATE) {
                    Note note = data.getParcelableExtra(NoteUpdate.EXTRA_NOTE);
                    int position = data.getIntExtra(NoteUpdate.EXTRA_POSITION, 0);
                    adapter.updateItem(position, note);
                    rvNotes.smoothScrollToPosition(position);
                    showSnackbarMessage("One item success edited");
                }
            } else if (resultCode == NoteUpdate.RESULT_DELETE) {
                int position = data.getIntExtra(NoteUpdate.EXTRA_POSITION, 0);
                adapter.removeItem(position);
                showSnackbarMessage("Satu item berhasil dihapus");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        noteHelp.close();
    }
    private void showSnackbarMessage(String message) {
        Snackbar.make(rvNotes, message, Snackbar.LENGTH_SHORT).show();
    }
}
