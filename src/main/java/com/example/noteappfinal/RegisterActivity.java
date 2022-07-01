package com.example.noteappfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {
    private Button btnRegister;
    private EditText username, password, confirmPass;
    private TextView reLogin, notice;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://noteappfinal-default-rtdb.firebaseio.com");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        this.setTitle("Đăng ký tài khoản");

        btnRegister = findViewById(R.id.btnRegister);
        notice = findViewById(R.id.notifi);
        reLogin = findViewById(R.id.login);
        firebaseAuth = FirebaseAuth.getInstance();

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        confirmPass = findViewById(R.id.confirmPass);

        reLogin.setOnClickListener(view -> {
            Intent backtoLoginP = new Intent(this, LoginActivity.class);
            startActivity(backtoLoginP);
        });
        btnRegister.setOnClickListener(view -> {
            register();
        });

    }

    private void register() {
        String user = username.getText().toString().trim();
        String pass = password.getText().toString().trim();
        String conpass = confirmPass.getText().toString().trim();

        if (user.isEmpty() || pass.isEmpty() || conpass.isEmpty()) {
            notice.setText("Vui lòng điền đẩy đủ thông tin!");
        }else if(pass.length() <= 6) {
            notice.setText("Mật khẩu phải dài hơn 6 ký tự!");
        }else if(!pass.equals(conpass)){
            notice.setText("Mật khẩu không khớp!");
        }else{
            firebaseAuth.createUserWithEmailAndPassword(user, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    databaseReference.child("username").child(firebaseAuth.getCurrentUser().getUid()).child("bin").setValue(3);
                    if(task.isSuccessful()){
                        FirebaseUser CurUser = firebaseAuth.getCurrentUser();

                        CurUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(RegisterActivity.this, "Email đã được gửi "+CurUser.getEmail(), Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(RegisterActivity.this, "", Toast.LENGTH_SHORT).show();
                            }
                        });

                        Intent verifyAccount = new Intent(RegisterActivity.this, ActiveAcountActivity.class);
                        verifyAccount.putExtra("username", user);
                        startActivityForResult(verifyAccount, 100);
                    }else{
                        Toast.makeText(RegisterActivity.this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

}