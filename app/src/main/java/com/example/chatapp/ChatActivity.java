package com.example.chatapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatActivity extends Activity {
    private RecyclerView recyclerViewOnlineUser;
    private RecyclerView recyclerViewChatUser;
    private OnlineUser onlineUser;
    private ChatUser chatUser;
    private ArrayList<OnlineUser> listOnlineUser;
    private ArrayList<ChatUser> listChatUser;
    private OnlineUsersAdapter onlineUsersAdapter;
    private ChatUsersAdapter chatUsersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerViewOnlineUser = (RecyclerView) findViewById(R.id.c_rcvOnlineUser);
        recyclerViewChatUser = (RecyclerView) findViewById(R.id.c_rcvChatUser);

        listOnlineUser = new ArrayList<>();
        dataOS();
        Log.d(this.getClass().getSimpleName(), listOnlineUser.toString());
        onlineUsersAdapter = new OnlineUsersAdapter(listOnlineUser,this);
        recyclerViewOnlineUser.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewOnlineUser.setAdapter(onlineUsersAdapter);

        listChatUser = new ArrayList<>();
        dataCS();
        chatUsersAdapter = new ChatUsersAdapter(listChatUser, this);
        recyclerViewChatUser.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerViewChatUser.setAdapter(chatUsersAdapter);

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
