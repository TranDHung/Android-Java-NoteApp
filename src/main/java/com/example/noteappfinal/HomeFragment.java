package com.example.noteappfinal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

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
import java.util.HashMap;
import java.util.List;

public class HomeFragment extends Fragment implements Toolbar.OnMenuItemClickListener {
    private RecyclerView recyclerView;
    private List<Note> listNote;
    private NoteListAdapter noteListAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser curUser;
    private String userId = "";
    private Boolean isGridView = false;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://noteappfinal-default-rtdb.firebaseio.com");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.notelistRecyclerView);

        getActivity().setTitle("Ghi chú");

        firebaseAuth = FirebaseAuth.getInstance();
        curUser = firebaseAuth.getCurrentUser();
        userId = curUser.getUid();

        getViewNote(layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        getData();

        return view;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_views:
                loadTypeView(item);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getViewNote(RecyclerView.LayoutManager layoutManager) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);


    }

    public void getData(){

        databaseReference = FirebaseDatabase.getInstance().getReference().child("username").child(userId).child("note");

        databaseReference.orderByKey().addValueEventListener(new ValueEventListener() {
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

                    if(isPin != null && isInBin != null && isPassOn != null){
                        if(!(Boolean) dataSnapshot.child("isPin").getValue() && !(Boolean) dataSnapshot.child("isInBin").getValue()){
                            if(isPassOn){
                                String password = String.valueOf(dataSnapshot.child("password").getValue());
                                listNote.add(new Note(id, title, content, isPin, isInBin, isPassOn, password, ""));
                            }else{
                                listNote.add(new Note(id, title, content, isPin, isInBin, isPassOn, ""));
                            }

                        }
                    }
                }
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    String id = String.valueOf(dataSnapshot.getKey());
                    String title = String.valueOf(dataSnapshot.child("title").getValue());
                    String content = String.valueOf(dataSnapshot.child("content").getValue());
                    Boolean isPin = (Boolean) dataSnapshot.child("isPin").getValue();
                    Boolean isInBin = (Boolean) dataSnapshot.child("isInBin").getValue();
                    Boolean isPassOn = (Boolean) dataSnapshot.child("isPasswOn").getValue();

                    if(isPin != null && isInBin != null && isPassOn != null){
                        if((Boolean) dataSnapshot.child("isPin").getValue() && !(Boolean) dataSnapshot.child("isInBin").getValue()){
                            if(isPassOn){
                                String password = String.valueOf(dataSnapshot.child("password").getValue());
                                listNote.add(new Note(id, title, content, isPin, isInBin, isPassOn, password, ""));
                            }else{
                                listNote.add(new Note(id, title, content, isPin, isInBin, isPassOn, ""));
                            }
                        }
                    }

                }
                Collections.reverse(listNote);
                noteListAdapter = new NoteListAdapter(getActivity(), listNote);
                recyclerView.setAdapter(noteListAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("TAG", "onCancelled: ");
            }
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_views:
                loadTypeView(item);
                break;
        }
        return false;
    }

    private void loadTypeView(MenuItem item) {
        if(isGridView){
            item.setIcon(R.drawable.ic_baseline_grid_view);
            layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
            isGridView = false;
        }else{
            item.setIcon(R.drawable.ic_baseline_view_stream);
            layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            isGridView = true;
        }
        getViewNote(layoutManager);
    }

}