package com.example.android.socialmediaappproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.example.android.socialmediaappproject.AdapterClasses.ChatAdapter;
import com.example.android.socialmediaappproject.ModelClasses.ChatModel;
import com.example.android.socialmediaappproject.ModelClasses.UsersInfoDataModel;
import com.example.android.socialmediaappproject.databinding.ActivityChatBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding binding;
    private String senderName;

    private ChatAdapter chatAdapter;
    private List<ChatModel> chatModelList;
    private RecyclerView chatMessagesRecView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.chatToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.chatToolbar.setNavigationOnClickListener((view -> {finish();}));



        binding.chatMessagesRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        binding.chatMessagesRecyclerView.setLayoutManager(linearLayoutManager);



        String userId = getIntent().getStringExtra("USER_ID");
        String userProfileUrl = getIntent().getStringExtra("USER_PROFILE");
        String userName = getIntent().getStringExtra("USER_NAME");

        Picasso.get().load(userProfileUrl).placeholder(R.drawable.placeholder_img).into(binding.chatProfileImg);
        binding.chatUserName.setText(userName);

        binding.chatMsgSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!binding.chatMsgEditTxt.getText().toString().trim().equals("")) {
                    sendMessage(FirebaseAuth.getInstance().getUid(), userId, binding.chatMsgEditTxt.getText().toString().trim(), userName);
                    binding.chatMsgEditTxt.setText("");
                } else {
                    Toast.makeText(ChatActivity.this, "Please enter some msg first...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        readMessage(FirebaseAuth.getInstance().getUid(),userId);


    }

    private void sendMessage(String sender, String receiver, String message, String receiverName) {

        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UsersInfoDataModel usersInfoDataModel = snapshot.getValue(UsersInfoDataModel.class);
                        senderName = usersInfoDataModel.getUsername();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        HashMap<String,Object> chatHashMap = new HashMap<>();
        chatHashMap.put("senderId",sender);
        chatHashMap.put("receiverId",receiver);
        chatHashMap.put("message",message);
        chatHashMap.put("senderName",senderName);
        chatHashMap.put("receiverName",receiverName);
        FirebaseDatabase.getInstance().getReference()
                .child("Chats")
                .push().setValue(chatHashMap);

//        List<ChatModel> chatModelList = new ArrayList<>();
//        chatModelList.add(new ChatModel(sender, receiver, message ,senderName, receiverName));
//        FirebaseDatabase.getInstance().getReference()
//                .child("Chats")
//                .push().setValue(chatModelList);

    }



    private void readMessage(String senderId, String receiverId) {
        chatModelList = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference()
                .child("Chats").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        chatModelList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            ChatModel chatModel = dataSnapshot.getValue(ChatModel.class);
                            if (chatModel.getReceiverId().equals(receiverId) && chatModel.getSenderId().equals(senderId) ||
                                    chatModel.getReceiverId().equals(senderId) && chatModel.getSenderId().equals(receiverId)) {
                                chatModelList.add(chatModel);
                            }

                            chatAdapter = new ChatAdapter(chatModelList,ChatActivity.this);
                            binding.chatMessagesRecyclerView.setAdapter(chatAdapter);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


}