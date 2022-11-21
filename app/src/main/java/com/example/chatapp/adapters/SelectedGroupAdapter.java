package com.example.chatapp.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.interfaces.AddSelectedListListener;
import com.example.chatapp.R;
import com.example.chatapp.models.AddGroupUser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SelectedGroupAdapter extends RecyclerView.Adapter<SelectedGroupsViewHolder>{
    Context context;
    List<AddGroupUser> list;
    AddSelectedListListener listener;

    public SelectedGroupAdapter(Context context, List<AddGroupUser> list, AddSelectedListListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SelectedGroupsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SelectedGroupsViewHolder(LayoutInflater.from(context).inflate(R.layout.item_group_selected, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedGroupsViewHolder holder, int position) {
//            Picasso.get().load(list.get(position).getInfo().getImage()).into(holder.imageView_avatar);
        FirebaseStorage.getInstance().getReference().child("images/"+list.get(position).getInfo().getImage())
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(holder.imageView_avatar);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });
//            holder.imageView_avatar.setImageResource(R.drawable.cute1);
            holder.imageView_removeItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onMemberClicked(holder.getAdapterPosition(), false);
                }
            });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

class SelectedGroupsViewHolder extends RecyclerView.ViewHolder{
    CircleImageView imageView_avatar;
    ImageView imageView_removeItem;
    public SelectedGroupsViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView_avatar = itemView.findViewById(R.id.imageView_avatar);
        imageView_removeItem= itemView.findViewById(R.id.imageView_removeItem);
    }
}
