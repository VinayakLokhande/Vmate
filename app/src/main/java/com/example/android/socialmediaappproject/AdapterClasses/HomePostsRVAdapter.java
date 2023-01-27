package com.example.android.socialmediaappproject.AdapterClasses;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.socialmediaappproject.ModelClasses.NotificationModel;
import com.example.android.socialmediaappproject.ModelClasses.PostsRVModel;
import com.example.android.socialmediaappproject.ModelClasses.UsersInfoDataModel;
import com.example.android.socialmediaappproject.PostCommentsActivity;
import com.example.android.socialmediaappproject.R;
import com.example.android.socialmediaappproject.databinding.PostsRecyclerViewDesignHomeFragBinding;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

public class HomePostsRVAdapter extends RecyclerView.Adapter<HomePostsRVAdapter.ViewHolderPosts> {

    List<PostsRVModel> postsList;
    Context context;

    public HomePostsRVAdapter(List<PostsRVModel> postsList, Context context) {
        this.postsList = postsList;
        this.context = context;
    }

    @NonNull
    @Override
    public HomePostsRVAdapter.ViewHolderPosts onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.posts_recycler_view_design_home_frag, parent, false);
        return new ViewHolderPosts(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomePostsRVAdapter.ViewHolderPosts holder, int position) {
        // getting the data from list of the particular position and storing it to PostsRVModel class
        PostsRVModel postsRVModel = postsList.get(position);

        // setting image,description,user info to view
        Picasso.get().load(postsRVModel.getPostImage())
                .placeholder(R.drawable.placeholder_img)
                .into(holder.binding.postUserPostImgView);
        // setting the total likes and total comments of the posts to views
        holder.binding.postLikeTxt.setText(postsRVModel.getTotalLikes() + "");
        holder.binding.postCommentIconTxtView.setText(postsRVModel.getTotalComments() + "");

        String formattedTime = TimeAgo.using(Long.parseLong(postsRVModel.getPostAt()));
        holder.binding.postTimeTxtView.setText(formattedTime);

        // if user has set any description on his post then description showing view will visible but if user has not given any description on his
        // on his post then description view will not visible
        String postDesc = postsRVModel.getPostDescription();
        if (postDesc != null) {
            holder.binding.postDescriptionTxt.setText(postsRVModel.getPostDescription());
            holder.binding.postDescriptionTxt.setVisibility(View.VISIBLE);

        } else {
            holder.binding.postDescriptionTxt.setVisibility(View.GONE);
        }


        // The data of the user who has the post was taken from the firebase db adn shown it on the all the views
        // like users image, name, profession
        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(postsRVModel.getPostBy()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UsersInfoDataModel userInfo = snapshot.getValue(UsersInfoDataModel.class);
                        Picasso.get()
                                .load(userInfo.getProfilePhoto())
                                .placeholder(R.drawable.placeholder_img)
                                .into(holder.binding.postUserProfileImgView);

                        holder.binding.postUserNameTxtView.setText(userInfo.getUsername());
                        holder.binding.postUserAboutTxtView.setText(userInfo.getProfession());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        // checking user has already the post or not if he had liked it previously then he can't like it again and if he don't have liked yet then he
        // can like the post and also that particular posts like also will be increment
        FirebaseDatabase.getInstance().getReference()
                .child("posts")
                .child(postsRVModel.getPostId())
                .child("likes")
                .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            holder.binding.postLikeTxt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart_solid, 0, 0, 0);

                        } else {
                            holder.binding.postLikeTxt.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // so when user will like the post then his id will store inside the likes child and he has liked that post so we are setting the value to his id true
                                    // for that particular post
                                    FirebaseDatabase.getInstance().getReference()
                                            .child("posts")
                                            .child(postsRVModel.getPostId())  // so inside postsRvModel obj we have current position na so if user like any post that posts exact postion will give this variable to us
                                            .child("likes")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {

                                                    // and if the child is stored successfully then we are incrementing that post likes and also setting them on the post likes text
                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child("posts")
                                                            .child(postsRVModel.getPostId())
                                                            .child("totalLikes")
                                                            .setValue(postsRVModel.getTotalLikes() + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    holder.binding.postLikeTxt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart_solid, 0, 0, 0);

                                                                    sendingNotification(postsRVModel);

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


        // if current user will click on the comment btn of post then sending that post info to comment activity and also sending the
        // current user also to comment activity screen via intent
        holder.binding.postCommentIconTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PostCommentsActivity.class);
                intent.putExtra("CLICKED_POST_ID", postsRVModel.getPostId());
                intent.putExtra("CLICKED_POST_POSTED_BY", postsRVModel.getPostBy());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });


        //sharing image link
        holder.binding.postShareIconImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,postsRVModel.getPostImage());
                context.startActivity(Intent.createChooser(intent, "Choose to share"));

            }
        });

    }

    @Override
    public int getItemCount() {
        return postsList.size();
    }

    public class ViewHolderPosts extends RecyclerView.ViewHolder {

        PostsRecyclerViewDesignHomeFragBinding binding;

        public ViewHolderPosts(@NonNull View itemView) {
            super(itemView);

            binding = PostsRecyclerViewDesignHomeFragBinding.bind(itemView);

        }
    }



    private void sendingNotification(PostsRVModel postsRVModel) {

        NotificationModel notificationModel = new NotificationModel();
        notificationModel.setNotificationBy(FirebaseAuth.getInstance().getUid());  // Taking the id of the current user bec he has liked someones post na so by using
        //  this id we can show all the info about post liked user to the owners notifications page that your post has liked by this person
        notificationModel.setNotificationAt(new Date().getTime());  // setting time when he has liked the post

        notificationModel.setPostId(postsRVModel.getPostId());  // getting that post id bec if the owner of the post will click that notification so he will directly redirect
        //  on that particular post which is liked by someone

        notificationModel.setPostedBy(postsRVModel.getPostBy());  // and by postedBy we can get all the info about that user who had posted that post

        notificationModel.setTypeOfNoti("like");  // and we are setting the notification type also bec user can only click like and comments notification and can redirect to
//                        another page but if he will click on the follow notification then he will not go anywhere so for distributing this we are setting type of every notification
        notificationModel.setCheckNotiOpenOrNot(false);
        // and storing every notification on the on unique key
        FirebaseDatabase.getInstance().getReference()
                .child("notifications")
                .child(postsRVModel.getPostBy())
                .push()// bec we have to send the notification to the owner of the post
                .setValue(notificationModel);

    }

}
