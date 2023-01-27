package com.example.android.socialmediaappproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.android.socialmediaappproject.AdapterClasses.ChatUsersListRVAdapter;
import com.example.android.socialmediaappproject.ModelClasses.FollowedFriendsModel;
import com.example.android.socialmediaappproject.ModelClasses.UsersInfoDataModel;
import com.example.android.socialmediaappproject.databinding.ActivityChatListBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatListActivity extends AppCompatActivity {
    ActivityChatListBinding binding;
    FirebaseDatabase firebaseDatabase;
    List<FollowedFriendsModel> followersList;
    ChatUsersListRVAdapter chatUsersListRVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.shimmerLayoutChatList.startShimmer();

        setSupportActionBar(binding.chatListToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.chatListToolbar.setNavigationOnClickListener((view -> {finish();}));


        followersList = new ArrayList<>();
        chatUsersListRVAdapter = new ChatUsersListRVAdapter(this,followersList);
        binding.chatListRecycler.setHasFixedSize(true);
        binding.chatListRecycler.setLayoutManager(new LinearLayoutManager(this));
        binding.chatListRecycler.setAdapter(chatUsersListRVAdapter);


        firebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(FirebaseAuth.getInstance().getUid())
                .child("followers").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        followersList.clear();
                        if (snapshot.exists()) {

                            for (DataSnapshot followers : snapshot.getChildren()) {
                                FollowedFriendsModel followedFriendsModel = followers.getValue(FollowedFriendsModel.class);
                                followersList.add(followedFriendsModel);
                            }
                            binding.shimmerLayoutChatList.stopShimmer();
                            binding.shimmerLayoutChatList.setVisibility(View.GONE);
                            binding.chatListRecycler.setVisibility(View.VISIBLE);
                            chatUsersListRVAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



    }

}