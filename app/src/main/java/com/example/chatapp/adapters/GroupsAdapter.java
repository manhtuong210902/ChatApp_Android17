package com.example.chatapp.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.interfaces.RecyclerViewInterface;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import com.example.chatapp.models.Group;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsViewHolder>{
    Context context;
    List<Group> list;
    private final RecyclerViewInterface recyclerViewInterface;

    public GroupsAdapter(Context context, List<Group> list, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.list = list;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public GroupsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GroupsViewHolder(LayoutInflater.from(context).inflate(R.layout.group_item,parent, false), recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupsViewHolder holder, int position) {
        Group group=list.get(position);

        FirebaseStorage.getInstance().getReference().child("images/"+group.getImageId())
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(holder.image_GroupAvatar);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });
        holder.textView_GroupName.setText(list.get(position).getName());
        holder.textView_GroupName.setSelected(true);
        holder.textView_GroupStatus.setText("news");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

class GroupsViewHolder extends RecyclerView.ViewHolder{
    TextView textView_GroupName,  textView_GroupStatus;
    CircleImageView image_GroupAvatar;
    private final RecyclerViewInterface recyclerViewInterface;
    public GroupsViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
        super(itemView);
        textView_GroupName = itemView.findViewById(R.id.textView_GroupName);
        textView_GroupStatus = itemView.findViewById(R.id.textView_GroupStatus);
        image_GroupAvatar = itemView.findViewById(R.id.image_GroupAvatar);
        this.recyclerViewInterface = recyclerViewInterface;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(GroupsViewHolder.this.recyclerViewInterface != null){
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        GroupsViewHolder.this.recyclerViewInterface.onItemClick(pos);
                    }
                }
            }
        });
    }
}
