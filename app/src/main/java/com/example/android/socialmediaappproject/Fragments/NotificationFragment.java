package com.example.android.socialmediaappproject.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.socialmediaappproject.AdapterClasses.NotificationRVAdapter;
import com.example.android.socialmediaappproject.ModelClasses.NotificationModel;
import com.example.android.socialmediaappproject.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class NotificationFragment extends Fragment {

    List<NotificationModel> notificationList;
    RecyclerView notificationRecView;

    FirebaseDatabase firebaseDatabase;

    ShimmerFrameLayout shimmerLayoutNotification;
    TextView noNotificationFount;

    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseDatabase = FirebaseDatabase.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        notificationRecView = view.findViewById(R.id.notificationRecView);
        noNotificationFount = view.findViewById(R.id.noNotificationFount);
        shimmerLayoutNotification = view.findViewById(R.id.shimmerLayoutNotification);

        shimmerLayoutNotification.startShimmer();

        notificationList = new ArrayList<>();

        NotificationRVAdapter notificationRVAdapter = new NotificationRVAdapter(notificationList,getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        notificationRecView.setLayoutManager(linearLayoutManager);
        notificationRecView.setAdapter(notificationRVAdapter);


        // fetching all the notifications of current user and storing them into arraylist
        firebaseDatabase.getReference()
                .child("notifications")
                .child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        notificationList.clear();
//                        NotificationModel notificationModel = snapshot.getValue(NotificationModel.class);
                        for (DataSnapshot notificationDataSnap : snapshot.getChildren()) {
                            NotificationModel notificationModel = notificationDataSnap.getValue(NotificationModel.class);
                            notificationModel.setNotificationId(notificationDataSnap.getKey());
                            notificationList.add(notificationModel);
                        }

                        shimmerLayoutNotification.stopShimmer();
                        shimmerLayoutNotification.setVisibility(View.GONE);
                        notificationRecView.setVisibility(view.VISIBLE);

                        if (notificationList.size() == 0) {
                            notificationRecView.setVisibility(view.GONE);
                            noNotificationFount.setVisibility(view.VISIBLE);
                        } else {
                            noNotificationFount.setVisibility(view.GONE);
                        }

                        notificationRVAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



        return view;


    }
}