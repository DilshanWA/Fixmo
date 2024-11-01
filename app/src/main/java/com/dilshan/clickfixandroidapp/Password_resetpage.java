package com.dilshan.clickfixandroidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class Password_resetpage extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailEditText;
    private Button resetButton;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_resetpage);

        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.userEmail);
        resetButton = findViewById(R.id.reset_btn);


        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                if (!email.isEmpty()) {
                    resetPassword(email);
                } else {
                    Toast.makeText(Password_resetpage.this, "Please enter your email address", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void resetPassword(String email) {
        ProgressDialog progressDialog = new ProgressDialog(Password_resetpage.this);
        progressDialog.setMessage("Sending  email...");
        progressDialog.setCancelable(false);

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                progressDialog.dismiss();
                Toast.makeText(Password_resetpage.this, "Password reset email sent", Toast.LENGTH_SHORT).show();
            } else {
                progressDialog.dismiss();
                Toast.makeText(Password_resetpage.this, "Failed to send reset email", Toast.LENGTH_SHORT).show();
            }
        });

        progressDialog.show();
    }

}
