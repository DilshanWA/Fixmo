package com.dilshan.clickfixandroidapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class Login_page extends AppCompatActivity {

    EditText email, password;
    Button login_btn;
    ImageView passwordToggle;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login_btn = findViewById(R.id.Login_btn);
        passwordToggle = findViewById(R.id.password_toggle);
        mAuth = FirebaseAuth.getInstance();

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed before text is changed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    passwordToggle.setVisibility(View.VISIBLE);
                } else {
                    passwordToggle.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed after text is changed
            }
        });

        passwordToggle.setOnClickListener(v -> {
            if (isPasswordVisible) {
                // Hide password
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                passwordToggle.setImageResource(R.drawable.icons_eye_visible); // Change icon to closed eye
                isPasswordVisible = false;
            } else {
                // Show password
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                passwordToggle.setImageResource(R.drawable.icons_eye_open);
                isPasswordVisible = true;
            }
            // Move cursor to end of the text
            password.setSelection(password.length());
        });

        login_btn.setOnClickListener(v -> {
            String emailInput = email.getText().toString().trim();
            String passwordInput = password.getText().toString().trim();

            if (emailInput.isEmpty()) {
                email.setError("Email is required");
                email.requestFocus();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
                email.setError("Please provide a valid email");
                email.requestFocus();
                return;
            }

            if (passwordInput.isEmpty()) {
                password.setError("Password is required");
                password.requestFocus();
                return;
            }

            if (passwordInput.length() < 8) {
                password.setError("Min password length is 8 characters");
                password.requestFocus();
                return;
            }

            mAuth.signInWithEmailAndPassword(emailInput, passwordInput).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(Login_page.this, Mainhome.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    email.setBackgroundResource(R.drawable.red_border_error);
                    password.setBackgroundResource(R.drawable.red_border_error);
                    Toast.makeText(Login_page.this, "Failed to login. Please check your credentials", Toast.LENGTH_LONG).show();
                }
            });
        });

        TextView textView = findViewById(R.id.signuplink);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login_page.this, custmersignup.class);
                startActivity(intent);
            }
        });

        TextView fogotPassword = findViewById(R.id.fogot_password);

        fogotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login_page.this,Password_resetpage.class);
                startActivity(intent);
            }
        });
    }

}
