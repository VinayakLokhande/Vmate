package com.example.android.socialmediaappproject.AdapterClasses;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.socialmediaappproject.ChatActivity;
import com.example.android.socialmediaappproject.ModelClasses.ChatModel;
import com.example.android.socialmediaappproject.ModelClasses.FollowedFriendsModel;
import com.example.android.socialmediaappproject.ModelClasses.UsersInfoDataModel;
import com.example.android.socialmediaappproject.R;
import com.example.android.socialmediaappproject.databinding.ChatListItemDesignBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ChatUsersListRVAdapter extends RecyclerView.Adapter<ChatUsersListRVAdapter.ChatUsersListViewHolder> {
    private Context context;
    private List<FollowedFriendsModel> followersChatList;
    private FirebaseDatabase firebaseDatabase;
    private String lastMessage;

    public ChatUsersListRVAdapter(Context context, List<FollowedFriendsModel> followersChatList) {
        this.context = context;
        this.followersChatList = followersChatList;
    }

    @NonNull
    @Override
    public ChatUsersListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatUsersListViewHolder(LayoutInflater.from(context).inflate(R.layout.chat_list_item_design,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatUsersListViewHolder holder, int position) {
        FollowedFriendsModel followedFriendsModel = followersChatList.get(position);

        firebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(followedFriendsModel.getFollowedBy()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UsersInfoDataModel usersInfoDataModel = snapshot.getValue(UsersInfoDataModel.class);

                        Picasso.get()
                                .load(usersInfoDataModel.getProfilePhoto())
                                .placeholder(R.drawable.placeholder_img)
                                .into(holder.binding.chatListUserImg);
                        holder.binding.chatListUserName.setText(usersInfoDataModel.getUsername());

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(context, ChatActivity.class);
                                intent.putExtra("USER_ID",followedFriendsModel.getFollowedBy());
                                intent.putExtra("USER_PROFILE",usersInfoDataModel.getProfilePhoto());
                                intent.putExtra("USER_NAME",usersInfoDataModel.getUsername());
                                context.startActivity(intent);
                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        lastMessage(followedFriendsModel.getFollowedBy(),holder.binding.chatListLastMessage,holder.binding.lastMsgTxt);

    }

    @Override
    public int getItemCount() {
        return followersChatList.size();
    }

    public class ChatUsersListViewHolder extends RecyclerView.ViewHolder {

        ChatListItemDesignBinding binding;

        public ChatUsersListViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = ChatListItemDesignBinding.bind(itemView);

        }
    }


    private void lastMessage(String userId, TextView chatLastMsgTxtView,TextView lastMsg) {

        FirebaseDatabase.getInstance().getReference()
                .child("Chats").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            ChatModel chatModel = dataSnapshot.getValue(ChatModel.class);
                            if (chatModel.getReceiverId().equals(FirebaseAuth.getInstance().getUid()) && chatModel.getSenderId().equals(userId) ||
                                    chatModel.getReceiverId().equals(userId) && chatModel.getSenderId().equals(FirebaseAuth.getInstance().getUid())) {
                                lastMessage = chatModel.getMessage();
                                lastMsg.setVisibility(View.VISIBLE);
                                chatLastMsgTxtView.setVisibility(View.VISIBLE);
                                chatLastMsgTxtView.setText(lastMessage);
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}
