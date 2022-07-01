package com.example.noteappfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ActiveAcountActivity extends AppCompatActivity {
    private TextView username, skipAcitve, checkActive;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_acount);

        username = findViewById(R.id.username);
        checkActive = findViewById(R.id.checkActive);

        Bundle i = getIntent().getExtras();
        String user =  (String)i.get("username");
        username.setText(user);

        skipAcitve = findViewById(R.id.skipActive);
        skipAcitve.setOnClickListener(view -> {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.getCurrentUser().reload();
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();

            if(currentUser.isEmailVerified()) {
                Toast.makeText(ActiveAcountActivity.this, "Tài khoản đã được kích hoạt", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Tài khoản chưa được kích hoạt", Toast.LENGTH_SHORT).show();
            }

            Intent loginAccount = new Intent(this, MainActivity.class);
            loginAccount.putExtra("inforLogin", user);
            startActivityForResult(loginAccount, 100);
        });

        checkActive.setOnClickListener(view -> {

            FirebaseAuth fbAuth = FirebaseAuth.getInstance();
            fbAuth.getCurrentUser().reload().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    FirebaseUser currentUser = fbAuth.getCurrentUser();

                    if(currentUser.isEmailVerified()){
                        Toast.makeText(ActiveAcountActivity.this, "Kích hoạt thành công tài khoản", Toast.LENGTH_SHORT).show();
                        Intent loginAccount = new Intent(ActiveAcountActivity.this, MainActivity.class);
                        loginAccount.putExtra("inforLogin", user);
                        startActivityForResult(loginAccount, 100);
                    }
                }
            });
        });

    }
}