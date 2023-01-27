package com.example.android.socialmediaappproject.AdapterClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.socialmediaappproject.ModelClasses.FollowedFriendsModel;
import com.example.android.socialmediaappproject.ModelClasses.NotificationModel;
import com.example.android.socialmediaappproject.ModelClasses.UsersInfoDataModel;
import com.example.android.socialmediaappproject.R;
import com.example.android.socialmediaappproject.databinding.FragSearchRecViewBinding;
import com.example.android.socialmediaappproject.databinding.FragmentSearchBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.Date;
import java.util.List;

public class SearchFragRVAdapter extends RecyclerView.Adapter<SearchFragRVAdapter.ViewHolderSearch> {
    Context context;
    List<UsersInfoDataModel> usersList;

    public SearchFragRVAdapter(Context context, List<UsersInfoDataModel> usersList) {
        this.context = context;
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public SearchFragRVAdapter.ViewHolderSearch onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.frag_search_rec_view, parent, false);
        return new ViewHolderSearch(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchFragRVAdapter.ViewHolderSearch holder, int position) {

        UsersInfoDataModel users = usersList.get(position);

        Picasso.get()
                .load(users
                .getProfilePhoto())
                .placeholder(R.drawable.placeholder_img)
                .into(holder.binding.searchFragProfile);
        holder.binding.searchFragName.setText(users.getUsername());
        holder.binding.searchFragProfession.setText(users.getProfession());


        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(users.getUserId())
                .child("followers")
                .child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        // if current user has already followed the other any user then that users id will be present in the current users followers list so current
                        // user cannot follow him again
                        if (snapshot.exists()) {

                            alreadyFollowedStyle(holder);

                        } else {
                            // if particular user is not present in the current users followers list then current user can follow him
                            holder.binding.searchFragFollowBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    // if user will follow someone then his following should also increase na so we are creating new child userFollowingCount
                                    // and we are increasing the following count of the current user
                                    UsersInfoDataModel userInfo = new UsersInfoDataModel();
                                    FirebaseDatabase.getInstance().getReference()
                                            .child("Users")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .child("userFollowingsCount")
                                            .setValue(userInfo.getUserFollowingsCount() + 1);

                                    // so if current user will follow someone then current users id and time when he has followed the other user will store to that
                                    // users followers list who had followed the current user
                                    FollowedFriendsModel follower = new FollowedFriendsModel();
                                    follower.setFollowedBy(FirebaseAuth.getInstance().getUid());
                                    follower.setFollowedAtTime(new Date().getTime());


                                    // going inside that users id who's follow button is being clicked by the current user and storing the current users id inside
                                    // that users followers child who has followed by current user
                                    FirebaseDatabase.getInstance().getReference()
                                            .child("Users")
                                            .child(users.getUserId())  // getting exact position of that user who's follow btn is clicked by using position variable which
                                            // is provided by onBindViewHolder() method
                                            .child("followers")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .setValue(follower).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    // after following someone his follower count also should be increase so increasing the follower count
                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child("Users")
                                                            .child(users.getUserId())
                                                            .child("followersCount")
                                                            .setValue(users.getFollowersCount() + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
//                                                                  // after following the user the buttons style is changed so current user cannot follow that particular user again
                                                                    alreadyFollowedStyle(holder);
                                                                    // so we have to send notification also to followed user so storing the current users info and time when he is following him
                                                                    NotificationModel notificationModel = new NotificationModel();
                                                                    notificationModel.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                                    notificationModel.setNotificationAt(new Date().getTime());
                                                                    notificationModel.setCheckNotiOpenOrNot(false);
                                                                    notificationModel.setTypeOfNoti("Follow");

                                                                    // and storing this notification to followed users notification list
                                                                    FirebaseDatabase.getInstance().getReference()
                                                                            .child("notifications")
                                                                            .child(users.getUserId())
                                                                            .push()
                                                                            .setValue(notificationModel);

                                                                }
                                                            });
                                                }
                                            });
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class ViewHolderSearch extends RecyclerView.ViewHolder {

        FragSearchRecViewBinding binding;

        public ViewHolderSearch(@NonNull View itemView) {
            super(itemView);

            binding = FragSearchRecViewBinding.bind(itemView);
        }

    }

    private void alreadyFollowedStyle(SearchFragRVAdapter.ViewHolderSearch holder) {
        holder.binding.searchFragFollowBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.follow_btn_style_outlined_bg));
        holder.binding.searchFragFollowBtn.setText("Following");
        holder.binding.searchFragFollowBtn.setTextColor(context.getResources().getColor(R.color.redish));
        holder.binding.searchFragFollowBtn.setEnabled(false);
    }


    // adding then searched word list to userList which is showing the list of all the users
    public void setFilteredList(List<UsersInfoDataModel> filteredList) {
        this.usersList = filteredList;
        notifyDataSetChanged();
    }
}
