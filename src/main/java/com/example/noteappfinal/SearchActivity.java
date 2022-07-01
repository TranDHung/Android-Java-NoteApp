package com.example.noteappfinal;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteappfinal.NoteModule.Note;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<Note> listNote;
    private NoteListAdapter noteListAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private EditText stringSearch;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser curUser;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://noteappfinal-default-rtdb.firebaseio.com");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarsearch);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.notelistSeachRecyclerView);
        layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        curUser = firebaseAuth.getCurrentUser();

        stringSearch = findViewById(R.id.searchNote);
        stringSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                getData(editable.toString());
            }
        });
    }
    public void getData(String data){

        databaseReference = FirebaseDatabase.getInstance().getReference().child("username").child(curUser.getUid()).child("note");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listNote = new ArrayList<>();

                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    String id = String.valueOf(dataSnapshot.getKey());
                    String title = String.valueOf(dataSnapshot.child("title").getValue());
                    String content = String.valueOf(dataSnapshot.child("content").getValue());
                    Boolean isPin = (Boolean) dataSnapshot.child("isPin").getValue();
                    Boolean isInBin = (Boolean) dataSnapshot.child("isInBin").getValue();
                    Boolean isPassOn = (Boolean) dataSnapshot.child("isPasswOn").getValue();

                    if(data.isEmpty()){
                        listNote = new ArrayList<>();
                    }else{
                        if(isPin != null && isInBin != null && !isInBin && isPassOn != null){
                            if(title.indexOf(data) >= 0 || content.indexOf(data) >= 0){
                                if(isPassOn){
                                    String password = String.valueOf(dataSnapshot.child("password").getValue());
                                    listNote.add(new Note(id, title, content, isPin, isInBin, isPassOn, password));
                                }else{
                                    listNote.add(new Note(id, title, content, isPin, isInBin, isPassOn, ""));
                                }
                            }
                        }
                    }

                }
                noteListAdapter = new NoteListAdapter(SearchActivity.this, listNote);
                recyclerView.setAdapter(noteListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}