package com.example.chatapp.fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.chatapp.R;
import com.example.chatapp.activities.LoginActivity;
import com.example.chatapp.db.DbReference;
import com.example.chatapp.models.User;
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
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {
    LinearLayout llProfile, btnShowInfo, btnShowOptions, expandInfo, expandOptions;
    ImageView iconDropInfo, iconDropOptions;
    Switch btnSwitchTheme;
    TextView tvProfile;
    Button btn_logout;

    CircleImageView avt;

    private FirebaseAuth mAuth;
    private StorageReference mStorage;
    private Uri imageUri;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    CircleImageView civImage;
    TextView tvUserName, tvProfileName, tvProfileEmail;
    boolean nighMode;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        llProfile = (LinearLayout) inflater.inflate(R.layout.fragment_profile, container, false);

        //config firebase
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();

        //UI
        progressDialog = new ProgressDialog(getContext());
        civImage = llProfile.findViewById(R.id.civImage);
        tvUserName = llProfile.findViewById(R.id.tvUserName);
        tvProfile = llProfile.findViewById(R.id.c_tvProfile);
        btnShowInfo = llProfile.findViewById(R.id.btnShowInfo);
        btnShowOptions = llProfile.findViewById(R.id.btnShowOptions);
        expandInfo = llProfile.findViewById(R.id.expandInfo);
        expandOptions = llProfile.findViewById(R.id.expandOptions);

        expandInfo.setVisibility(View.GONE);
        expandOptions.setVisibility(View.GONE);

        iconDropInfo = llProfile.findViewById(R.id.iconDropInfo);
        iconDropOptions = llProfile.findViewById(R.id.iconDropOptions);
        tvProfileName = llProfile.findViewById(R.id.tvProfileName);
        tvProfileEmail = llProfile.findViewById(R.id.tvProfileEmail);

        btnSwitchTheme = llProfile.findViewById(R.id.btnSwitchTheme);

        //set expandlist
        btnShowInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                togglePersonInfo(expandInfo.getVisibility() == View.GONE);
                if(expandInfo.getVisibility() == View.VISIBLE){
                    togglePersonOptions(false);
                }
            }
        });

        btnShowOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                togglePersonOptions(expandOptions.getVisibility() == View.GONE);
                if(expandOptions.getVisibility() == View.VISIBLE){
                    togglePersonInfo(false);
                }
            }
        });

        //config sharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = sharedPreferences.edit();
        nighMode = sharedPreferences.getBoolean("night", false); //light mode default false
        boolean reRender = sharedPreferences.getBoolean("render", false);

        if(reRender){
            togglePersonOptions(true);
            editor.putBoolean("render", false);
            editor.apply();
        }

        if(nighMode){
            btnSwitchTheme.setChecked(true);
        }
        //set darkmode
        btnSwitchTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nighMode){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor.putBoolean("night", false);
                    editor.putBoolean("render", true);
                }
                else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor.putBoolean("night", true);
                    editor.putBoolean("render", true);
                }
                editor.commit();
            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users").child(mAuth.getCurrentUser().getUid());
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                //set info
                tvUserName.setText(user.getName());
                tvProfileName.setText(user.getName());
                tvProfileEmail.setText(user.getEmail());

                //img
                FirebaseStorage.getInstance().getReference().child("images/" + user.getImage())
                        .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.get().load(uri).into(civImage);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {}
                        });
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

//        //end get data
//
//        expandableListView = (ExpandableListView) llProfile.findViewById(R.id.expandableListView);
//        expandableListDetail = ExpandableListDataPumb.getData();
//        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
//        expandableListAdapter = new CustomExpandableListAdapter(getContext(),expandableListTitle,expandableListDetail);
//        expandableListView.setAdapter(expandableListAdapter);

        //logout

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
//                DbReference.writeIsOnlineUserAndGroup(mAuth.getCurrentUser().getUid(), false);
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
                progressDialog.dismiss();
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DbReference.writeImageUser(uid, imageId);
                avt.setImageBitmap(BitmapFactory.decodeByteArray(fileInBytes, 0, fileInBytes.length));
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                progressDialog.setTitle("Change avatar");
                progressDialog.setMessage("Uploading image");
                progressDialog.show();
            }
        });
        return imageId;
    }

    private void togglePersonInfo(Boolean visible){
        if(visible){
            expandInfo.setVisibility(View.VISIBLE);
            iconDropInfo.setImageResource(R.drawable.ic_arrow_up_s_line);
        }
        else{
            expandInfo.setVisibility(View.GONE);
            iconDropInfo.setImageResource(R.drawable.ic_arrow_right_s_line);
        }
    }

    private void togglePersonOptions(Boolean visible){
        if(visible){
            expandOptions.setVisibility(View.VISIBLE);
            iconDropOptions.setImageResource(R.drawable.ic_arrow_up_s_line);
        }
        else{
            expandOptions.setVisibility(View.GONE);
            iconDropOptions.setImageResource(R.drawable.ic_arrow_right_s_line);
        }
    }
}
