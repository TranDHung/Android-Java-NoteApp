package com.example.noteappfinal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteappfinal.NoteModule.Note;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.NoteListHolder> {
    private List<Note> listNote;
    private Context context;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser curUser;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://noteappfinal-default-rtdb.firebaseio.com");

    public NoteListAdapter(Context context, List<Note> noteList){
        this.context = context;
        this.listNote = noteList;

    }

    @NonNull
    @Override
    public NoteListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_view, parent, false);
        return new NoteListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteListHolder holder, int i) {
        Note note = listNote.get(i);
        holder.titleNote.setText(note.getTitle());
        holder.contentNote.setText(note.getContent());
        if(note.getPassOn()) {
            holder.contentNote.setText("*********");
        }
        if(note.getPin()){
            holder.view.setBackgroundResource(R.drawable.box_pin);
        }else{
            holder.view.setBackgroundResource(R.drawable.box);
        }
        holder.view.setOnClickListener(view -> {
            if(note.getPassOn()){
                EditText inputPass = new EditText(context);
                inputPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                new AlertDialog.Builder(context)
                        .setTitle("Nhập mật khẩu")
                        .setView(inputPass)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Editable editable = inputPass.getText();
                                String passInput = editable.toString();

                                if(passInput.equals(note.getPassword())){
                                    Intent intent = new Intent(context, AddNoteActivity.class);
                                    intent.putExtra("idNote", note.getId());
                                    ((Activity) context).startActivityForResult(intent, 10);
                                }else {
                                    Toast.makeText(context, "Mật khẩu sai rồi!!!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            }else{
                Intent intent = new Intent(context, AddNoteActivity.class);
                intent.putExtra("idNote", note.getId());
                ((Activity) context).startActivityForResult(intent, 10);
            }
        });
        if(note.getInBin()){
            holder.iconView.setImageResource(R.drawable.ic_baseline_restore_from_trash);
            holder.iconView.setOnClickListener(view -> {
                restore(note);
            });
        }else{
            holder.iconView.setOnClickListener(view -> {
                moveToBin(note);
            });
        }
    }

    private void restore(Note note) {
        curUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference.child("username").child(curUser.getUid()).child("note").child(note.getId()).child("isInBin").setValue(false);
    }

    public void moveToBin(Note note){
        curUser = FirebaseAuth.getInstance().getCurrentUser();
        Date dateMoteBin = Calendar.getInstance().getTime();
        databaseReference.child("username").child(curUser.getUid()).child("note").child(note.getId()).child("isInBin").setValue(true);
        databaseReference.child("username").child(curUser.getUid()).child("note").child(note.getId()).child("dateMoveToBin").setValue(dateMoteBin.toString());

    }

    @Override
    public int getItemCount() {
        return listNote.size();
    }


    public class NoteListHolder extends RecyclerView.ViewHolder{
        TextView titleNote, contentNote;
        View view;
        ImageView iconView;

        public NoteListHolder(@NonNull View itemView) {
            super(itemView);
            titleNote = itemView.findViewById(R.id.noteTitle);
            contentNote = itemView.findViewById(R.id.noteContent);
            iconView = itemView.findViewById(R.id.iconNoteView);

            view = itemView.findViewById(R.id.viewNote);
        }
    }
}
