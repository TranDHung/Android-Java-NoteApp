package com.example.noteappfinal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class LabelAdapter extends RecyclerView.Adapter<LabelAdapter.LabelHolder> {
    private List<Label> listLabel;
    private Context context;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://noteappfinal-default-rtdb.firebaseio.com");

    public LabelAdapter(Context context, List<Label> listLabel){
        this.context = context;
        this.listLabel = listLabel;
    }

    @NonNull
    @Override
    public LabelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.label_view, parent, false);
        return new LabelHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LabelHolder holder, int i) {
        Label label = listLabel.get(i);
        holder.labelName.setText(label.getLabelName());
        holder.labelContent.setText(label.getLabelContent());
        holder.labelIcon.setOnClickListener(v -> {
            FirebaseAuth firebaseAuth;
            FirebaseUser curUser = FirebaseAuth.getInstance().getCurrentUser();
            String userId = curUser.getUid();
            new AlertDialog.Builder(context)
                    .setTitle("Xóa nhãn!")
                    .setMessage("Nhãn "+ label.getLabelName() +" sẽ bị xóa vĩnh viễn. Muốn xóa không?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("username").child(userId).child("label").child(label.getLabelId());
                            databaseRef.removeValue();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        });

        holder.view.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddLabelActivity.class);
            intent.putExtra("updateLabel", label.getLabelId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listLabel.size();
    }

    public class LabelHolder extends RecyclerView.ViewHolder{
        TextView labelName, labelContent;
        ImageView labelIcon;
        View view;

        public LabelHolder(@NonNull View itemView) {
            super(itemView);

            labelName = itemView.findViewById(R.id.labelTitle);
            labelContent = itemView.findViewById(R.id.labelContent);
            labelIcon = itemView.findViewById(R.id.iconLabelView);
            view = itemView.findViewById(R.id.labelView);
        }
    }

}
