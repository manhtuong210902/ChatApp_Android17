package com.example.chatapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddGroupAdapter extends RecyclerView.Adapter<AddGroupViewHolder> {
    Context context;
    List<CallHistory> list;
    AddSelectedListListener addListener;
    RemoveSelectedListener removeLisner;

    public AddGroupAdapter(Context context, List<CallHistory> list, AddSelectedListListener addListener, RemoveSelectedListener removeLisner) {
        this.context = context;
        this.list = list;
        this.addListener = addListener;
        this.removeLisner = removeLisner;
    }

    @NonNull
    @Override
    public AddGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AddGroupViewHolder(LayoutInflater.from(context).inflate(R.layout.add_group_member_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AddGroupViewHolder holder, int position) {
        holder.textView_name.setText(list.get(position).getName());
        Picasso.get().load(list.get(position).getImage()).into(holder.image_avatar);
//        holder.checkBox_Select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
//                if(isChecked){
//                    addListener.onMemberClicked(holder.getAdapterPosition());
//                }
//                else{
//                    removeLisner.onSelectedClicked(holder.getAdapterPosition());
//                }
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}


class AddGroupViewHolder extends RecyclerView.ViewHolder{
    TextView textView_name;
    CircleImageView image_avatar;
    AppCompatCheckBox checkBox_Select;
    public AddGroupViewHolder(@NonNull View itemView) {
        super(itemView);
        textView_name = itemView.findViewById(R.id.textView_name);
        image_avatar = itemView.findViewById(R.id.image_avatar);
        checkBox_Select = (AppCompatCheckBox) itemView.findViewById(R.id.checkBox_Select);
    }
}