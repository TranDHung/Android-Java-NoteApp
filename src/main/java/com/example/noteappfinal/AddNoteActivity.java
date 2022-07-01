package com.example.noteappfinal;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.noteappfinal.NoteModule.Note;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class AddNoteActivity extends AppCompatActivity{
    private static final int REQUEST_CODE = 222;
    private static final int REQUEST_CODE_IMG = 200;
    private static final int REQUEST_CODE_VIDEO = 201;
    private static final int REQUEST_CODE_RECORD = 245;

    private BottomAppBar bottomAppBar;
    private EditText title, content, passNote;
    private ImageView iconPass, imageSelected, imageDelete, videoDelete, audioDelete, audioRemote;
    private VideoView videoSelected;
    private TextView audioName;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser curUser;
    private Boolean pin = false;
    private Boolean isNotifi = false;
    private Boolean isCheckNotifi = true;
    private Boolean isInBin = false;
    private String date = "";
    private String time = "";
    private String noteId = "";
    private String userId = "";
    private Boolean isPassOn = false;
    private Uri imageURI;
    private Uri videoURI;
    private Uri audioURI;
    private MediaPlayer player;
    HashMap noteUpdate = new HashMap();
    private Boolean URLImage = false, URLVideo = false, URLAudio = false;
    private MediaController mediaController;
    private ValueEventListener valueEventListener;
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private StorageReference storageReference = firebaseStorage.getReferenceFromUrl("gs://noteappfinal.appspot.com");
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://noteappfinal-default-rtdb.firebaseio.com");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        firebaseAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarAdd);
        toolbar.setTitle("Thêm ghi chú");
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        curUser = firebaseAuth.getCurrentUser();

        userId = curUser.getUid();

        bottomAppBar = findViewById(R.id.bottomAppBar);

        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.addPicture:
                        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                            requestPermission();
                        }else{
                            choosePicture();
                        }
                        URLImage = false;
                        break;
                    case R.id.addVideo:
                        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                            requestPermission();
                        }else{
                            chooseVideo();
                        }
                        URLVideo = false;
                        break;
                    case R.id.addRecord:
                        player.stop();
                        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                            requestPermission();
                        }else{
                            chooseRecord();
                        }
                        URLAudio = false;
                        break;
                }
                return false;
            }

        });


        passNote = findViewById(R.id.passwordOfNote);
        iconPass = findViewById(R.id.iconPass);
        imageSelected = findViewById(R.id.imageSelected);
        imageDelete = findViewById(R.id.deleteImage);
        videoSelected = findViewById(R.id.videoSelected);
        videoDelete = findViewById(R.id.deleteVideo);

        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        audioName = findViewById(R.id.audioSelected);
        audioDelete = findViewById(R.id.deleteAudio);
        audioRemote = findViewById(R.id.audioRemote);

        mediaController = new MediaController(this);
        videoSelected.setMediaController(mediaController);
        mediaController.setAnchorView(videoSelected);
        if(audioURI == null){
            audioDelete.setVisibility(View.GONE);
            audioName.setVisibility(View.GONE);
            audioRemote.setVisibility(View.GONE);
        }
        if(imageURI == null){
            imageDelete.setVisibility(View.GONE);
        }
        if(videoURI == null){
            videoSelected.setVisibility(View.GONE);
            videoDelete.setVisibility(View.GONE);
        }

        audioDelete.setOnClickListener(view -> {
            Toast.makeText(this, "Đã xóa", Toast.LENGTH_SHORT).show();
            audioName.setText("");
            audioDelete.setVisibility(View.GONE);
            audioName.setVisibility(View.GONE);
            audioRemote.setVisibility(View.GONE);
            audioURI = null;
            player.stop();
            player = new MediaPlayer();
            noteUpdate.put("audio", null);
        });

        audioRemote.setOnClickListener(view -> {
            if(audioRemote.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.ic_baseline_pause).getConstantState())){
                player.pause();
                audioRemote.setImageResource(R.drawable.ic_baseline_play_arrow);
            }else{
                player.start();
                audioRemote.setImageResource(R.drawable.ic_baseline_pause);
            }
        });

        imageDelete.setOnClickListener(view -> {
            Toast.makeText(this, "Đã xóa", Toast.LENGTH_SHORT).show();
            imageSelected.setImageBitmap(null);
            imageDelete.setVisibility(View.GONE);
            imageURI = null;
            noteUpdate.put("image", null);
        });

        videoDelete.setOnClickListener(view -> {
            Toast.makeText(this, "Đã xóa", Toast.LENGTH_SHORT).show();
            videoSelected.setVideoURI(null);
            videoSelected.setVisibility(View.GONE);
            videoDelete.setVisibility(View.GONE);
            videoURI = null;
            noteUpdate.put("video", null);
        });

        if(getIntent().getExtras() == null){
            passNote.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if(!editable.toString().isEmpty()){
                        iconPass.setImageResource(R.drawable.ic_baseline_key_on);
                        isPassOn = true;
                    }else{
                        iconPass.setImageResource(R.drawable.ic_baseline_key_off);
                        isPassOn = false;
                    }
                }
            });
        }

        if(getIntent().getExtras() == null){
        }else{
            toolbar.setTitle("Sửa ghi chú");
            Bundle i = getIntent().getExtras();
            noteId =  (String) i.get("idNote");
            title = findViewById(R.id.titleNoteCre);
            content = findViewById(R.id.contentNoteCre);

            databaseReference = FirebaseDatabase.getInstance().getReference().child("username").child(userId).child("note").child(noteId);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if((String.valueOf(snapshot.child("title").getValue()) != null && (String.valueOf(snapshot.child("content").getValue())) != null)){
                        title.setText(String.valueOf(snapshot.child("title").getValue()));
                        content.setText(String.valueOf(snapshot.child("content").getValue()));
                        pin = (Boolean) snapshot.child("isPin").getValue();
                        isInBin = (Boolean) snapshot.child("isInBin").getValue();
                        isPassOn = (Boolean) snapshot.child("isPasswOn").getValue();
                        isNotifi = (Boolean) (snapshot.child("dateReminder").getValue() != null) ? true:false;

                        String password = String.valueOf(snapshot.child("password").getValue());
                        String imageU = String.valueOf(snapshot.child("image").getValue());
                        String videoU = String.valueOf(snapshot.child("video").getValue());
                        String audioU = String.valueOf(snapshot.child("audio").getValue());

                        if(isInBin != null){
                            if(isInBin){
                                toolbar.setTitle("");
                                title.setEnabled(false);
                                content.setEnabled(false);
                                passNote.setEnabled(false);
                                imageDelete.setVisibility(View.GONE);
                                videoDelete.setVisibility(View.GONE);
                                audioDelete.setVisibility(View.GONE);
                                bottomAppBar.setVisibility(View.GONE);
                            }
                        }


                        if(isPassOn != null){
                            if(isPassOn){
                                iconPass.setImageResource(R.drawable.ic_baseline_key_on);
                            }else{
                                iconPass.setImageResource(R.drawable.ic_baseline_key_off);
                            }

                        }
                        if(snapshot.child("password").getValue() != null){
                            passNote.setText(password);
                        }
                        if(snapshot.child("image").getValue() != null){
                            imageURI = Uri.parse(imageU);
                            Picasso.with(AddNoteActivity.this)
                                    .load(Uri.parse(imageU))
                                    .into(imageSelected);
                            imageDelete.setVisibility(View.VISIBLE);
                            noteUpdate.put("image", imageURI.toString());
                        }
                        if(snapshot.child("video").getValue() != null){
                            videoURI = Uri.parse(videoU);

                            mediaController = new MediaController(AddNoteActivity.this);

                            videoSelected.setVideoURI(videoURI);
                            videoSelected.requestFocus();
                            videoSelected.start();

                            videoDelete.setVisibility(View.VISIBLE);
                            videoSelected.setVisibility(View.VISIBLE);
                            noteUpdate.put("video", videoURI.toString());
                        }
                        if(snapshot.child("audio").getValue() != null){
                            audioURI = Uri.parse(audioU);
                            player = new MediaPlayer();
                            try {
                                player.setDataSource(AddNoteActivity.this, audioURI);
                                player.prepare();
                            } catch (IOException e) {
                            }
                            audioRemote.setImageResource(R.drawable.ic_baseline_play_arrow);
                            audioName.setText("File Audio");
                            audioDelete.setVisibility(View.VISIBLE);
                            audioName.setVisibility(View.VISIBLE);
                            audioRemote.setVisibility(View.VISIBLE);
                            noteUpdate.put("audio", audioURI.toString());
                        }
                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }

        iconPass.setOnClickListener(view -> {
            if(!isPassOn){
                Toast.makeText(AddNoteActivity.this, "Password On", Toast.LENGTH_SHORT).show();
                iconPass.setImageResource(R.drawable.ic_baseline_key_on);
                isPassOn = true;
            }else{
                Toast.makeText(AddNoteActivity.this, "Pasword Off", Toast.LENGTH_SHORT).show();
                iconPass.setImageResource(R.drawable.ic_baseline_key_off);
                isPassOn = false;
            }
        });


    }

    private void chooseRecord() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_RECORD);
    }

    private void chooseVideo() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_VIDEO);
    }

    private void choosePicture() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_IMG);

    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[] {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }
                , REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode != REQUEST_CODE){
            return;
        }
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            choosePicture();
        }else{
            Toast.makeText(this, "Từ chối cấp quyền", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_IMG && resultCode == RESULT_OK){
            if(data != null){
                imageURI = data.getData();
                if(imageURI != null) {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(imageURI);
                        Bitmap btm = BitmapFactory.decodeStream(inputStream);
                        imageSelected.setImageBitmap(btm);
                        imageDelete.setVisibility(View.VISIBLE);
                        URLImage = true;
                    }catch (Exception e){
                    }
                }
            }
        }

        if(requestCode == REQUEST_CODE_VIDEO && resultCode == RESULT_OK){
            if(data != null){
                videoURI = data.getData();
                if(videoURI != null) {
                    videoSelected.setVideoURI(videoURI);
                    videoDelete.setVisibility(View.VISIBLE);
                    videoSelected.setVisibility(View.VISIBLE);
                    videoSelected.start();
                    URLVideo = true;
                }
            }
        }
        if(requestCode == REQUEST_CODE_RECORD && resultCode == RESULT_OK){
            if(data != null){
                audioURI = data.getData();
                if(audioURI != null) {
                    try {
                        player.setDataSource(this, audioURI);
                        player.prepare();
                        player.start();

                        audioName.setText("File Audio");
                        audioDelete.setVisibility(View.VISIBLE);
                        audioName.setVisibility(View.VISIBLE);
                        audioRemote.setVisibility(View.VISIBLE);
                        audioRemote.setImageResource(R.drawable.ic_baseline_pause);
                        URLAudio = true;
                    }catch (Exception e){
                    }
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_add_note, menu);
        if(getIntent().getExtras() == null){
            menu.findItem(R.id.action_movetoBin).setVisible(false);
        }
        if(pin){
            menu.findItem(R.id.action_pin).setIcon(R.drawable.ic_baseline_push_pin_true);
        }
        if(isNotifi){
            menu.findItem(R.id.action_reminder).setIcon(R.drawable.ic_baseline_notifications_active);
        }
        if(isInBin){
            menu.findItem(R.id.action_movetoBin).setVisible(false);

            menu.findItem(R.id.action_pin).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            menu.findItem(R.id.action_pin).setTitle("Khôi phục");

            menu.findItem(R.id.action_reminder).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            menu.findItem(R.id.action_reminder).setTitle("Xóa vĩnh viễn");

        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                addNote();
                player.stop();
                onBackPressed();
                break;
            case R.id.action_pin:
                if(isInBin){
                    restore();
                }else{
                    if (!pin) {
                        item.setIcon(R.drawable.ic_baseline_push_pin_true);
                        pin = true;
                    } else {
                        item.setIcon(R.drawable.ic_outline_push_pin);
                        pin = false;
                    }
                }
                break;
            case R.id.action_reminder:
                if(isInBin){
                    new AlertDialog.Builder(this)
                            .setTitle("Xóa nhãn!")
                            .setMessage("Ghi chú " + title.getText().toString() + " sẽ bị xóa vĩnh viễn. Muốn xóa không?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("username").child(userId).child("note").child(noteId);
                                    databaseRef.removeValue();
                                    onBackPressed();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }else{
                    if (!isNotifi) {
                        showDatePickerDialog(item);
                    } else {
                        item.setIcon(R.drawable.ic_baseline_add_alert);
                        date = null;
                        isNotifi = false;
                        isCheckNotifi = false;
                    }
                }
                break;

            case R.id.action_movetoBin:
                if (getIntent().getExtras() != null) {
                    isInBin = true;
                    Toast.makeText(this, "Note " + title.getText().toString() + " đã xóa vào thùng rác", Toast.LENGTH_SHORT).show();
                    addNote();
                } else {
                    item.setIcon(R.drawable.ic_baseline_delete_off);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showTimePickerDialog(MenuItem item) {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                time = i+ ":"+i1;
                item.setIcon(R.drawable.ic_baseline_notifications_active);
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener, 24, 0, true);
        timePickerDialog.show();
    }

    private void showDatePickerDialog(MenuItem item) {
        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                i1++;
                date = i2 +"/"+ i1+ "/"+i;
                showTimePickerDialog(item);
                isNotifi = true;
            }
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                onDateSetListener,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                );
        datePickerDialog.show();
    }

    private void addNote() {
        title = findViewById(R.id.titleNoteCre);
        content = findViewById(R.id.contentNoteCre);

        String til = title.getText().toString().trim();
        String cont = content.getText().toString().trim();
        String pass = passNote.getText().toString().trim();

        if(noteId.isEmpty()){
            noteId = databaseReference.push().getKey();
            String titleStr = title.getText().toString().trim();
            String contentStr = content.getText().toString().trim();

            if (titleStr.isEmpty() && contentStr.isEmpty()){
                Toast.makeText(this, "Ghi chú rỗng đã tự động xóa", Toast.LENGTH_SHORT).show();
            }else{
                databaseReference = FirebaseDatabase.getInstance().getReference();

                if(audioURI != null){
                    pushAudioFile();
                }

                if(imageURI != null) {
                    pushImageFile();
                }
                if(videoURI != null){
                    pushVideoFile();
                }
                databaseReference.child("username").child(userId).child("note").child(noteId).child("title").setValue(til);
                databaseReference.child("username").child(userId).child("note").child(noteId).child("content").setValue(cont);
                databaseReference.child("username").child(userId).child("note").child(noteId).child("isPin").setValue(pin);
                databaseReference.child("username").child(userId).child("note").child(noteId).child("isInBin").setValue(false);
                if(isNotifi){
                    databaseReference.child("username").child(userId).child("note").child(noteId).child("dateReminder").setValue(date);
                    if(time != null){
                        databaseReference.child("username").child(userId).child("note").child(noteId).child("timeReminder").setValue(time);

                    }
                }
                databaseReference.child("username").child(userId).child("note").child(noteId).child("isPasswOn").setValue(false);
                if(!pass.isEmpty()){
                    databaseReference.child("username").child(userId).child("note").child(noteId).child("isPasswOn").setValue(isPassOn);
                    databaseReference.child("username").child(userId).child("note").child(noteId).child("password").setValue(pass);
                }
            }
        }else{
            noteUpdate.put("title", til);
            noteUpdate.put("content", cont);
            noteUpdate.put("isPin", pin);
            noteUpdate.put("isInBin", isInBin);

            if(isInBin){
                Date date = Calendar.getInstance().getTime();
                noteUpdate.put("isInBin", isInBin);
                noteUpdate.put("dateMoveToBin", date.toString());
            }

            if(isNotifi){
                noteUpdate.put("dateReminder", date);
                noteUpdate.put("timeReminder", time);
                if(time != null){
                    noteUpdate.put("timeReminder", null);
                }
            }

            if(!isCheckNotifi){
                noteUpdate.put("dateReminder", null);
                noteUpdate.put("timeReminder", null);
            }

            if(pass.isEmpty()){
                noteUpdate.put("isPasswOn", false);
                noteUpdate.put("password", "");
            }else{
                noteUpdate.put("isPasswOn", isPassOn);
                noteUpdate.put("password", pass);
            }
            if(imageURI != null && URLImage){
                pushImageFile();
            }
            if(videoURI != null && URLVideo){
                pushVideoFile();
            }
            if(audioURI != null && URLAudio){
                pushAudioFile();
            }

            databaseReference = FirebaseDatabase.getInstance().getReference().child("username").child(userId).child("note").child(noteId);
            databaseReference.updateChildren(noteUpdate);
        }
        onBackPressed();
    }

    public void pushAudioFile(){
        DatabaseReference databaseRefer = FirebaseDatabase.getInstance().getReference();
        StorageReference storageRf = storageReference.child("audio").child(noteId);
        storageRf.putFile(audioURI)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageRf.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                databaseRefer.child("username").child(userId).child("note").child(noteId).child("audio").setValue(String.valueOf(uri));
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    public void pushVideoFile(){
        DatabaseReference databaseRefer = FirebaseDatabase.getInstance().getReference();
        StorageReference storageRf = storageReference.child("video").child(noteId);
        storageRf.putFile(videoURI)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageRf.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                databaseRefer.child("username").child(userId).child("note").child(noteId).child("video").setValue(String.valueOf(uri));
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    public void pushImageFile(){
        DatabaseReference databaseRefer = FirebaseDatabase.getInstance().getReference();
        StorageReference storageRf = storageReference.child("image").child(noteId);
        storageRf.putFile(imageURI)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageRf.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                databaseRefer.child("username").child(userId).child("note").child(noteId).child("image").setValue(String.valueOf(uri));
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    public void restore(){
        HashMap noteUpdate = new HashMap();
        noteUpdate.put("isInBin", false);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("username").child(userId).child("note").child(noteId);
        databaseReference.updateChildren(noteUpdate);
        onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}