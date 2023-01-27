package com.example.android.socialmediaappproject.AdapterClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.socialmediaappproject.ModelClasses.FollowedFriendsModel;
import com.example.android.socialmediaappproject.ModelClasses.UsersInfoDataModel;
import com.example.android.socialmediaappproject.R;
import com.example.android.socialmediaappproject.databinding.ProfileFragMyFriendsRecViewDesignBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FollowersDataAdapter extends RecyclerView.Adapter<FollowersDataAdapter.ViewHolderProfile> {

    List<FollowedFriendsModel> friendsList;
    Context context;

    public FollowersDataAdapter(List<FollowedFriendsModel> friendsList, Context context) {
        this.friendsList = friendsList;
        this.context = context;
    }

    @NonNull
    @Override
    public FollowersDataAdapter.ViewHolderProfile onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.profile_frag_my_friends_rec_view_design,parent,false);
        return new ViewHolderProfile(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowersDataAdapter.ViewHolderProfile holder, int position) {
        FollowedFriendsModel followedUser = friendsList.get(position);

        // getting user image and username from database and setting it to my friends recycler view
        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(followedUser.getFollowedBy()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UsersInfoDataModel userInfo = snapshot.getValue(UsersInfoDataModel.class);
                        Picasso.get().load(userInfo.getProfilePhoto())
                                .placeholder(R.drawable.placeholder_img)
                                .into(holder.binding.friendsProfileImgView);

                        holder.binding.friendNameTxt.setText(userInfo.getUsername());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    public class ViewHolderProfile extends RecyclerView.ViewHolder {

        ProfileFragMyFriendsRecViewDesignBinding binding;

        public ViewHolderProfile(@NonNull View itemView) {
            super(itemView);
            binding = ProfileFragMyFriendsRecViewDesignBinding.bind(itemView);
        }
    }
}
