package com.example.android.socialmediaappproject.Fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.socialmediaappproject.AdapterClasses.FollowersDataAdapter;
import com.example.android.socialmediaappproject.EditProfileActivity;
import com.example.android.socialmediaappproject.LoginActivity;
import com.example.android.socialmediaappproject.MainActivity;
import com.example.android.socialmediaappproject.ModelClasses.FollowedFriendsModel;
import com.example.android.socialmediaappproject.ModelClasses.UsersInfoDataModel;
import com.example.android.socialmediaappproject.R;
import com.example.android.socialmediaappproject.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class ProfileFragment extends Fragment {
    FragmentProfileBinding binding;
    List<FollowedFriendsModel> friendsList;

    FirebaseAuth firebaseAuth;
    FirebaseStorage firebaseStorage;
    FirebaseDatabase firebaseDatabase;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // getting firebase components instances
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //  binding the profile fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        binding.shimmerLayoutProfile.startShimmer();


        // fetching all the info of current logged in user and showing it to all the views of profile frag
        firebaseDatabase.getReference()
                .child("Users")
                .child(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            UsersInfoDataModel usersInfo = snapshot.getValue(UsersInfoDataModel.class);
                            Picasso.get()
                                    .load(usersInfo.getCoverPhoto())
                                    .placeholder(R.drawable.placeholder_img)
                                    .into(binding.profileFragBigImg);  // users cover photo

                            Picasso.get()
                                    .load(usersInfo
                                    .getProfilePhoto())
                                    .placeholder(R.drawable.placeholder_img)
                                    .into(binding.profileFragSmallImg);  // users profile photo

                            binding.profileFragUserName.setText(usersInfo.getUsername()); // users username
                            binding.profileFragUserAboutTxt.setText(usersInfo.getProfession()); // users profession
                            binding.profileFragUserFollowersTxt.setText(usersInfo.getFollowersCount() + "");  // users total follower count
                            binding.profileFragUserPostsTxt.setText(usersInfo.getUserPostsCount() + "");  // users total posts count
                            binding.profileFragUserFollowingTxt.setText(usersInfo.getUserFollowingsCount() + "");  // users total following count

                            // if current user has added his bio to his profile then only bio will show in his profile otherwise not
                            if (usersInfo.getUserBio() != null) {
                                binding.noBioTxt.setVisibility(View.GONE);
                                binding.profileFragUserBioTxt.setVisibility(View.VISIBLE);
                                binding.profileFragUserBioTxt.setText(usersInfo.getUserBio());
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        // current users Friends List
        friendsList = new ArrayList<>();

        FollowersDataAdapter profileAdapter = new FollowersDataAdapter(friendsList,getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),4);
        binding.profileFriendsRecView.setLayoutManager(gridLayoutManager);
        binding.profileFriendsRecView.setAdapter(profileAdapter);

        // fetching all the followers stored in current users followers child in db and storing them in the list
        firebaseDatabase.getReference()
                .child("Users")
                .child(firebaseAuth.getUid())
                .child("followers").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            friendsList.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                FollowedFriendsModel follower = dataSnapshot.getValue(FollowedFriendsModel.class);
                                friendsList.add(follower);
                            }
                            binding.shimmerLayoutProfile.stopShimmer();
                            binding.shimmerLayoutProfile.setVisibility(View.GONE);
                            binding.profileFriendsRecView.setVisibility(View.VISIBLE);
                            profileAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


        // log out the user
        binding.Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                startActivity(new Intent(getContext(), LoginActivity.class));
                getActivity().finish();
            }
        });


        binding.profileFragEditProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EditProfileActivity.class);
                startActivity(intent);
            }
        });


        return binding.getRoot();
    }
}