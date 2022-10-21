package com.example.chatapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsViewHolder>{
    Context context;
    List<GroupData> list;

    public GroupsAdapter(Context context, List<GroupData> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public GroupsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GroupsViewHolder(LayoutInflater.from(context).inflate(R.layout.group_item,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GroupsViewHolder holder, int position) {
        holder.textView_GroupName.setText(list.get(position).getName());
        holder.textView_GroupName.setSelected(true);
        holder.textView_GroupStatus.setText(list.get(position).getStatus());
        Picasso.get().load(list.get(position).getImage()).into(holder.image_GroupAvatar);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

class GroupsViewHolder extends RecyclerView.ViewHolder{
    TextView textView_GroupName,  textView_GroupStatus;
    CircleImageView image_GroupAvatar;
    public GroupsViewHolder(@NonNull View itemView) {
        super(itemView);
        textView_GroupName = itemView.findViewById(R.id.textView_GroupName);
        textView_GroupStatus = itemView.findViewById(R.id.textView_GroupStatus);
        image_GroupAvatar = itemView.findViewById(R.id.image_GroupAvatar);
    }
}
