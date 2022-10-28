package com.example.chatapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddGroupAdapter extends RecyclerView.Adapter<AddGroupViewHolder> {
    Context context;
    List<CallHistory> list;
    AddSelectedListListener listener;

    public AddGroupAdapter(Context context, List<CallHistory> list, AddSelectedListListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AddGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AddGroupViewHolder(LayoutInflater.from(context).inflate(R.layout.add_group_member_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AddGroupViewHolder holder, int position) {
        holder.textView_name.setText(list.get(position).getName());
        holder.textView_activity.setText(list.get(position).getTime());
        Picasso.get().load(list.get(position).getImage()).into(holder.image_avatar);
        holder.checkBox_Select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                listener.onMemberClicked(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}


class AddGroupViewHolder extends RecyclerView.ViewHolder{
    TextView textView_name, textView_activity;
    CircleImageView image_avatar;
    CheckBox checkBox_Select;
    public AddGroupViewHolder(@NonNull View itemView) {
        super(itemView);
        textView_activity = itemView.findViewById(R.id.textView_activity);
        textView_name = itemView.findViewById(R.id.textView_name);
        image_avatar = itemView.findViewById(R.id.image_avatar);
        checkBox_Select = itemView.findViewById(R.id.checkBox_Select);
    }
}