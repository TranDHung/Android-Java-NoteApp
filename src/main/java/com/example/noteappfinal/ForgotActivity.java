package com.example.noteappfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotActivity extends AppCompatActivity {
    private TextView usernameText, message;
    private Button returnLogin, sendEmail;
    private EditText username;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);
        this.setTitle("Quên mật khẩu");

        returnLogin = findViewById(R.id.btnReturnLogin);
        usernameText = findViewById(R.id.username);
        username = findViewById(R.id.userEditText);
        sendEmail = findViewById(R.id.sendForgotB);
        message = findViewById(R.id.mesageforgot);
        firebaseAuth = FirebaseAuth.getInstance();

        message.setText("");

        Bundle i = getIntent().getExtras();
        username.setText(i.getString("userforgotPass"));

        sendEmail.setOnClickListener(view ->{
            firebaseAuth.sendPasswordResetEmail(username.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        message.setText("Link thay đổi mật khẩu đã được gửi đến email của bạn. Chuyển đến đăng nhập sau 5s");
                        usernameText.setText(username.getText().toString());
                        username.setVisibility(View.GONE);
                        sendEmail.setVisibility(View.INVISIBLE);

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                Intent intent = new Intent(ForgotActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }
                        }, 5000);

                    }else{
                        message.setText("Không gửi được Email");
                    }
                }
            });
        });


        returnLogin.setOnClickListener(view -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });
    }
}