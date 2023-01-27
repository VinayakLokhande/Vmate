package com.example.android.socialmediaappproject.Fragments;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.socialmediaappproject.ModelClasses.PostsRVModel;
import com.example.android.socialmediaappproject.ModelClasses.UsersInfoDataModel;
import com.example.android.socialmediaappproject.R;
import com.example.android.socialmediaappproject.databinding.FragmentAddBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.Locale;


public class AddFragment extends Fragment {
    FragmentAddBinding binding;
    ActivityResultLauncher<String> takePhotoFromGallery;
    Uri imgUri;


    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;

    ProgressDialog progressDialog;

    public AddFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        progressDialog = new ProgressDialog(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAddBinding.inflate(inflater, container, false);


        // Setting current users profile and his name
        firebaseDatabase.getReference().child("Users")
                .child(firebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            UsersInfoDataModel userInfo = snapshot.getValue(UsersInfoDataModel.class);
                            Picasso.get().load(userInfo.getProfilePhoto()).placeholder(R.drawable.placeholder_img)
                                    .into(binding.addPostFragProfileImg);
                            binding.addPostFragUserNameTxt.setText(userInfo.getUsername());
                            binding.addPostFragProfileTypeTxt.setText(userInfo.getProfession());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



        // Getting image from gallery and setting it to post img View
        takePhotoFromGallery = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {

                if (result != null) {
                    // to change the post button style if user set a image then only post button is going to enabled otherwise disabled
                    binding.addPostFragPostBtn.setBackgroundDrawable(ContextCompat.getDrawable(getContext(),R.drawable.follow_btn_style_solid_bg));
                    binding.addPostFragPostBtn.setTextColor(getContext().getResources().getColor(R.color.white));
                    binding.addPostFragPostBtn.setEnabled(true);


                    imgUri = result;
                    binding.addPostFragPostImg.setImageURI(result);
                    binding.addPostFragPostImg.setVisibility(View.VISIBLE);

                }
            }
        });


        binding.addPostFragSelectImgFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhotoFromGallery.launch("image/*");
            }
        });


        // Progress dialog enabled till image will storing in database
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle("Uploading post");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        binding.addPostFragPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog.show();

                UsersInfoDataModel userInfo = new UsersInfoDataModel();
                firebaseDatabase.getReference()
                        .child("Users")
                        .child(firebaseAuth.getUid())
                        .child("userPostsCount")
                        .setValue(userInfo.getUserPostsCount() + 1);  // increasing users post count


                final StorageReference storeRef = firebaseStorage.getReference()
                        .child("posts")
                        .child(firebaseAuth.getUid())
                        .child(new Date().getTime() + "");

                // storing users selected post image uri to firebase storage
                storeRef.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        // storing users selected post image url to firebase database
                        storeRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                PostsRVModel postModel = new PostsRVModel();
                                postModel.setPostImage(uri.toString());
                                postModel.setPostBy(firebaseAuth.getUid());
                                // some users are not setting the description to there post so that's why checking it first it he adds he description then it will be
                                // add on db otherwise not
                                if (!binding.addPostFragHashtagDescriptionsEditTxt.getText().toString().trim().isEmpty()) {
                                    postModel.setPostDescription(binding.addPostFragHashtagDescriptionsEditTxt.getText().toString().trim());
                                }
                                postModel.setPostAt(new Date().getTime() + ""); // setting current time when user is posting his post


                                // one user can store more than one post so that's storing every post and its all the info on different different child's by using push() method
                                firebaseDatabase.getReference()
                                        .child("posts")
                                        .push()
                                        .setValue(postModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                Toast.makeText(getContext(), "Post uploaded successfully.", Toast.LENGTH_SHORT).show();

                                                // after successfully storing the post to db erasing all the views data which user has added for his previous post
                                                binding.addPostFragHashtagDescriptionsEditTxt.setText("");
                                                binding.addPostFragPostBtn.setBackgroundDrawable(ContextCompat.getDrawable(getContext()
                                                        ,R.drawable.follow_btn_style_outlined_bg));
                                                binding.addPostFragPostBtn.setTextColor(getContext().getResources().getColor(R.color.redish));
                                                binding.addPostFragPostBtn.setEnabled(false);
                                                binding.addPostFragPostImg.setImageResource(0);

                                            }
                                        });

                            }
                        });
                    }
                });
            }
        });


        return binding.getRoot();
    }
}