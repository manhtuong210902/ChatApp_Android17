package com.example.chatapp.activities;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.chatapp.models.AddGroupUser;
import com.example.chatapp.interfaces.AddSelectedListListener;
import com.example.chatapp.R;
import com.example.chatapp.adapters.SelectedGroupAdapter;
import com.example.chatapp.adapters.AddGroupAdapter;
import com.example.chatapp.models.User;

import java.util.ArrayList;
import java.util.List;

public class AddGroupActivity extends Activity {
    private EditText editText_groupName;
    private RecyclerView recyclerView_listFriend, recyclerView_listSelected;
    private List<AddGroupUser> listFriend, listSelected;
    private AddGroupAdapter addGroupAdapter;
    private SelectedGroupAdapter selectedGroupAdapter;
    LinearLayout layout_selectedMember, linearLayout;
    ImageView btn_close;
    TextView textView_Title, textView_numSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        listFriend = new ArrayList<>();
        listSelected = new ArrayList<>();

        editText_groupName = findViewById(R.id.editText_groupName);
        recyclerView_listFriend = findViewById(R.id.recyclerView_listFriend);
        recyclerView_listSelected = findViewById(R.id.recyclerView_listSelected);
        layout_selectedMember = findViewById(R.id.layout_selectedMember);
        linearLayout = findViewById(R.id.linearLayout);
        textView_numSelected = findViewById(R.id.textView_numSelected);
        textView_Title = findViewById(R.id.textView_Title);
        Data();

        recyclerView_listFriend.setLayoutManager(new GridLayoutManager(AddGroupActivity.this, 1));
        addGroupAdapter = new AddGroupAdapter(AddGroupActivity.this, listFriend, addSelectedListListener);
        recyclerView_listFriend.setAdapter(addGroupAdapter);

        recyclerView_listSelected.setLayoutManager(new LinearLayoutManager(AddGroupActivity.this, LinearLayoutManager.HORIZONTAL, false));
        selectedGroupAdapter = new SelectedGroupAdapter(AddGroupActivity.this, listSelected, removeSelectedListener);
        recyclerView_listSelected.setAdapter(selectedGroupAdapter);

        layout_selectedMember.setVisibility(View.INVISIBLE);
        btn_close = findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void Data() {
        listFriend.add(new  AddGroupUser(new User("1", "Mew 1", "c1.jpg", true), false));
        listFriend.add(new  AddGroupUser(new User("2", "Mew 2", "avtdefault.jpg", true), false));
        listFriend.add(new  AddGroupUser(new User("3", "Mew 3", "f2.jpg", true), false));
        listFriend.add(new  AddGroupUser(new User("4", "Mew 4", "c1.jpg", true), false));
        listFriend.add(new  AddGroupUser(new User("5", "Mew 5", "avtdefault.jpg", true), false));
        listFriend.add(new  AddGroupUser(new User("6", "Mew 6", "c1.jpg", true), false));
    }

    private final AddSelectedListListener addSelectedListListener = new AddSelectedListListener() {
        @Override
        public void onMemberClicked(int pos, boolean isCheck) {
            if(isCheck){
                listFriend.get(pos).setChecked(true);
                listSelected.add(listFriend.get(pos));
                selectedGroupAdapter.notifyItemInserted(listSelected.size()-1);
            }
            else {
                listFriend.get(pos).setChecked(false);
                int i=0;
                for(i=0;i<listSelected.size();i++){
                    if(listFriend.get(pos).getInfo().getUid()== listSelected.get(i).getInfo().getUid()){
                        break;
                    }
                }
                listSelected.remove(i);
                selectedGroupAdapter.notifyItemRemoved(i);
            }

            if(listSelected.size() !=0)
                layout_selectedMember.setVisibility(View.VISIBLE);
            else layout_selectedMember.setVisibility(View.INVISIBLE);
            textView_numSelected.setText("Selected: " + String.valueOf(listSelected.size()));
        }

    };

    private final AddSelectedListListener removeSelectedListener = new AddSelectedListListener() {
        @Override
        public void onMemberClicked(int pos, boolean isCheck) {
            for( int i = 0; i < listFriend.size(); i++) {
                if (pos< listSelected.size() && listFriend.get(i).getInfo().getUid() == listSelected.get(pos).getInfo().getUid()) {
                    listSelected.remove(pos);
                    selectedGroupAdapter.notifyItemRemoved(pos);
                    listFriend.get(i).setChecked(false);
                    addGroupAdapter.notifyItemChanged(i);
                    break;
                }
            }

            if (listSelected.size() == 0) {
                layout_selectedMember.setVisibility(View.INVISIBLE);
            }
            textView_numSelected.setText("Selected: " + String.valueOf(listSelected.size()));
        }
    };
}