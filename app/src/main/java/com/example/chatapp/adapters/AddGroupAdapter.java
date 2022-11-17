package com.example.chatapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.models.AddGroupUser;
import com.example.chatapp.interfaces.AddSelectedListListener;
import com.example.chatapp.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddGroupAdapter extends RecyclerView.Adapter<AddGroupViewHolder> {
    Context context;
    List<AddGroupUser> list;
    AddSelectedListListener listener;

    public AddGroupAdapter(Context context, List<AddGroupUser> list, AddSelectedListListener listener) {
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
        holder.textView_name.setText(list.get(position).getInfo().getName());
//        Picasso.get().load(list.get(position).getInfo().getImage()).into(holder.image_avatar);
        holder.image_avatar.setImageResource(R.drawable.cute1);
        holder.checkBox_Select.setChecked(list.get(position).isChecked());
        holder.checkBox_Select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                listener.onMemberClicked(holder.getAdapterPosition(), isChecked);
            }
        });

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