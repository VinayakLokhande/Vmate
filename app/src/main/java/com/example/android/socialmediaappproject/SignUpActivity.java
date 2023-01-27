package com.example.android.socialmediaappproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.android.socialmediaappproject.ModelClasses.UsersInfoDataModel;
import com.example.android.socialmediaappproject.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    ActivitySignUpBinding binding;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        binding.signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // if field is empty and user trying to login then showing him error
                if (binding.signUpUsernameInputEditText.getText().toString().trim().equals("") || binding.signUpEmailInputEditTxt.getText().toString().trim().equals("") ||
                        binding.signUpPasswordInputEditTxt.getText().toString().trim().equals("") || binding.signUpPhoneInputEditText.getText().toString().trim().equals("") ||
                        binding.signUpProfessionInputEditText.getText().toString().trim().equals("")) {

                    checkIsEmptyOrNot();


                } else {

                    // storing user info to the firebase db
                    binding.progressBarSignUp.setVisibility(View.VISIBLE);
                    binding.signUpBtn.setEnabled(false);

                    firebaseAuth.createUserWithEmailAndPassword(binding.signUpEmailInputEditTxt.getText().toString().trim(),
                                    binding.signUpPasswordInputEditTxt.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        UsersInfoDataModel model = new UsersInfoDataModel(binding.signUpUsernameInputEditText.getText().toString().trim(),
                                                binding.signUpEmailInputEditTxt.getText().toString().trim(), binding.signUpPasswordInputEditTxt.getText().toString().trim(),
                                                binding.signUpPhoneInputEditText.getText().toString().trim(), binding.signUpProfessionInputEditText.getText().toString().trim());

                                        // getting current signed up users id,creating it a new child and storing all the info to that id
                                        String id = task.getResult().getUser().getUid();
                                        firebaseDatabase.getReference()
                                                .child("Users")
                                                .child(id)
                                                .setValue(model);
                                        Toast.makeText(SignUpActivity.this, "SignedUp Successfully.", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        binding.progressBarSignUp.setVisibility(View.GONE);
                                        finish();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SignUpActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }
        });

    }

    // checking field is empty or not if empty then showing him error
    private void checkIsEmptyOrNot() {

        if (binding.signUpUsernameInputEditText.getText().toString().equals("")) {
            showError(binding.signUpUsernameInputEditText, "Please enter username field");

        } else if (binding.signUpEmailInputEditTxt.getText().toString().equals("") || !binding.signUpEmailInputEditTxt.getText().toString().contains("@gmail.com")) {
            showError(binding.signUpEmailInputEditTxt, "Please enter valid email address");

        } else if (binding.signUpPasswordInputEditTxt.getText().toString().equals("") || binding.signUpPasswordInputEditTxt.getText().toString().length() < 6) {
            showError(binding.signUpPasswordInputEditTxt, "Please enter minimum 6 or more characters.");

        } else if (binding.signUpPhoneInputEditText.getText().toString().equals("") || binding.signUpPhoneInputEditText.getText().toString().length() < 10) {
            showError(binding.signUpPhoneInputEditText, "Please enter valid phone number");

        } else if (binding.signUpProfessionInputEditText.getText().toString().equals("")) {
            showError(binding.signUpProfessionInputEditText, "Please enter profession field");
        }

    }

    private void showError(TextInputEditText inputTxt, String errorMsg) {
        inputTxt.setError(errorMsg);
        inputTxt.requestFocus();
    }


    // if firebase have particular user id who is using this application in his device so he is logged in already then directly main activity will be shown to him
    // but if logged out or not created his ac yet then login activity will be appear to him first
    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseUser != null) {
            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}