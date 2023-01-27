package com.example.android.socialmediaappproject.AdapterClasses;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.socialmediaappproject.ModelClasses.NotificationModel;
import com.example.android.socialmediaappproject.ModelClasses.UsersInfoDataModel;
import com.example.android.socialmediaappproject.PostCommentsActivity;
import com.example.android.socialmediaappproject.R;
import com.example.android.socialmediaappproject.databinding.NotificationRecViewDesignBinding;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NotificationRVAdapter extends RecyclerView.Adapter<NotificationRVAdapter.ViewHolderNotiChild> {
    List<NotificationModel> notificationList;
    Context context;

    public NotificationRVAdapter(List<NotificationModel> notiChildList, Context context) {
        this.notificationList = notiChildList;
        this.context = context;
    }

    @NonNull
    @Override
    public NotificationRVAdapter.ViewHolderNotiChild onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_rec_view_design,parent,false);
        return new ViewHolderNotiChild(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationRVAdapter.ViewHolderNotiChild holder, int position) {
        NotificationModel notiModel = notificationList.get(position);
         // so we can differentiate the noti text by using this type na we will know that which noti should have to place which type of text na
        String notiType = notiModel.getTypeOfNoti();


            FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(notiModel.getNotificationBy()) // getting that users info who had sends the notification
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            UsersInfoDataModel userInfo = snapshot.getValue(UsersInfoDataModel.class);
                            Picasso.get()
                                    .load(userInfo.getProfilePhoto())
                                    .placeholder(R.drawable.placeholder_img)
                                    .into(holder.binding.notificationProfileImg);

                            // we are differentiating the notification here if its like notification then like condition if comment then comment notification condition and
                            // if follow then follow condition
                            if (notiType.equals("like")) {
                                // highlighting the username only of every notification by using html tag
                                holder.binding.notificationNotiText.setText(Html.fromHtml("<b>" + userInfo.getUsername() + "</b>" + " has liked your post."));
                            } else if (notiType.equals("comment")) {
                                holder.binding.notificationNotiText.setText(Html.fromHtml("<b>" + userInfo.getUsername() + "</b>" + " has commented on your post."));
                            } else {
                                holder.binding.notificationNotiText.setText(Html.fromHtml("<b>" + userInfo.getUsername() + "</b>" + " has started following you."));
                            }

                            // getting every notification time
                            String formattedTime = TimeAgo.using(notiModel.getNotificationAt());
                            holder.binding.notificationTimeTxt.setText(formattedTime);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


        holder.binding.notificationDesignParentItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // if notification type is follow means if there is follow related notification so then there is no clickable operation but if
                // the notification is belongs to like or comment then that notification is clickable.so by clicking it we are going to that particular
                // posts page only
                if (!notiType.equals("Follow")) {

                    FirebaseDatabase.getInstance().getReference()
                            .child("notifications")
                            .child(notiModel.getNotificationBy())
                            .child(notiModel.getNotificationId())
                            .child("checkNotiOpenOrNot")
                            .setValue(true);

                    holder.binding.notificationDesignParentItem.setBackgroundColor(context.getResources().getColor(R.color.white));
                    Intent intent = new Intent(context, PostCommentsActivity.class);
                    intent.putExtra("CLICKED_POST_ID", notiModel.getPostId());
                    intent.putExtra("CLICKED_POST_POSTED_BY", notiModel.getPostedBy());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        });


        // so if user had clicked the noti previously then color will be change to white otherwise not
        boolean checkNotiOpenedOrNot = notiModel.isCheckNotiOpenOrNot();
        if (checkNotiOpenedOrNot == true) {
            holder.binding.notificationDesignParentItem.setBackgroundColor(context.getResources().getColor(R.color.white));
        }


    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }



    public class ViewHolderNotiChild extends RecyclerView.ViewHolder {
        NotificationRecViewDesignBinding binding;

        public ViewHolderNotiChild(@NonNull View itemView) {
            super(itemView);

            binding = NotificationRecViewDesignBinding.bind(itemView);

        }
    }
}
