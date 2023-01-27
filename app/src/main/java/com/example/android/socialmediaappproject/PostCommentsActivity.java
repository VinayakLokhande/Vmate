package com.example.android.socialmediaappproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.android.socialmediaappproject.AdapterClasses.CommentsRVAdapter;
import com.example.android.socialmediaappproject.ModelClasses.CommentsModel;
import com.example.android.socialmediaappproject.ModelClasses.NotificationModel;
import com.example.android.socialmediaappproject.ModelClasses.PostsRVModel;
import com.example.android.socialmediaappproject.ModelClasses.UsersInfoDataModel;
import com.example.android.socialmediaappproject.databinding.ActivityPostCommentsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PostCommentsActivity extends AppCompatActivity {
    ActivityPostCommentsBinding binding;
    Intent intent;
    String postId;
    String postedByData;

    List<CommentsModel> commentsList;

    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostCommentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //setting action bar to the activity
        setSupportActionBar(binding.toolbar);
        PostCommentsActivity.this.setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        // fetching post info from intent which is send from home frag
        intent = getIntent();
        postId = intent.getStringExtra("CLICKED_POST_ID");
        postedByData = intent.getStringExtra("CLICKED_POST_POSTED_BY");


        // getting all the post data belongs to that particular id which is came from home frag by intent and showing it to the views
        firebaseDatabase.getReference()
                .child("posts")
                .child(postId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        PostsRVModel postModel = snapshot.getValue(PostsRVModel.class);

                        Picasso.get()
                                .load(postModel.getPostImage())
                                .placeholder(R.drawable.placeholder_img)
                                .into(binding.commentPostImg);
                        binding.commentPostDescription.setText(postModel.getPostDescription());
                        binding.postLikeIconTxtView.setText(postModel.getTotalLikes() + "");
                        binding.postCommentIconTxtView.setText(postModel.getTotalComments() + "");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        // fetching post owner user info from firebase and showing it to view
        firebaseDatabase.getReference().child("Users")
                .child(postedByData).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UsersInfoDataModel userInfo = snapshot.getValue(UsersInfoDataModel.class);
                        Picasso.get()
                                .load(userInfo
                                        .getProfilePhoto())
                                .placeholder(R.drawable.placeholder_img)
                                .into(binding.commentProfileImg);
                        binding.commentProfileUserName.setText(userInfo.getUsername());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        binding.commentSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // if user has written any comment in the comment box then only it will store in the db
                if (binding.commentEditTxt.getText().toString() != null) {

                    // first getting all the comment text from the comment field written by the current user for that particular post and time of comment writing and current
                    // users id also and storing it to CommentsModel model
                    CommentsModel commentsModel = new CommentsModel();
                    commentsModel.setCommentBody(binding.commentEditTxt.getText().toString().trim());
                    commentsModel.setCommentedAt(new Date().getTime());
                    commentsModel.setCommentedBy(firebaseAuth.getUid());

                    // pushing every comment on new child by using push() method on the post owner id
                    firebaseDatabase.getReference()
                            .child("posts")
                            .child(postId)
                            .child("comments")
                            .push()
                            .setValue(commentsModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    firebaseDatabase.getReference()
                                            .child("posts")
                                            .child(postId)
                                            .child("totalComments").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    // storing total comment count belongs to particular post in the variable
                                                    int commentsCount = 0;
                                                    if (snapshot.exists()) {
                                                        commentsCount = snapshot.getValue(Integer.class);
                                                    }

                                                    // after sending the comment on particular post the totalComment count also increasing by 1
                                                    firebaseDatabase.getReference()
                                                            .child("posts")
                                                            .child(postId)
                                                            .child("totalComments")
                                                            .setValue(commentsCount + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    // after clicking on comment send btn making the comment field null again
                                                                    binding.commentEditTxt.setText("");

                                                                    // we are sending the notification after comment also so if current user will comment someones post then current users
                                                                    // info and time and all will be stored in NotificationModel model
                                                                    NotificationModel notificationModel = new NotificationModel();
                                                                    notificationModel.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                                    notificationModel.setNotificationAt(new Date().getTime());
                                                                    notificationModel.setPostId(postId);
                                                                    notificationModel.setPostedBy(postedByData);
                                                                    notificationModel.setTypeOfNoti("comment");
                                                                    notificationModel.setCheckNotiOpenOrNot(false);

                                                                    // and this model which have all the data of commented user is getting stored on notification list
                                                                    FirebaseDatabase.getInstance().getReference()
                                                                            .child("notifications")
                                                                            .child(postedByData)
                                                                            .push()
                                                                            .setValue(notificationModel);
                                                                }
                                                            });

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                }
                            });

                }
            }
        });


        commentsList = new ArrayList<>();
        CommentsRVAdapter commentsRVAdapter = new CommentsRVAdapter(this, commentsList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.commentsRecyclerView.setLayoutManager(linearLayoutManager);
        binding.commentsRecyclerView.setAdapter(commentsRVAdapter);


//        getting data from db and storing it in arraylist
        firebaseDatabase.getReference()
                .child("posts")
                .child(postId)
                .child("comments").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        commentsList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            CommentsModel commentsModel = dataSnapshot.getValue(CommentsModel.class);
                            commentsList.add(commentsModel);
                        }
                        commentsRVAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}