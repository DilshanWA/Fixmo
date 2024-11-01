package com.dilshan.clickfixandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class homeFragment extends Fragment {

    private ArrayList<Service> serviceList;
    private homeServiceAdapter serviceAdapter;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReference2;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private Handler autoScrollHandler;
    private Runnable autoScrollRunnable;
    private int currentIndex = 0;
    private boolean isScrollingForward = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Handle any arguments passed to the fragment
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        serviceList = new ArrayList<>();
        serviceAdapter = new homeServiceAdapter(getContext(), serviceList);
        recyclerView.setAdapter(serviceAdapter);

        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("services");
        FirebaseUser currentUser = mAuth.getCurrentUser();
        databaseReference2 = FirebaseDatabase.getInstance().getReference("Users");

        // Fetch latest 5 services from Firebase
        databaseReference.limitToLast(5).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                serviceList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Service service = snapshot.getValue(Service.class);
                    if (service != null) {
                        serviceList.add(service);
                    }
                }
                serviceAdapter.notifyDataSetChanged();
                startAutoScroll();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase Error", "Error fetching services", databaseError.toException());
            }
        });

        TextView headerNameText = view.findViewById(R.id.headerName);

        if (currentUser != null) {
            // Get current user's UID
            String uid = currentUser.getUid();

            // Retrieve user data from Firebase Database
            databaseReference2.child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String name = snapshot.child("name").getValue(String.class);
                        String address = snapshot.child("address").getValue(String.class);

                        // Set retrieved data to TextViews
                        headerNameText.setText("Hi " + name);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle possible errors.
                }
            });
        }

        // Add CardView click listeners
        addCardViewListeners(view);

        autoScrollHandler = new Handler(Looper.getMainLooper());

        return view;
    }

    private void addCardViewListeners(View view) {
        CardView cardView1 = view.findViewById(R.id.card_1);
        CardView cardView2 = view.findViewById(R.id.card_2);
        CardView cardView3 = view.findViewById(R.id.card_3);
        CardView cardView4 = view.findViewById(R.id.card_4);
        CardView cardView5 = view.findViewById(R.id.card_5);
        CardView cardView6 = view.findViewById(R.id.card_6);

        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), painter.class);
                startActivity(intent);
            }
        });

        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Cleaning.class);
                startActivity(intent);
            }
        });

        cardView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Chef.class);
                startActivity(intent);
            }
        });

        cardView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Carpenter.class);
                startActivity(intent);
            }
        });

        cardView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Plumbing.class);
                startActivity(intent);
            }
        });

        cardView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Electrician.class);
                startActivity(intent);
            }
        });
    }

    private void startAutoScroll() {
        autoScrollRunnable = new Runnable() {
            @Override
            public void run() {
                if (serviceList.size() == 0) return;

                if (isScrollingForward) {
                    currentIndex++;
                    if (currentIndex >= serviceList.size()) {
                        currentIndex = serviceList.size() - 1;
                        isScrollingForward = false;
                    }
                } else {
                    currentIndex--;
                    if (currentIndex < 0) {
                        currentIndex = 0;
                        isScrollingForward = true;
                    }
                }

                RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(getContext()) {
                    @Override
                    protected int getHorizontalSnapPreference() {
                        return LinearSmoothScroller.SNAP_TO_START;
                    }

                    @Override
                    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                        return 200f / displayMetrics.densityDpi; // Adjust this value to control the scrolling speed
                    }
                };

                smoothScroller.setTargetPosition(currentIndex);
                recyclerView.getLayoutManager().startSmoothScroll(smoothScroller);
                autoScrollHandler.postDelayed(this, 10000);
            }
        };
        autoScrollHandler.postDelayed(autoScrollRunnable, 10000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        autoScrollHandler.removeCallbacks(autoScrollRunnable);
    }
}
