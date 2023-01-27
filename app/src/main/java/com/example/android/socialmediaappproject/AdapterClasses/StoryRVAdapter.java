package com.example.android.socialmediaappproject.AdapterClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.socialmediaappproject.ModelClasses.PostedStoryInfoModel;
import com.example.android.socialmediaappproject.ModelClasses.StoriesRVModel;
import com.example.android.socialmediaappproject.ModelClasses.UsersInfoDataModel;
import com.example.android.socialmediaappproject.R;
import com.example.android.socialmediaappproject.databinding.StoriesRecyclerViewDesignBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class StoryRVAdapter extends RecyclerView.Adapter<StoryRVAdapter.ViewHolderStory> {

    List<StoriesRVModel> storiesList;
    Context context;

    public StoryRVAdapter(List<StoriesRVModel> storiesList, Context context) {
        this.storiesList = storiesList;
        this.context = context;
    }

    @NonNull
    @Override
    public StoryRVAdapter.ViewHolderStory onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.stories_recycler_view_design, parent, false);
        return new ViewHolderStory(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryRVAdapter.ViewHolderStory holder, int position) {
        StoriesRVModel storiesModel = storiesList.get(position);

        // if there are stories present then go inside if block
        if (storiesModel.getStories().size() > 0) {

            // getting every users last story image which he has posted and showing it on the circular image view of story recycler view
            PostedStoryInfoModel postedLastStory = storiesModel.getStories().get(storiesModel.getStories().size() - 1);

            Picasso.get().load(postedLastStory.getStoryImg())
                    .into(holder.binding.storiesRecViewStoryImg);
            holder.binding.storiesRecViewStoryImg.setBorderColor(context.getResources().getColor(R.color.redish));
            holder.binding.storiesRecViewStoryImg.setBorderOverlay(true);
            holder.binding.storiesRecViewStoryImg.setBorderWidth(7);

            // getting all that users only who had posted stories previously
            FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(storiesModel.getStoryBy()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            UsersInfoDataModel userInfo = snapshot.getValue(UsersInfoDataModel.class);
                            holder.binding.storiesRecViewStoryTxt.setText(userInfo.getUsername());  // showing user name who had posted the story

                            // after clicking the story image in stories recycler view user should see the all the stories of that user who had posted stories so for
                            // that the code is as follows
                            holder.binding.storiesRecViewStoryImg.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // when user will click on a story of the particular position of story recycler view then fetching all the stories belongs to
                                    // that position and storing it in arraylist
                                    ArrayList<MyStory> myStories = new ArrayList<>();

                                    for (PostedStoryInfoModel stories : storiesModel.getStories()) {
                                        myStories.add(new MyStory(stories.getStoryImg()));
                                    }

                                    // showing all the stories stored in arraylist on the full screen story viewer screen
                                    new StoryView.Builder(((AppCompatActivity) context).getSupportFragmentManager())
                                            .setStoriesList(myStories)  // showing all the stories
                                            .setStoryDuration(5000)  // showing duration of every story
                                            .setTitleText(userInfo.getUsername()) // showing story owner name
                                            .setTitleLogoUrl(userInfo.getProfilePhoto()) // showing story owner profile image
                                            .build() // Must be called before calling show method
                                            .show();
                                }
                            });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }


    }

    @Override
    public int getItemCount() {
        return storiesList.size();
    }


    public class ViewHolderStory extends RecyclerView.ViewHolder {
        StoriesRecyclerViewDesignBinding binding;

        public ViewHolderStory(@NonNull View itemView) {
            super(itemView);

            binding = StoriesRecyclerViewDesignBinding.bind(itemView);

        }
    }
}
