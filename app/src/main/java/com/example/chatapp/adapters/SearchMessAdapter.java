package com.example.chatapp.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.activities.LoginActivity;
import com.example.chatapp.interfaces.RecyclerViewInterface;
import com.example.chatapp.models.ChatMessage;
import com.example.chatapp.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;
////////import com.squareup.picasso.Picasso;*////

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchMessAdapter extends RecyclerView.Adapter<SearchMessAdapter.ViewHolder> {
    private ArrayList<ChatMessage> listMess;
    private Context context;
    private final RecyclerViewInterface recyclerViewInterface;
    private DatabaseReference mDatabase;
    public SearchMessAdapter(ArrayList<ChatMessage> listMess, Context context, RecyclerViewInterface recyclerViewInterface) {
        this.listMess = listMess;
        this.context = context;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.search_mess_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(itemView, recyclerViewInterface);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessage cm = listMess.get(position);
        String mess = cm.getMessage();
        holder.tvMess.setText(mess);
        ArrayList<User> listUser=new ArrayList<>();
        DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User u=dataSnapshot.getValue(User.class);
                    if(u.getUid().equals(cm.getSendBy())) {
                        listUser.add(u);
                        break;
                    }
                }
                holder.tvNameMess.setText(listUser.get(0).getName());
                FirebaseStorage.getInstance().getReference().child("images/"+ listUser.get(0).getImage())
                        .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.get().load(uri).into(holder.civAvatar);
                                Log.i("uri", uri.toString());
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                            }
                        });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }

    @Override
    public int getItemCount() {
        return listMess.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView civAvatar;
        TextView tvMess;
        TextView tvNameMess;
        public ViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            civAvatar = (CircleImageView)itemView.findViewById(R.id.civSearchAvt);
            tvMess = (TextView) itemView.findViewById(R.id.tvSearchMess);
            tvNameMess = (TextView) itemView.findViewById(R.id.tvSearchName);
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