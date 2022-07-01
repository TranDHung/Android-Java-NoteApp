package com.example.noteappfinal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteappfinal.Label;
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

public class MamagerLabelsFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<Label> listLabel;
    private LabelAdapter labelAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser curUser;
    private String userId = "";
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://noteappfinal-default-rtdb.firebaseio.com");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manager_labels, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycleLabelsView);

        getActivity().setTitle("Quản lý nhãn");

        firebaseAuth = FirebaseAuth.getInstance();
        curUser = firebaseAuth.getCurrentUser();
        userId = curUser.getUid();

        layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        getData();

        return view;
    }

    private void getData() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("username").child(userId).child("label");

        databaseReference.orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listLabel = new ArrayList<>();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    String id = String.valueOf(dataSnapshot.getKey());
                    String title = String.valueOf(dataSnapshot.child("title").getValue());
                    String content = String.valueOf(dataSnapshot.child("content").getValue());

                    listLabel.add(new Label(id, title, content));
                }
                Collections.reverse(listLabel);
                labelAdapter = new LabelAdapter(getActivity(), listLabel);
                recyclerView.setAdapter(labelAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_views).setVisible(false);
        menu.findItem(R.id.action_changepass).setVisible(false);
        menu.findItem(R.id.action_logout).setVisible(false);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
