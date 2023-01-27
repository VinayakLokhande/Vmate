package com.example.android.socialmediaappproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.car.ui.IFocusArea;
import com.example.android.socialmediaappproject.Fragments.AddFragment;
import com.example.android.socialmediaappproject.Fragments.HomeFragment;
import com.example.android.socialmediaappproject.Fragments.NotificationFragment;
import com.example.android.socialmediaappproject.Fragments.ProfileFragment;
import com.example.android.socialmediaappproject.Fragments.SearchFragment;
import com.example.android.socialmediaappproject.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

// *** new
//public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
public class MainActivity extends AppCompatActivity {
    private static final String ROOT_FRAGMENT_TAG = "root_fragment";
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // home fragment will be appear for first to the user when he will open the application at any time
        fragmentCreator(new HomeFragment(), true);


        // tracking users click on the bottom nav bar and showing him particular clicked fields fragment
        binding.bottomNavViewBar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.notificationBottomItem:
                        fragmentCreator(new NotificationFragment(), false);
                        return true;

                    case R.id.addBottomItem:
                        fragmentCreator(new AddFragment(), false);
                        return true;

                    case R.id.searchBottomItem:
                        fragmentCreator(new SearchFragment(), false);
                        return true;

                    case R.id.profileBottomItem:
                        fragmentCreator(new ProfileFragment(), false);
                        return true;

                    default:
                        fragmentCreator(new HomeFragment(), false);
                        return true;
                }
            }
        });






        //  **** new
//        loadFragment(new HomeFragment());
//        binding.bottomNavViewBar.setOnItemSelectedListener(this);

    }


        // **** new
//    public boolean loadFragment(Fragment fragment) {
//        if (fragment != null) {
//            getSupportFragmentManager().beginTransaction().replace(R.id.frameContainer, fragment).commit();
//        }
//        return true;
//    }


    // **** new
//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        Fragment fragment =  null;
//        switch (item.getItemId()) {
//            case R.id.homeBottomItem:
//                fragment = new HomeFragment();
//                break;
//
//            case R.id.notificationBottomItem:
//                fragment = new NotificationFragment();
//                break;
//
//            case R.id.addBottomItem:
//                fragment = new AddFragment();
//                break;
//
//            case R.id.searchBottomItem:
//                fragment = new SearchFragment();
//                break;
//
//            case R.id.profileBottomItem:
//                fragment = new ProfileFragment();
//                break;
//        }
//        return loadFragment(fragment);
//    }


    // managing the fragments
    public void fragmentCreator(Fragment fragment, boolean flag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (flag) {
            fragmentTransaction.add(R.id.frameContainer, fragment);
            fragmentManager.popBackStack(ROOT_FRAGMENT_TAG,0);
            fragmentTransaction.addToBackStack(ROOT_FRAGMENT_TAG);
        } else {
            fragmentTransaction.replace(R.id.frameContainer, fragment);
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (binding.bottomNavViewBar.getSelectedItemId() == R.id.homeBottomItem) {
            super.onBackPressed();
            finish();

        } else if (binding.bottomNavViewBar.getSelectedItemId() == R.id.notificationBottomItem){
            binding.bottomNavViewBar.setSelectedItemId(R.id.homeBottomItem);

        } else if (binding.bottomNavViewBar.getSelectedItemId() == R.id.addBottomItem){
            binding.bottomNavViewBar.setSelectedItemId(R.id.homeBottomItem);

        } else if (binding.bottomNavViewBar.getSelectedItemId() == R.id.searchBottomItem){
            binding.bottomNavViewBar.setSelectedItemId(R.id.homeBottomItem);

        } else if (binding.bottomNavViewBar.getSelectedItemId() == R.id.profileBottomItem){
            binding.bottomNavViewBar.setSelectedItemId(R.id.homeBottomItem);
        }

    }


    // ***** new
//    @Override
//    public void onBackPressed() {
//        if (binding.bottomNavViewBar.getSelectedItemId() == R.id.homeBottomItem) {
//            super.onBackPressed();
//            finish();
//        } else {
//            binding.bottomNavViewBar.setSelectedItemId(R.id.homeBottomItem);
//        }
//    }

}