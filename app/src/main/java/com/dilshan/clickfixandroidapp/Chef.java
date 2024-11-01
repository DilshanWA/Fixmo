package com.dilshan.clickfixandroidapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.dilshan.clickfixandroidapp.databinding.ActivityChefBinding;
import com.dilshan.clickfixandroidapp.databinding.ActivityCleaningBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Chef extends AppCompatActivity {

    private ActivityChefBinding binding;

    private DatabaseReference databaseReference;
    private ArrayList<Service> serviceList;
    private ServiceAdapter serviceAdapter;
    private ImageView backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChefBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("services");
        serviceList = new ArrayList<>();
        serviceAdapter = new ServiceAdapter(this, serviceList);
        binding.listView.setAdapter(serviceAdapter);


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                serviceList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Service service = postSnapshot.getValue(Service.class);
                    if (service != null && "Chef".equals(service.getCategory())) {
                        serviceList.add(service);
                    }
                }
                serviceAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Chef", "Error retrieving data from Firebase: " + databaseError.getMessage());
            }
        });

        backArrow = findViewById(R.id.backArrow);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Chef.this, Mainhome.class);
                startActivity(intent);
            }
        });

    }
}