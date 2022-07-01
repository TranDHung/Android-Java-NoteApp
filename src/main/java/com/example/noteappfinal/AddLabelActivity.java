package com.example.noteappfinal;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.noteappfinal.NoteModule.Note;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AddLabelActivity extends AppCompatActivity {

    private EditText nameLabel, contentLabel;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser curUser;
    private String userId = "";
    private String labelId = "";
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://noteappfinal-default-rtdb.firebaseio.com");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_label);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbaraddLabel);
        toolbar.setTitle("Thêm nhãn");
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        curUser = firebaseAuth.getCurrentUser();
        userId = curUser.getUid();

        nameLabel = findViewById(R.id.nameLabel);
        contentLabel = findViewById(R.id.contentLabel);


        if(getIntent().getExtras() == null){
            ImageView iconAdd;
            iconAdd = findViewById(R.id.addNoteInLabel);
            iconAdd.setVisibility(View.GONE);
        }else{
            toolbar.setTitle("Sửa nhãn");
            Bundle i = getIntent().getExtras();
            labelId = i.get("updateLabel").toString();
            databaseReference = FirebaseDatabase.getInstance().getReference().child("username").child(userId).child("label").child(labelId);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if((String.valueOf(snapshot.child("title").getValue()) != null && (String.valueOf(snapshot.child("content").getValue())) != null)){
                        nameLabel.setText(String.valueOf(snapshot.child("title").getValue()));
                        contentLabel.setText(String.valueOf(snapshot.child("content").getValue()));
                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            addLabel();
        }
        return super.onOptionsItemSelected(item);
    }

    private void addLabel() {
        String title = nameLabel.getText().toString().trim();
        String content = contentLabel.getText().toString().trim();
        if(getIntent().getExtras() == null){
            labelId = databaseReference.push().getKey();
            if (title.isEmpty() && content.isEmpty()){
                Toast.makeText(this, "Nhãn rỗng đã tự động xóa", Toast.LENGTH_SHORT).show();
            }else{
                databaseReference.child("username").child(userId).child("label").child(labelId).child("title").setValue(title);
                databaseReference.child("username").child(userId).child("label").child(labelId).child("content").setValue(content);
            }
        }else{
            HashMap labelUpdate = new HashMap();
            labelUpdate.put("title", title);
            labelUpdate.put("content", content);
            databaseReference = FirebaseDatabase.getInstance().getReference().child("username").child(userId).child("label").child(labelId);
            databaseReference.updateChildren(labelUpdate);
        }
        onBackPressed();
    }
}