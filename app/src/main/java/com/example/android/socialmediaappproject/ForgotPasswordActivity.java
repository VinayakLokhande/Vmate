package com.example.android.socialmediaappproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.android.socialmediaappproject.databinding.ActivityForgotPasswordBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    ActivityForgotPasswordBinding binding;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.forgotPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.forgotPasswordEmailInputEditTxt.getText().toString().trim().equals("")) {
                    checkIsEmptyOrNot();
                } else {
                    email = binding.forgotPasswordEmailInputEditTxt.getText().toString().trim();
                    forgotPassword(email);
                }
            }
        });

    }

    private void forgotPassword(String email) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Check your email please.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ForgotPasswordActivity.this,LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Error : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    // checking field is empty or not if empty then showing him error
    private void checkIsEmptyOrNot() {

        if (binding.forgotPasswordEmailInputEditTxt.getText().toString().equals("")) {
            showError(binding.forgotPasswordEmailInputEditTxt, "Please enter this field.");
        }

    }

    // if field is empty and user trying to login then showing him error
    private void showError(TextInputEditText inputTxt, String errorMsg) {
        inputTxt.setError(errorMsg);
        inputTxt.requestFocus();
    }


}