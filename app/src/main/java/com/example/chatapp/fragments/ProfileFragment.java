package com.example.chatapp.fragments;


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
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.chatapp.R;
import com.example.chatapp.activities.LoginActivity;
import com.example.chatapp.adapters.CustomExpPersonalListAdapter;
import com.example.chatapp.adapters.CustomExpandableListAdapter;
import com.example.chatapp.db.DbReference;
import com.example.chatapp.others.ExpPersonalListData;
import com.example.chatapp.others.ExpandableListDataPumb;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

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

    CircleImageView avt;

    private FirebaseAuth mAuth;
    private StorageReference mStorage;
    private Uri imageUri;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        llProfile = (LinearLayout) inflater.inflate(R.layout.fragment_profile, container, false);

        mStorage = FirebaseStorage.getInstance().getReference();

        personalListView=(ExpandableListView) llProfile.findViewById(R.id.expandablePersonalListView);
        personalListDetail= ExpPersonalListData.getData();
        personalListTitle=new ArrayList<String>(personalListDetail.keySet());
        personalListAdapter=new CustomExpPersonalListAdapter(getContext(),personalListTitle,personalListDetail);
        personalListView.setAdapter(personalListAdapter);

        expandableListView = (ExpandableListView) llProfile.findViewById(R.id.expandableListView);
        expandableListDetail = ExpandableListDataPumb.getData();
        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        expandableListAdapter = new CustomExpandableListAdapter(getContext(),expandableListTitle,expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = sharedPreferences.edit();
        mAuth = FirebaseAuth.getInstance();

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

