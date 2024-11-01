package com.dilshan.clickfixandroidapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.dilshan.clickfixandroidapp.databinding.ActivityServiceDetailsingleBinding;
import com.google.firebase.auth.FirebaseAuth;

public class ServiceDetailActivity extends AppCompatActivity {

    private ActivityServiceDetailsingleBinding binding;
    private Button findLocationBtn;

    private FirebaseAuth mAuth;

    public Button BookingServiceBtn;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityServiceDetailsingleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Retrieve data from Intent
        intent = getIntent();
        String category = intent.getStringExtra("category");
        String username = intent.getStringExtra("username");
        String service = intent.getStringExtra("service");
        String mobile = intent.getStringExtra("phone");
        String description = intent.getStringExtra("description");
        String location = intent.getStringExtra("location");
        String imageUrl = intent.getStringExtra("imageUrl");

        // Set data to views
        binding.service.setText(service);
        binding.category.setText(category);
        binding.description.setText(description);
        binding.userName.setText(username);
        binding.location.setText(location);

        // Load image using Glide
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.google)
                .error(R.drawable.eyeclose)
                .into(binding.serviceImage);

        binding.backPage.setOnClickListener(view -> {
            Intent intent1 = new Intent(ServiceDetailActivity.this, Mainhome.class);
            startActivity(intent1);
        });

        findLocationBtn = findViewById(R.id.find_location);
        findLocationBtn.setOnClickListener(v -> findlocationonmap());

    }

    private void findlocationonmap() {
        String location = intent.getStringExtra("location");

        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(location));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            // Handle the case where no map application is available
            Toast.makeText(ServiceDetailActivity.this, "No map application found", Toast.LENGTH_SHORT).show();
        }
    }

}
