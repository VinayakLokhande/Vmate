package com.example.android.socialmediaappproject;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.android.socialmediaappproject.Fragments.ProfileFragment;
import com.example.android.socialmediaappproject.databinding.ActivityEditProfileBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.internal.api.FirebaseNoSignedInUserException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {
    ActivityEditProfileBinding binding;

    ActivityResultLauncher<String> coverImg;
    ActivityResultLauncher<String> profileImg;

    FirebaseStorage firebaseStorage;
    FirebaseDatabase firebaseDatabase;

    Uri coverImageUri = null;
    Uri profileImageUri = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseStorage = FirebaseStorage.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();


        coverImageUri();

        // launching user selected cover image on the cover image view
        binding.changeCoverImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // image/* means all photo types
                coverImg.launch("image/*");

            }
        });


        profileImageUri();

        binding.changeProfileImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileImg.launch("image/*");
            }
        });




        binding.EditProfileDoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // if new cover image is selected by the user then only it will be store in the db
                if (coverImageUri != null) {
                    storeCoverImgToStorageAndDB();

                }


                // if new profile image is selected by the user then only it will be store in the db
                if (profileImageUri != null) {
                    storeProfileImgToStorageAndDB();

                }

                // if wants to edit his profession and he wants to update his profession and if he write something in profession edit field then only that new profession
                // will be update in the db
                if (!binding.UserProfessionEditText.getText().toString().equals("")) {
                    updateFirebaseData(binding.UserProfessionEditText.getText().toString().trim(),"profession");
                }


                // if user already have a bio for his profile then he have userBio child in the db to his id so he will go inside if condition and his new bio will
                // be updated but if he is writing his profile bio for the first time then new child will be create in the db for his id and that bio will be store there
                if (!binding.UserBioEditTxt.getText().toString().equals("")) {
                    firebaseDatabase.getReference()
                            .child("Users")
                            .child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.hasChild("userBio")) {
                                        updateFirebaseData(binding.UserBioEditTxt.getText().toString().trim(),"userBio");

                                    } else {

                                        firebaseDatabase.getReference()
                                                .child("Users")
                                                .child(FirebaseAuth.getInstance().getUid())
                                                .child("userBio")
                                                .setValue(binding.UserBioEditTxt.getText().toString().trim());
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }

                finish();
            }
        });

    }



    private void coverImageUri() {
        // for cover image fetching the image from gallery
        coverImg = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if (result != null) {
                            binding.coverImgView.setImageURI(result);
                            coverImageUri = result;
                        }
                    }
                }
        );
    }


    private void profileImageUri() {
        // for profile image fetching the image from gallery
        profileImg = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if (result != null) {
                            binding.profileImg.setImageURI(result);
                            profileImageUri = result;
                        }
                    }
                }
        );
    }


    private void storeCoverImgToStorageAndDB() {
        // creating a new child to store cover image and getting id of current user to store the image on his cover image child
        final StorageReference storageRef = firebaseStorage.getReference()
                .child("cover_photo/")
                .child(FirebaseAuth.getInstance().getUid());

        // storing image uri on the current users id in firebase storage
        storageRef.putFile(coverImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // so here we are getting a url for the image
                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        // accessing cover photo child belongs to current user id in db and storing new updated image on that child
                                        firebaseDatabase.getReference()
                                                .child("Users")
                                                .child(FirebaseAuth.getInstance().getUid())
                                                .child("coverPhoto")
                                                .setValue(uri.toString());

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(EditProfileActivity.this, "Failed to store cover image to Firebase Database " + e, Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProfileActivity.this, "Failed to store cover image to Firebase Storage " + e, Toast.LENGTH_SHORT).show();
                    }
                });


    }

    private void storeProfileImgToStorageAndDB() {

        // creating a new child to store profile image and getting id of current user to store the image on his profile image child
        final StorageReference storageRef = firebaseStorage.getReference()
                .child("profile_photo")
                .child(FirebaseAuth.getInstance().getUid());

        // storing image uri on the current users id in firebase storage
        storageRef.putFile(profileImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        // so here we are getting a url for the image
                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        // accessing profile photo child belongs to current user id in db and storing new updated image on that child
                                        firebaseDatabase.getReference()
                                                .child("Users")
                                                .child(FirebaseAuth.getInstance().getUid())
                                                .child("profilePhoto")
                                                .setValue(uri.toString());

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(EditProfileActivity.this, "Failed to store profile image to Firebase Database " + e, Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProfileActivity.this, "Failed to store profile image to Firebase Storage " + e, Toast.LENGTH_SHORT).show();
                    }
                });

    }



    private void updateFirebaseData(String updateString,String databaseField) {

        // updating the users profession and bio
        Map<String,Object> updateData = new HashMap<>();

        if (databaseField == "profession") {
            updateData.put("profession",updateString);
        }

        if (databaseField == "userBio") {
            updateData.put("userBio",updateString);
        }

        firebaseDatabase.getReference()
                .child("Users")
                .child(FirebaseAuth.getInstance().getUid())
                .updateChildren(updateData).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProfileActivity.this, "Failed to update the data", Toast.LENGTH_SHORT).show();
                    }
                });

    }


}