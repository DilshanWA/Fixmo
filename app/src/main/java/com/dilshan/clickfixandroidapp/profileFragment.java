package com.dilshan.clickfixandroidapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class profileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private Button saveProfileButton;
    private LinearLayout signOutButton, startServiceBtn;
    private ImageButton selectImageButton;
    private CircleImageView profileImageView;

    private Uri imageUri;
    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private StorageReference profileImagesRef;
    private TextView userNameProfile;
    private TextView userEmailProfile;
    private ImageView backBtnToHome;

    public profileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        profileImagesRef = FirebaseStorage.getInstance().getReference().child("profileImages");

        selectImageButton = view.findViewById(R.id.selectImageBtn);
        saveProfileButton = view.findViewById(R.id.editProfile);
        profileImageView = view.findViewById(R.id.profileImageViewR);
        userNameProfile = view.findViewById(R.id.userNameProfile);
        userEmailProfile = view.findViewById(R.id.userEmailProfile);
        signOutButton = view.findViewById(R.id.sign_out);
        startServiceBtn = view.findViewById(R.id.StartService);
        backBtnToHome = view.findViewById(R.id.backPage);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading profile image...");
        progressDialog.setCancelable(false);

        selectImageButton.setOnClickListener(v -> openFileChooser());
        saveProfileButton.setOnClickListener(v -> saveProfile());
        signOutButton.setOnClickListener(v -> confirmSignOut());
        startServiceBtn.setOnClickListener(v -> addServices());
        backBtnToHome.setOnClickListener(v -> backToHome());

        // Load the user's profile data
        loadUserProfile();

        return view;
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), PICK_IMAGE_REQUEST);
    }

    private void confirmSignOut() {
        new AlertDialog.Builder(getContext())
                .setTitle("Sign Out")
                .setMessage("Are you sure you want to sign out?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        signOutFunction();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void signOutFunction() {
        mAuth.signOut();
        Intent intent = new Intent(getActivity(), Login_page.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
    }

    private void addServices() {
        Intent intent = new Intent(getActivity(), Service_add_form.class);
        startActivity(intent);
    }

    private void backToHome() {
        Intent intent = new Intent(getActivity(), Mainhome.class);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileImageView.setImageURI(imageUri);
        }
    }

    private void saveProfile() {
        if (imageUri != null) {
            progressDialog.show();

            final StorageReference fileRef = profileImagesRef.child(mAuth.getCurrentUser().getUid() + ".jpg");
            fileRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            updateUserProfile(imageUrl);
                        });
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Failed to upload profile image", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(getContext(), "Please select a profile image", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUserProfile(String imageUrl) {
        String userId = mAuth.getCurrentUser().getUid();

        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User currentUser = dataSnapshot.getValue(User.class);
                    if (currentUser != null) {
                        currentUser.setImageUrl(imageUrl);

                        usersRef.child(userId).setValue(currentUser)
                                .addOnSuccessListener(aVoid -> {
                                    progressDialog.dismiss();
                                    Toast.makeText(getContext(), "Profile image updated successfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getActivity(), Mainhome.class));
                                    getActivity().finish();
                                })
                                .addOnFailureListener(e -> {
                                    progressDialog.dismiss();
                                    Toast.makeText(getContext(), "Failed to update profile image", Toast.LENGTH_SHORT).show();
                                });
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserProfile() {
        String userId = mAuth.getCurrentUser().getUid();

        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User currentUser = dataSnapshot.getValue(User.class);
                    if (currentUser != null) {
                        if (currentUser.getImageUrl() != null) {
                            Glide.with(getContext()).load(currentUser.getImageUrl()).into(profileImageView);
                        }
                        if (currentUser.getName() != null) {
                            userNameProfile.setText(currentUser.getName());
                        }
                        if (currentUser.getEmail() != null) {
                            userEmailProfile.setText(currentUser.getEmail());
                        }
                    } else {
                        Toast.makeText(getContext(), "User data is null", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
