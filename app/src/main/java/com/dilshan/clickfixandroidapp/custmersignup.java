package com.dilshan.clickfixandroidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class custmersignup extends AppCompatActivity {

    EditText name, email, password, mobile, city, address;
    Button create_btn;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custmersignup);

        TextView textView = findViewById(R.id.login);
        ImageView backbtn = findViewById(R.id.back_Btn);
        textView.setOnClickListener(v -> {
            Intent intent = new Intent(custmersignup.this, Login_page.class);
            startActivity(intent);
        });
        backbtn.setOnClickListener(v -> {
            Intent intent  = new Intent(custmersignup.this,Login_page.class);
            startActivity(intent);
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        mAuth = FirebaseAuth.getInstance();

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        mobile = findViewById(R.id.mobile);
        city = findViewById(R.id.City);
        address = findViewById(R.id.address);
        create_btn = findViewById(R.id.Sign_upBtn);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating account...");
        progressDialog.setCancelable(false);

        addData();
    }

    // Data Save in Database
    public void addData() {
        create_btn.setOnClickListener(v -> {
            String nameInput = name.getText().toString().trim();
            String emailInput = email.getText().toString().trim();
            String passwordInput = password.getText().toString().trim();
            String mobileInput = mobile.getText().toString().trim();
            String cityInput = city.getText().toString().trim();
            String addressInput = address.getText().toString().trim();

            if (nameInput.isEmpty() || emailInput.isEmpty() || passwordInput.isEmpty() || mobileInput.isEmpty() || cityInput.isEmpty() || addressInput.isEmpty()) {
                Toast.makeText(custmersignup.this, "All fields are required", Toast.LENGTH_LONG).show();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
                email.setError("Please provide a valid email address");
                email.requestFocus();
                return;
            }
            if (passwordInput.length() < 8) {
                password.setError("Password must be at least 8 characters long");
                password.requestFocus();
                return;
            }

            progressDialog.show();

            // User Auth
            mAuth.createUserWithEmailAndPassword(emailInput, passwordInput)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            assert user != null;

                            // Create a new user object to store in the database
                            User newUser = new User(nameInput, emailInput, passwordInput, mobileInput, cityInput, addressInput);

                            // Add the user to the database
                            databaseReference.child(user.getUid()).setValue(newUser).addOnCompleteListener(task1 -> {
                                progressDialog.dismiss(); // Dismiss progress dialog here

                                if (task1.isSuccessful()) {
                                    Toast.makeText(custmersignup.this, "Account Created Successfully", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(custmersignup.this, Mainhome.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // If writing to the database fails
                                    Toast.makeText(custmersignup.this, "Failed to save user data. Try again.", Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(e -> {
                                // Handle failure to write to the database
                                progressDialog.dismiss(); // Ensure progressDialog is dismissed
                                Toast.makeText(custmersignup.this, "Database error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });

                        } else {
                            progressDialog.dismiss(); // Dismiss progress dialog on failure
                            Toast.makeText(custmersignup.this, "Authentication failed. Try again.", Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(e -> {
                        // Handle failure to create the account
                        progressDialog.dismiss(); // Ensure progressDialog is dismissed
                        Toast.makeText(custmersignup.this, "Authentication error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });

        });
    }
}
