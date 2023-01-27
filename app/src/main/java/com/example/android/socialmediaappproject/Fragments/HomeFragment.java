package com.example.android.socialmediaappproject.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.socialmediaappproject.AdapterClasses.HomePostsRVAdapter;
import com.example.android.socialmediaappproject.AdapterClasses.StoryRVAdapter;
import com.example.android.socialmediaappproject.ChatListActivity;
import com.example.android.socialmediaappproject.ModelClasses.PostedStoryInfoModel;
import com.example.android.socialmediaappproject.ModelClasses.PostsRVModel;
import com.example.android.socialmediaappproject.ModelClasses.StoriesRVModel;
import com.example.android.socialmediaappproject.ModelClasses.UsersInfoDataModel;
import com.example.android.socialmediaappproject.R;
import com.example.android.socialmediaappproject.databinding.FragmentHomeBinding;
import com.example.android.socialmediaappproject.databinding.FragmentSearchBinding;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment {
    FragmentHomeBinding binding;
    List<StoriesRVModel> storiesList;
    List<PostsRVModel> postsList;

    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    FirebaseStorage firebaseStorage;

    ActivityResultLauncher<String> getStoryImg;
    Uri storyImgUri;

    ProgressDialog progressDialog;

    StoryRVAdapter storyRVAdapter;
    HomePostsRVAdapter homePostsRVAdapter;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        progressDialog = new ProgressDialog(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        // starting shimmer effect on stories and posts recycler view
        binding.shimmerLayoutPosts.startShimmer();
        binding.shimmerLayoutStories.startShimmer();

        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle("Uploading Story");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);


//        Post recycler view work
        postsList = new ArrayList<>();

        // setting adapter to posts recycler view
        homePostsRVAdapter = new HomePostsRVAdapter(postsList, getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
        binding.postsRV.setLayoutManager(linearLayoutManager);
        binding.postsRV.setNestedScrollingEnabled(false);
        binding.postsRV.setAdapter(homePostsRVAdapter);

        // fetching all posts from database to show them into posts recycler view
        fetchingPosts(homePostsRVAdapter);


//        Story recycler view work
        storiesList = new ArrayList<>();

        // setting adapter to posts recycler view
        storyRVAdapter = new StoryRVAdapter(storiesList, getContext());
        LinearLayoutManager linearLManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, true);
        binding.homeFragStoriesRecView.setLayoutManager(linearLManager);
        binding.homeFragStoriesRecView.setNestedScrollingEnabled(false);
        binding.homeFragStoriesRecView.setAdapter(storyRVAdapter);

        // fetching all stories from database to show them into story recycler view
        fetchingStories();

        // getting uri of users selected image and storing it to firebase storage and db
        getStoryImg = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result != null) {
                    storyImgUri = result;
                    progressDialog.show();

                    // storing user selected story image to firebase storage with time of posting
                    final StorageReference reference = firebaseStorage.getReference()
                            .child("stories")
                            .child(FirebaseAuth.getInstance().getUid())
                            .child(new Date().getTime() + "");

                    // storing image uri result to firebase storage
                    reference.putFile(result).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            // getting stored image download url to store it to firebase db
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    // method to store user story on firebase db
                                    storeUserStoryOnTime(uri);
                                }
                            });
                        }
                    });
                }
            }
        });


        binding.homeFragAddStoryImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getStoryImg.launch("image/*");

            }
        });


        binding.homeFragChatListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ChatListActivity.class));
            }
        });


        homeFragStoryProfileImage(binding.homeFragAddStoryImgView);


        return binding.getRoot();
    }


    private void fetchingPosts(HomePostsRVAdapter homePostsRVAdapter) {

        // fetching all posts from database to show them into posts recycler view
        firebaseDatabase.getReference().child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                   // so inside the posts node as many as children's are present that all children's we are fetching and that all children's
                   // are stored in this snapshot so by using for loop we are getting one by one every child and storing it to the arraylist

                // clearing previously stored data in list and storing again fresh posts related data to postList arraylist
                postsList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    PostsRVModel postsModel = dataSnapshot.getValue(PostsRVModel.class);
                    postsModel.setPostId(dataSnapshot.getKey());  // we have set every post on unique key which is generated by push() method so we are getting all that key
                      // so by getting that key only we can fetch all the data of the post which is related to that keys
                    postsList.add(postsModel);
                }
                // setting shimmer effect visibility gone and fragments view components visibility visible
                binding.shimmerLayoutPosts.stopShimmer();
                binding.shimmerLayoutPosts.setVisibility(View.GONE);
                binding.postsRV.setVisibility(View.VISIBLE);

                // and we have notified the adapter also that data set has been changed
                homePostsRVAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void fetchingStories() {
        // Fetching all the stories and storing it to arraylist of type PostedStoryInfoModel class
        firebaseDatabase.getReference()
                .child("stories").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // if snapshot exists then stories are present in the db so previous stories stored in arraylist should be clear and new updated story data should be
                        // added to arraylist
                        if (snapshot.exists()) {
                            storiesList.clear();
                            for (DataSnapshot storySnapshot : snapshot.getChildren()) {
                                StoriesRVModel storiesModel = new StoriesRVModel();
                                storiesModel.setStoryBy(storySnapshot.getKey());
                                storiesModel.setStoryAt(storySnapshot.child("storyPostedAt").getValue(Long.class));

                                // fetching all the stories inside the userStories child and storing it to PostedStoryInfoModel type list to store tyat list to StoriesRVModel
                                // models stories obj
                                List<PostedStoryInfoModel> postedStoryList = new ArrayList<>();
                                for (DataSnapshot postedStorySnapshot : storySnapshot.child("userStories").getChildren()) {
                                    PostedStoryInfoModel postedStoriesModel = postedStorySnapshot.getValue(PostedStoryInfoModel.class);
                                    postedStoryList.add(postedStoriesModel);
                                }

                                //  now we have list also with info so we are going to store that list in StoriesRVModel list
                                storiesModel.setStories(postedStoryList);

                                //  and finally we have added all the data in adapters stroyList
                                storiesList.add(storiesModel);
                            }

                            // setting shimmer effect visibility gone and fragments view components visibility visible
                            binding.shimmerLayoutStories.stopShimmer();
                            binding.shimmerLayoutStories.setVisibility(View.GONE);
                            binding.homeFragStoriesRecView.setVisibility(View.VISIBLE);

                            // and we have notified the adapter also that data set has been changed
                            storyRVAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }


    private void homeFragStoryProfileImage(ImageView addStoryImgView) {

        firebaseDatabase.getReference()
                .child("Users")
                .child(firebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UsersInfoDataModel userModel = snapshot.getValue(UsersInfoDataModel.class);

                        Picasso.get().load(userModel.getProfilePhoto())
                                .placeholder(R.drawable.placeholder_img)
                                .into(addStoryImgView);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }



    private void storeUserStoryOnTime(Uri uri) {

        StoriesRVModel storyModel = new StoriesRVModel();
        storyModel.setStoryAt(new Date().getTime());

        // storing the image story to firebase db on specific time
        firebaseDatabase.getReference()
                .child("stories")
                .child(firebaseAuth.getUid())
                .child("storyPostedAt")
                .setValue(storyModel.getStoryAt()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // soring current added story to PostedStoryInfoModel model
                        PostedStoryInfoModel postedStoriesModel = new PostedStoryInfoModel(uri.toString(), storyModel.getStoryAt());

                        // pushing current users every story on unique key generating by push method
                        firebaseDatabase.getReference()
                                .child("stories")
                                .child(firebaseAuth.getUid())
                                .child("userStories")
                                .push()
                                .setValue(postedStoriesModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        progressDialog.dismiss();
                                    }
                                });
                    }
                });
    }
}
