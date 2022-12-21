package com.example.chatapp.activities;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.adapters.ChatMessageAdapter;
import com.example.chatapp.interfaces.RecyclerViewInterface;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.chatapp.R;
import com.example.chatapp.adapters.SearchMessAdapter;
import com.example.chatapp.models.ChatMessage;
import com.example.chatapp.models.Group;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchMessActivity extends Activity {
    private ImageView btnSearch;
    private EditText search;
    private RecyclerView rvSearchMess;
    private FirebaseAuth mAuth;
    private SearchMessAdapter searchMessAdapter;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_mess);
        rvSearchMess=(RecyclerView) findViewById(R.id.rcvSearchMess);
        btnSearch =(ImageView) findViewById(R.id.ivSearchMess);
        search= (EditText) findViewById(R.id.etSearchMess);
        ArrayList<ChatMessage> listMess=new ArrayList<>();

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                listMess.clear();
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {

            Bundle bundleRev = getIntent().getExtras();
            String idGroup = bundleRev.getString("idGroup");
            String nameGroup=bundleRev.getString("nmGroup");
            String imageGroup=bundleRev.getString("imgGroup");
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ChatMessage").child(idGroup);

            @Override
            public void onClick(View view) {
                String searchKey= String.valueOf(search.getText());
                Toast.makeText(SearchMessActivity.this, "click", Toast.LENGTH_SHORT).show();
                databaseReference.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listMess.clear();
                        Toast.makeText(SearchMessActivity.this, "aaa", Toast.LENGTH_SHORT).show();

                        int i = 0;
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            if( dataSnapshot.getValue(ChatMessage.class).getMessage().contains(searchKey))
                            {
                                listMess.add(dataSnapshot.getValue(ChatMessage.class));
                            }
                        }
                        searchMessAdapter=new SearchMessAdapter(listMess,SearchMessActivity.this,recyclerViewInterface);
                        rvSearchMess.setLayoutManager(new LinearLayoutManager(SearchMessActivity.this,LinearLayoutManager.VERTICAL,false));
                        rvSearchMess.setAdapter(searchMessAdapter);

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }

                });

            }
            private final RecyclerViewInterface recyclerViewInterface = new RecyclerViewInterface() {
                @Override
                public void onItemClick(int position) {
                    ChatMessage cm = listMess.get(position);
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        Group group;
                        @Override

                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                group = dataSnapshot.getValue(Group.class);
                                if(group.getGid().equals(idGroup)) {
                                    break;
                                }
                            }
                            ArrayList<String> listUid=new ArrayList<>();
                            mAuth = FirebaseAuth.getInstance();
                            for(String uid : group.getListUidMember()){
                                if(!uid.equals(mAuth.getCurrentUser().getUid())){

                                }
                                else {
                                    listUid.add(uid);
                                }
                            }
                            Intent intent = new Intent(SearchMessActivity.this, ChatMessageActivity.class);
                            Bundle bundleSent = new Bundle();

                            bundleSent.putString("idGroup", idGroup);
                            bundleSent.putString("nameGroup", nameGroup);
                            bundleSent.putString("imageGroup", imageGroup);
                            bundleSent.putString("uidChat",listUid.get(0));
                            bundleSent.putString("chatPos",cm.getMessageId());
                            intent.putExtras(bundleSent);

                            startActivity(intent);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            };

        });
    }

}