package com.example.android.socialmediaappproject.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android.socialmediaappproject.AdapterClasses.SearchFragRVAdapter;
import com.example.android.socialmediaappproject.ModelClasses.UsersInfoDataModel;
import com.example.android.socialmediaappproject.R;
import com.example.android.socialmediaappproject.databinding.FragmentSearchBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class SearchFragment extends Fragment {
    FragmentSearchBinding binding;
    List<UsersInfoDataModel> usersList = new ArrayList<>();
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
//    HashSet<UsersInfoDataModel> usersHashSet = new HashSet<>();

    public SearchFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // binding the activity with layout
        binding = FragmentSearchBinding.inflate(inflater, container, false);

        binding.shimmerLayoutSearch.startShimmer();

        SearchFragRVAdapter adapter = new SearchFragRVAdapter(getContext(), usersList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.searchFragRV.setLayoutManager(linearLayoutManager);
        binding.searchFragRV.setAdapter(adapter);

        // Fetching those users data who are using this app and storing it to arraylist
        firebaseDatabase.getReference()
                .child("Users").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        usersList.clear();
                        if (snapshot != null) {

                            //  fetching one by one every users data present in the db
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                UsersInfoDataModel userInfo = dataSnapshot.getValue(UsersInfoDataModel.class);   // getValue returns object type but we want UserInfoModel
                                // class type so we have type casted it by writing UsersInfoDataModel.class inside the getValue method
                                userInfo.setUserId(dataSnapshot.getKey());  // storing key means particular students id and adding it into arraylist

                                // we don't want to show current user to follow list na bec current user itself following ourself is not correct scenario na so that's why
                                // while fetching if id matches to current users id then that id will not be added to arraylist which we are going to show to search list
                                if (!dataSnapshot.getKey().equals(FirebaseAuth.getInstance().getUid())) {
                                    usersList.add(userInfo);
                                }

                            }
                            // setting shimmer effect visibility gone and fragments view components visibility visible
                            binding.shimmerLayoutSearch.stopShimmer();
                            binding.shimmerLayoutSearch.setVisibility(View.GONE);
                            binding.searchFragRV.setVisibility(View.VISIBLE);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        // SearchItem on search bar
        searchItemMethod(usersList, adapter);


        return binding.getRoot();
    }

    // searching the user by his name
    private void searchItemMethod(List<UsersInfoDataModel> usersList, SearchFragRVAdapter adapter) {
        binding.searchFragSearchView.clearFocus(); // bec of this when we open the search page then keyboard will not appear for search field when user will click on that
        // search field when user will click on that search field then only keyboard will appear to screen
        binding.searchFragSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<UsersInfoDataModel> searchList = new ArrayList<>();

                for (UsersInfoDataModel listItem : usersList) {
                    // searching the typed word in list if exists then adding it to searchList
                    if (listItem.getUsername().toLowerCase().contains(newText.toLowerCase())) {
                        searchList.add(listItem);
                    }
                }

                // if searchList is empty means there no user is present of typed name in db and if that names user is present then notifying the adapter to update
                // the recycler view
                if (searchList.isEmpty()) {
                    Toast.makeText(getContext(), "No data found", Toast.LENGTH_SHORT).show();
                } else {
                    adapter.setFilteredList(searchList);
                }
                return true;
            }
        });
    }


}