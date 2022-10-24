package com.example.chatapp;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CallHistoryActivity extends Activity {
    private RecyclerView recyclerViewCallHistory;
    private ArrayList<CallHistory> listCallHistory;
    private CallHistoryAdapter callHistoryAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callhistory);

        recyclerViewCallHistory = (RecyclerView) findViewById(R.id.rcv_CallHistory);

        listCallHistory = new ArrayList<>();
        Data();

        callHistoryAdapter = new CallHistoryAdapter(listCallHistory,this);
        recyclerViewCallHistory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerViewCallHistory.setAdapter(callHistoryAdapter);
    }

    private void Data() {
        listCallHistory.add(new CallHistory("Mew1", R.drawable.cute1, false, false, true, "22:20"));
        listCallHistory.add(new CallHistory("Mew1", R.drawable.cute2, true, false, true, "22:21"));
        listCallHistory.add(new CallHistory("Mew1", R.drawable.cute3, false, true, true, "22:22"));
        listCallHistory.add(new CallHistory("Mew1", R.drawable.cute1, false, false, true, "22:23"));
        listCallHistory.add(new CallHistory("Mew1", R.drawable.cute2, true, true, true, "22:24"));
        listCallHistory.add(new CallHistory("Mew1", R.drawable.cute3, false, true, true, "22:25"));
        listCallHistory.add(new CallHistory("Mew1", R.drawable.cute1, false, false, true, "22:26"));
        listCallHistory.add(new CallHistory("Mew1", R.drawable.cute2, true, true, true, "22:27"));
        listCallHistory.add(new CallHistory("Mew1", R.drawable.cute3, false, false, true, "22:28"));
        listCallHistory.add(new CallHistory("Mew1", R.drawable.cute1, true, true, true, "22:29"));
        listCallHistory.add(new CallHistory("Mew1", R.drawable.cute2, true, false, true, "22:30"));
        listCallHistory.add(new CallHistory("Mew1", R.drawable.cute3, false, true, true, "22:31"));

    }
}
