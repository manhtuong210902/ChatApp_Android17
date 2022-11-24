package com.example.chatapp.fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.chatapp.R;
import com.example.chatapp.activities.LoginActivity;
import com.example.chatapp.adapters.CustomExpPersonalListAdapter;
import com.example.chatapp.adapters.CustomExpandableListAdapter;
import com.example.chatapp.models.User;
import com.example.chatapp.others.ExpPersonalListData;
import com.example.chatapp.others.ExpandableListDataPumb;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    LinearLayout llProfile;


    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, List<String>> expandableListDetail;

    ExpandableListView personalListView;
    ExpandableListAdapter personalListAdapter;
    List<String> personalListTitle;
    HashMap<String,HashMap<String,String>> personalListDetail;
    Button btn_logout;
    private FirebaseAuth mAuth;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    HashMap<String,String> per=new HashMap<>();
    private Uri imageUri;
    CircleImageView civImage;
    TextView tvUserName;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        llProfile = (LinearLayout) inflater.inflate(R.layout.fragment_profile, container, false);
        mAuth = FirebaseAuth.getInstance();

        personalListView=(ExpandableListView) llProfile.findViewById(R.id.expandablePersonalListView);
        personalListDetail= ExpPersonalListData.getData();
        personalListTitle=new ArrayList<String>(personalListDetail.keySet());
        personalListAdapter=new CustomExpPersonalListAdapter(getContext(),personalListTitle,personalListDetail);
        personalListView.setAdapter(personalListAdapter);
        //UI
        civImage = llProfile.findViewById(R.id.civImage);
        tvUserName = llProfile.findViewById(R.id.tvUserName);

        //get data
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users").child(mAuth.getCurrentUser().getUid());
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                per.clear();
                User A = dataSnapshot.getValue(User.class);
                per.put("Name", A.getName());
                per.put("Email",mAuth.getCurrentUser().getEmail());
                personalListDetail.put("Personal info",per);

                //set info
                tvUserName.setText(A.getName());

                //img
                FirebaseStorage.getInstance().getReference().child("images/"+A.getImage())
                        .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.get().load(uri).into(civImage);
                                Log.i("uri", uri.toString());
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                            }
                        });
                //end set info
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        //end get data

        expandableListView = (ExpandableListView) llProfile.findViewById(R.id.expandableListView);
        expandableListDetail = ExpandableListDataPumb.getData();
        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        expandableListAdapter = new CustomExpandableListAdapter(getContext(),expandableListTitle,expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);

        //logout
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = sharedPreferences.edit();
        btn_logout = llProfile.findViewById(R.id.btn_logout);
        Intent intent = new Intent(this.getActivity(), LoginActivity.class);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("EmailLogin", "");
                editor.putString("PasswordLogin", "");
                editor.commit();
                mAuth.signOut();
                startActivity(intent);
            }
        });
        return llProfile;
    }
}