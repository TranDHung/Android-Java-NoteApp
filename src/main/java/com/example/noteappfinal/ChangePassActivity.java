package com.example.noteappfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassActivity extends AppCompatActivity {

    private EditText oldpass, newpass, newPassConf;
    private Button saveChange;
    private TextView notice;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser curUser;
    private String oldPwd;
    private String newPwd;
    private String newPwdCf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarchangepass);
        toolbar.setTitle("Thay đổi mật khẩu");
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        curUser = firebaseAuth.getCurrentUser();

        oldpass = findViewById(R.id.oldpass);
        newpass = findViewById(R.id.newpassword);
        newPassConf = findViewById(R.id.confirmNewPass);
        notice = findViewById(R.id.notifiChangePass);
        saveChange = findViewById(R.id.btnsave);

        saveChange.setOnClickListener(view -> {
            oldPwd = oldpass.getText().toString();
            newPwd = newpass.getText().toString();
            newPwdCf = newPassConf.getText().toString();

            if (oldPwd.isEmpty() || newPwd.isEmpty() || newPwdCf.isEmpty()) {
                notice.setText("Vui lòng điền đẩy đủ thông tin!");
            }else if(newPwd.length() <= 6){
                notice.setText("Mật khẩu mới phải dài tối thiếu 6 ký tự!");
            } else if(!newPwd.equals(newPwdCf)){
                notice.setText("Mật khẩu không khớp!");
            }else{
                changePassWord();
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

    public void changePassWord(){
        AuthCredential authCredential = EmailAuthProvider.getCredential(curUser.getEmail(), oldPwd);

        curUser.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    curUser.updatePassword(newPwd).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ChangePassActivity.this, "Đã thay đổi mật khầu thành công", Toast.LENGTH_SHORT).show();
                                finish();
                            }else{
                                Toast.makeText(ChangePassActivity.this, "Xảy ra lồi rồi, Oppss", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    notice.setText("Hình như mật khẩu hiện tại không đúng!");
                }
            }
        });
    }
}