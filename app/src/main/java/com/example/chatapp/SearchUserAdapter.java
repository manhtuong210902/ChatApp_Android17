package com.example.chatapp;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.models.Group;
import com.example.chatapp.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;
////////import com.squareup.picasso.Picasso;*////

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchUserAdapter extends RecyclerView.Adapter<SearchUserAdapter.ViewHolder> {
    private ArrayList<User> listUser;
    private Context context;
    private final RecyclerViewInterface recyclerViewInterface;

    public SearchUserAdapter(ArrayList<User> listUser, Context context, RecyclerViewInterface recyclerViewInterface) {
        this.listUser = listUser;
        this.context = context;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.search_users_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(itemView, recyclerViewInterface);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = listUser.get(position);
        String name = user.getName();
        holder.tvName.setText(name);
        holder.civAvatar.setImageResource(R.drawable.cute1);
    }

    @Override
    public int getItemCount() {
        return listUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView civAvatar;
        TextView tvName;
        public ViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            civAvatar = (CircleImageView)itemView.findViewById(R.id.civSearchAvatar);
            tvName = (TextView) itemView.findViewById(R.id.tvSearchName);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(recyclerViewInterface != null){
                        int pos = getAdapterPosition();
                        if(pos != RecyclerView.NO_POSITION){
                            recyclerViewInterface.onItemClick(pos);
                        }
                    }
                }
            });
        }
    }
}