package com.example.chatapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.adapters.CallHistoryAdapter;
import com.example.chatapp.models.CallHistory;

import java.util.ArrayList;

public class CallHistoryFragment extends Fragment {
    private LinearLayout llCallHistory;
    private RecyclerView recyclerViewCallHistory;
    private ArrayList<CallHistory> listCallHistory;
    private CallHistoryAdapter callHistoryAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        llCallHistory = (LinearLayout) inflater.inflate(R.layout.fragment_callhistory, container, false);
        recyclerViewCallHistory = (RecyclerView) llCallHistory.findViewById(R.id.rcv_CallHistory);

        listCallHistory = new ArrayList<>();
        Data();

        callHistoryAdapter = new CallHistoryAdapter(listCallHistory,getContext());
        recyclerViewCallHistory.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerViewCallHistory.setAdapter(callHistoryAdapter);
        return llCallHistory;
    }

    private void Data() {
        listCallHistory.add(new CallHistory(1,"Mew1", R.drawable.cute1, false, false, true, "22:20"));
        listCallHistory.add(new CallHistory(2,"Mew1", R.drawable.cute2, true, false, true, "22:21"));
        listCallHistory.add(new CallHistory(3,"Mew1", R.drawable.cute3, false, true, true, "22:22"));
        listCallHistory.add(new CallHistory(4,"Mew1", R.drawable.cute1, false, false, true, "22:23"));
        listCallHistory.add(new CallHistory(5,"Mew1", R.drawable.cute2, true, true, true, "22:24"));
        listCallHistory.add(new CallHistory(6,"Mew1", R.drawable.cute3, false, true, true, "22:25"));
        listCallHistory.add(new CallHistory(7,"Mew1", R.drawable.cute1, false, false, true, "22:26"));
        listCallHistory.add(new CallHistory(8,"Mew1", R.drawable.cute2, true, true, true, "22:27"));
        listCallHistory.add(new CallHistory(9,"Mew1", R.drawable.cute3, false, false, true, "22:28"));
        listCallHistory.add(new CallHistory(10,"Mew1", R.drawable.cute1, true, true, true, "22:29"));
        listCallHistory.add(new CallHistory(11,"Mew1", R.drawable.cute2, true, false, true, "22:30"));
        listCallHistory.add(new CallHistory(12,"Mew1", R.drawable.cute3, false, true, true, "22:31"));

    }
}
