package com.example.chatapp.fragments;


import android.app.UiModeManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.chatapp.R;
import com.example.chatapp.activities.LoginActivity;
import com.example.chatapp.adapters.CustomExpPersonalListAdapter;
import com.example.chatapp.adapters.CustomExpandableListAdapter;
import com.example.chatapp.db.DbReference;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {
    LinearLayout llProfile;
    TextView tvProfile;


    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, List<String>> expandableListDetail;

    ExpandableListView personalListView;
    ExpandableListAdapter personalListAdapter;
    List<String> personalListTitle;
    HashMap<String,HashMap<String,String>> personalListDetail;
    Button btn_logout;

    CircleImageView avt;

    private FirebaseAuth mAuth;
    private StorageReference mStorage;
    private Uri imageUri;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    HashMap<String,String> per=new HashMap<>();
    CircleImageView civImage;
    TextView tvUserName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        llProfile = (LinearLayout) inflater.inflate(R.layout.fragment_profile, container, false);
        mAuth = FirebaseAuth.getInstance();

        mStorage = FirebaseStorage.getInstance().getReference();

        personalListView=(ExpandableListView) llProfile.findViewById(R.id.expandablePersonalListView);
        personalListDetail= ExpPersonalListData.getData();
        personalListTitle=new ArrayList<String>(personalListDetail.keySet());
        personalListAdapter=new CustomExpPersonalListAdapter(getContext(),personalListTitle,personalListDetail);
        personalListView.setAdapter(personalListAdapter);
        //UI
        civImage = llProfile.findViewById(R.id.civImage);
        tvUserName = llProfile.findViewById(R.id.tvUserName);
        tvProfile = llProfile.findViewById(R.id.c_tvProfile);

        tvProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        });

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

        mAuth = FirebaseAuth.getInstance();

        //set avatar
        avt = (CircleImageView) llProfile.findViewById(R.id.civImage);
        avt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Click avt!", Toast.LENGTH_SHORT).show();
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 200);
            }
        });

        //btn logout
        btn_logout = llProfile.findViewById(R.id.btn_logout);
        Intent intent = new Intent(this.getActivity(), LoginActivity.class);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DbReference.writeIsOnlineUserAndGroup(mAuth.getCurrentUser().getUid(), false);
                editor.putString("EmailLogin", "");
                editor.putString("PasswordLogin", "");
                editor.commit();
                mAuth.signOut();
                startActivity(intent);
            }
        });
        return llProfile;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && data != null) {
            imageUri = data.getData();

            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            //here you can choose quality factor in third parameter(ex. i choosen 25)
            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            byte[] fileInBytes = baos.toByteArray();

            uploadImageToFirebase(fileInBytes);
        }
    }

    private String uploadImageToFirebase(byte[] fileInBytes) {
        final String imageId = UUID.randomUUID().toString() + ".jpg";
        StorageReference imgRef = mStorage.child("images/" + imageId);
        UploadTask uploadTask = imgRef.putBytes(fileInBytes);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getActivity(), "Upload image failed!", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getActivity(), "Upload image successes!", Toast.LENGTH_SHORT).show();
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DbReference.writeImageUser(uid, imageId);
                avt.setImageBitmap(BitmapFactory.decodeByteArray(fileInBytes, 0, fileInBytes.length));
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Log.d("TAG", "Upload is " + progress + "% done");
            }
        });
        return imageId;
    }
}
