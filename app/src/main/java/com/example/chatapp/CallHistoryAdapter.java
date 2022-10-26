package com.example.chatapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CallHistoryAdapter extends RecyclerView.Adapter<CallHistoryAdapter.ViewHolder> {
    private ArrayList<CallHistory> listCall;
    private Context context;

    public CallHistoryAdapter(ArrayList<CallHistory> listCall, Context context) {
        this.listCall = listCall;
        this.context = context;
    }


    @NonNull
    @Override
    public CallHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_callhistory,parent,false);
        CallHistoryAdapter.ViewHolder viewHolder = new CallHistoryAdapter.ViewHolder(itemView);
        return viewHolder;
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CallHistory user = listCall.get(position);
        Picasso.get().load(user.getImage()).into(holder.civ_avatar);
        holder.tv_name.setText(user.getName());
        holder.tv_isMissed.setText(user.getToStringIsMissed());
        holder.tv_time.setText(user.getTime());
        if(user.isVideoCall()) {
            holder.imv_isVideoCall.setImageResource(R.drawable.ic_video_chat_line);
        } else {
            holder.imv_isVideoCall.setImageResource(R.drawable.ic_phone_line);
        }
    }

    @Override
    public int getItemCount() {
        return listCall.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView civ_avatar;
        TextView tv_name;
        TextView tv_isMissed;
        TextView tv_time;
        ImageView imv_isVideoCall;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            civ_avatar = (CircleImageView)itemView.findViewById(R.id.civImage);
            tv_name = (TextView) itemView.findViewById(R.id.tvName);
            tv_isMissed = (TextView) itemView.findViewById(R.id.tvIsMissed);
            tv_time = (TextView) itemView.findViewById(R.id.tvTime);
            imv_isVideoCall = (ImageView) itemView.findViewById(R.id.imvIsCallVideo);
        }
    }
}
