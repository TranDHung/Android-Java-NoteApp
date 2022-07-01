package com.example.noteappfinal;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteappfinal.NoteModule.Note;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MainActivity extends AppCompatActivity{

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    private RecyclerView recyclerView;
    private NoteListAdapter noteListAdapter;
    RecyclerView.LayoutManager layoutManager;
    private ImageView addNote;
    private ValueEventListener valueEventListener;
    private List<Note> listNote;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://noteappfinal-default-rtdb.firebaseio.com");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        HomeFragment homeFragment = new HomeFragment();

        getSupportFragmentManager().beginTransaction().add(R.id.fragmentLayout, homeFragment).commit();

        drawerLayout = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.nav_view);
        navigationView.bringToFront();

        recyclerView = findViewById(R.id.notelistRecyclerView);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();
        RecycleBinFragment recycleBinFragment = new RecycleBinFragment();
        MamagerLabelsFragment mamagerLabelsFragment = new MamagerLabelsFragment();
        ReminderFragment reminderFragment = new ReminderFragment();

        navigationView.setNavigationItemSelectedListener(
            new NavigationView.OnNavigationItemSelectedListener(){
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    drawerLayout.closeDrawer(GravityCompat.START,true);
                    switch (item.getItemId()){
                        case R.id.nav_labels:
                            startActivity(new Intent(MainActivity.this, AddLabelActivity.class));
                            break;
                        case R.id.nav_note:
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayout, homeFragment).addToBackStack(null).commit();
                            break;
                        case R.id.nav_recycle_bin:
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayout, recycleBinFragment).commit();
                            break;
                        case R.id.nav_ma_labels:
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayout, mamagerLabelsFragment).commit();
                            break;
                        case R.id.nav_reminder:
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayout, reminderFragment).commit();
                            break;
                        case R.id.nav_setting:
                            Intent settingIntent = new Intent(MainActivity.this, SettingActivity.class);
                            startActivity(settingIntent);
                            break;
                        case R.id.nav_logout:
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            break;
                    }
                    return false;
                }
            });
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser CurUser = firebaseAuth.getCurrentUser();

        View header = navigationView.getHeaderView(0);
        Button btnActiveAccount = header.findViewById(R.id.activeAcount);
        TextView emailUser = header.findViewById(R.id.textView);
        emailUser.setText(CurUser.getEmail().toString());
        if(CurUser.isEmailVerified()){
            btnActiveAccount.setVisibility(View.GONE);
            header.setBackgroundResource(R.drawable.side_nav_bar);
        }


        btnActiveAccount.setOnClickListener(view -> {
            CurUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    String user = CurUser.getEmail().toString();
                    Intent verifyAccount = new Intent(MainActivity.this, ActiveAcountActivity.class);
                    verifyAccount.putExtra("username", user);
                    startActivityForResult(verifyAccount, 113);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "Không gửi được email", Toast.LENGTH_SHORT).show();
                }
            });
        });

        addNote = findViewById(R.id.fab);
        addNote.setOnClickListener(view -> {
            Intent addNoteIntent = new Intent(this, AddNoteActivity.class);
            startActivity(addNoteIntent);
        });

        String userId = CurUser.getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("username").child(userId).child("note");
        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long numNote = snapshot.getChildrenCount();
                addNote.setOnClickListener(view -> {
                    if(!CurUser.isEmailVerified() && numNote > 4){
                        Toast.makeText(MainActivity.this, "Đã đạt giới hạn ghi chú. Kích hoạt tài khoản để thêm ghi chú", Toast.LENGTH_SHORT).show();
                    }else{
                        Intent addNoteIntent = new Intent(MainActivity.this, AddNoteActivity.class);
                        startActivity(addNoteIntent);
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                break;
            case R.id.action_changepass:
                if(item.getIcon() != null){
                    if(item.getIcon().getConstantState().equals(getResources().getDrawable(R.drawable.ic_baseline_delete_forever).getConstantState())){
                    }else{}
                }else{
                    startActivity(new Intent(MainActivity.this, ChangePassActivity.class));
                }
                break;
            case R.id.action_views:
                break;
            case R.id.action_search:
                Intent searchNote = new Intent(this, SearchActivity.class);
                startActivity(searchNote);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(valueEventListener);
    }
}