package com.example.chatapp.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.models.Group;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class OnlineUsersAdapter extends RecyclerView.Adapter<OnlineUsersAdapter.ViewHolder>{

    private ArrayList<Group> listUsers;
    private Context context;

    public OnlineUsersAdapter(ArrayList<Group> listUsers, Context context) {
        this.listUsers = listUsers;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_onlineuser,parent,false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Group user = listUsers.get(position);
        String name = user.getName();
        FirebaseStorage.getInstance().getReference().child("images/"+user.getImageId())
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(holder.im_item);
                        Log.i("uri", uri.toString());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });
        holder.tv_name.setText(name);

        if(!user.isOnline()) {
            holder.im_online.setImageResource(R.color.yellow_circle);
        } else {
            holder.im_online.setImageResource(R.color.green_circle);
        }
    }

    @Override
    public int getItemCount() {
        return listUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView im_item;
        TextView tv_name;
        CircleImageView im_online;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            im_item = (CircleImageView)itemView.findViewById(R.id.civImage);
            im_online = (CircleImageView) itemView.findViewById(R.id.civOnlineCircle);
            tv_name = (TextView) itemView.findViewById(R.id.tvOnlineUser);
        }
    }
}
