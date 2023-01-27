package com.example.android.socialmediaappproject.AdapterClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.socialmediaappproject.ModelClasses.ChatModel;
import com.example.android.socialmediaappproject.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    public static final int CHAT_MSG_LEFT = 0;
    public static final int CHAT_MSG_RIGHT = 1;
    List<ChatModel> chatMessagesModelList;
    Context context;

    public ChatAdapter(List<ChatModel> chatMessagesModelList, Context context) {
        this.chatMessagesModelList = chatMessagesModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == CHAT_MSG_RIGHT) {
            return new ChatViewHolder(LayoutInflater.from(context).inflate(R.layout.chat_item_msg_left_layout,parent,false));
        } else {
            return new ChatViewHolder(LayoutInflater.from(context).inflate(R.layout.chat_item_msg_right_layout,parent,false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatModel chatModel = chatMessagesModelList.get(position);

        holder.chatMsgTxt.setText(chatModel.getMessage());



    }

    @Override
    public int getItemCount() {
        return chatMessagesModelList.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {

        TextView chatMsgTxt;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            chatMsgTxt = itemView.findViewById(R.id.chatMsgTxt);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (chatMessagesModelList.get(position).getSenderId().equals(FirebaseAuth.getInstance().getUid())) {
            return CHAT_MSG_RIGHT;
        } else {
            return CHAT_MSG_LEFT;
        }
    }
}
