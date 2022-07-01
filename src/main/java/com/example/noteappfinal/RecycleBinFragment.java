package com.example.noteappfinal;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteappfinal.NoteModule.Note;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class RecycleBinFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Note> listNote;
    private NoteListAdapter noteListAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser curUser;
    private int timeAutoRemove;
    private ValueEventListener valueEventListener, valueEventListenerLoad;
    private String userId = "";
    private SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://noteappfinal-default-rtdb.firebaseio.com");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycle_bin, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycleBinFragment);

        getActivity().setTitle("Thùng rác");

        firebaseAuth = FirebaseAuth.getInstance();
        curUser = firebaseAuth.getCurrentUser();
        userId = curUser.getUid();

        layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("username").child(userId).child("bin");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                timeAutoRemove = snapshot.getValue(Integer.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        getData();
        return view;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_views).setIcon(R.drawable.ic_outline_auto_delete);
        menu.findItem(R.id.action_changepass).setIcon(R.drawable.ic_baseline_delete_forever);
        menu.findItem(R.id.action_changepass).setTitle("Xóa sạch thùng rác!");
        menu.findItem(R.id.action_logout).setVisible(false);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_changepass:
                new AlertDialog.Builder(getActivity())
                        .setTitle("Xóa sạch!")
                        .setMessage("Bạn muốn xóa sạch thùng rác?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                deleteAll(true);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                Toast.makeText(getActivity(), "Xóa sạch thùng rác", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_views:
                showDialogChooseTimeAutoDelete();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void showDialogChooseTimeAutoDelete() {
        int selected = 0;
        switch (timeAutoRemove){
            case 3:
                break;
            case 5:
                selected = 1;
                break;
            case 7:
                selected = 2;
                break;
            case 15:
                selected = 3;
                break;
            case 30:
                selected = 4;
                break;
            case 0:
                selected = 5;
                break;
        }
        String[] stringList = new String[]{"3 ngày", "5 ngày", "7 ngày", "15 ngày", "30 ngày", "15 giây"};

        new MaterialAlertDialogBuilder(getActivity())
                .setTitle("Thời gian tự động xóa")
                .setSingleChoiceItems(stringList, selected, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                timeAutoRemove = 3;
                                Log.i("TAG", "onClick: 0");
                                dialogInterface.dismiss();
                                break;
                            case 1:
                                timeAutoRemove = 5;
                                Log.i("TAG", "onClick: 1");
                                dialogInterface.dismiss();
                                break;
                            case 2:
                                timeAutoRemove = 7;
                                Log.i("TAG", "onClick: 2");
                                dialogInterface.dismiss();
                                break;
                            case 3:
                                timeAutoRemove = 15;
                                Log.i("TAG", "onClick: 3");
                                dialogInterface.dismiss();
                                break;
                            case 4:
                                timeAutoRemove = 30;
                                Log.i("TAG", "onClick: 4");
                                dialogInterface.dismiss();
                                break;
                            case 5:
                                timeAutoRemove = 0;
                                Log.i("TAG", "onClick: 5");
                                dialogInterface.dismiss();
                                break;
                        }

                        HashMap timeAutoRemoveUpdate = new HashMap();
                        timeAutoRemoveUpdate.put("bin", timeAutoRemove);
                        databaseReference = FirebaseDatabase.getInstance().getReference().child("username").child(userId);
                        databaseReference.updateChildren(timeAutoRemoveUpdate);
                        listNote = new ArrayList<>();
                        getData();
                    }
                })
                .show();
    }

    public void getData(){
        Date currentTime = Calendar.getInstance().getTime();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("username").child(userId).child("note");

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("username").child(userId).child("bin");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                timeAutoRemove = snapshot.getValue(Integer.class);

                valueEventListenerLoad = databaseReference.orderByValue().addValueEventListener(new ValueEventListener() {
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
                            String moveToBin = (String) dataSnapshot.child("dateMoveToBin").getValue();

                            int timeR = timeAutoRemove*24*60*60;

                            if(isPin != null && isInBin != null && isPassOn != null){
                                if (timeR == 0){
                                    timeR = 15;
                                }

                                if(isInBin){
                                    String timeMove = String.valueOf(dataSnapshot.child("dateMoveToBin").getValue());
                                    long timeSeconds = -1;
                                    try {
                                        Date dateBin = format.parse(timeMove);
                                        timeSeconds = (currentTime.getTime() - dateBin.getTime())/1000;
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    if (timeSeconds != -1) {
                                        if (timeSeconds >= timeR) {
                                            dataSnapshot.getRef().removeValue();
                                        } else {
                                            listNote.add(new Note(id, title, content, isPin, isInBin, isPassOn, moveToBin));
                                        }
                                    }
                                }
                            }
                        }
                        Set<Note> hashSet = new LinkedHashSet(listNote);
                        ArrayList<Note> listNote1 = new ArrayList(hashSet);

                        Collections.sort(listNote1, new Comparator<Note>() {
                            public int compare(Note obj1, Note obj2) {
                                return obj2.getDateMoveToBin().compareTo(obj1.getDateMoveToBin());
                            }
                        });
                        noteListAdapter = new NoteListAdapter(getActivity(), listNote);
                        recyclerView.setAdapter(noteListAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void deleteAll(Boolean check) {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("username").child(userId).child("note");
        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Boolean isInBin = (Boolean) dataSnapshot.child("isInBin").getValue();

                    if(isInBin != null){
                        if((Boolean) dataSnapshot.child("isInBin").getValue() ){
                            dataSnapshot.getRef().removeValue();
                        }
                    }
                }
                databaseReference.removeEventListener(valueEventListenerLoad);
                noteListAdapter = new NoteListAdapter(getActivity(), new ArrayList<Note>());
                recyclerView.setAdapter(noteListAdapter);
                noteListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public void onPause() {
        super.onPause();
        if(valueEventListener != null){
            databaseReference.removeEventListener(valueEventListener);
        }
        listNote = new ArrayList<>();
    }
}
