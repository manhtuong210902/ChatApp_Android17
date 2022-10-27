package com.example.chatapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatHomeFragment extends Fragment {
    private LinearLayout llHomeChats;
    private RecyclerView recyclerViewOnlineUser;
    private RecyclerView recyclerViewChatUser;
    private ArrayList<OnlineUser> listOnlineUser;
    private ArrayList<ChatUser> listChatUser;
    private OnlineUsersAdapter onlineUsersAdapter;
    private ChatUsersAdapter chatUsersAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        llHomeChats = (LinearLayout) inflater.inflate(R.layout.fragment_home_chat, container, false);
        recyclerViewOnlineUser = (RecyclerView) llHomeChats.findViewById(R.id.c_rcvOnlineUser);
        recyclerViewChatUser = (RecyclerView) llHomeChats.findViewById(R.id.c_rcvChatUser);

        listOnlineUser = new ArrayList<>();
        dataOS();
        Log.d(this.getClass().getSimpleName(), listOnlineUser.toString());
        onlineUsersAdapter = new OnlineUsersAdapter(listOnlineUser, getContext());
        recyclerViewOnlineUser.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewOnlineUser.setAdapter(onlineUsersAdapter);

        listChatUser = new ArrayList<>();
        dataCS();
        chatUsersAdapter = new ChatUsersAdapter(listChatUser, getContext());
        recyclerViewChatUser.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerViewChatUser.setAdapter(chatUsersAdapter);

        return llHomeChats;
    }

    private void dataOS(){
        listOnlineUser.add(new OnlineUser("OnlineUser1",R.drawable.cute1));
        listOnlineUser.add(new OnlineUser("OnlineUser2",R.drawable.cute2));
        listOnlineUser.add(new OnlineUser("OnlineUser3",R.drawable.cute1));
        listOnlineUser.add(new OnlineUser("OnlineUser4",R.drawable.cute2));
        listOnlineUser.add(new OnlineUser("OnlineUser5",R.drawable.cute1));
        listOnlineUser.add(new OnlineUser("OnlineUser6",R.drawable.cute2));
        listOnlineUser.add(new OnlineUser("OnlineUser7",R.drawable.cute1));
        listOnlineUser.add(new OnlineUser("OnlineUser8",R.drawable.cute2));
        listOnlineUser.add(new OnlineUser("OnlineUser9",R.drawable.cute3));
        listOnlineUser.add(new OnlineUser("OnlineUser10",R.drawable.cute2));
        listOnlineUser.add(new OnlineUser("OnlineUser11",R.drawable.cute1));
        listOnlineUser.add(new OnlineUser("OnlineUser12",R.drawable.cute3));
        listOnlineUser.add(new OnlineUser("OnlineUser13",R.drawable.cute1));
        listOnlineUser.add(new OnlineUser("OnlineUser14",R.drawable.cute2));
    }

    private void dataCS(){
        listChatUser.add(new ChatUser("OnlineUser1",R.drawable.cute1, "message123", "22:22", "3"));
        listChatUser.add(new ChatUser("OnlineUser2",R.drawable.cute2, "message123", "22:22", "3"));
        listChatUser.add(new ChatUser("OnlineUser3",R.drawable.cute1, "message123", "22:22", "3"));
        listChatUser.add(new ChatUser("OnlineUser4",R.drawable.cute2, "message123", "22:22", "3"));
        listChatUser.add(new ChatUser("OnlineUser5",R.drawable.cute1, "message123", "22:22", "3"));
        listChatUser.add(new ChatUser("OnlineUser6",R.drawable.cute2, "message123", "22:22", "3"));
        listChatUser.add(new ChatUser("OnlineUser7",R.drawable.cute1, "message123", "22:22", "3"));
        listChatUser.add(new ChatUser("OnlineUser8",R.drawable.cute2, "message123", "22:22", "3"));
        listChatUser.add(new ChatUser("OnlineUser9",R.drawable.cute3, "message123", "22:22", "3"));
        listChatUser.add(new ChatUser("OnlineUser10",R.drawable.cute2, "message123", "22:22", "3"));
        listChatUser.add(new ChatUser("OnlineUser11",R.drawable.cute1, "message123", "22:22", "3"));
        listChatUser.add(new ChatUser("OnlineUser12",R.drawable.cute3, "message123", "22:22", "3"));
        listChatUser.add(new ChatUser("OnlineUser13",R.drawable.cute1, "message123", "22:22", "3"));
        listChatUser.add(new ChatUser("OnlineUser14",R.drawable.cute2, "message123", "22:22", "3"));
    }
}
