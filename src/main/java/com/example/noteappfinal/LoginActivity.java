package com.example.noteappfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    private Button BtnLogin;
    private EditText username, password;
    private TextView register, forgotPass, notice;
    private FirebaseAuth firebaseAuth;

    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.setTitle("Đăng nhập");

        BtnLogin = findViewById(R.id.btnLogin);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        register = findViewById(R.id.register);
        forgotPass = findViewById(R.id.forgotpass);
        notice = findViewById(R.id.notifi);

        firebaseAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://noteappfinal-default-rtdb.firebaseio.com");

        register.setOnClickListener(view -> {
            Intent registerpage = new Intent(this, RegisterActivity.class);
            startActivity(registerpage);
        });

        BtnLogin.setOnClickListener(view -> {
            login();
        });

        forgotPass.setOnClickListener(view -> {
            Intent forgotPass = new Intent(this, ForgotActivity.class);
            forgotPass.putExtra("userforgotPass", username.getText().toString().trim());
            startActivityForResult(forgotPass, 130);
        });



    }

    private void login() {
        String user = username.getText().toString().trim();
        String pass = password.getText().toString().trim();
        if (user.isEmpty() || pass.isEmpty()) {
            notice.setText("Vui lòng điền đẩy đủ thông tin!");
        }else{
            firebaseAuth.signInWithEmailAndPassword(user, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Intent loginAccount = new Intent(LoginActivity.this, MainActivity.class);
                        loginAccount.putExtra("inforLogin", user);
                        startActivityForResult(loginAccount, 100);
                        finishAffinity();
                    }else{
                        Toast.makeText(LoginActivity.this, "Đã xãy ra lỗi", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}