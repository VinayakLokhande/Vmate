package com.example.android.socialmediaappproject.AdapterClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.socialmediaappproject.ModelClasses.CommentsModel;
import com.example.android.socialmediaappproject.ModelClasses.UsersInfoDataModel;
import com.example.android.socialmediaappproject.R;
import com.example.android.socialmediaappproject.databinding.CommentsRecyViewDesignBinding;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CommentsRVAdapter extends RecyclerView.Adapter<CommentsRVAdapter.ViewHolderComments> {
    Context context;
    List<CommentsModel> commentsList;

    public CommentsRVAdapter(Context context, List<CommentsModel> commentsList) {
        this.context = context;
        this.commentsList = commentsList;
    }

    @NonNull
    @Override
    public CommentsRVAdapter.ViewHolderComments onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comments_recy_view_design,parent,false);
        return new ViewHolderComments(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsRVAdapter.ViewHolderComments holder, int position) {
        CommentsModel commentsModel = commentsList.get(position);

        // showing all the comments and the info of user belongs to every particular comment
        holder.binding.commentBodyTxt.setText(commentsModel.getCommentBody());

        String formattedTime = TimeAgo.using(commentsModel.getCommentedAt());
        holder.binding.commentTime.setText(formattedTime);


        // fetching commented user data using his id
        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(commentsModel.getCommentedBy()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UsersInfoDataModel userInfo = snapshot.getValue(UsersInfoDataModel.class);
                        Picasso.get().load(userInfo.getProfilePhoto())
                                     .placeholder(R.drawable.placeholder_img)
                                     .into(holder.binding.commentProfileImg);
                        holder.binding.commentRecyclerUsername.setText(userInfo.getUsername());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    public class ViewHolderComments extends RecyclerView.ViewHolder {
        CommentsRecyViewDesignBinding binding;
        public ViewHolderComments(@NonNull View itemView) {
            super(itemView);

            binding = CommentsRecyViewDesignBinding.bind(itemView);
        }
    }
}
