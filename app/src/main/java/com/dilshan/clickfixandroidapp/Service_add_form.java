package com.dilshan.clickfixandroidapp;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.dilshan.clickfixandroidapp.painter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Service_add_form extends AppCompatActivity {

    private static final int IMAGE_PICK_CODE = 1000;

    private EditText nameInput, emailInput, phoneInput, PriceInput, locationInput, descriptionInput;
    private Spinner spinner;
    private Button submitButton, selectImageButton;
    private ImageView selectedImageView;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private Uri imageUri;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_add_form);

        // Initialize Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("services");
        storageReference = FirebaseStorage.getInstance().getReference("service_images");

        nameInput = findViewById(R.id.et_name);
        emailInput = findViewById(R.id.et_email);
        phoneInput = findViewById(R.id.et_phone);
        spinner = findViewById(R.id.spinner);
        PriceInput = findViewById(R.id.price_perhour);
        locationInput = findViewById(R.id.et_location);
        descriptionInput = findViewById(R.id.et_description);
        submitButton = findViewById(R.id.submit_button);
        selectImageButton = findViewById(R.id.select_image_button);
        selectedImageView = findViewById(R.id.image_view);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_items, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Starting Your Service...");
        progressDialog.setCancelable(false);

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open gallery to select an image
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, IMAGE_PICK_CODE);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = nameInput.getText().toString();
                final String email = emailInput.getText().toString();
                final String phone = phoneInput.getText().toString();
                final String category = spinner.getSelectedItem().toString();
                final String price = PriceInput.getText().toString();
                final String location = locationInput.getText().toString();
                final String description = descriptionInput.getText().toString();

                if (!name.isEmpty() && !email.isEmpty() && !phone.isEmpty() && !category.isEmpty() && !price.isEmpty() && !location.isEmpty() && !description.isEmpty() && imageUri != null) {
                    // Show progress dialog
                    progressDialog.show();

                    // Upload image to Firebase Storage
                    final StorageReference imageRef = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
                    imageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get the download URL
                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();

                                    // Create a new Service object
                                    Service serviceObject = new Service(name, email, phone, category, price, location, description, imageUrl);
                                    // Store the object in Firebase
                                    databaseReference.push().setValue(serviceObject);

                                    // Dismiss progress dialog
                                    progressDialog.dismiss();

                                    Toast.makeText(Service_add_form.this, "Service add Successfull: " + category, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Service_add_form.this, Mainhome.class);
                                    startActivity(intent);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Dismiss progress dialog
                                    progressDialog.dismiss();

                                    Toast.makeText(Service_add_form.this, "Image Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Dismiss progress dialog
                            progressDialog.dismiss();

                            Toast.makeText(Service_add_form.this, "Image Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(Service_add_form.this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            selectedImageView.setImageURI(imageUri);
        }
    }

    private String getFileExtension(Uri uri) {
        return getContentResolver().getType(uri).split("/")[1];
    }
}

// Modify the Service class to include an image URL field
class Service {
    public String name;
    public String email;
    public String phone;
    public String category;
    public String price;
    public String location;
    public String description;
    public String imageUrl;

    public Service() {
        // Default constructor required for calls to DataSnapshot.getValue(Service.class)
    }

    public Service(String name, String email, String phone, String category, String price, String location, String description, String imageUrl) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.category = category;
        this.price = price;
        this.location = location;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getCategory() {
        return category;
    }

    public String getPrice() {
        return price;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
