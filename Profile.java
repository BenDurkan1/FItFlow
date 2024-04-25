package com.example.FitFlow;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity {

    private TextView tFullName, tEmail, tDoB, tGender, tPhone, tWelcome, tWeight;
    private ProgressBar progressBar;
    private String fullName, email, doB, gender, mobile, weight, imageUrl;
    private ImageView imageView_profile_dp;
    private FirebaseAuth authProfile;
    private Button btnContinue;
    private Button btn1Edit;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initializeUIComponents();

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser == null) {
         //   Toast.makeText(Profile.this, "Something went wrong", Toast.LENGTH_SHORT).show();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            showUser(firebaseUser);
        }
    }

    private void initializeUIComponents() {
        imageView_profile_dp = findViewById(R.id.imageView_profile_dp);
        tFullName = findViewById(R.id.textViewFullName);
        tEmail = findViewById(R.id.textViewEmail);
        tDoB = findViewById(R.id.textViewDOB);
        tGender = findViewById(R.id.textViewGender);
        tPhone = findViewById(R.id.textViewMobile);
        progressBar = findViewById(R.id.progress_bar);
        tWelcome = findViewById(R.id.textViewWelcome);
        tWeight = findViewById(R.id.textViewWeight);
        btnContinue = findViewById(R.id.btnContinue);
        btn1Edit = findViewById(R.id.btn1Edit);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setNavigationViewListener();
    }

    private void setNavigationViewListener() {
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                startActivity(new Intent(Profile.this, Profile.class));
            } else if (itemId == R.id.runs) {
                Intent intent = new Intent(Profile.this, MainActivity.class);
                intent.putExtra("fullName", fullName);
                intent.putExtra("weight", weight);
                startActivity(intent);
                finish();
            } else if (itemId == R.id.fitness) {
                startActivity(new Intent(Profile.this, Path.class));
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        updateUIFromIntent(intent);
    }

    private void updateUIFromIntent(Intent intent) {
        if (intent != null) {
            fullName = intent.getStringExtra("fullName");
            email = intent.getStringExtra("email");
            doB = intent.getStringExtra("doB");
            gender = intent.getStringExtra("gender");
            mobile = intent.getStringExtra("mobile");
            weight = intent.getStringExtra("weight");

            tFullName.setText(fullName);
            tEmail.setText(email);
            tDoB.setText(doB);
            tGender.setText(gender);
            tPhone.setText(mobile);
            tWeight.setText(weight);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showUser(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readUserDetails != null) {
                    fullName = readUserDetails.fullName;
                    email = firebaseUser.getEmail();
                    doB = readUserDetails.doB;
                    gender = readUserDetails.gender;
                    mobile = readUserDetails.phone;
                    weight = readUserDetails.weight;
                     imageUrl = snapshot.child("profileImageUrl").getValue(String.class);

                    Log.d("ProfileActivity", "Image URL: " + imageUrl);

                    tWelcome.setText("Welcome " + fullName);
                    tFullName.setText(fullName);
                    tEmail.setText(email);
                    tDoB.setText(doB);
                    tGender.setText(gender);
                    tPhone.setText(mobile);
                    tWeight.setText(weight);

                    if (imageView_profile_dp != null) {
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Glide.with(Profile.this)
                                    .load(imageUrl)
                                    .placeholder(R.drawable.ic_baseline_person_2_24)
                                    .error(R.drawable.ic_baseline_person_2_24)
                                    .circleCrop()
                                    .into(imageView_profile_dp);
                        } else {
                            imageView_profile_dp.setImageResource(R.drawable.ic_baseline_person_2_24);
                        }
                    } else {
                    //    Log.e("ProfileActivity", "ImageView is null");
                    }
                } else {
                    Toast.makeText(Profile.this, "User data is empty.", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Profile.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });



        btnContinue.setOnClickListener(v -> {
            Intent intent = new Intent(Profile.this, MainActivity.class);
            intent.putExtra("fullName", fullName);
            intent.putExtra("weight", weight);
            startActivity(intent);
        });

        btn1Edit.setOnClickListener(v -> {
            Intent intent = new Intent(Profile.this, EditActivity.class);
            intent.putExtra("fullName", fullName);
            intent.putExtra("email", email);
            intent.putExtra("dob", doB);
            intent.putExtra("gender", gender);
            intent.putExtra("mobile", mobile);
            intent.putExtra("weight", weight);
            intent.putExtra("profileImageUrl", imageUrl);

            startActivity(intent);
        });
    }
}
