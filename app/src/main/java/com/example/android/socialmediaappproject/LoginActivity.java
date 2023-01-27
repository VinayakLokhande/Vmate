package com.example.android.socialmediaappproject;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.android.socialmediaappproject.databinding.ActivityLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();



        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // if field is empty and user trying to login then showing him error
                if (binding.loginEmailInputEditTxt.getText().toString().trim().equals("") || binding.loginPasswordInputEditTxt.getText().toString().trim().equals("")) {

                    checkIsEmptyOrNot();

                } else {

                    binding.progressBarLogin.setVisibility(View.VISIBLE);
                    binding.loginBtn.setEnabled(false);

                    // if user has filled all the fields then his info will be check in the firebase db if the info he has filled in the fields is correct
                    // then he will be login successfully otherwise not
                    firebaseAuth.signInWithEmailAndPassword(binding.loginEmailInputEditTxt.getText().toString().trim(),
                                    binding.loginPasswordInputEditTxt.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {

                                        binding.progressBarLogin.setVisibility(View.GONE);

                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                }
            }
        });


        // if user don't have an account yet and he wanted to create one ac then he will be thrown on sign up activity
        binding.loginCreateTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });


        binding.forgotPasswordTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,ForgotPasswordActivity.class));
            }
        });

    }


    // if firebase have particular user id who is using this application in his device so he is logged in already then directly main activity will be shown to him
    // but if logged out or not logged in yet then login activity will be appear to him first
    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseUser != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }


    // checking field is empty or not if empty then showing him error
    private void checkIsEmptyOrNot() {

        if (binding.loginEmailInputEditTxt.getText().toString().equals("") || !binding.loginEmailInputEditTxt.getText().toString().contains("@gmail.com")) {
            showError(binding.loginEmailInputEditTxt, "Please enter valid email address.");

        } else if (binding.loginPasswordInputEditTxt.getText().toString().equals("")) {
            showError(binding.loginPasswordInputEditTxt, "Please enter minimum 6 or more characters.");
        }

    }

    // if field is empty and user trying to login then showing him error
    private void showError(TextInputEditText inputTxt, String errorMsg) {
        inputTxt.setError(errorMsg);
        inputTxt.requestFocus();
    }

}